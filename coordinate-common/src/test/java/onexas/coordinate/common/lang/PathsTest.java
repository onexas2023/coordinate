package onexas.coordinate.common.lang;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PathsTest extends CoordinateCommonTestBase {

	@Test
	public void testMerge() {

		Assert.assertEquals("/a/b/c/d", Paths.merge('/', "/a", "b", "/c", "d"));
		Assert.assertEquals("/a/b/c/d/", Paths.merge('/', "/a", "/b", "c", "d/"));
		Assert.assertEquals("a/b/c/d", Paths.merge('/', "a", "b", "/c", "d"));
		Assert.assertEquals("a/b/c/d/", Paths.merge('/', "a", "/b", "c", "d/"));

		Assert.assertEquals("\\a1\\b2\\c3\\d4", Paths.merge('\\', "\\a1", "b2", "\\c3", "d4"));
		Assert.assertEquals("\\a1\\b2\\c3\\d4\\", Paths.merge('\\', "\\a1", "\\b2", "c3", "d4\\"));
		Assert.assertEquals("a1\\b2\\c3\\d4", Paths.merge('\\', "a1", "b2", "\\c3", "d4"));
		Assert.assertEquals("a1\\b2\\c3\\d4\\", Paths.merge('\\', "a1", "\\b2", "c3", "d4\\"));
	}
}
