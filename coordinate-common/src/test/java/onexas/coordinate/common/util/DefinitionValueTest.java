package onexas.coordinate.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DefinitionValueTest extends CoordinateCommonTestBase {

	@Value("${value1}")
	String value1;

	@Value("${value2}")
	int value2;

	@Value("${value_default:abcd}")
	String value_default;

	@Value("${value_default:#{null}}")
	String value_defaultnull;

	@Test
	public void testSimple() {
		Assert.assertEquals("Test", value1);
		Assert.assertEquals(123, value2);
		Assert.assertEquals("abcd", value_default);
		Assert.assertNull(value_defaultnull);
	}

	@Test
	public void testAppContext() {
		AppContext ctx = AppContext.instance();
		Assert.assertEquals("Test", ctx.resolveDefinitionValue("__${value1}"));
		Assert.assertEquals(123, ctx.resolveDefinitionValue("__${value2}", Integer.class).intValue());
		Assert.assertEquals("abcd", ctx.resolveDefinitionValue("__${value_default:abcd}"));
		Assert.assertNull(value_defaultnull, ctx.resolveDefinitionValue("__${value_default:#{null}}"));
	}
}
