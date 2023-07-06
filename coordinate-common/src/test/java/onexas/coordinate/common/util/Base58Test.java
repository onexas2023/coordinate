package onexas.coordinate.common.util;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.common.test.CoordinateCommonTestBase;


/**
 * 
 * @author Dennis Chen
 *
 */
public class Base58Test extends CoordinateCommonTestBase {

	@Test
	public void testSimple() {
		for (int i = 1; i < 200; i++) {
			byte[] data = new byte[i];
			
			Randoms.random.nextBytes(data);
//			for (int x = 0; x < data.length; x++) {
//				System.out.print("0x"+Integer.toHexString(data[x]& 0xFF)+" ");
//			}
			System.out.println();
			
			
			String str = Base58.doEncode(data);			
			byte[] data2 = Base58.doDecode(str);
			String str2 = Base58.doEncode(data2);
			
//			for (int x = 0; x < data2.length; x++) {
//				System.out.print("0x"+Integer.toHexString(data2[x]& 0xFF)+" ");
//			}
			
			System.out.println("1>>"+str);
			System.out.println("2>>"+str2);
			
			Assert.assertEquals(str, str2);
		}
	}
	
	@Test
	public void testUID() {
		for (int i = 0; i < 1000; i++) {
			UUID uuid1 = UUID.randomUUID();
//			System.out.println(">>1 " + uuid1.toString());
			String str1 = Base58UUID.encode(uuid1);
//			System.out.println(">>2 " + str1);
			UUID uuid2 = Base58UUID.decode(str1);
			Assert.assertEquals(uuid1, uuid2);
		}
	}
}