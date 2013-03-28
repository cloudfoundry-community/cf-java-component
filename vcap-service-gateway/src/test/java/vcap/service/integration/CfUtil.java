package vcap.service.integration;

import io.netty.util.CharsetUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CfUtil {

	private static final Path VMC_PATH = Paths.get(System.getProperty("user.home"), ".cf");

	public static HostToken getTargetToken() {
		final String target = getTarget();
		final Map<String, Map<String, Object>> tokens = getTokens();
		final Map<String, Object> targetMap = tokens.get(target);
		if (targetMap != null) {
			final Object token = targetMap.get(":token");
			final Integer version = (Integer) targetMap.get(":version");
			final String spaceGuid = targetMap.get(":space").toString();
			if (token != null) {
				return new HostToken(target, token.toString(), version, spaceGuid);
			}
		}
		return null;
	}

	private static String getTarget() {
		try {
			final List<String> strings = Files.readAllLines(VMC_PATH.resolve("target"), CharsetUtil.UTF_8);
			if (strings.size() == 0) {
				return null;
			}
			return strings.get(0);
		} catch (IOException e) {
			throw new RuntimeException("Could not read VMC target ", e);
		}
	}

	private static Map<String, Map<String, Object>> getTokens() {
		final Yaml yaml = new Yaml();
		try (InputStream vmcTokens = Files.newInputStream(VMC_PATH.resolve("tokens.yml"))) {
			return yaml.loadAs(vmcTokens, Map.class);
		} catch (IOException e) {
			throw new RuntimeException("Could not open VMC token file. Have you logged in to Cloud Foundry instances using VMC?", e);
		}

	}

	public static class HostToken {
		private final String host;
		private final String token;
		private final Integer version;
		private final String spaceGuid;

		private HostToken(String host, String token, Integer version, String spaceGuid) {
			this.host = host;
			this.token = token;
			this.version = version;

			this.spaceGuid = spaceGuid;
		}

		public String getHost() {
			return host;
		}

		public String getToken() {
			return token;
		}

		public Integer getVersion() {
			return version;
		}

		public String getSpaceGuid() {
			return spaceGuid;
		}
	}

}
