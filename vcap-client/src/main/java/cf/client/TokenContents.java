package cf.client;

import org.codehaus.jackson.annotate.JsonProperty;
import cf.common.JsonObject;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class TokenContents extends JsonObject {

	private final String jti;
	private final List<String> scope;
	private final String clientId;
	private final String userId;
	private final String userName;
	private final String email;

	private final Long issuedAt; // Time in seconds
	private final Long expires; // Time in seconds

	private final URI issuer;

	private final List<String> audience;

	public TokenContents(
			@JsonProperty("jti") String jti,
			@JsonProperty("scope") List<String> scope,
			@JsonProperty("client_id") String clientId,
			@JsonProperty("user_id") String userId,
			@JsonProperty("user_name") String userName,
			@JsonProperty("email") String email,
			@JsonProperty("iat") Long issuedAt,
			@JsonProperty("exp") Long expires,
			@JsonProperty("aud") List<String> audience,
			@JsonProperty("iss") URI issuer) {
		this.jti = jti;
		this.scope = scope;
		this.clientId = clientId;
		this.userId = userId;
		this.userName = userName;
		this.email = email;
		this.issuedAt = issuedAt;
		this.expires = expires;
		this.audience = audience;
		this.issuer = issuer;
	}

	public String getJti() {
		return jti;
	}

	public List<String> getScope() {
		return scope;
	}

	public String getClientId() {
		return clientId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public Date getIssuedAt() {
		return new Date(issuedAt * 1000);
	}

	public Date getExpires() {
		return new Date(expires * 1000);
	}

	public URI getIssuer() {
		return issuer;
	}

	public List<String> getAudience() {
		return audience;
	}
}
