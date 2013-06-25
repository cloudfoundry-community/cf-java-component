package cf.spring;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NettyEventLoopGroupFactoryBean implements FactoryBean<EventLoopGroup>, DisposableBean {

	private EventLoopGroup eventLoopGroup;

	public NettyEventLoopGroupFactoryBean() {
		eventLoopGroup = new NioEventLoopGroup();
	}

	public NettyEventLoopGroupFactoryBean(int threads) {
		eventLoopGroup = new NioEventLoopGroup(threads);
	}

	@Override
	public void destroy() throws Exception {
		eventLoopGroup.shutdownGracefully();
	}

	@Override
	public EventLoopGroup getObject() throws Exception {
		return eventLoopGroup;
	}

	@Override
	public Class<?> getObjectType() {
		return eventLoopGroup.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
