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
package cf.client;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
* @author Mike Heath <elcapo@gmail.com>
*/
public class Resource<T> {
	private final T entity;
	private final String guidString;
	private final URI uri;
	private final Date created;
	private final Date updated;

	public Resource(T entity, String guidString, URI uri, Date created, Date updated) {
		this.entity = entity;
		this.guidString = guidString;
		this.uri = uri;
		this.created = created;
		this.updated = updated;
	}

	public T getEntity() {
		return entity;
	}

	public UUID getGuid() {
		return UUID.fromString(guidString);
	}
	
	public String getGuidString() {
		return guidString;
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
