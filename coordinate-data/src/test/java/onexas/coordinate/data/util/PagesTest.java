package onexas.coordinate.data.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.model.PageFilter;
import onexas.coordinate.data.test.CoordinateDataTestBase;



/**
 * 
 * @author Dennis Chen
 *
 */
public class PagesTest extends CoordinateDataTestBase{

	@Test
	public void testSimple() {
		List<String> total = new LinkedList<>();
		for(int i=0;i<105;i++) {
			total.add(Integer.toString(i));
		}
		
		ListPage<String> lp = Pages.page(total, null);
		Assert.assertEquals(0, lp.getPageIndex().intValue());
		Assert.assertEquals(total.size(), lp.getPageSize().intValue());
		Assert.assertEquals(1, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals("0", lp.getItems().get(0));
		Assert.assertEquals("10", lp.getItems().get(10));
		Assert.assertEquals("104", lp.getItems().get(104));
		
		lp = Pages.page(total, new PageFilter().withPageIndex(0).withPageSize(10));
		Assert.assertEquals(0, lp.getPageIndex().intValue());
		Assert.assertEquals(10, lp.getPageSize().intValue());
		Assert.assertEquals(11, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals(10, lp.getItems().size());
		Assert.assertEquals("0", lp.getItems().get(0));
		Assert.assertEquals("9", lp.getItems().get(9));
		
		lp = Pages.page(total, new PageFilter().withPageIndex(10).withPageSize(10));
		Assert.assertEquals(10, lp.getPageIndex().intValue());
		Assert.assertEquals(10, lp.getPageSize().intValue());
		Assert.assertEquals(11, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals(5, lp.getItems().size());
		Assert.assertEquals("100", lp.getItems().get(0));
		Assert.assertEquals("104", lp.getItems().get(4));
		
		
		lp = Pages.page(total, new PageFilter().withPageIndex(1).withPageSize(5));
		Assert.assertEquals(1, lp.getPageIndex().intValue());
		Assert.assertEquals(5, lp.getPageSize().intValue());
		Assert.assertEquals(21, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals(5, lp.getItems().size());
		Assert.assertEquals("5", lp.getItems().get(0));
		Assert.assertEquals("9", lp.getItems().get(4));
	
		
		lp = Pages.page(total, new PageFilter().withPageIndex(20).withPageSize(5));
		Assert.assertEquals(20, lp.getPageIndex().intValue());
		Assert.assertEquals(5, lp.getPageSize().intValue());
		Assert.assertEquals(21, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals(5, lp.getItems().size());
		Assert.assertEquals("100", lp.getItems().get(0));
		Assert.assertEquals("104", lp.getItems().get(4));
		
		lp = Pages.page(total, new PageFilter().withPageIndex(21).withPageSize(5));
		Assert.assertEquals(21, lp.getPageIndex().intValue());
		Assert.assertEquals(5, lp.getPageSize().intValue());
		Assert.assertEquals(21, lp.getPageTotal().intValue());
		Assert.assertEquals(total.size(), lp.getItemTotal().intValue());
		Assert.assertEquals(0, lp.getItems().size());
	}
}
