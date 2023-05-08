package onexas.coordinate.common.util;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecruitysTest {

	@Test
	public void testSecret() {
		SecretKey key = Securitys.generateSecretKey();
		
		String keyStr = Securitys.encodeSecretKey(key);
		System.out.println(">>>>> "+keyStr+" <<<<<");
		
		String text1 = Strings.randomUid(10000);
		System.out.println(">>> "+text1);
		byte[] byte1 = text1.getBytes();
		System.out.println(">>> "+byte1.length);
		byte[] bytem = Securitys.encrypt(byte1, key);
		System.out.println(">>> "+bytem.length);
		byte[] byte2 = Securitys.decrypt(bytem, key);
		System.out.println(">>> "+byte2.length);
		
		String text2 = new String(byte2);
		System.out.println(">>> "+text2);
		
		Assert.assertEquals(text1, text2);
		
	}
}
