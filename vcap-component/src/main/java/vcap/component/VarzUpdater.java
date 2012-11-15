package vcap.component;

import org.codehaus.jackson.map.util.JSONPObject;

/**
 * @author Mike Heath <elcapo@gmail.com>
 */
public interface VarzUpdater {

	Varz update(Varz varz);

}
