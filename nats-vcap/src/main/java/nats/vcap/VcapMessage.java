package nats.vcap;

import nats.client.Message;
import nats.client.Publication;

import java.util.concurrent.TimeUnit;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface VcapMessage<T extends VcapMessageBody<R>, R> {

	Message getNatsMessage();

	T getMessage();

	Publication reply(R message);

	Publication reply(R message, long delay, TimeUnit unit);

}
