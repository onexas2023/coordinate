package onexas.coordinate.common.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.service.CacheDemoService.Item;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CacheDemoServiceTest extends CoordinateCommonTestBase {

	@Autowired
	CacheDemoService service;
	
	@Autowired
	CacheEvictService evictService;

	@Autowired
	CacheManager cacheManager;

	@Test
	public void testSimple() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// test key2
		item = service.getByArg0("item1", "abc");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		items = service.list();

		item = items.get(0);
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		item = items.get(1);
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// clear item1
		item1 = service.remove("item1");
		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(2, service.getCallGet());

		// not hit 3
		item = service.get("item1");
		Assert.assertNull(item);
		Assert.assertEquals(3, service.getCallGet());

		// not hit 4
		item = service.get("item1");
		Assert.assertNull(item);
		Assert.assertEquals(4, service.getCallGet());

		// not hit 4
		item = service.get("item2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// clear
		service.update("item2", "BB");

		Assert.assertEquals(4, service.getCallGet());

		// not hit 5
		item = service.get("item2");
		Assert.assertEquals(5, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("BB", item.getName());

		// hit 5
		item = service.get("item2");
		Assert.assertEquals(5, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("BB", item.getName());

		item2 = service.remove("item2");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("BB", item2.getName());

		// not hit 6
		item = service.get("item2");
		Assert.assertEquals(6, service.getCallGet());
		Assert.assertNull(item);

		// not hit 7
		item = service.get("item2");
		Assert.assertEquals(7, service.getCallGet());
		Assert.assertNull(item);

		service.resetCallGet();
		Assert.assertEquals(0, service.getCallGet());
	}
	
	@Test
	public void test2Key() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.getBy2Key("item","1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.getBy2Key("item","1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// test key2
		item = service.getByArg0("item1", "abc");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		items = service.list();

		item = items.get(0);
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		item = items.get(1);
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// clear item1
		item1 = service.remove("item1");
		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(2, service.getCallGet());

		// not hit 3
		item = service.get("item1");
		Assert.assertNull(item);
		Assert.assertEquals(3, service.getCallGet());

		// not hit 4
		item = service.getBy2Key("item","1");
		Assert.assertNull(item);
		Assert.assertEquals(4, service.getCallGet());

		// not hit 4
		item = service.getBy2Key("item","2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// clear
		service.update("item2", "BB");

		Assert.assertEquals(4, service.getCallGet());

		// not hit 5
		item = service.getBy2Key("item","2");
		Assert.assertEquals(5, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("BB", item.getName());

		// hit 5
		item = service.getBy2Key("item","2");
		Assert.assertEquals(5, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("BB", item.getName());

		item2 = service.remove("item2");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("BB", item2.getName());

		// not hit 6
		item = service.getBy2Key("item","2");
		Assert.assertEquals(6, service.getCallGet());
		Assert.assertNull(item);

		// not hit 7
		item = service.getBy2Key("item","2");
		Assert.assertEquals(7, service.getCallGet());
		Assert.assertNull(item);

		service.resetCallGet();
		Assert.assertEquals(0, service.getCallGet());
	}

	@Test
	public void testClearAll() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		service.clearAll(null);

		// not hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		
		// not hit 4
		item = service.get("item2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 4
		item = service.get("item2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());
		
		service.remove("item1");
		service.remove("item2");
		service.resetCallGet();
		
	}
	
	
	@Test
	public void testByResultAll() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		
		service.clearByResult(null, "item1");
		
		
		// not hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		try {
			service.clearByResult(null, "itemxyz");
		}catch(NotFoundException x) {}
		
		// hit 3
		item = service.get("item2");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 3
		item = service.get("item2");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());
		
		service.remove("item1");
		service.remove("item2");
		service.resetCallGet();
		
	}
	
	@Test
	public void testCacheEvictServiceClear() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		evictService.clear("cache-test");

		// not hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 3
		item = service.get("item1");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		
		// not hit 4
		item = service.get("item2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 4
		item = service.get("item2");
		Assert.assertEquals(4, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());
		
		service.remove("item1");
		service.remove("item2");
		service.resetCallGet();
		
	}
	
	
	@Test
	public void testCacheEvictServiceEvict() {
		System.out.println(">>>>>" + cacheManager.getCacheNames());

		List<Item> items = service.list();

		Assert.assertEquals(0, items.size());

		Item item1 = service.add("item1", "A");

		Assert.assertEquals("item1", item1.getCode());
		Assert.assertEquals("A", item1.getName());

		Assert.assertEquals(0, service.getCallGet());

		// not hit 1
		Item item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 1
		item = service.get("item1");
		Assert.assertEquals(1, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		Item item2 = service.add("item2", "B");

		Assert.assertEquals("item2", item2.getCode());
		Assert.assertEquals("B", item2.getName());

		Assert.assertEquals(1, service.getCallGet());
		// not hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 2
		item = service.get("item2");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		//evict not such name or key
		evictService.clear("cache-test1");
		evictService.evict("item1", "cache-test1");
		evictService.evict("item3", "cache-test");
		
		evictService.evict("item2", "cache-test");

		// hit 2
		item = service.get("item1");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		// hit 2
		item = service.get("item1");
		Assert.assertEquals(2, service.getCallGet());
		Assert.assertEquals("item1", item.getCode());
		Assert.assertEquals("A", item.getName());

		
		// not hit 3
		item = service.get("item2");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());

		// hit 3
		item = service.get("item2");
		Assert.assertEquals(3, service.getCallGet());
		Assert.assertEquals("item2", item.getCode());
		Assert.assertEquals("B", item.getName());
		
		service.remove("item1");
		service.remove("item2");
		service.resetCallGet();
		
	}
}
