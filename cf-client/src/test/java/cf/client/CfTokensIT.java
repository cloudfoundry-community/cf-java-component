package cf.client;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author Mike Heath
 */
public class CfTokensIT {

	@Test
	public void available() {
		final CfTokens tokens = new CfTokens();
		assertTrue(tokens.getTargets().size() > 0, "You must have successfully logged in to a Cloud Foundry instances for this test to work. ");
		final String currentTarget = tokens.getCurrentTarget();
		assertNotNull(currentTarget);

		final CfTokens.CfToken token = tokens.getCurrentTargetToken();
		assertNotNull(token);
		assertNotNull(token.getOrganizationGuid(), "Use 'cf target --ask-org' to specify an organization to use");
		assertNotNull(token.getSpaceGuid(), "Use 'cf target --ask-space' to select a space to use");
		assertNotNull(token.getToken());
		assertEquals(token.getVersion(), Integer.valueOf(2), "Should be using CF v2 components.");
		assertEquals(token.getTarget(), currentTarget);
	}

}
