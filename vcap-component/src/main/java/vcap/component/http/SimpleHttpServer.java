/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package vcap.component.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a simple embeddable HTTP server for handling simple web service end-points and things like publishing
 * /varz and /healthz information.
 *
 * @author "Mike Heath <elcapo@gmail.com>"
 */
public class SimpleHttpServer implements Closeable {

	private final ServerBootstrap bootstrap;

	// Access must be synchronized on self
	private final List<RequestHandle> requestHandles = new ArrayList<>();

	private final NioEventLoopGroup parentGroup;
	private final NioEventLoopGroup childGroup;

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServer.class);

	public SimpleHttpServer(SocketAddress localAddress) {
		parentGroup = new NioEventLoopGroup();
		childGroup = new NioEventLoopGroup();
		bootstrap = initBootstrap(localAddress, parentGroup, childGroup);
	}

	public SimpleHttpServer(SocketAddress localAddress, NioEventLoopGroup parentGroup, NioEventLoopGroup childGroup) {
		this.parentGroup = null;
		this.childGroup = null;
		bootstrap = initBootstrap(localAddress, parentGroup, childGroup);
	}

	private ServerBootstrap initBootstrap(SocketAddress localAddress, NioEventLoopGroup parentGroup, NioEventLoopGroup childGroup) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(parentGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.localAddress(localAddress)
				.childHandler(new SimpleHttpServerInitializer())
				.bind().awaitUninterruptibly(); // Make sure the server is bound before the constructor returns.

		LOGGER.info("Server listening on " + localAddress);

		return bootstrap;
	}

	@Override
	public void close() {
		if (parentGroup != null || childGroup != null) {
			bootstrap.shutdown();
		}
	}

	public void addHandler(Pattern uriPattern, RequestHandler requestHandler) {
		synchronized (requestHandles) {
			requestHandles.add(new RequestHandle(uriPattern, requestHandler));
		}
	}

	private class SimpleHttpServerInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		public void initChannel(SocketChannel ch) throws Exception {
			final ChannelPipeline pipeline = ch.pipeline();

			pipeline.addLast("decoder", new HttpRequestDecoder());
			pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
			pipeline.addLast("encoder", new HttpResponseEncoder());
			pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

			pipeline.addLast("handler", new SimpleHttpServerHandler());
		}
	}

	private class SimpleHttpServerHandler extends ChannelInboundMessageHandlerAdapter<FullHttpRequest> {
		@Override
		public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
			System.out.println("Method: " + request.getMethod());
			System.out.println("URI: " + request.getUri());
			for (String name : request.headers().names()) {
				System.out.println(name);
				for (String value : request.headers().getAll(name)) {
					System.out.println("\t" + value);
				};
			}
			System.out.println();
			System.out.println(request.data().toString(CharsetUtil.UTF_8));
			System.out.println("==================");

			if (!request.getDecoderResult().isSuccess()) {
				sendError(ctx, HttpResponseStatus.BAD_REQUEST);
				return;
			}

			final String uri = request.getUri();
			final RequestHandler requestHandler;
			final Matcher uriMatcher;
			lock: synchronized (requestHandles) {
				for (RequestHandle handle : requestHandles) {
					final Matcher matcher = handle.uriPattern.matcher(uri);
					if (matcher.matches()) {
						requestHandler = handle.handler;
						uriMatcher = matcher;
						break lock;
					}
				}
				requestHandler = null;
				uriMatcher = null;
			}

			if (requestHandler != null) {
				final HttpResponse httpResponse;
				httpResponse = requestHandler.handleRequest(request, uriMatcher, request.data());
				// Close the connection as soon as the message is sent.
				ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
			} else {
				System.out.println("Returning 404");
				sendError(ctx, HttpResponseStatus.NOT_FOUND);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			LOGGER.debug("Received connection from {}", ctx.channel().remoteAddress());
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			LOGGER.debug("Connection to {} closed", ctx.channel().remoteAddress());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			LOGGER.error(cause.getMessage(), cause);
			if (ctx.channel().isOpen()) {
				if (cause instanceof RequestException) {
					sendError(ctx, ((RequestException) cause).getStatus());
					return;
				} else {
					sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}

		private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
			final ByteBuf buf = Unpooled.copiedBuffer(
					"Failure: " + status.toString() + "\r\n",
					CharsetUtil.UTF_8);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
			response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

			// Close the connection as soon as the error message is sent.
			ctx.write(response).addListener(ChannelFutureListener.CLOSE);
		}
	}

	private class RequestHandle {
		private final Pattern uriPattern;
		private final RequestHandler handler;

		private RequestHandle(Pattern uriPattern, RequestHandler handler) {
			this.uriPattern = uriPattern;
			this.handler = handler;
		}
	}
}
