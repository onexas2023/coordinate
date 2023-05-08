package onexas.coordinate.api.v1.sdk;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AAdmin;
import onexas.coordinate.api.v1.sdk.model.AInitRequest;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.api.v1.sdk.model.UPasswordUpdate;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminInitApiTest extends CoordinateApiSDKTestBase {

	
	@Test
	public void testInit() {
		try {
			ApiClient client = getApiClient();

			CoordinateAdminInitApi api = new CoordinateAdminInitApi(client);
			
			try{
				api.initAdmin(new AInitRequest().secret("wrongsecret"));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			
			AAdmin admin = api.initAdmin(new AInitRequest().secret("ilovedennis"));
			String password = admin.getPassword();
			Assert.assertNotNull(password);
			try{
				api.initAdmin(new AInitRequest().secret("ilovedennis"));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			ApiClient newClient = getApiClient();
			CoordinateAuthApi authApi = new CoordinateAuthApi(newClient);
			
			Authentication auth = authApi.authenticate(new AuthenticationRequest().account("admin").password(password));

			newClient.setApiKey(auth.getToken());
			
			CoordinateProfileApi profileApi = new CoordinateProfileApi(newClient);
			
			Response res = profileApi.updatePassword(new UPasswordUpdate().oldPassword(password).newPassword("5678"));
			Assert.assertFalse(Boolean.TRUE.equals(res.getErr()));
			
			Authentication token = authApi.authenticate(new AuthenticationRequest().account("admin").password("5678"));
			
			Assert.assertNotNull(token);
			Assert.assertNotNull(token.getToken());
			
			newClient.setApiKey(token.getToken());
			//it is admin, delete it to prevent other test error
			deleteUserByAccount(AppContext.config().getString("app.adminAccount"));
			deleteRoleByCode(AppContext.config().getString("app.adminRole"));
			
			
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
