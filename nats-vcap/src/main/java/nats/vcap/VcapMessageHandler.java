package nats.vcap;

import nats.client.Message;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface VcapMessageHandler<T extends VcapMessageBody<R>, R> {

	void onMessage(VcapMessage<T, R> message);

}
