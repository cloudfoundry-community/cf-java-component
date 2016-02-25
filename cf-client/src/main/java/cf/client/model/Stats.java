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
package cf.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mike Heath
 */
public class Stats {
		
	
	public final double uptime;
	public final double mem_quota;
	public final double disk_quota;
	public final double fds_quota;
	public final Usage usage;
	
	@JsonCreator
	public Stats(
			@JsonProperty("uptime") double uptime,
			@JsonProperty("mem_quota") double mem_quota,
			@JsonProperty("disk_quota") double disk_quota,
			@JsonProperty("fds_quota") double fds_quota,
			@JsonProperty("usage") Usage usage
			
	) {
		
		this.uptime=uptime;
		this.mem_quota=mem_quota;
		this.disk_quota=disk_quota;
		this.fds_quota=fds_quota;
		this.usage=usage;
		
	}

	public double getUptime() {
		return uptime;
	}


	public double getMem_quota() {
		return mem_quota;
	}


	public double getDisk_quota() {
		return disk_quota;
	}

	public double getFds_quota() {
		return fds_quota;
	}

	public Usage getUsage() {
		return usage;
	}




}
