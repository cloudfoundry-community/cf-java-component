package vcap.client;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
* @author Mike Heath <elcapo@gmail.com>
*/
public class Resource<T> {
	private final T entity;
	private final UUID guid;
	private final URI uri;
	private final Date created;
	private final Date updated;

	public Resource(T entity, UUID guid, URI uri, Date created, Date updated) {
		this.entity = entity;
		this.guid = guid;
		this.uri = uri;
		this.created = created;
		this.updated = updated;
	}

	public T getEntity() {
		return entity;
	}

	public UUID getGuid() {
		return guid;
	}

	public URI getUri() {
		return uri;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}
}
