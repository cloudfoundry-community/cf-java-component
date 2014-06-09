package cf.component;

import cf.component.util.DateTimeUtils;
import cf.component.util.ProcessUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class BasicVarzProducer implements VarzProducer {

	private final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();

	private final VarzBuilder varz = VarzBuilder.create();

	public BasicVarzProducer(String type, int index, String uuid) {
		varz.set("type", type)
			.set("index", index)
			.set("uuid", uuid)
			.set("start", DateTimeUtils.formatDateTime(startTime))
			.set("num_cores", Runtime.getRuntime().availableProcessors());
	}

	@Override
	public Map<String, ?> produceVarz() {
		synchronized (this) {
			try {
				final ProcessUtils.ProcessStats processStats = ProcessUtils.getProcessStats();
				final long freeMemory = Runtime.getRuntime().freeMemory();
				return varz
					.set("uptime", DateTimeUtils.formatUptime(startTime))
					.set("cpu_load_average", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
					.set("cpu", processStats.cpuUtilization)
					.set("mem_bytes", processStats.residentSetSize * 1024)
					.set("mem_used_bytes", Runtime.getRuntime().maxMemory() - freeMemory)
					.set("mem_free_bytes", freeMemory)
					.build();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
