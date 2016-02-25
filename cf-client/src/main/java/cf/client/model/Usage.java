package cf.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Usage {

	public final String time;
	public final double cpu;
	public final double mem;
	public final double disk;
	

	@JsonCreator
	public Usage(
			@JsonProperty("time") String time,
			@JsonProperty("cpu") double cpu,
			@JsonProperty("mem") double mem,
			@JsonProperty("disk") double disk
	
			
	) {
		
		this.time=time;
		this.cpu=cpu;
		this.mem=mem;
		this.disk=disk;
	
		
	}

	public String getTime() {
		return time;
	}

	public double getCpu() {
		return cpu;
	}

	public double getMem() {
		return mem;
	}

	public double getDisk() {
		return disk;
	}
	
}
