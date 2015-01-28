package cf.spring.servicebroker;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class ConflictException extends ServiceBrokerException {
	public ConflictException() {
		super(HttpServletResponse.SC_CONFLICT, (String)null);
	}
}
