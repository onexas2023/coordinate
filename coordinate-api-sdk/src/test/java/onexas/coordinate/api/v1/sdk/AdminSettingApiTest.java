package onexas.coordinate.api.v1.sdk;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AServerSetting;
import onexas.coordinate.api.v1.sdk.model.AServerSettingUpdate;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
@ActiveProfiles(profiles = Env.PROFILE_JOB_NODE)
public class AdminSettingApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "somerole");

			CoordinateAdminSettingApi api = new CoordinateAdminSettingApi(client);
			try {
				api.getServerSetting();
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new onexas.coordinate.model.PrincipalPermission(
					onexas.coordinate.api.v1.admin.AdminSettingApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminSettingApi.ACTION_VIEW));
			api = new CoordinateAdminSettingApi(client);
			api.getServerSetting();

			try {
				AServerSettingUpdate settingUpdate = new AServerSettingUpdate();
				settingUpdate.adminEmail("a@b.c");

				api.updateServerSetting(settingUpdate);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testSetting() {
		try {
			
			
			String defaultAdminEmail = "admin@bar.foo.com";
			
			String adminEmail1 = "dennis@somewhere";
			
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminSettingApi api = new CoordinateAdminSettingApi(client);

			AServerSetting setting = api.getServerSetting();
			Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());
			
			setting = api.updateServerSetting(new AServerSettingUpdate().adminEmail(adminEmail1));
			
			Assert.assertEquals(adminEmail1, setting.getAdminEmail());
			
			api.cleanCache();
			setting = api.getServerSetting();
			Assert.assertEquals(adminEmail1, setting.getAdminEmail());
			
			setting = api.getServerSetting();
			Assert.assertEquals(adminEmail1, setting.getAdminEmail());
			
			
			setting = api.resetServerSetting();
			Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());
			
			setting = api.getServerSetting();
			Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());;

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	
}
