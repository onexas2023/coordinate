package onexas.coordinate.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class InfoCacheServiceTest extends CoordinateImplTestBase {

	@Autowired
	InfoCacheService service;

	@Test
	public void testSimple() {
		service.prune(System.currentTimeMillis());

		Assert.assertEquals(0, service.count());

		String info = service.acquire(Strings.randomUid(), false);
		Assert.assertNull(info);

		long now = System.currentTimeMillis();
		
		String value1 = Strings.randomUid();
		String value2 = Strings.randomPassword(3000);

		String token1 = service.put(value1, now + 10000);
		String token2 = service.put(value2, now + 10000);
		
		Assert.assertEquals(2, service.count());
		
		Assert.assertNotNull(token1);
		Assert.assertNotNull(token2);
		
		String info1 = service.acquire(token1, true);
		Assert.assertNotNull(info1);
		Assert.assertEquals(value1, info1);
		Assert.assertEquals(1, service.count());
		info1 = service.acquire(token1, true);
		Assert.assertNull(info1);
		info1 = service.acquire(token1, false);
		Assert.assertNull(info1);

		String info2 = service.acquire(token2, false);
		Assert.assertNotNull(info2);
		Assert.assertEquals(value2, info2);
		Assert.assertEquals(1, service.count());
		info2 = service.acquire(token2, false);
		Assert.assertNotNull(info2);
		Assert.assertEquals(value2, info2);
		Assert.assertEquals(1, service.count());
		
		service.prune(now+100001);
		Assert.assertEquals(0, service.count());
		info2 = service.acquire(token2, false);
		Assert.assertNull(info2);
	}

}
