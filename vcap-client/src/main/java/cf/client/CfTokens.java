package cf.client;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public class CfTokens {
	private static final Path VMC_PATH = Paths.get(System.getProperty("user.home"), ".cf");

	private final String target;
	private final Map<String, CfToken> tokens;

	public CfTokens() {
		this(VMC_PATH);
	}

	public CfTokens(Path cfDirectory) {
		target = getTarget(cfDirectory);
		tokens = parseTokens(cfDirectory);
	}

	public String getTarget() {
		return target;
	}

	public CfToken getTargetToken() {
		return tokens.get(target);
	}

	public Set<String> getTargets() {
		return tokens.keySet();
	}

	public CfToken getToken(String target) {
		return tokens.get(target);
	}

	private static String getTarget(Path cfDirectory) {
		try {
			final List<String> strings = Files.readAllLines(cfDirectory.resolve("target"), Charset.defaultCharset());
			if (strings.size() == 0) {
				return null;
			}
			return strings.get(0);
		} catch (IOException e) {
			throw new RuntimeException("Could not read VMC target ", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, CfToken> parseTokens(Path cfDirectory) {
		final Map<String, CfToken> parsedTokens = new HashMap<>();

		final Yaml yaml = new Yaml();
		try (InputStream vmcTokens = Files.newInputStream(cfDirectory.resolve("tokens.yml"))) {
			final Map<String, Map<String, Object>> tokens = yaml.loadAs(vmcTokens, Map.class);
			for (Map.Entry<String, Map<String, Object>> entry : tokens.entrySet()) {
				final String target = entry.getKey();
				final Map<String, Object> targetValues = entry.getValue();
				final Object tokenValue = targetValues.get(":token");
				final Token token = tokenValue == null ? null : Token.parseAuthorization(tokenValue.toString());
				final Integer version = (Integer) targetValues.get(":version");
				final Object organizationGuidValue = targetValues.get(":organization");
				final UUID organizationGuid = organizationGuidValue == null ? null : UUID.fromString(organizationGuidValue.toString());
				final Object spaceGuidValue = targetValues.get(":space");
				final UUID spaceGuid = spaceGuidValue == null ? null : UUID.fromString(spaceGuidValue.toString());

				final CfToken cfToken = new CfToken(target, token, version, organizationGuid, spaceGuid);
				parsedTokens.put(target, cfToken);
			}
			return parsedTokens;
		} catch (IOException e) {
			throw new RuntimeException("Could not open VMC token file. Have you logged in to Cloud Foundry instances using VMC?", e);
		}
	}

	public static class CfToken {
		private final String target;
		private final Token token;
		private final Integer version;
		private final UUID organizationGuid;
		private final UUID spaceGuid;

		private CfToken(String target, Token token, Integer version, UUID organizationGuid, UUID spaceGuid) {
			this.target = target;
			this.token = token;
			this.version = version;

			this.organizationGuid = organizationGuid;
			this.spaceGuid = spaceGuid;
		}

		public String getTarget() {
			return target;
		}

		public Token getToken() {
			return token;
		}

		public Integer getVersion() {
			return version;
		}

		public UUID getOrganizationGuid() {
			return organizationGuid;
		}

		public UUID getSpaceGuid() {
			return spaceGuid;
		}
	}

}
