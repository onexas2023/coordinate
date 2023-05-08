package onexas.coordinate.api.v1.sdk;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AHook;
import onexas.coordinate.api.v1.sdk.model.AHookCreate;
import onexas.coordinate.api.v1.sdk.model.UHook;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.service.HookService;
import onexas.coordinate.service.HookServiceTestListener;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testTrigger() {
		try {
			ApiClient admClient = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminHookApi admApi = new CoordinateAdminHookApi(admClient);

			ApiClient client = getApiClient();

			CoordinateHookApi api = new CoordinateHookApi(client);

			Assert.assertEquals(0, admApi.listHook(null).getItems().size());

			AHook hook1 = admApi
					.createHook(new AHookCreate().zone(HookService.ZONE_PUBLIC).data("hook1").triggerLife(3));

			UHook hook = api.triggerHook(HookService.ZONE_PUBLIC, hook1.getUid());
			Assert.assertEquals(hook1.getUid(), hook.getUid());
			Assert.assertEquals(1, hook.getTrigger().intValue());

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			sleep1();

			try {
				hook = api.triggerHook("aaa", hook1.getUid());
				Assert.fail("not here");
			} catch (ApiException x) {
				Assert.assertEquals(404, x.getCode());
			}

			hook1 = admApi.getHook(hook1.getUid(), false);
			Assert.assertEquals("hook1", hook1.getData());
			Assert.assertEquals(3, hook1.getTriggerLife().intValue());
			Assert.assertEquals(1, hook1.getTrigger().intValue());

			Map<String, Object> arg = new LinkedHashMap<>();
			arg.put("key", "aaa");

			hook = api.triggerHookWithArgs(HookService.ZONE_PUBLIC, hook1.getUid(), arg);
			Assert.assertEquals(hook1.getUid(), hook.getUid());
			Assert.assertEquals(2, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertEquals("aaa", HookServiceTestListener.hookDataMap.get("key"));

			try {
				hook = api.triggerHookWithArgs("ddd", hook1.getUid(), arg);
				Assert.fail("not here");
			} catch (ApiException x) {
				Assert.assertEquals(404, x.getCode());
			}

			hook = api.triggerHook(HookService.ZONE_PUBLIC, hook1.getUid());
			Assert.assertEquals(3, hook.getTrigger().intValue());
			sleep1();

			Assert.assertEquals("hook1", HookServiceTestListener.hookDataMap.get(hook.getUid()));
			Assert.assertNull(HookServiceTestListener.hookDataMap.get("key"));

			try {
				hook1 = admApi.getHook(hook1.getUid(), false);
				Assert.fail("not here");
			} catch (ApiException x) {
			}

			try {
				hook = api.triggerHookWithArgs(HookService.ZONE_PUBLIC, hook1.getUid(), arg);
				Assert.fail("not here");
			} catch (ApiException x) {
				Assert.assertEquals(404, x.getCode());
			}

			Assert.assertEquals(0, admApi.listHook(null).getItems().size());

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
