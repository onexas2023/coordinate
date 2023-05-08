package onexas.coordinate.common.lang;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;
/**
 * 
 * @author Dennis Chen
 *
 */
public class StringsTest  extends CoordinateCommonTestBase{

	@Test
	public void testRandomUid() {
		String id = Strings.randomUid();
		String id1 = Strings.randomUid(1);
		String id2 = Strings.randomUid(2);
		String id3 = Strings.randomUid(3);
		System.out.println(">>>"+id);
		System.out.println(">>>"+id1);
		System.out.println(">>>"+id2);
		System.out.println(">>>"+id3);
		Assert.assertNotEquals(id, id1);
		Assert.assertNotEquals(id, id2);
		Assert.assertNotEquals(id, id3);
		
		Assert.assertNotEquals(id1, id2);
		Assert.assertNotEquals(id1, id3);
		
		Assert.assertNotEquals(id2, id3);
		
	}
	
	@Test
	public void testTrim() {
		Assert.assertEquals(null, Strings.trim(null));
		Assert.assertEquals("abc", Strings.trim("abc"));
		Assert.assertEquals("a b c", Strings.trim(" a b c "));
		
		Assert.assertEquals(null, Strings.trim(null, 2));
		Assert.assertEquals("", Strings.trim("", 2));
		Assert.assertEquals("a", Strings.trim("a", 2));
		Assert.assertEquals("ab", Strings.trim("ab", 2));
		Assert.assertEquals("ab", Strings.trim("abc", 2));
		Assert.assertEquals("a b c", Strings.trim(" a b c ", 5));
		Assert.assertEquals("a b", Strings.trim(" a b c ", 4));
		Assert.assertEquals("a b", Strings.trim(" a b c ", 3));
		Assert.assertEquals("a", Strings.trim(" a b c ", 2));
		Assert.assertEquals("a", Strings.trim(" a b c ", 1));
		Assert.assertEquals("", Strings.trim(" a b c ", 0));
		
		Assert.assertEquals(null, Strings.trimToNull(null, 2));
		Assert.assertEquals(null, Strings.trimToNull("", 2));
		Assert.assertEquals("a", Strings.trimToNull("a", 2));
		Assert.assertEquals("ab", Strings.trimToNull("ab", 2));
		Assert.assertEquals("ab", Strings.trimToNull("abc", 2));
		Assert.assertEquals("a b c", Strings.trimToNull(" a b c ", 5));
		Assert.assertEquals("a b", Strings.trimToNull(" a b c ", 4));
		Assert.assertEquals("a b", Strings.trimToNull(" a b c ", 3));
		Assert.assertEquals("a", Strings.trimToNull(" a b c ", 2));
		Assert.assertEquals("a", Strings.trimToNull(" a b c ", 1));
		Assert.assertEquals(null, Strings.trimToNull(" a b c ", 0));
		
		
		
		Assert.assertEquals("", Strings.trimToEmpty(null, 2));
		Assert.assertEquals("", Strings.trimToEmpty("", 2));
		Assert.assertEquals("a", Strings.trimToEmpty("a", 2));
		Assert.assertEquals("ab", Strings.trimToEmpty("ab", 2));
		Assert.assertEquals("ab", Strings.trimToEmpty("abc", 2));
		Assert.assertEquals("a b c", Strings.trimToEmpty(" a b c ", 5));
		Assert.assertEquals("a b", Strings.trimToEmpty(" a b c ", 4));
		Assert.assertEquals("a b", Strings.trimToEmpty(" a b c ", 3));
		Assert.assertEquals("a", Strings.trimToEmpty(" a b c ", 2));
		Assert.assertEquals("a", Strings.trimToEmpty(" a b c ", 1));
		Assert.assertEquals("", Strings.trimToEmpty(" a b c ", 0));
		
	}
}
