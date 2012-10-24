package vcap.nats.spring;

import nats.NatsException;
import nats.client.Nats;
import nats.vcap.NatsVcap;
import nats.vcap.VcapMessage;
import nats.vcap.VcapMessageBody;
import nats.vcap.VcapMessageHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NatsVcapFactoryBean implements FactoryBean<NatsVcap>, InitializingBean {

	private final NatsVcap vcap;

	private final Collection<VcapSubscriptionConfig> subscriptions;

	public NatsVcapFactoryBean(Nats nats, Collection<VcapSubscriptionConfig> subscriptions) {
		vcap = new NatsVcap(nats);
		this.subscriptions = subscriptions;
	}

	@Override
	public NatsVcap getObject() throws Exception {
		return vcap;
	}

	@Override
	public Class<?> getObjectType() {
		return vcap == null ? NatsVcap.class : vcap.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		for (VcapSubscriptionConfig subscription : subscriptions) {
			final Object bean = subscription.getBean();
			final String methodName = subscription.getMethodName();
			final Method method = bean.getClass().getMethod(methodName, VcapMessage.class);
			final ParameterizedType parameterTypes = (ParameterizedType) method.getGenericParameterTypes()[0];
			Class<VcapMessageBody<Object>> parameterType = (Class<VcapMessageBody<Object>>) parameterTypes.getActualTypeArguments()[0];
			final String queueGroup = subscription.getQueueGroup();
			vcap.subscribe(parameterType, queueGroup, new VcapMessageHandler<VcapMessageBody<Object>, Object>() {
				@Override
				public void onMessage(VcapMessage<VcapMessageBody<Object>, Object> message) {
					try {
						method.invoke(bean, message);
					} catch (IllegalAccessException e) {
						throw new Error(e);
					} catch (InvocationTargetException e) {
						throw new NatsException(e.getTargetException());
					}
				}
			});
		}
	}
}
