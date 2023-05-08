package onexas.coordinate.api.v1.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AHook;
import onexas.coordinate.api.v1.sdk.model.AHookCreate;
import onexas.coordinate.api.v1.sdk.model.AHookFilter;
import onexas.coordinate.api.v1.sdk.model.AHookListPage;
import onexas.coordinate.api.v1.sdk.model.AHookUpdate;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.service.HookService;
import onexas.coordinate.service.HookServiceTestListener;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminHookApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "someorg");

			CoordinateAdminHookApi api = new CoordinateAdminHookApi(client);
			try {
				api.listHook(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new onexas.coordinate.model.PrincipalPermission(
					onexas.coordinate.api.v1.admin.AdminHookApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminHookApi.ACTION_VIEW));
			api = new CoordinateAdminHookApi(client);
			api.listHook(null);

			try {
				AHookCreate hookCreate = new AHookCreate();
				hookCreate.setZone("orgx");
				hookCreate.setDescription("The hook x");

				api.createHook(hookCreate);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testSimple() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminHookApi api = new CoordinateAdminHookApi(client);

			List<AHook> list = api.listHook(null).getItems();
			Assert.assertEquals(0, list.size());

			AHookCreate hookCreate = new AHookCreate();
			hookCreate.setZone("orgx");
			hookCreate.setDescription("The hook x");

			AHook hook1 = api.createHook(hookCreate);
			String uid = hook1.getUid();
			Assert.assertNotNull(hook1.getUid());
			Assert.assertEquals(hookCreate.getZone(), hook1.getZone());
			Assert.assertEquals(hookCreate.getDescription(), hook1.getDescription());

			hook1 = api.getHook(uid, false);
			Assert.assertEquals(uid, hook1.getUid());
			Assert.assertEquals(hookCreate.getZone(), hook1.getZone());
			Assert.assertEquals(hookCreate.getDescription(), hook1.getDescription());

			// update
			AHookUpdate update = new AHookUpdate();
			update.setDescription("The hook y");
			AHook hook2 = api.updateHook(uid, update);

			hook1 = api.getHook(uid, true);
			Assert.assertEquals(uid, hook2.getUid());
			Assert.assertEquals(hook2.getZone(), hook1.getZone());
			Assert.assertEquals(hook2.getDescription(), hook1.getDescription());

			list = api.listHook(null).getItems();
			Assert.assertEquals(1, list.size());

			hook1 = list.get(0);
			Assert.assertEquals(uid, hook2.getUid());
			Assert.assertEquals(hook2.getZone(), hook1.getZone());
			Assert.assertEquals(hook2.getDescription(), hook1.getDescription());

			api.deleteHook(uid, true);

			hook1 = api.getHook(uid, true);
			Assert.assertNull(hook1);

			try {
				api.getHook(uid, false);
				Assert.fail();
			} catch (ApiException e) {
				Assert.assertEquals(404, e.getCode());
			}

			list = api.listHook(null).getItems();
			Assert.assertEquals(0, list.size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testFilterPage() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminHookApi api = new CoordinateAdminHookApi(client);

			AHookListPage page = api.listHook(null);
			Assert.assertEquals(0, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				AHookCreate hookCreate = new AHookCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					hookCreate.setZone("org-a" + p);
					hookCreate.setDescription("HookA" + p);
					break;
				case 1:
					hookCreate.setZone("org-b" + p);
					hookCreate.setDescription("HookB" + p);
					break;
				}
				api.createHook(hookCreate);
			}

			// test filter, page
			page = api.listHook(new AHookFilter().zone("org-a"));
			Assert.assertEquals(0, page.getItems().size());
			Assert.assertEquals((Long) 0L, page.getItemTotal());
			page = api.listHook(new AHookFilter().zone("org-a00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long) 1L, page.getItemTotal());
			AHook hook00 = page.getItems().get(0);
			Assert.assertEquals("org-a00", hook00.getZone());
			Assert.assertEquals("HookA00", hook00.getDescription());

			page = api.listHook(new AHookFilter().zone("org-a").strContaining(true).strIgnoreCase(true)
					.sortField("zone").sortDesc(true));
			Assert.assertEquals(50, page.getItems().size());
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			hook00 = page.getItems().get(49);
			Assert.assertEquals("org-a00", hook00.getZone());
			Assert.assertEquals("HookA00", hook00.getDescription());

			AHook hook98 = page.getItems().get(0);
			Assert.assertEquals("org-a98", hook98.getZone());
			Assert.assertEquals("HookA98", hook98.getDescription());

			page = api.listHook(
					new AHookFilter().zone("org-b").strContaining(true).strIgnoreCase(true).pageIndex(1).pageSize(10));
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			AHook hook21 = page.getItems().get(0);
			Assert.assertEquals("org-b21", hook21.getZone());

			AHook hook39 = page.getItems().get(9);
			Assert.assertEquals("org-b39", hook39.getZone());

			page = api.listHook(
					new AHookFilter().zone("org-b").strContaining(true).strIgnoreCase(true).pageIndex(3).pageSize(15));
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			AHook hook91 = page.getItems().get(0);
			Assert.assertEquals("org-b91", hook91.getZone());

			AHook hook99 = page.getItems().get(4);
			Assert.assertEquals("org-b99", hook99.getZone());

			for (AHook hook : api.listHook(null).getItems()) {
				api.deleteHook(hook.getUid(), false);
			}
			page = api.listHook(null);
			Assert.assertEquals(0, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testTrigger() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminHookApi api = new CoordinateAdminHookApi(client);
			
			Assert.assertEquals(0, api.listHook(null).getItems().size());

			AHook hook1 = api
					.createHook(new AHookCreate().zone(HookService.ZONE_PUBLIC).data("hook1").triggerLife(3));

			AHook hook = api.triggerHook(hook1.getUid());
			Assert.assertEquals("hook1", hook.getData());
			Assert.assertEquals(3, hook.getTriggerLife().intValue());
			Assert.assertEquals(1, hook.getTrigger().intValue());

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			sleep1();
			hook = api.getHook(hook1.getUid(), false);
			Assert.assertEquals("hook1", hook.getData());
			Assert.assertEquals(3, hook.getTriggerLife().intValue());
			Assert.assertEquals(1, hook.getTrigger().intValue());

			Map<String, Object> arg = new LinkedHashMap<>();
			arg.put("key", "aaa");

			hook = api.triggerHookWithArgs(hook1.getUid(), arg);
			Assert.assertEquals("hook1", hook.getData());
			Assert.assertEquals(3, hook.getTriggerLife().intValue());
			Assert.assertEquals(2, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertEquals("aaa", HookServiceTestListener.hookDataMap.get("key"));

			hook = api.triggerHook(hook1.getUid());
			Assert.assertEquals("hook1", hook.getData());
			Assert.assertEquals(3, hook.getTriggerLife().intValue());
			Assert.assertEquals(3, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			try {
				hook = api.getHook(hook1.getUid(), false);
				Assert.fail("not here");
			} catch (ApiException x) {
			}

			AHook hook2 = api.createHook(new AHookCreate().zone(HookService.ZONE_PUBLIC).data("hook2"));

			hook = api.triggerHook(hook2.getUid());
			Assert.assertEquals("hook2", hook.getData());
			Assert.assertNull(hook.getTriggerLife());
			Assert.assertEquals(1, hook.getTrigger().intValue());

			Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			sleep1();
			hook = api.getHook(hook2.getUid(), false);
			Assert.assertEquals("hook2", hook.getData());
			Assert.assertNull(hook.getTriggerLife());
			Assert.assertEquals(1, hook.getTrigger().intValue());

			arg = new LinkedHashMap<>();
			arg.put("key", "bbb");

			hook = api.triggerHookWithArgs(hook2.getUid(), arg);
			Assert.assertEquals("hook2", hook.getData());
			Assert.assertNull(hook.getTriggerLife());
			Assert.assertEquals(2, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertEquals("bbb", HookServiceTestListener.hookDataMap.get("key"));

			hook = api.triggerHook(hook2.getUid());
			Assert.assertEquals("hook2", hook.getData());
			Assert.assertNull(hook.getTriggerLife());
			Assert.assertEquals(3, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook2", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			hook = api.getHook(hook2.getUid(), true);
			Assert.assertEquals("hook2", hook.getData());
			Assert.assertNull(hook.getTriggerLife());
			Assert.assertEquals(3, hook.getTrigger().intValue());

			api.deleteHook(hook2.getUid(), false);

			Assert.assertEquals(0, api.listHook(null).getItems().size());

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
