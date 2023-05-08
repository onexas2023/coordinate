package onexas.coordinate.api.v1.sdk;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.api.v1.sdk.model.UPasswordUpdate;
import onexas.coordinate.api.v1.sdk.model.UProfile;
import onexas.coordinate.api.v1.sdk.model.UProfileUpdate;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.PrincipalPermission;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ProfileApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClient();

			CoordinateProfileApi api = new CoordinateProfileApi(client);
			try {
				api.updatePassword(new UPasswordUpdate().oldPassword("1111").newPassword("5678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 401, x.getCode());
			}
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	
	@Test
	public void testUpdatePassword() {
		try {
			ApiClient client = getApiClientWithAuthCreate("dennis", "1234", "users");

			CoordinateProfileApi api = new CoordinateProfileApi(client);
			
			try{
				api.updatePassword(new UPasswordUpdate().newPassword("5678"));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 400, x.getCode());
			}
			try{
				api.updatePassword(new UPasswordUpdate().oldPassword("1111").newPassword(""));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 400, x.getCode());
			}
			try{
				api.updatePassword(new UPasswordUpdate().oldPassword("1111").newPassword("5678"));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 403, x.getCode());
			}
			
			ApiClient newClient = getApiClient();
			CoordinateAuthApi authApi = new CoordinateAuthApi(newClient);
			
			try {
				authApi.authenticate(new AuthenticationRequest().account("dennis").password("5678"));
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 401, x.getCode());
			}
			Response res = api.updatePassword(new UPasswordUpdate().oldPassword("1234").newPassword("5678"));
			Assert.assertFalse(Boolean.TRUE.equals(res.getErr()));
			
			Authentication token = authApi.authenticate(new AuthenticationRequest().account("dennis").password("5678"));
			
			Assert.assertNotNull(token);
			Assert.assertNotNull(token.getToken());
			
			
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	
	
	@Test
	public void testUpdateProfileNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("dennis", "1234", "users");

			CoordinateProfileApi api = new CoordinateProfileApi(client);
			
			UProfile profile = api.getProfile();
			Assert.assertEquals("dennis", profile.getAccount());
			Assert.assertEquals("local", profile.getDomain());
			Assert.assertEquals("dennis", profile.getDisplayName());
			Assert.assertEquals(null, profile.getEmail());
			Assert.assertEquals(null, profile.getAddress());
			Assert.assertEquals(null, profile.getPhone());
			
			try {
				profile = api.updateProfile(new UProfileUpdate().displayName("DENNIS").email("abc@def.com"));
				Assert.fail("not here");
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	
	@Test
	public void testUpdateProfile() {
		try {
			ApiClient client = getApiClientWithAuthCreate("dennis", "1234", "users",
					new PrincipalPermission(onexas.coordinate.api.v1.ProfileApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.ProfileApi.ACTION_MODIFY));

			CoordinateProfileApi api = new CoordinateProfileApi(client);
			
			UProfile profile = api.getProfile();
			Assert.assertEquals("dennis", profile.getAccount());
			Assert.assertEquals("local", profile.getDomain());
			Assert.assertEquals("dennis", profile.getDisplayName());
			Assert.assertEquals(null, profile.getEmail());
			Assert.assertEquals(null, profile.getAddress());
			Assert.assertEquals(null, profile.getPhone());
			
			profile = api.updateProfile(new UProfileUpdate().displayName("DENNIS").email("abc@def.com"));
			Assert.assertEquals("dennis", profile.getAccount());
			Assert.assertEquals("local", profile.getDomain());
			Assert.assertEquals("DENNIS", profile.getDisplayName());
			Assert.assertEquals("abc@def.com", profile.getEmail());
			Assert.assertEquals(null, profile.getAddress());
			Assert.assertEquals(null, profile.getPhone());
			
			profile = api.updateProfile(new UProfileUpdate().phone("1234567").address("1st st"));
			Assert.assertEquals("DENNIS", profile.getDisplayName());
			Assert.assertEquals("abc@def.com", profile.getEmail());
			Assert.assertEquals("1st st", profile.getAddress());
			Assert.assertEquals("1234567", profile.getPhone());
			
			profile = api.getProfile();
			Assert.assertEquals("DENNIS", profile.getDisplayName());
			Assert.assertEquals("abc@def.com", profile.getEmail());
			Assert.assertEquals("1st st", profile.getAddress());
			Assert.assertEquals("1234567", profile.getPhone());
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
