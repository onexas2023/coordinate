package onexas.coordinate.api.v1.sdk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.PrincipalPermission;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PreferenceApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClient();

			CoordinatePreferenceApi api = new CoordinatePreferenceApi(client);
			try {
				api.updatePreferences(new HashMap<>());
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(x.getResponseBody(), 401, x.getCode());
			}
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	
	
	@Test
	public void testPreference() {
		try {
			ApiClient client = getApiClientWithAuthCreate("dennis", "1234", "users",
					new PrincipalPermission(onexas.coordinate.api.v1.PreferenceApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.PreferenceApi.ACTION_MODIFY));

			CoordinatePreferenceApi api = new CoordinatePreferenceApi(client);
			
			Map<String, String> preference = api.getPreferences();
			Assert.assertEquals(0, preference.size());
			
			String p = api.findPreference("nokey");
			Assert.assertNull(p);
			
			Map<String, String> map = new LinkedHashMap<>();
			map.put("name", "Dennis");
			map.put("email", "abc@def.com");
			
			preference = api.updatePreferences(map);
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("Dennis", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			
			p = api.findPreference("nokey");
			Assert.assertNull(p);
			p = api.findPreference("name");
			Assert.assertEquals("Dennis", p);
			p = api.findPreference("email");
			Assert.assertEquals("abc@def.com", p);
			
			preference = api.getPreferences();
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("Dennis", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			
			
			map = new LinkedHashMap<>();
			map.put("name", "DennisX");
			map.put("age", "18");
			
			preference = api.updatePreferences(map);
			Assert.assertEquals(3, preference.size());
			Assert.assertEquals("DennisX", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			Assert.assertEquals("18", preference.get("age"));
			
			preference = api.getPreferences();
			Assert.assertEquals(3, preference.size());
			Assert.assertEquals("DennisX", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			Assert.assertEquals("18", preference.get("age"));
			
			api.updatePreference("age", "47");
			api.updatePreference("address", "Somewhere");
			
			preference = api.getPreferences();
			Assert.assertEquals(4, preference.size());
			Assert.assertEquals("DennisX", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			Assert.assertEquals("47", preference.get("age"));
			Assert.assertEquals("Somewhere", preference.get("address"));
			
			
			map = new LinkedHashMap<>();
			map.put("aa", "AA");
			map.put("bb", "BB");
			
			
			preference = api.resetPreferences(map);
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("AA", preference.get("aa"));
			Assert.assertEquals("BB", preference.get("bb"));
			
			preference = api.getPreferences();
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("AA", preference.get("aa"));
			Assert.assertEquals("BB", preference.get("bb"));
			
			preference = api.resetPreferences(new LinkedHashMap<>());
			Assert.assertEquals(0, preference.size());
			preference = api.getPreferences();
			Assert.assertEquals(0, preference.size());
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
