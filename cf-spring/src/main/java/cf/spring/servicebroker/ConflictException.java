package cf.spring.servicebroker;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Heath
 */
public class ConflictException extends ServiceBrokerException {
	public ConflictException() {
		super(HttpServletResponse.SC_CONFLICT, (String)null);
	}
}
