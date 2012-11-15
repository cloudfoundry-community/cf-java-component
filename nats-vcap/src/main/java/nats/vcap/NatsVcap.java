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
package nats.vcap;

import nats.NatsException;
import nats.client.Message;
import nats.client.MessageHandler;
import nats.client.Nats;
import nats.client.Publication;
import nats.client.Subscription;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class NatsVcap {

	private final Nats nats;
	private final ObjectMapper mapper;

	public NatsVcap(Nats nats) {
		this.nats = nats;

		// Configure the Jackson JSON mapper
		mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public Publication publish(VcapMessage message) {
		if (message == null) {
			throw new IllegalArgumentException("message cannot be null");
		}
		final String subject = getSubject(message.getClass());
		if (subject == null) {
			throw new NatsException("Unable to publish message of type " + message.getClass().getName() + ", missing annotation " + NatsSubject.class.getName());
		}
		final String encoding = encode(message);
		return nats.publish(subject, encoding);
	}

	public <T extends VcapMessage<R>, R> Subscription subscribe(Class<T> type, VcapPublicationHandler<T, R> handler) {
		return subscribe(type, null, null, handler);
	}

	public <T extends VcapMessage<R>, R> Subscription subscribe(Class<T> type, Integer maxMessages, VcapPublicationHandler<T, R> handler) {
		return subscribe(type, null, maxMessages, handler);
	}

	public <T extends VcapMessage<R>, R> Subscription subscribe(Class<T> type, String queueGroup, VcapPublicationHandler<T, R> handler) {
		return subscribe(type, queueGroup, null, handler);
	}

	public <T extends VcapMessage<R>, R> Subscription subscribe(final Class<T> type, String queueGroup, Integer maxMessages, final VcapPublicationHandler<T, R> handler) {
		final Subscription subscribe = nats.subscribe(getSubject(type), queueGroup, maxMessages);
		final ObjectReader reader = (VcapJsonMessage.class.isAssignableFrom(type)) ? mapper.reader(type) : null;
		subscribe.addMessageHandler(new MessageHandler() {
			@Override
			public void onMessage(final Message message) {
				final String body = message.getBody();
				try {
					final T vcapMessage = reader == null ? type.newInstance() : reader.<T>readValue(body);
					handler.onMessage(new VcapPublication<T, R>() {
						@Override
						public Message getNatsMessage() {
							return message;
						}

						@Override
						public T getMessage() {
							return vcapMessage;
						}

						@Override
						public Publication reply(R replyMessage) {
							return message.reply(encode(replyMessage));
						}

						@Override
						public Publication reply(R replyMessage, long delay, TimeUnit unit) {
							return message.reply(encode(replyMessage), delay, unit);
						}
					});
				} catch (Exception e) {
					throw new NatsException(e);
				}

			}
		});
		return subscribe;
	}

	public Nats getNats() {
		return nats;
	}

	private String encode(Object value) {
		if (!(value instanceof VcapJsonMessage)) {
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
