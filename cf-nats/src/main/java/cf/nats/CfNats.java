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

import nats.client.Registration;
import nats.client.Request;
import nats.client.Subscription;

import java.util.concurrent.TimeUnit;

/**
 * A {@link nats.client.Nats} wrapper to simplify working with Cloud Foundry. For example, to register with a Cloud Foundry router
 * using the plain NATS client, you would have to do something like:
 *
 * <code>
 *     nats.publish("router.register", "{\"host\":\"10.1.2.3\",\"port\":9022,\"uris\":[\"foo.mikeheath.cloudfoundry.me\"]}");
 * </code>
 *
 * Using this wrapper, you could instead simple do something like:
 *
 * <code>
 *     cfNats.publish(new RouterRegister("10.1.2.3", 9022, "foo.mikeheath.cloudfoundry.me");
 * </code>
 *
 * which accomplishes the same thing but in a much less error prone manner.
 *
 * @author Mike Heath
 */
public interface CfNats {

	/**
	 * Indicates if the underlying NATS client is connected to a NATS server.
	 *
	 * @return {@code true} if the client is connected, {@code false} otherwise.
	 */
	boolean isConnected();

	/**
	 * Publishes a Cloud Foundry message over NATS.
	 *
	 * @param message the message to be published.
	 */
	void publish(MessageBody message);

	Registration publish(MessageBody message, long period, TimeUnit timeUnit);

	public <R extends MessageBody<Void>> Request request(MessageBody<R> message, long timeout, TimeUnit unit, RequestResponseHandler<R> handler);

	/**
	 * Subscribes to a Cloud Foundry NATS subject.
	 *
	 * @param type the type of message to be received.
	 * @param handler the handler object that is invoked when a message arrives.
	 * @param <T> the message type
	 * @param <R> the reply type
	 * @return a NATS subscription object.
	 */
	<T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, PublicationHandler<T, R> handler);

	/**
	 * Subscribes to a Cloud Foundry NATS subject.
	 *
	 * @param type the type of message to be received.
	 * @param handler the handler object that is invoked when a message arrives.
	 * @param maxMessages the maximum number of messages to receive before the subscription is automatically closed
	 * @param <T> the message type
	 * @param <R> the reply type
	 * @return a NATS subscription object.
	 */
	<T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, Integer maxMessages, PublicationHandler<T, R> handler);

	/**
	 * Subscribes to a Cloud Foundry NATS subject.
	 *
	 * @param type the type of message to be received.
	 * @param handler the handler object that is invoked when a message arrives.
	 * @param queueGroup the NATS queue group to join
	 * @param <T> the message type
	 * @param <R> the reply type
	 * @return a NATS subscription object.
	 */
	<T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, String queueGroup, PublicationHandler<T, R> handler);

	/**
	 * Subscribes to a Cloud Foundry NATS subject.
	 *
	 * @param type the type of message to be received.
	 * @param handler the handler object that is invoked when a message arrives.
	 * @param queueGroup the NATS queue group to join
	 * @param maxMessages the maximum number of messages to receive before the subscription is automatically closed
	 * @param <T> the message type
	 * @param <R> the reply type
	 * @return a NATS subscription object.
	 */
	<T extends MessageBody<R>, R> Subscription subscribe(Class<T> type, String queueGroup, Integer maxMessages, PublicationHandler<T, R> handler);
}
