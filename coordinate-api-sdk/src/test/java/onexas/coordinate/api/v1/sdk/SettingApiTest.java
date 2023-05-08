package onexas.coordinate.api.v1.sdk;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AServerSettingUpdate;
import onexas.coordinate.api.v1.sdk.model.UServerSetting;
import onexas.coordinate.api.v1.sdk.model.USetting;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SettingApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "somerole");

			CoordinateSettingApi api = new CoordinateSettingApi(client);
			try {
				api.getSetting();
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new onexas.coordinate.model.PrincipalPermission(
					onexas.coordinate.api.v1.SettingApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.SettingApi.ACTION_VIEW));
			api = new CoordinateSettingApi(client);
			api.getSetting();

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

			CoordinateAdminSettingApi adminApi = new CoordinateAdminSettingApi(client);
			CoordinateSettingApi api = new CoordinateSettingApi(client);

			USetting setting = api.getSetting();
			UServerSetting serverSetting = setting.getServer();
			
			Assert.assertEquals(defaultAdminEmail, serverSetting.getAdminEmail());
			
			serverSetting = api.getServerSetting();
			Assert.assertEquals(defaultAdminEmail, serverSetting.getAdminEmail());
			
			adminApi.updateServerSetting(new AServerSettingUpdate().adminEmail(adminEmail1));
			
			
			setting = api.getSetting();
			serverSetting = setting.getServer();
			
			Assert.assertEquals(adminEmail1, serverSetting.getAdminEmail());
			
			serverSetting = api.getServerSetting();
			Assert.assertEquals(adminEmail1, serverSetting.getAdminEmail());
			

			adminApi.resetServerSetting();
			
			setting = api.getSetting();
			serverSetting = setting.getServer();
			
			Assert.assertEquals(defaultAdminEmail, serverSetting.getAdminEmail());
			serverSetting = api.getServerSetting();
			Assert.assertEquals(defaultAdminEmail, serverSetting.getAdminEmail());
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
