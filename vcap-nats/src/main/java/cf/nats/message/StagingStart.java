/*
 *   Copyright (c) 2012 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.nats.message;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import cf.common.JsonObject;
import cf.nats.MessageBody;
import cf.nats.NatsSubject;

/**
 * Listens for staging messages.  Useful when you want to apply certain changes to an application only when it re-stages/restarts with service binding changes.
 * @author youngstrommj
 */

@NatsSubject("staging.*.start")
public class StagingStart extends JsonObject implements MessageBody<Void> {
	private final String appId;
	private final String taskId;
	private final String downloadUri;
	private final String uploadUri;
	private final String buildpackCacheDownloadUri;
	private final String buildpackCacheUploadUri;
	private final Map<String, Object> properties;

	@JsonCreator
	public StagingStart(
			@JsonProperty("app_id") String appId,
			@JsonProperty("task_id") String taskId,
			@JsonProperty("download_uri") String downloadUri,
			@JsonProperty("upload_uri") String uploadUri,
			@JsonProperty("buildpack_cache_download_uri") String buildpackCacheDownloadUri,
			@JsonProperty("buildpack_cache_upload_uri") String buildpackCacheUploadUri,
			@JsonProperty("properties") Map<String, Object> properties) {
		this.appId = appId;
		this.taskId = taskId;
		this.downloadUri = downloadUri;
		this.uploadUri = uploadUri;
		this.buildpackCacheDownloadUri = buildpackCacheDownloadUri;
		this.buildpackCacheUploadUri = buildpackCacheUploadUri;
		this.properties = properties;
	}

	public String getAppId() {
		return appId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getDownloadUri() {
		return downloadUri;
	}

	public String getUploadUri() {
		return uploadUri;
	}

	public String getBuildpackCacheDownloadUri() {
		return buildpackCacheDownloadUri;
	}

	public String getBuildpackCacheUploadUri() {
		return buildpackCacheUploadUri;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
}
