package onexas.coordinate.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Hook;
import onexas.coordinate.model.HookCreate;
import onexas.coordinate.model.HookFilter;
import onexas.coordinate.model.HookUpdate;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookServiceTest extends CoordinateImplTestBase {

	@Autowired
	HookService service;

	@Test
	public void testSimple() {
		ListPage<Hook> page = service.list(null);
		Assert.assertEquals(0, page.size());

		HookCreate hookCreate = new HookCreate().withZone("abc");
		hookCreate.setSubjectType("subjectA");
		hookCreate.setSubjectUid("1234");
		hookCreate.setDescription("one hook");
		hookCreate.setOwnerType("user");
		hookCreate.setOwnerUid("5678");

		Hook hook1 = service.create(hookCreate);
		String uid = hook1.getUid();
		Assert.assertNotNull(hook1.getUid());
		Assert.assertEquals(hookCreate.getZone(), hook1.getZone());
		Assert.assertEquals(hookCreate.getSubjectType(), hook1.getSubjectType());
		Assert.assertEquals(hookCreate.getSubjectUid(), hook1.getSubjectUid());
		Assert.assertEquals(hookCreate.getDescription(), hook1.getDescription());
		Assert.assertEquals(hookCreate.getOwnerType(), hook1.getOwnerType());
		Assert.assertEquals(hookCreate.getOwnerUid(), hook1.getOwnerUid());
		Assert.assertEquals(hookCreate.getTriggerLife(), hook1.getTriggerLife());

		hook1 = service.get(uid);
		Assert.assertEquals(uid, hook1.getUid());
		Assert.assertEquals(hookCreate.getSubjectType(), hook1.getSubjectType());
		Assert.assertEquals(hookCreate.getSubjectUid(), hook1.getSubjectUid());
		Assert.assertEquals(hookCreate.getDescription(), hook1.getDescription());
		Assert.assertEquals(hookCreate.getOwnerType(), hook1.getOwnerType());
		Assert.assertEquals(hookCreate.getOwnerUid(), hook1.getOwnerUid());
		Assert.assertEquals(hookCreate.getTriggerLife(), hook1.getTriggerLife());

		// update
		HookUpdate update = new HookUpdate();
		update.setDescription("depart.one.com");

		Hook hook2 = service.update(uid, update);

		hook1 = service.find(uid);
		Assert.assertEquals(uid, hook2.getUid());
		Assert.assertEquals(update.getDescription(), hook1.getDescription());
		
		Assert.assertEquals(hookCreate.getSubjectType(), hook1.getSubjectType());
		Assert.assertEquals(hookCreate.getSubjectUid(), hook1.getSubjectUid());
		Assert.assertNotEquals(hookCreate.getDescription(), hook1.getDescription());
		Assert.assertEquals(hookCreate.getOwnerType(), hook1.getOwnerType());
		Assert.assertEquals(hookCreate.getOwnerUid(), hook1.getOwnerUid());
		Assert.assertEquals(hookCreate.getTriggerLife(), hook1.getTriggerLife());

		page = service.list(null);
		Assert.assertEquals(1, page.size());

		hook1 = page.getItems().get(0);
		Assert.assertEquals(uid, hook2.getUid());
		Assert.assertEquals(hook2.getDescription(), hook1.getDescription());
		Assert.assertEquals(hook2.getSubjectType(), hook1.getSubjectType());
		Assert.assertEquals(hook2.getSubjectUid(), hook1.getSubjectUid());
		Assert.assertEquals(hook2.getDescription(), hook1.getDescription());
		Assert.assertEquals(hook2.getOwnerType(), hook1.getOwnerType());
		Assert.assertEquals(hook2.getOwnerUid(), hook1.getOwnerUid());
		Assert.assertEquals(hook2.getTriggerLife(), hook1.getTriggerLife());

		service.delete(uid, true);

		hook1 = service.find(uid);
		Assert.assertNull(hook1);

		try {
			service.get(uid);
			Assert.fail();
		} catch (NotFoundException e) {
		}

		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testFilterPage() {
		ListPage<Hook> page = service.list(null);
		Assert.assertEquals(0, page.size());

		for (int i = 0; i < 100; i++) {
			HookCreate hookCreate = new HookCreate().withZone(HookService.ZONE_PUBLIC);
			String p = (i <= 9 ? "0" : "") + i;
			switch (i % 2) {
			case 0:
				hookCreate.setSubjectUid("org-a" + p);
				hookCreate.setDescription("HookA" + p);
				break;
			case 1:
				hookCreate.setSubjectUid("org-b" + p);
				hookCreate.setDescription("HookB" + p);
				break;
			}
			service.create(hookCreate);
			sleep(1);
		}

		// test filter, page
		page = service.list(new HookFilter().withSubjectUid("org-a00"));
		Assert.assertEquals(1, page.size());
		Hook hook00 = page.getItems().get(0);
		Assert.assertEquals("org-a00", hook00.getSubjectUid());
		Assert.assertEquals("HookA00", hook00.getDescription());
		Assert.assertEquals(HookService.ZONE_PUBLIC, hook00.getZone());

		page = service.list(new HookFilter().withSubjectUid("org-a").withStrContaining(true).withStrIgnoreCase(true)
				.withSortDesc(true));
		Assert.assertEquals(50, page.size());
		hook00 = page.getItems().get(49);
		Assert.assertEquals("org-a00", hook00.getSubjectUid());
		Assert.assertEquals("HookA00", hook00.getDescription());

		Hook hook98 = page.getItems().get(0);
		Assert.assertEquals("org-a98", hook98.getSubjectUid());
		Assert.assertEquals("HookA98", hook98.getDescription());

		page = service.list(new HookFilter().withSubjectUid("org-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		Hook hook21 = page.getItems().get(0);
		Assert.assertEquals("org-b21", hook21.getSubjectUid());

		Hook hook39 = page.getItems().get(9);
		Assert.assertEquals("org-b39", hook39.getSubjectUid());

		page = service.list(new HookFilter().withSubjectUid("org-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		Hook hook91 = page.getItems().get(0);
		Assert.assertEquals("org-b91", hook91.getSubjectUid());

		Hook hook99 = page.getItems().get(4);
		Assert.assertEquals("org-b99", hook99.getSubjectUid());

		for (Hook hook : service.list(null).getItems()) {
			service.delete(hook.getUid(), false);
		}
		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testTrigger() {
		Assert.assertEquals(0, service.list(null).size());

		
		Hook hook1 = service.create(new HookCreate().withZone(HookService.ZONE_PUBLIC).withData("hook1").withTriggerLife(3));
		
		Hook hook = service.trigger(hook1.getUid(), null, null);
		Assert.assertEquals("hook1", hook.getData());
		Assert.assertEquals(3, hook.getTriggerLife().intValue());
		Assert.assertEquals(1, hook.getTrigger().intValue());
		
		Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));
		
		sleep1();
		hook = service.get(hook1.getUid());
		Assert.assertEquals("hook1", hook.getData());
		Assert.assertEquals(3, hook.getTriggerLife().intValue());
		Assert.assertEquals(1, hook.getTrigger().intValue());
		
		
		Map<String, Object> arg = new LinkedHashMap<>();
		arg.put("key", "aaa");
		
		hook = service.trigger(hook1.getUid(), arg, null);
		Assert.assertEquals("hook1", hook.getData());
		Assert.assertEquals(3, hook.getTriggerLife().intValue());
		Assert.assertEquals(2, hook.getTrigger().intValue());
		sleep1();
		
		Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertEquals("aaa", HookServiceTestListener.hookDataMap.get("key"));
		
		hook = service.trigger(hook1.getUid(), null, null);
		Assert.assertEquals("hook1", hook.getData());
		Assert.assertEquals(3, hook.getTriggerLife().intValue());
		Assert.assertEquals(3, hook.getTrigger().intValue());
		sleep1();
		
		Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));
		
		try {
			hook = service.get(hook1.getUid());
			Assert.fail("not here");
		}catch(NotFoundException x) {}
		
		
		
		Hook hook2 = service.create(new HookCreate().withZone(HookService.ZONE_PUBLIC).withData("hook2"));
		
		hook = service.trigger(hook2.getUid(), null, null);
		Assert.assertEquals("hook2", hook.getData());
		Assert.assertNull(hook.getTriggerLife());
		Assert.assertEquals(1, hook.getTrigger().intValue());
		
		Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));
		
		sleep1();
		hook = service.get(hook2.getUid());
		Assert.assertEquals("hook2", hook.getData());
		Assert.assertNull(hook.getTriggerLife());
		Assert.assertEquals(1, hook.getTrigger().intValue());
		
		arg = new LinkedHashMap<>();
		arg.put("key", "bbb");
		
		hook = service.trigger(hook2.getUid(), arg, null);
		Assert.assertEquals("hook2", hook.getData());
		Assert.assertNull(hook.getTriggerLife());
		Assert.assertEquals(2, hook.getTrigger().intValue());
		sleep1();
		
		Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertEquals("bbb", HookServiceTestListener.hookDataMap.get("key"));
		
		hook = service.trigger(hook2.getUid(), null, null);
		Assert.assertEquals("hook2", hook.getData());
		Assert.assertNull(hook.getTriggerLife());
		Assert.assertEquals(3, hook.getTrigger().intValue());
		sleep1();
		
		Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
		Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));
		
		
		hook = service.get(hook2.getUid());
		Assert.assertEquals("hook2", hook.getData());
		Assert.assertNull(hook.getTriggerLife());
		Assert.assertEquals(3, hook.getTrigger().intValue());
		
		service.delete(hook2.getUid(), false);
		
		Assert.assertEquals(0, service.list(null).size());
	}
}
