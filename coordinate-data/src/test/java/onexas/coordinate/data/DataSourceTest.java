package onexas.coordinate.data;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import onexas.coordinate.data.test.CoordinateDataTestBase;



/**
 * 
 * @author Dennis Chen
 *
 */
public class DataSourceTest extends CoordinateDataTestBase{

	@Autowired
	DataSource ds;
	
	@Autowired
	@Qualifier(CoordinateDataConfiguration.DATA_SOURCE)
	DataSource xsds;
	
	@Test
	public void run() {
		Assert.assertNotNull(ds);
		Assert.assertNotNull(xsds);
	}
}
