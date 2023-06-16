package onexas.coordinate.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;


/**
 * 
 * @author Dennis Chen
 *
 */
public class StringSubstitutorsTest extends CoordinateCommonTestBase {

	@Test
	public void testSimple() {
		Map<String,Object> variables = new LinkedHashMap<>();
		variables.put("var1", 3);
		variables.put("var2", "Dennis");
		
		Assert.assertEquals("1+1", StringSubstitutors.replace("1+1", variables));
		Assert.assertEquals("1+${varx}", StringSubstitutors.replace("1+${varx}", variables));
		Assert.assertEquals("1+3", StringSubstitutors.replace("1+${var1}", variables));
		Assert.assertEquals("Iam+${varx}", StringSubstitutors.replace("Iam+${varx}", variables));
		Assert.assertEquals("IamDennis", StringSubstitutors.replace("Iam${var2}",variables));
	}
}