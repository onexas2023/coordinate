package onexas.coordinate.common.util;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.model.Quantity;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class QuantityFormatTest extends CoordinateCommonTestBase {

	@Test
	public void testParseFormat() {
		QuantityFormatter formatter = new QuantityFormatter();
		Quantity q1 = formatter.parse("10Gi");
		Quantity q2 = formatter.parse("10.25Gi");
		Quantity q3 = formatter.parse("10G");
		Quantity q4 = formatter.parse("10.25G");
		System.out.println(">>Q1 " + q1);
		System.out.println(">>Q2 " + q2);
		System.out.println(">>Q3 " + q3);
		System.out.println(">>Q4 " + q4);

		System.out.println(">>Q1 " + q1.toSuffixedString());
		System.out.println(">>Q2 " + q2.toSuffixedString());
		System.out.println(">>Q3 " + q3.toSuffixedString());
		System.out.println(">>Q4 " + q4.toSuffixedString());

		Assert.assertEquals(10737418240L, q1.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, q1.getFormat());
		Assert.assertEquals(11005853696L, q2.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, q1.getFormat());
		Assert.assertEquals(10000000000L, q3.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, q1.getFormat());
		Assert.assertEquals(10250000000L, q4.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, q1.getFormat());

		String f1 = formatter.format(q1);
		String f2 = formatter.format(q2);
		String f3 = formatter.format(q3);
		String f4 = formatter.format(q4);

		Assert.assertEquals("10Gi", f1);
		Assert.assertEquals("11005853696", f2);
		Assert.assertEquals("10G", f3);
		Assert.assertEquals("10250M", f4);

	}

	public static class Bean {
		Quantity q1;
		Quantity q2;

		public Quantity getQ1() {
			return q1;
		}

		public void setQ1(Quantity q1) {
			this.q1 = q1;
		}

		public Quantity getQ2() {
			return q2;
		}

		public void setQ2(Quantity q2) {
			this.q2 = q2;
		}
	}

	@Test
	public void testJson() {
		QuantityFormatter formatter = new QuantityFormatter();
		Bean b1 = new Bean();
		b1.q1 = formatter.parse("10Gi");
		b1.q2 = formatter.parse("10.25Gi");

		System.out.println(">>>>" + Jsons.jsonify(b1));
		Assert.assertEquals("{\"q1\":\"10Gi\",\"q2\":\"11005853696\"}", Jsons.jsonify(b1));

		Bean b2 = Jsons.transform(b1, Bean.class);
		Assert.assertNotEquals(b1, b2);
		Assert.assertEquals(10737418240L, b2.q1.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, b2.q1.getFormat());
		Assert.assertEquals(11005853696L, b2.q2.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, b2.q1.getFormat());
	}

	@Test
	public void testGson() {
		QuantityFormatter formatter = new QuantityFormatter();
		Bean b1 = new Bean();
		b1.q1 = formatter.parse("10Gi");
		b1.q2 = formatter.parse("10.25Gi");

		System.out.println(">>>>" + Gsons.jsonify(b1));
		Assert.assertEquals("{\"q1\":\"10Gi\",\"q2\":\"11005853696\"}", Jsons.jsonify(b1));

		Bean b2 = Gsons.transform(b1, Bean.class);
		Assert.assertNotEquals(b1, b2);
		Assert.assertEquals(10737418240L, b2.q1.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, b2.q1.getFormat());
		Assert.assertEquals(11005853696L, b2.q2.getNumber().longValue());
		Assert.assertEquals(Quantity.Format.BINARY_SI, b2.q1.getFormat());
	}

}