package vcap.component.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
* @author Mike Heath <heathma@ldschurch.org>
*/
public abstract class JsonTextResponseRequestHandler implements RequestHandler {

	@Override
	public HttpResponse handleRequest(HttpRequest request) throws RequestException {
		final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
		final String body = handle(request);
		final ByteBuf buffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
		response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, Integer.valueOf(buffer.readableBytes()));
		response.setContent(buffer);
		return response;
	}

	public abstract String handle(HttpRequest request) throws RequestException;
}
