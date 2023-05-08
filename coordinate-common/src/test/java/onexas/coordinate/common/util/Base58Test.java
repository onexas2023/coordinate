package onexas.coordinate.common.util;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;


/**
 * 
 * @author Dennis Chen
 *
 */
public class Base58Test extends CoordinateCommonTestBase {

	@Test
	public void testProjectFolder() {
		for (int i = 0; i < 1000; i++) {
			UUID uuid1 = UUID.randomUUID();
//			System.out.println(">>1 " + uid1.toString());
			String str1 = Base58UUID.encode(uuid1);
//			System.out.println(">>2 " + str1);
			UUID uuid2 = Base58UUID.decode(str1);
			Assert.assertEquals(uuid1, uuid2);
		}
	}
}