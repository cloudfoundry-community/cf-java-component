package vcap.nats.spring;

import nats.vcap.VcapMessage;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class VcapSubscriptionConfig {

	private final Object bean;
	private final String methodName;
	private final String queueGroup;

	public VcapSubscriptionConfig(Object bean, String methodName, String queueGroup) {
		this.bean = bean;
		this.methodName = methodName;
		this.queueGroup = queueGroup;
	}

	public Object getBean() {
		return bean;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getQueueGroup() {
		return queueGroup;
	}

}
