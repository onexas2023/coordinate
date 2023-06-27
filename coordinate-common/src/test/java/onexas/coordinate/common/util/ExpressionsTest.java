package onexas.coordinate.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ELException;
import javax.el.PropertyNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ExpressionsTest extends CoordinateCommonTestBase {

	@Test
	public void testSimple() {
		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("var1", 3);
		variables.put("var2", "Dennis");
		variables.put("bean1", new Bean("Alice"));
		

		Assert.assertEquals(2, Expressions.eval("${1+1}", Integer.class).intValue());
		Assert.assertEquals(true, Expressions.eval("${true}", Boolean.class).booleanValue());
		Assert.assertEquals(false, Expressions.eval("${false}", Boolean.class).booleanValue());
		Assert.assertEquals(false, Expressions.eval("${2>3}", Boolean.class).booleanValue());
		Assert.assertEquals("Answer is false", Expressions.eval("Answer is ${2>3}", String.class));
		Assert.assertEquals("Answer is A", Expressions.eval("Answer is ${empty ''?'A':'B'}", String.class));
		Assert.assertEquals("Answer is A", Expressions.eval("Answer is ${empty novar ?'A':'B'}", String.class,
				Expressions.AllowNullBaseObjectResolver));

		// variables
		Assert.assertEquals("Answer is Dennis", Expressions.eval("Answer is ${var2}", String.class, variables));
		Assert.assertEquals("Answer is Alice", Expressions.eval("Answer is ${bean1.name}", String.class, variables));
		Assert.assertEquals("Answer is Dennis",
				Expressions.eval("Answer is ${empty var3?var2:var1}", String.class, variables, Expressions.AllowNullBaseObjectResolver));
		Assert.assertEquals("Answer is 3",
				Expressions.eval("Answer is ${not empty var3?var2:var1}", String.class, variables, Expressions.AllowNullBaseObjectResolver));
		Assert.assertEquals("Answer is ",
				Expressions.eval("Answer is ${empty var2?var2:var3}", String.class, variables, Expressions.AllowNullBaseObjectResolver));

		
		Assert.assertEquals("Answer is ${var2}", Expressions.eval("Answer is \\${var2}", String.class, variables));

		// exception
		try {
			Expressions.eval("Answer is ${var2", String.class, variables);
			Assert.fail("not here");
		} catch (ELException x) {
			System.out.print(x.getMessage());
		}
		try {
			Expressions.eval("Answer is ${empty novar ?'A':'B'}", String.class);
			Assert.fail("not here");
		} catch (PropertyNotFoundException x) {
			System.out.print(x.getMessage());
		}
		try {
			Expressions.eval("Answer is ${var2.nofield}", String.class, variables);
			Assert.fail("not here");
		} catch (PropertyNotFoundException x) {
			System.out.print(x.getMessage());
		}

		try {
			Expressions.eval("Answer is ${[var2.nofield}", String.class, variables);
			Assert.fail("not here");
		} catch (ELException x) {
			System.out.print(x.getMessage());
		}
	}
	
	public static class Bean{
		String name;

		public Bean(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}