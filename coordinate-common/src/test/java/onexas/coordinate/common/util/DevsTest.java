package onexas.coordinate.common.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DevsTest extends CoordinateCommonTestBase {

	@Test
	public void testLoadConfig() {
		Map<String, Object> config = Devs.loadDevTestYaml(getClass());

		Assert.assertEquals("def", config.get("abc"));
	}
}