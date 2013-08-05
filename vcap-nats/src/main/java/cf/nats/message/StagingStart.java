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
