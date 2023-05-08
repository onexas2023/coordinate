package onexas.coordinate.common.util;

import java.io.File;

import org.junit.Test;

import onexas.coordinate.common.test.CoordinateCommonTestBase;
/**
 * 
 * @author Dennis Chen
 *
 */
public class FilesTest  extends CoordinateCommonTestBase{

	@Test
	public void testProjectFolder() { // NOSONAR
		File f1 = new File("");
		File f2 = new File(".");
		
		System.out.println("1>>>>"+f1.getAbsolutePath());
		System.out.println("2>>>>"+f2.getAbsolutePath());
	}
}
