package onexas.coordinate.api.v1.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleCreate;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.AUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.api.v1.sdk.model.AUserUpdate;
import onexas.coordinate.api.v1.sdk.model.Response;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.PrincipalPermission;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminUserApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone","1234", "somerole");

			CoordinateAdminUserApi api = new CoordinateAdminUserApi(client);
			try {
				api.listUser(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new PrincipalPermission(onexas.coordinate.api.v1.admin.AdminUserApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminUserApi.ACTION_VIEW));
			api = new CoordinateAdminUserApi(client);
			api.listUser(null);

			try {
				AUserCreate userCreate = new AUserCreate();
				userCreate.setDomain(Domain.LOCAL);
				userCreate.setDisplayName("Dennis");
				userCreate.setAccount("dennis");
				userCreate.setEmail("abc@def.com");
				userCreate.setPassword("1234");
				api.createUser(userCreate);
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
					new PrincipalPermission("*", "*"));

			CoordinateAdminUserApi api = new CoordinateAdminUserApi(client);
			List<AUser> list = api.listUser(null).getItems();
			// the auth user
			Assert.assertEquals(1, list.size());

			AUserCreate userCreate = new AUserCreate();
			userCreate.setDomain(Domain.LOCAL);
			userCreate.setDisplayName("Dennis");
			userCreate.setAccount("dennis");
			userCreate.setEmail("abc@def.com");
			userCreate.setPassword("1234");

			AUser user1 = api.createUser(userCreate);
			String uid = user1.getUid();
			Assert.assertNotNull(user1.getUid());
			Assert.assertEquals(userCreate.getAccount(), user1.getAccount());
			Assert.assertEquals(userCreate.getDisplayName(), user1.getDisplayName());
			Assert.assertEquals(userCreate.getEmail(), user1.getEmail());
			Assert.assertEquals(userCreate.getDomain(), user1.getDomain());
			Assert.assertFalse(user1.getDisabled());

			user1 = api.getUser(uid, false);
			Assert.assertEquals(uid, user1.getUid());
			Assert.assertEquals(userCreate.getAccount(), user1.getAccount());
			Assert.assertEquals(userCreate.getDisplayName(), user1.getDisplayName());
			Assert.assertEquals(userCreate.getEmail(), user1.getEmail());
			Assert.assertEquals(userCreate.getDomain(), user1.getDomain());
			Assert.assertFalse(user1.getDisabled());

			// update
			AUserUpdate update = new AUserUpdate();
			update.setEmail("xyz@g.f.h");
			update.setDisplayName("XYZ");
			update.setPassword("5678");
			AUser user2 = api.updateUser(uid, update);

			user1 = api.getUser(uid, true);
			Assert.assertEquals(uid, user2.getUid());
			Assert.assertEquals(user2.getAccount(), user1.getAccount());
			Assert.assertEquals(user2.getDisplayName(), user1.getDisplayName());
			Assert.assertEquals(user2.getEmail(), user1.getEmail());
			Assert.assertEquals(user2.getDomain(), user1.getDomain());
			Assert.assertEquals(user2.getDisabled(), user1.getDisabled());

			list = api.listUser(null).getItems();
			Assert.assertEquals(2, list.size());

			user1 = list.get(1);
			Assert.assertEquals(uid, user2.getUid());
			Assert.assertEquals(user2.getAccount(), user1.getAccount());
			Assert.assertEquals(user2.getDisplayName(), user1.getDisplayName());
			Assert.assertEquals(user2.getEmail(), user1.getEmail());
			Assert.assertEquals(user2.getDomain(), user1.getDomain());
			Assert.assertEquals(user2.getDisabled(), user1.getDisabled());

			api.deleteUser(uid, true);

			user1 = api.getUser(uid, true);
			Assert.assertNull(user1);

			try {
				api.getUser(uid, false);
				Assert.fail();
			} catch (ApiException e) {
				Assert.assertEquals(404, e.getCode());
			}

			list = api.listUser(null).getItems();
			Assert.assertEquals(1, list.size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testFilterPage() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));

			CoordinateAdminUserApi api = new CoordinateAdminUserApi(client);

			AUserListPage page = api.listUser(null);
			Assert.assertEquals(1, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				AUserCreate userCreate = new AUserCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					userCreate.setAccount("dennis" + p);
					userCreate.setEmail("dennis" + p + "@abc.com");
					userCreate.setDomain(Domain.LOCAL);
					userCreate.setDisplayName("Dennis" + p);
					userCreate.setPassword("1234");
					break;
				case 1:
					userCreate.setAccount("alice" + p);
					userCreate.setEmail("alice" + p + "@def.com");
					userCreate.setDomain(Domain.LOCAL);
					userCreate.setDisplayName("Alice" + p);
					userCreate.setPassword("5678");
					break;
				}
				api.createUser(userCreate);
			}

			// test filter, page
			page = api.listUser(new AUserFilter().account("dennis00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long)1L, page.getItemTotal());
			AUser user00 = page.getItems().get(0);
			Assert.assertEquals("dennis00", user00.getAccount());
			Assert.assertEquals("dennis00@abc.com", user00.getEmail());

			page = api.listUser(new AUserFilter().account("dennis").strContaining(true).strIgnoreCase(true)
					.sortField("email").sortDesc(true));
			Assert.assertEquals(50, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			user00 = page.getItems().get(49);
			Assert.assertEquals("dennis00", user00.getAccount());
			Assert.assertEquals("dennis00@abc.com", user00.getEmail());

			AUser user98 = page.getItems().get(0);
			Assert.assertEquals("dennis98", user98.getAccount());
			Assert.assertEquals("dennis98@abc.com", user98.getEmail());

			page = api.listUser(new AUserFilter().account("alice").strContaining(true).strIgnoreCase(true)
					.pageIndex(1).pageSize(10));
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			AUser user21 = page.getItems().get(0);
			Assert.assertEquals("alice21", user21.getAccount());

			AUser user39 = page.getItems().get(9);
			Assert.assertEquals("alice39", user39.getAccount());

			page = api.listUser(new AUserFilter().account("alice").strContaining(true).strIgnoreCase(true)
					.pageIndex(3).pageSize(15));
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			AUser user91 = page.getItems().get(0);
			Assert.assertEquals("alice91", user91.getAccount());

			AUser user99 = page.getItems().get(4);
			Assert.assertEquals("alice99", user99.getAccount());

			for (AUser user : api.listUser(null).getItems()) {
				if (user.getAccount().equals("authuser")) {
					continue;
				}
				api.deleteUser(user.getUid(), false);
			}
			page = api.listUser(null);
			Assert.assertEquals(1, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testUserRole() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));
			CoordinateAdminUserApi api = new CoordinateAdminUserApi(client);
			CoordinateAdminRoleApi roleApi = new CoordinateAdminRoleApi(client);

			// the auth user & role
			Assert.assertEquals(1, api.listUser(null).getItems().size());			
			Assert.assertEquals(1, roleApi.listRole(null).getItems().size());

			AUserCreate userCreate = new AUserCreate();
			userCreate.setAccount("dennis");
			userCreate.setDisplayName("Dennis");
			userCreate.setDomain(Domain.LOCAL);
			userCreate.setEmail("dennis@abc.com");
			userCreate.setPassword("1234");

			AUser user1 = api.createUser(userCreate);
			String uid = user1.getUid();

			ARoleCreate roleCreate = new ARoleCreate();
			roleCreate.setCode("roleadmin");
			roleCreate.setName("Admin");
			ARole role1 = roleApi.createRole(roleCreate);

			roleCreate = new ARoleCreate();
			roleCreate.setCode("roleusers");
			roleCreate.setName("Users");
			ARole role2 = roleApi.createRole(roleCreate);

			roleCreate = new ARoleCreate();
			roleCreate.setCode("roleadv");
			roleCreate.setName("Adv");
			ARole role3 = roleApi.createRole(roleCreate);

			List<ARole> roles = api.listUserRole(uid);
			Assert.assertEquals(0, roles.size());

			Response r = api.addUserRole(user1.getUid(), Collections.asList(role1.getUid(), role2.getUid()));
			Assert.assertFalse(Boolean.TRUE.equals(r.getErr()));

			roles = api.listUserRole(uid);
			Assert.assertEquals(2, roles.size());
			Assert.assertTrue(roles.contains(role1));
			Assert.assertTrue(roles.contains(role2));

			// repeat, shouldn't cause any error
			r = api.addUserRole(user1.getUid(), Collections.asList(role1.getUid()));
			Assert.assertFalse(Boolean.TRUE.equals(r.getErr()));

			roles = api.listUserRole(uid);
			Assert.assertEquals(2, roles.size());
			Assert.assertTrue(roles.contains(role1));
			Assert.assertTrue(roles.contains(role2));

			r = api.setUserRole(user1.getUid(), Collections.asList(role1.getUid(), role3.getUid()));
			Assert.assertFalse(Boolean.TRUE.equals(r.getErr()));

			roles = api.listUserRole(uid);
			Assert.assertEquals(2, roles.size());
			Assert.assertTrue(roles.contains(role1));
			Assert.assertTrue(roles.contains(role3));

			r = api.removeUserRole(user1.getUid(), Collections.asList(role3.getUid()));
			Assert.assertFalse(Boolean.TRUE.equals(r.getErr()));

			// not exist role, shouldn't cause any error
			r = api.removeUserRole(user1.getUid(), Collections.asList(role2.getUid()));
			Assert.assertFalse(Boolean.TRUE.equals(r.getErr()));

			roles = api.listUserRole(uid);
			Assert.assertEquals(1, roles.size());
			Assert.assertTrue(roles.contains(role1));

			roleApi.deleteRole(role1.getUid(), true);
			roles = api.listUserRole(uid);
			Assert.assertEquals(0, roles.size());

			// delete user
			api.deleteUser(uid, true);
			user1 = api.getUser(uid, true);
			Assert.assertNull(user1);

			// delete role
			roleApi.deleteRole(role2.getUid(), true);
			roleApi.deleteRole(role3.getUid(), true);
			// the auth role
			Assert.assertEquals(1, roleApi.listRole(null).getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	
	@Test
	public void testResetPreference() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));
			CoordinateAdminUserApi api = new CoordinateAdminUserApi(client);
			
			ApiClient userClient = getApiClientWithAuthCreate("dennis", "1234", "users",
					new PrincipalPermission(onexas.coordinate.api.v1.PreferenceApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.PreferenceApi.ACTION_MODIFY));
			CoordinatePreferenceApi prefApi = new CoordinatePreferenceApi(userClient);

			AUser user1 = api.listUser(new AUserFilter().account("dennis")).getItems().get(0);
			
			String uid = user1.getUid();

			Map<String, String> preference = prefApi.getPreferences();
			Assert.assertEquals(0, preference.size());
			
			Map<String, String> map = new LinkedHashMap<>();
			map.put("name", "Dennis");
			map.put("email", "abc@def.com");
			
			preference = prefApi.updatePreferences(map);
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("Dennis", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			
			preference = prefApi.getPreferences();
			Assert.assertEquals(2, preference.size());
			Assert.assertEquals("Dennis", preference.get("name"));
			Assert.assertEquals("abc@def.com", preference.get("email"));
			
			api.resetUserPreferences(uid);
			
			
			preference = prefApi.getPreferences();
			Assert.assertEquals(0, preference.size());

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
