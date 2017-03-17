package cf.spring.servicebroker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import cf.common.JsonObject;

/**
 * Class to support binding a volume mount service to an application.
 * @author Mike Youngstrom
 *
 */
public class VolumeMount extends JsonObject {
	private final String driver;
	private final String containerDir;
	private final Mode mode;
	private final DeviceType deviceType;
	private final Device device;

	/**
	 * Create a volume mount service
	 * 
	 * @param driver Name of the volume driver plugin which manages the device
	 * @param containerDir The directory to mount inside the application container
	 * @param mode R to mount the volume read-only, or RW to mount it read-write
	 * @param deviceType specifying the type of device to mount.
	 * @param device Device object containing device_type specific details.
	 */
	public VolumeMount(String driver, String containerDir, Mode mode, DeviceType deviceType, Device device) {
		super();
		this.driver = driver;
		this.containerDir = containerDir;
		this.mode = mode;
		this.deviceType = deviceType;
		this.device = device;
	}

	@JsonProperty("driver")
	public String getDriver() {
		return driver;
	}
	
	@JsonProperty("container_dir")
	public String getContainerDir() {
		return containerDir;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	@JsonProperty("device_type")
	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public interface Device {
	}
	
	/**
	 * Represents a distributed file system which can be mounted on all app instances simultaneously.
	 */
	public static class SharedDevice extends JsonObject implements Device {
		private final String volumeId;
		private final Object mountConfig;

		/**
		 * Create a shared device with only a volumeId
		 *
		 * @param volumeId ID of the shared volume to mount on every app instance.
		 */
		public SharedDevice(String volumeId) {
			this(volumeId, null);
		}

		/**
		 * Create a shared device
		 * 
		 * @param volumeId ID of the shared volume to mount on every app instance.
		 * @param mountConfig Configuration object to be passed to the driver when the volume is mounted (optional)
		 */
		public SharedDevice(String volumeId, Object mountConfig) {
			super();
			this.volumeId = volumeId;
			this.mountConfig = mountConfig;
		}
		
		@JsonProperty("volume_id")
		public String getVolumeId() {
			return volumeId;
		}
		
		@JsonProperty("mount_config")
		public Object getMountConfig() {
			return mountConfig;
		}
	}

	public enum DeviceType {
		SHARED {
			@JsonValue
			@Override
			public String toString() {
				return "shared";
			}
		}
	}

	public enum Mode {
		R {
			@JsonValue
			@Override
			public String toString() {
				return "r";
			}
		},
		RW {
			@JsonValue
			@Override
			public String toString() {
				return "rw";
			}
		}

	}

}
