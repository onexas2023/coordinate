package onexas.coordinate.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Log;
import onexas.coordinate.model.LogFilter;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogServiceTest extends CoordinateImplTestBase {

	@Autowired
	LogService service;

	@Test
	public void testSimple() {
		service.prune(Log.ERROR, System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		try {
			Assert.assertNull(service.get(Randoms.random.nextLong()));
			Assert.fail();
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(Randoms.random.nextLong()));

		String objUid;

		Log log1 = service.debug(LogServiceTest.class, objUid = Strings.randomUid(), User.class, null, null,
				"log content {}", "1");
		sleep(2);
		Assert.assertNotNull(log1.getCreatedDateTime());
		Assert.assertEquals(LogServiceTest.class.getName(), log1.getReporter());
		Assert.assertEquals(objUid, log1.getObjUid());
		Assert.assertEquals(User.class.getName(), log1.getObjType());
		Assert.assertEquals("log content 1", log1.getContent());
		Assert.assertEquals(Log.DEBUG, log1.getLevel().intValue());

		Log log = service.get(log1.getId());
		Assert.assertEquals(log1.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log1.getReporter(), log.getReporter());
		Assert.assertEquals(log1.getObjUid(), log.getObjUid());
		Assert.assertEquals(log1.getObjType(), log.getObjType());
		Assert.assertEquals(log1.getContent(), log.getContent());
		Assert.assertEquals(log1.getLevel(), log.getLevel());

		Log log2 = service.info(LogServiceTest.class, objUid = Strings.randomUid(), User.class, null, null,
				"log content {}", "2");
		sleep(2);
		Assert.assertNotNull(log2.getCreatedDateTime());
		Assert.assertEquals(LogServiceTest.class.getName(), log2.getReporter());
		Assert.assertEquals(objUid, log2.getObjUid());
		Assert.assertEquals(User.class.getName(), log2.getObjType());
		Assert.assertEquals("log content 2", log2.getContent());
		Assert.assertEquals(Log.INFO, log2.getLevel().intValue());

		log = service.find(log2.getId());
		Assert.assertEquals(log2.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log2.getReporter(), log.getReporter());
		Assert.assertEquals(log2.getObjUid(), log.getObjUid());
		Assert.assertEquals(log2.getObjType(), log.getObjType());
		Assert.assertEquals(log2.getContent(), log.getContent());
		Assert.assertEquals(log2.getLevel(), log.getLevel());

		Log log3 = service.warn(LogServiceTest.class, objUid = Strings.randomUid(), User.class, null, null,
				"log content {}", "3");
		sleep(2);
		Assert.assertNotNull(log3.getCreatedDateTime());
		Assert.assertEquals(LogServiceTest.class.getName(), log3.getReporter());
		Assert.assertEquals(objUid, log3.getObjUid());
		Assert.assertEquals(User.class.getName(), log3.getObjType());
		Assert.assertEquals("log content 3", log3.getContent());
		Assert.assertEquals(Log.WARN, log3.getLevel().intValue());

		Log log4 = service.error(LogServiceTest.class, objUid = Strings.randomUid(), User.class, null, null,
				"log content {}", "4");
		sleep(2);
		Assert.assertNotNull(log4.getCreatedDateTime());
		Assert.assertEquals(LogServiceTest.class.getName(), log4.getReporter());
		Assert.assertEquals(objUid, log4.getObjUid());
		Assert.assertEquals(User.class.getName(), log4.getObjType());
		Assert.assertEquals("log content 4", log4.getContent());
		Assert.assertEquals(Log.ERROR, log4.getLevel().intValue());

		ListPage<Log> page = service.list(null);
		Assert.assertEquals(4, page.getItemTotal().intValue());
		log = page.getItems().get(0);
		Assert.assertEquals(log1.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log1.getReporter(), log.getReporter());
		Assert.assertEquals(log1.getObjUid(), log.getObjUid());
		Assert.assertEquals(log1.getObjType(), log.getObjType());
		Assert.assertEquals(log1.getContent(), log.getContent());
		Assert.assertEquals(log1.getLevel(), log.getLevel());

		log = page.getItems().get(1);
		Assert.assertEquals(log2.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log2.getReporter(), log.getReporter());
		Assert.assertEquals(log2.getObjUid(), log.getObjUid());
		Assert.assertEquals(log2.getObjType(), log.getObjType());
		Assert.assertEquals(log2.getContent(), log.getContent());
		Assert.assertEquals(log2.getLevel(), log.getLevel());

		log = page.getItems().get(2);
		Assert.assertEquals(log3.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log3.getReporter(), log.getReporter());
		Assert.assertEquals(log3.getObjUid(), log.getObjUid());
		Assert.assertEquals(log3.getObjType(), log.getObjType());
		Assert.assertEquals(log3.getContent(), log.getContent());
		Assert.assertEquals(log3.getLevel(), log.getLevel());

		log = page.getItems().get(3);
		Assert.assertEquals(log4.getCreatedDateTime(), log.getCreatedDateTime());
		Assert.assertEquals(log4.getReporter(), log.getReporter());
		Assert.assertEquals(log4.getObjUid(), log.getObjUid());
		Assert.assertEquals(log4.getObjType(), log.getObjType());
		Assert.assertEquals(log4.getContent(), log.getContent());
		Assert.assertEquals(log4.getLevel(), log.getLevel());

		service.prune(Log.ERROR, System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());
	}

	@Test
	public void testFilter() {
		service.prune(Log.ERROR, System.currentTimeMillis());

		Assert.assertEquals(0, service.list(null).size());

		for (int i = 0; i < 100; i++) {
			switch (i % 4) {
			case 0:
				service.debug(LogServiceTest.class, Strings.randomUid(), null, null, User.class, "log {}", i);
				break;
			case 1:
				service.info(LogServiceTest.class, Strings.randomUid(), Role.class, null, null, "log {}", i);
				break;
			case 2:
				service.warn(LogServiceTest.class, Strings.randomUid(), User.class, null, null, "log {}", i);
				break;
			case 3:
				service.error(LogServiceTest.class, Strings.randomUid(), Role.class, null, null, "log {}", i);
				break;
			}
			sleep(2);
		}

		LogFilter filter = new LogFilter();
		ListPage<Log> page = service.list(filter);
		Assert.assertEquals(100, page.getItemTotal().intValue());

		filter = new LogFilter().withPageSize(20).withPageIndex(1);
		page = service.list(filter);
		Assert.assertEquals(100, page.getItemTotal().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(20, page.getPageSize().intValue());
		Log log = page.getItems().get(0);// 20
		Assert.assertEquals("log 20", log.getContent());
		log = page.getItems().get(19);// 39
		Assert.assertEquals("log 39", log.getContent());

		filter = new LogFilter().withPageSize(10).withPageIndex(1).withLevelGe(Log.WARN);
		page = service.list(filter);
		Assert.assertEquals(50, page.getItemTotal().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		log = page.getItems().get(0);// 2,3, 6,7 10,11 14,15 18,19 22,23
		Assert.assertEquals("log 22", log.getContent());
		log = page.getItems().get(9);// 39
		Assert.assertEquals("log 39", log.getContent());

		// match all
		filter = new LogFilter().withPageSize(10).withPageIndex(2).withLevelGe(Log.WARN)
				.withObjType(User.class.getName()).withReporter(LogServiceTest.class.getName())
				.withRequestUid(Strings.randomUid()).withObjUid(Strings.randomUid());
		page = service.list(filter);
		Assert.assertEquals(0, page.getItemTotal().intValue());
		Assert.assertEquals(0, page.getPageTotal().intValue());
		Assert.assertEquals(2, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());

		// match one, containing, igore cases
		filter = new LogFilter().withPageSize(10).withPageIndex(2).withLevelGe(Log.WARN)
				.withObjType(User.class.getName()).withReporter(LogServiceTest.class.getName())
				.withRequestUid(Strings.randomUid()).withObjUid(Strings.randomUid()).withStrContaining(true)
				.withStrIgnoreCase(true).withMatchAny(true);
		page = service.list(filter);
		Assert.assertEquals(100, page.getItemTotal().intValue());
		Assert.assertEquals(10, page.getPageTotal().intValue());
		Assert.assertEquals(2, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());

		service.prune(Log.ERROR, System.currentTimeMillis());
		Assert.assertEquals(0, service.list(null).size());
	}

}
