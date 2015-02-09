/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.nats;

import cf.common.JsonObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import nats.NatsException;
import nats.client.Message;
import nats.client.MessageHandler;
import nats.client.Nats;
import nats.client.Registration;
import nats.client.Request;
import nats.client.Subscription;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of {@code CfNats}.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public class DefaultCfNats implements CfNats {

	private final Nats nats;
	private final ObjectMapper mapper;

	public DefaultCfNats(Nats nats) {
		this.nats = nats;

		// Configure the Jackson JSON mapper
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Override
	public boolean isConnected() {
		return nats.isConnected();
	}

	@Override
	public void publish(MessageBody message) {
		final String subject = getPublishSubject(message);
		final String encoding = encode(message);
		nats.publish(subject, encoding);
	}

	@Override
	public Registration publish(MessageBody message, long period, TimeUnit timeUnit) {
		final String subject = getPublishSubject(message);
		final String encoding = encode(message);
		return nats.publish(subject, encoding, period, timeUnit);
	}

	@Override
	public <R extends MessageBody<Void>> Request request(MessageBody<R> message, long timeout, TimeUnit unit, final RequestResponseHandler<R> handler) {
		final String subject = getPublishSubject(message);
		final String encoding = encode(message);
		final Type[] genericInterfaces = message.getClass().getGenericInterfaces();
		final ParameterizedType replyType = (ParameterizedType) genericInterfaces[0];
		final Class<R> messageReplyClass = (Class<R>) replyType.getActualTypeArguments()[0];

		return nats.request(subject, encoding, timeout, unit, createMessageHandler(messageReplyClass, new PublicationHandler<R, Void>() {
			@Override
			public void onMessage(Publication<R, Void> publication) {
				handler.onResponse(publication);
			}
		}));
	}

	private String getPublishSubject(MessageBody message) {
		if (message == null) {
			throw new IllegalArgumentException("message cannot be null");
		}
		final String subject = getSubject(message.getClass());
		if (subject == null) {
			throw new NatsException("Unable to publish message of type " + message.getClass().getName() + ", missing annotation " + NatsSubject.class.getName());
		}
		return subject;
	}

	@Override
	public <T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, PublicationHandler<T, R> handler) {
		return subscribe(type, null, null, handler);
	}

	@Override
	public <T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, Integer maxMessages, PublicationHandler<T, R> handler) {
		return subscribe(type, null, maxMessages, handler);
	}

	@Override
	public <T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, String queueGroup, PublicationHandler<T, R> handler) {
		return subscribe(type, queueGroup, null, handler);
	}

	@Override
	public <T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, String queueGroup, Integer maxMessages, PublicationHandler<T, R> handler) {
		final Subscription subscribe = nats.subscribe(getSubject(type), queueGroup, maxMessages);
		subscribe.addMessageHandler(createMessageHandler(type, handler));
		return subscribe;
	}

	private <T extends MessageBody<R>, R> MessageHandler createMessageHandler(final Class<T> type, final PublicationHandler<T, R> handler) {
		final ObjectReader reader = (JsonObject.class.isAssignableFrom(type)) ? mapper.reader(type) : null;
		return new MessageHandler() {
					@Override
					public void onMessage(final Message message) {
						final String body = message.getBody();
						try {
							final T cfMessage;
							if (reader == null) {
								final Constructor<T> defaultConstructor = type.getConstructor();
								defaultConstructor.setAccessible(true);
								cfMessage = defaultConstructor.newInstance();
							} else {
								cfMessage = reader.<T>readValue(body);
							}
							handler.onMessage(new Publication<T, R>() {
								@Override
								public Message getNatsMessage() {
									return message;
								}

								@Override
								public T getMessageBody() {
									return cfMessage;
								}

								@Override
								public void reply(R replyMessage) {
									message.reply(encode(replyMessage));
								}

								@Override
								public void reply(R replyMessage, long delay, TimeUnit unit) {
									message.reply(encode(replyMessage), delay, unit);
								}
							});
						} catch (Exception e) {
							throw new NatsException(e);
						}
					}
				};
	}

	public Nats getNats() {
		return nats;
	}

	private String encode(Object value) {
		if (!(value instanceof JsonObject)) {
			return null;
		}
		try {
			return mapper.writeValueAsString(value);
		} catch (IOException e) {
			throw new NatsException(e);
		}
	}

	private String getSubject(Class<?> type) {
		NatsSubject subject = type.getAnnotation(NatsSubject.class);
		return subject.value();
	}
}
