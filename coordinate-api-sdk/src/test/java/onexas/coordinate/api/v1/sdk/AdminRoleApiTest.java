package onexas.coordinate.api.v1.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleCreate;
import onexas.coordinate.api.v1.sdk.model.ARoleFilter;
import onexas.coordinate.api.v1.sdk.model.ARoleListPage;
import onexas.coordinate.api.v1.sdk.model.ARoleUpdate;
import onexas.coordinate.api.v1.sdk.model.ARoleUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Domain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminRoleApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone","1234", "somerole");

			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(client);
			try {
				api.listRole(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new onexas.coordinate.model.PrincipalPermission(onexas.coordinate.api.v1.admin.AdminRoleApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminRoleApi.ACTION_VIEW));
			api = new CoordinateAdminRoleApi(client);
			api.listRole(null);
			
			try {
				ARoleCreate roleCreate = new ARoleCreate();
				roleCreate.setCode("rolex");
				roleCreate.setName("RoleX");

				roleCreate.setDescription("The role x");

				api.createRole(roleCreate);
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
			ApiClient client = getApiClientWithAuthCreate("authuser",Strings.randomPassword(10), "authrole", new onexas.coordinate.model.PrincipalPermission("*","*"));

			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(client);

			List<ARole> list = api.listRole(null).getItems();
			Assert.assertEquals(1, list.size());

			ARoleCreate roleCreate = new ARoleCreate();
			roleCreate.setCode("rolex");
			roleCreate.setName("RoleX");

			roleCreate.setDescription("The role x");

			ARole role1 = api.createRole(roleCreate);
			String uid = role1.getUid();
			Assert.assertNotNull(role1.getUid());
			Assert.assertEquals(roleCreate.getCode(), role1.getCode());
			Assert.assertEquals(roleCreate.getName(), role1.getName());
			Assert.assertEquals(roleCreate.getDescription(), role1.getDescription());

			role1 = api.getRole(uid, false);
			Assert.assertEquals(uid, role1.getUid());
			Assert.assertEquals(roleCreate.getCode(), role1.getCode());
			Assert.assertEquals(roleCreate.getName(), role1.getName());
			Assert.assertEquals(roleCreate.getDescription(), role1.getDescription());

			// update
			ARoleUpdate update = new ARoleUpdate();
			update.setName("RoleY");
			update.setDescription("The role y");
			ARole role2 = api.updateRole(uid, update);

			role1 = api.getRole(uid, true);
			Assert.assertEquals(uid, role2.getUid());
			Assert.assertEquals(role2.getCode(), role1.getCode());
			Assert.assertEquals(role2.getName(), role1.getName());
			Assert.assertEquals(role2.getDescription(), role1.getDescription());

			list = api.listRole(null).getItems();
			Assert.assertEquals(2, list.size());

			role1 = list.get(1);
			Assert.assertEquals(uid, role2.getUid());
			Assert.assertEquals(role2.getCode(), role1.getCode());
			Assert.assertEquals(role2.getName(), role1.getName());
			Assert.assertEquals(role2.getDescription(), role1.getDescription());

			api.deleteRole(uid, true);

			role1 = api.getRole(uid, true);
			Assert.assertNull(role1);

			try {
				api.getRole(uid, false);
				Assert.fail();
			} catch (ApiException e) {
				Assert.assertEquals(404, e.getCode());
			}

			list = api.listRole(null).getItems();
			Assert.assertEquals(1, list.size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x) ;
		}
	}

	@Test
	public void testFilterPage() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser",Strings.randomPassword(10), "authrole", new onexas.coordinate.model.PrincipalPermission("*","*"));

			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(client);

			ARoleListPage page = api.listRole(null);
			Assert.assertEquals(1, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				ARoleCreate roleCreate = new ARoleCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					roleCreate.setCode("r-a" + p);
					roleCreate.setName("RoleA" + p);
					break;
				case 1:
					roleCreate.setCode("r-b" + p);
					roleCreate.setName("RoleB" + p);
					break;
				}
				api.createRole(roleCreate);
			}

			// test filter, page
			page = api.listRole(new ARoleFilter().code("r-a"));
			Assert.assertEquals(0, page.getItems().size());
			Assert.assertEquals((Long)0L, page.getItemTotal());
			page = api.listRole(new ARoleFilter().code("r-a00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long)1L, page.getItemTotal());
			ARole role00 = page.getItems().get(0);
			Assert.assertEquals("r-a00", role00.getCode());
			Assert.assertEquals("RoleA00", role00.getName());

			page = api.listRole(new ARoleFilter().code("r-a").strContaining(true).strIgnoreCase(true)
					.sortField("name").sortDesc(true));
			Assert.assertEquals(50, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			role00 = page.getItems().get(49);
			Assert.assertEquals("r-a00", role00.getCode());
			Assert.assertEquals("RoleA00", role00.getName());

			ARole role98 = page.getItems().get(0);
			Assert.assertEquals("r-a98", role98.getCode());
			Assert.assertEquals("RoleA98", role98.getName());

			page = api.listRole(
					new ARoleFilter().code("r-b").strContaining(true).strIgnoreCase(true).pageIndex(1).pageSize(10));
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			ARole role21 = page.getItems().get(0);
			Assert.assertEquals("r-b21", role21.getCode());

			ARole role39 = page.getItems().get(9);
			Assert.assertEquals("r-b39", role39.getCode());

			page = api.listRole(
					new ARoleFilter().code("r-b").strContaining(true).strIgnoreCase(true).pageIndex(3).pageSize(15));
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			ARole role91 = page.getItems().get(0);
			Assert.assertEquals("r-b91", role91.getCode());

			ARole role99 = page.getItems().get(4);
			Assert.assertEquals("r-b99", role99.getCode());

			for (ARole role : api.listRole(null).getItems()) {
				if(role.getCode().equals("authrole")) {
					continue;
				}
				api.deleteRole(role.getUid(), false);
			}
			page = api.listRole(null);
			Assert.assertEquals(1, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x) ;
		}
	}

	@Test
	public void testRoleUser() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser",Strings.randomPassword(10), "authrole", new onexas.coordinate.model.PrincipalPermission("*","*"));

			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(client);

			CoordinateAdminUserApi userApi = new CoordinateAdminUserApi(client);

			Assert.assertEquals(1, api.listRole(null).getItems().size());
			ARoleCreate roleCreate1 = new ARoleCreate();
			roleCreate1.setCode("r1");
			roleCreate1.setName("Role1");

			ARoleCreate roleCreate2 = new ARoleCreate();
			roleCreate2.setCode("r2");
			roleCreate2.setName("Role2");

			ARole role1 = api.createRole(roleCreate1);
			ARole role2 = api.createRole(roleCreate2);

			Assert.assertEquals(1, userApi.listUser(null).getItems().size());

			// account > user
			Map<String, AUser> accountMap = new LinkedHashMap<>();
			for (int i = 0; i < 100; i++) {
				AUserCreate userCreate = new AUserCreate();
				String p = (i <= 9 ? "0" : "") + i;
				userCreate.setAccount("dennis" + p);
				userCreate.setDisplayName("Dennis"+i);
				userCreate.setDomain(Domain.LOCAL);
				userCreate.setEmail("dennis" + p + "@abc.com");
				userCreate.setPassword("1234");

				AUser user = userApi.createUser(userCreate);
				accountMap.put(user.getAccount(), user);

				switch (i % 2) {
				case 0:
					api.addRoleUser(role1.getUid(), Collections.asList(user.getUid()));
					break;
				case 1:
					api.addRoleUser(role2.getUid(), Collections.asList(user.getUid()));
					break;
				}

			}
			Assert.assertEquals(101, userApi.listUser(null).getItems().size());

			List<AUser> list = api.listRoleUser(role1.getUid(), null).getItems();
			Assert.assertEquals(50, list.size());
			AUser user1 = list.get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			user1 = list.get(49);
			Assert.assertEquals("dennis98", user1.getAccount());

			AUserListPage users = api.listRoleUser(role2.getUid(), null);
			Assert.assertEquals(1, users.getPageTotal().intValue());
			Assert.assertEquals(50, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis01", user1.getAccount());
			user1 = users.getItems().get(49);
			Assert.assertEquals("dennis99", user1.getAccount());

			ARoleUserFilter filter = new ARoleUserFilter();
			filter.setPageSize(10);
			filter.setPageIndex(1);
			users = api.listRoleUser(role2.getUid(), filter);
			Assert.assertEquals(5, users.getPageTotal().intValue());
			Assert.assertEquals(10, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis21", user1.getAccount());
			user1 = users.getItems().get(9);
			Assert.assertEquals("dennis39", user1.getAccount());

			filter = new ARoleUserFilter();
			filter.setPageSize(10);
			filter.setPageIndex(2);
			users = api.listRoleUser(role2.getUid(), filter);
			Assert.assertEquals(5, users.getPageTotal().intValue());
			Assert.assertEquals(10, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis41", user1.getAccount());
			user1 = users.getItems().get(9);
			Assert.assertEquals("dennis59", user1.getAccount());

			api.addRoleUser(role1.getUid(),
					Collections.asList(accountMap.get("dennis01").getUid(), accountMap.get("dennis03").getUid()));
			// repeat
			api.addRoleUser(role1.getUid(), Collections.asList(accountMap.get("dennis02").getUid()));
			users = api.listRoleUser(role1.getUid(), null);
			Assert.assertEquals(52, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis01", user1.getAccount());
			user1 = users.getItems().get(2);
			Assert.assertEquals("dennis02", user1.getAccount());
			user1 = users.getItems().get(3);
			Assert.assertEquals("dennis03", user1.getAccount());

			api.removeRoleUser(role1.getUid(),
					Collections.asList(accountMap.get("dennis01").getUid(), accountMap.get("dennis03").getUid()));
			// not exist
			api.removeRoleUser(role1.getUid(), Collections.asList(accountMap.get("dennis05").getUid()));
			users = api.listRoleUser(role1.getUid(), null);
			Assert.assertEquals(50, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis02", user1.getAccount());

			api.setRoleUser(role1.getUid(),
					Collections.asList(accountMap.get("dennis01").getUid(), accountMap.get("dennis11").getUid()));
			users = api.listRoleUser(role1.getUid(), null);
			Assert.assertEquals(2, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis01", user1.getAccount());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis11", user1.getAccount());

			// delete 01 & 11
			userApi.deleteUser(accountMap.get("dennis01").getUid(), false);
			userApi.deleteUser(accountMap.get("dennis11").getUid(), false);
			users = api.listRoleUser(role1.getUid(), null);
			Assert.assertEquals(0, users.getItems().size());
			users = api.listRoleUser(role2.getUid(), null);
			Assert.assertEquals(48, users.getItems().size());

			for (ARole role : api.listRole(null).getItems()) {
				if(role.getCode().equals("authrole")) {
					continue;
				}
				api.deleteRole(role.getUid(), false);
			}
			Assert.assertEquals(1, api.listRole(null).getItems().size());

			for (AUser user : userApi.listUser(null).getItems()) {
				if(user.getAccount().equals("authuser")) {
					continue;
				}
				userApi.deleteUser(user.getUid(), false);
			}
			Assert.assertEquals(1, userApi.listUser(null).getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x) ;
		}
	}

	@Test
	public void testRolePermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser",Strings.randomPassword(10), "authrole", new onexas.coordinate.model.PrincipalPermission("*","*"));

			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(client);

			Assert.assertEquals(1, api.listRole(null).getItems().size());
			ARoleCreate roleCreate1 = new ARoleCreate();
			roleCreate1.setCode("role1");
			roleCreate1.setName("Role1");

			ARoleCreate roleCreate2 = new ARoleCreate();
			roleCreate2.setCode("role2");
			roleCreate2.setName("Role2");

			ARole role1 = api.createRole(roleCreate1);
			ARole role2 = api.createRole(roleCreate2);

			List<APrincipalPermission> list = api.listRolePermission(role1.getUid());
			Assert.assertEquals(0, list.size());
			list = api.listRolePermission(role2.getUid());
			Assert.assertEquals(0, list.size());

			// add
			api.addRolePermission(role1.getUid(),
					Collections.asList(new APrincipalPermission().target("fn1").action("view"),
							new APrincipalPermission().target("fn2").action("view"),
							new APrincipalPermission().target("fn3").action("*")));
			list = api.listRolePermission(role1.getUid());
			Assert.assertEquals(3, list.size());
			APrincipalPermission pp = list.get(0);
			Assert.assertEquals("fn1", pp.getTarget());
			Assert.assertEquals("view", pp.getAction());
			pp = list.get(1);
			Assert.assertEquals("fn2", pp.getTarget());
			Assert.assertEquals("view", pp.getAction());
			pp = list.get(2);
			Assert.assertEquals("fn3", pp.getTarget());
			Assert.assertEquals("*", pp.getAction());

			api.addRolePermission(role2.getUid(),
					Collections.asList(new APrincipalPermission().target("fn1").action("*")));
			list = api.listRolePermission(role2.getUid());
			Assert.assertEquals(1, list.size());
			pp = list.get(0);
			Assert.assertEquals("fn1", pp.getTarget());
			Assert.assertEquals("*", pp.getAction());

			// add repeat
			api.addRolePermission(role1.getUid(),
					Collections.asList(new APrincipalPermission().target("fn1").action("view"),
							new APrincipalPermission().target("fn2").action("edit")));
			list = api.listRolePermission(role1.getUid());
			Assert.assertEquals(4, list.size());
			pp = list.get(0);
			Assert.assertEquals("fn1", pp.getTarget());
			Assert.assertEquals("view", pp.getAction());
			pp = list.get(1);
			Assert.assertEquals("fn2", pp.getTarget());
			Assert.assertEquals("edit", pp.getAction());
			pp = list.get(2);
			Assert.assertEquals("fn2", pp.getTarget());
			Assert.assertEquals("view", pp.getAction());
			pp = list.get(3);
			Assert.assertEquals("fn3", pp.getTarget());
			Assert.assertEquals("*", pp.getAction());

			// set
			api.setRolePermission(role2.getUid(),
					Collections.asList(new APrincipalPermission().target("fn1").action("edit"),
							new APrincipalPermission().target("fn2").action("edit")));
			list = api.listRolePermission(role2.getUid());
			Assert.assertEquals(2, list.size());
			pp = list.get(0);
			Assert.assertEquals("fn1", pp.getTarget());
			Assert.assertEquals("edit", pp.getAction());
			pp = list.get(1);
			Assert.assertEquals("fn2", pp.getTarget());
			Assert.assertEquals("edit", pp.getAction());

			// remove, remove non exist
			api.removeRolePermission(role2.getUid(),
					Collections.asList(new APrincipalPermission().target("fn1").action("edit"),
							new APrincipalPermission().target("fn3").action("edit")));
			list = api.listRolePermission(role2.getUid());
			Assert.assertEquals(1, list.size());
			pp = list.get(0);
			Assert.assertEquals("fn2", pp.getTarget());
			Assert.assertEquals("edit", pp.getAction());

			for (ARole role : api.listRole(null).getItems()) {
				if(role.getCode().equals("authrole")) {
					continue;
				}
				api.deleteRole(role.getUid(), false);
			}
			Assert.assertEquals(1, api.listRole(null).getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x) ;
		}
	}

}
