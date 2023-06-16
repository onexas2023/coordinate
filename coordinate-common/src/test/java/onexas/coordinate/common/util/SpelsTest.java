package onexas.coordinate.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;

import onexas.coordinate.common.test.CoordinateCommonTestBase;


/**
 * 
 * @author Dennis Chen
 *
 */
public class SpelsTest extends CoordinateCommonTestBase {

	@Test
	public void testSimple() {
		Map<String,Object> variables = new LinkedHashMap<>();
		variables.put("var1", 3);
		variables.put("var2", "Dennis");
		
		Assert.assertEquals(2, Spels.eval("1+1", Integer.class).intValue());
		try {
			Assert.assertEquals(1, Spels.eval("1+#var1", Object.class));
			Assert.fail("not here");
		}catch(EvaluationException x) {
			System.out.println(">>>>"+x.getMessage());
		}
		Assert.assertEquals(4, Spels.eval("1+#var1", Integer.class, variables).intValue());
		Assert.assertEquals("Iamnull", Spels.eval("'Iam'+#var2", String.class));
		Assert.assertEquals("IamDennis", Spels.eval("'Iam'+#var2", String.class, variables));
		
		try {
			Assert.assertEquals("IamDennis", Spels.eval("'Iam+#var2", String.class, variables));
			Assert.fail("not here");
		}catch(ParseException x) {
			System.out.println(">>>>"+x.getMessage());
		}
	}
}