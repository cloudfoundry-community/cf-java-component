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
package cf.spring;

import cf.nats.CfNats;
import cf.nats.DefaultCfNats;
import cf.nats.Publication;
import nats.NatsException;
import nats.client.Nats;
import cf.nats.MessageBody;
import cf.nats.PublicationHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NatsVcapFactoryBean implements FactoryBean<CfNats>, InitializingBean {

	private final CfNats cfNats;

	private final Collection<VcapSubscriptionConfig> subscriptions;

	public NatsVcapFactoryBean(Nats nats, Collection<VcapSubscriptionConfig> subscriptions) {
		cfNats = new DefaultCfNats(nats);
		this.subscriptions = subscriptions;
	}

	@Override
	public CfNats getObject() throws Exception {
		return cfNats;
	}

	@Override
	public Class<?> getObjectType() {
		return cfNats == null ? DefaultCfNats.class : cfNats.getClass();
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
			final Method method = bean.getClass().getMethod(methodName, Publication.class);
			final ParameterizedType parameterTypes = (ParameterizedType) method.getGenericParameterTypes()[0];
			Class<MessageBody<Object>> parameterType = (Class<MessageBody<Object>>) parameterTypes.getActualTypeArguments()[0];
			final String queueGroup = subscription.getQueueGroup();
			cfNats.subscribe(parameterType, queueGroup, new PublicationHandler<MessageBody<Object>, Object>() {
				@Override
				public void onMessage(Publication publication) {
					try {
						method.invoke(bean, publication);
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
