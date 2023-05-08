package onexas.coordinate.api.v1.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationCreate;
import onexas.coordinate.api.v1.sdk.model.AOrganizationFilter;
import onexas.coordinate.api.v1.sdk.model.AOrganizationListPage;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUpdate;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUser;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserFilter;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserListPage;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserRelation;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.AUserOrganization;
import onexas.coordinate.api.v1.sdk.model.OrganizationUserRelationType;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Domain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminOrganizationApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "someorg");

			CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(client);
			try {
				api.listOrganization(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new onexas.coordinate.model.PrincipalPermission(
					onexas.coordinate.api.v1.admin.AdminOrganizationApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminOrganizationApi.ACTION_VIEW));
			api = new CoordinateAdminOrganizationApi(client);
			api.listOrganization(null);

			try {
				AOrganizationCreate organizationCreate = new AOrganizationCreate();
				organizationCreate.setCode("orgx");
				organizationCreate.setName("OrganizationX");

				organizationCreate.setDescription("The organization x");

				api.createOrganization(organizationCreate);
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

			CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(client);

			List<AOrganization> list = api.listOrganization(null).getItems();
			Assert.assertEquals(0, list.size());

			AOrganizationCreate organizationCreate = new AOrganizationCreate();
			organizationCreate.setCode("orgx");
			organizationCreate.setName("OrganizationX");

			organizationCreate.setDescription("The organization x");

			AOrganization organization1 = api.createOrganization(organizationCreate);
			String uid = organization1.getUid();
			Assert.assertNotNull(organization1.getUid());
			Assert.assertEquals(organizationCreate.getCode(), organization1.getCode());
			Assert.assertEquals(organizationCreate.getName(), organization1.getName());
			Assert.assertEquals(organizationCreate.getDescription(), organization1.getDescription());

			organization1 = api.getOrganization(uid, false);
			Assert.assertEquals(uid, organization1.getUid());
			Assert.assertEquals(organizationCreate.getCode(), organization1.getCode());
			Assert.assertEquals(organizationCreate.getName(), organization1.getName());
			Assert.assertEquals(organizationCreate.getDescription(), organization1.getDescription());

			// update
			AOrganizationUpdate update = new AOrganizationUpdate();
			update.setName("OrganizationY");
			update.setDescription("The organization y");
			AOrganization organization2 = api.updateOrganization(uid, update);

			organization1 = api.getOrganization(uid, true);
			Assert.assertEquals(uid, organization2.getUid());
			Assert.assertEquals(organization2.getCode(), organization1.getCode());
			Assert.assertEquals(organization2.getName(), organization1.getName());
			Assert.assertEquals(organization2.getDescription(), organization1.getDescription());

			list = api.listOrganization(null).getItems();
			Assert.assertEquals(1, list.size());

			organization1 = list.get(0);
			Assert.assertEquals(uid, organization2.getUid());
			Assert.assertEquals(organization2.getCode(), organization1.getCode());
			Assert.assertEquals(organization2.getName(), organization1.getName());
			Assert.assertEquals(organization2.getDescription(), organization1.getDescription());

			api.deleteOrganization(uid, true);

			organization1 = api.getOrganization(uid, true);
			Assert.assertNull(organization1);

			try {
				api.getOrganization(uid, false);
				Assert.fail();
			} catch (ApiException e) {
				Assert.assertEquals(404, e.getCode());
			}

			list = api.listOrganization(null).getItems();
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

			CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(client);

			AOrganizationListPage page = api.listOrganization(null);
			Assert.assertEquals(0, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				AOrganizationCreate organizationCreate = new AOrganizationCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					organizationCreate.setCode("org-a" + p);
					organizationCreate.setName("OrganizationA" + p);
					break;
				case 1:
					organizationCreate.setCode("org-b" + p);
					organizationCreate.setName("OrganizationB" + p);
					break;
				}
				api.createOrganization(organizationCreate);
			}

			// test filter, page
			page = api.listOrganization(new AOrganizationFilter().code("org-a"));
			Assert.assertEquals(0, page.getItems().size());
			Assert.assertEquals((Long)0L, page.getItemTotal());
			page = api.listOrganization(new AOrganizationFilter().code("org-a00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long)1L, page.getItemTotal());
			AOrganization organization00 = page.getItems().get(0);
			Assert.assertEquals("org-a00", organization00.getCode());
			Assert.assertEquals("OrganizationA00", organization00.getName());

			page = api.listOrganization(new AOrganizationFilter().code("org-a").strContaining(true)
					.strIgnoreCase(true).sortField("name").sortDesc(true));
			Assert.assertEquals(50, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			organization00 = page.getItems().get(49);
			Assert.assertEquals("org-a00", organization00.getCode());
			Assert.assertEquals("OrganizationA00", organization00.getName());

			AOrganization organization98 = page.getItems().get(0);
			Assert.assertEquals("org-a98", organization98.getCode());
			Assert.assertEquals("OrganizationA98", organization98.getName());

			page = api.listOrganization(new AOrganizationFilter().code("org-b").strContaining(true)
					.strIgnoreCase(true).pageIndex(1).pageSize(10));
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			AOrganization organization21 = page.getItems().get(0);
			Assert.assertEquals("org-b21", organization21.getCode());

			AOrganization organization39 = page.getItems().get(9);
			Assert.assertEquals("org-b39", organization39.getCode());

			page = api.listOrganization(new AOrganizationFilter().code("org-b").strContaining(true)
					.strIgnoreCase(true).pageIndex(3).pageSize(15));
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			AOrganization organization91 = page.getItems().get(0);
			Assert.assertEquals("org-b91", organization91.getCode());

			AOrganization organization99 = page.getItems().get(4);
			Assert.assertEquals("org-b99", organization99.getCode());

			for (AOrganization organization : api.listOrganization(null).getItems()) {
				if (organization.getCode().equals("authrole")) {
					continue;
				}
				api.deleteOrganization(organization.getUid(), false);
			}
			page = api.listOrganization(null);
			Assert.assertEquals(0, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testOrganizationUser() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(client);

			CoordinateAdminUserApi userApi = new CoordinateAdminUserApi(client);

			Assert.assertEquals(0, api.listOrganization(null).getItems().size());
			AOrganizationCreate organizationCreate1 = new AOrganizationCreate();
			organizationCreate1.setCode("org1");
			organizationCreate1.setName("Organization1");

			AOrganizationCreate organizationCreate2 = new AOrganizationCreate();
			organizationCreate2.setCode("org2");
			organizationCreate2.setName("Organization2");

			AOrganization organization1 = api.createOrganization(organizationCreate1);
			AOrganization organization2 = api.createOrganization(organizationCreate2);

			Assert.assertEquals(1, userApi.listUser(null).getItems().size());

			// account > user
			Map<String, AUser> accountMap = new LinkedHashMap<>();
			for (int i = 0; i < 100; i++) {
				AUserCreate userCreate = new AUserCreate();
				String p = (i <= 9 ? "0" : "") + i;
				userCreate.setAccount("dennis" + p);
				userCreate.setDisplayName("Dennis" + i);
				userCreate.setDomain(Domain.LOCAL);
				userCreate.setEmail("dennis" + p + "@abc.com");
				userCreate.setPassword("1234");

				AUser user = userApi.createUser(userCreate);
				accountMap.put(user.getAccount(), user);

				switch (i % 2) {
				case 0:
					api.addOrganizationUser(organization1.getUid(), Collections.asList(new AOrganizationUserRelation()
							.userUid(user.getUid()).type(OrganizationUserRelationType.MEMBER)));
					break;
				case 1:
					api.addOrganizationUser(organization2.getUid(), Collections.asList(new AOrganizationUserRelation()
							.userUid(user.getUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
					break;
				}

			}
			Assert.assertEquals(101, userApi.listUser(null).getItems().size());
			
			
			List<AUserOrganization> uorgList = userApi.listUserOrganization(accountMap.get("dennis00").getUid());
			Assert.assertEquals(1, uorgList.size());
			AUserOrganization uorg = uorgList.get(0);
			Assert.assertEquals(organization1.getCode(), uorg.getCode());
			Assert.assertEquals(organization1.getName(), uorg.getName());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, uorg.getRelationType());
			
			uorgList = userApi.listUserOrganization(accountMap.get("dennis01").getUid());
			Assert.assertEquals(1, uorgList.size());
			uorg = uorgList.get(0);
			Assert.assertEquals(organization2.getCode(), uorg.getCode());
			Assert.assertEquals(organization2.getName(), uorg.getName());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, uorg.getRelationType());

			List<AOrganizationUser> list = api.listOrganizationUser(organization1.getUid(),null).getItems();
			Assert.assertEquals(50, list.size());
			AOrganizationUser user1 = list.get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());
			user1 = list.get(49);
			Assert.assertEquals("dennis98", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

			AOrganizationUserListPage users = api.listOrganizationUser(organization2.getUid(), null);
			Assert.assertEquals(1, users.getPageTotal().intValue());
			Assert.assertEquals(50, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis01", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
			user1 = users.getItems().get(49);
			Assert.assertEquals("dennis99", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

			AOrganizationUserFilter filter = new AOrganizationUserFilter();
			filter.setPageSize(10);
			filter.setPageIndex(1);
			users = api.listOrganizationUser(organization2.getUid(), filter);
			Assert.assertEquals(5, users.getPageTotal().intValue());
			Assert.assertEquals(10, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis21", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
			user1 = users.getItems().get(9);
			Assert.assertEquals("dennis39", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

			filter = new AOrganizationUserFilter();
			filter.setPageSize(10);
			filter.setPageIndex(2);
			users = api.listOrganizationUser(organization2.getUid(), filter);
			Assert.assertEquals(5, users.getPageTotal().intValue());
			Assert.assertEquals(10, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis41", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
			user1 = users.getItems().get(9);
			Assert.assertEquals("dennis59", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

			api.addOrganizationUser(organization1.getUid(),
					Collections.asList(
							new AOrganizationUserRelation().userUid(accountMap.get("dennis01").getUid())
									.type(OrganizationUserRelationType.SUPERVISOR),
							new AOrganizationUserRelation().userUid(accountMap.get("dennis03").getUid())
									.type(OrganizationUserRelationType.MEMBER)));
			// repeat
			api.addOrganizationUser(organization1.getUid(), Collections.asList(new AOrganizationUserRelation()
					.userUid(accountMap.get("dennis02").getUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
			
			
			uorgList = userApi.listUserOrganization(accountMap.get("dennis01").getUid());
			Assert.assertEquals(2, uorgList.size());
			uorg = uorgList.get(0);
			Assert.assertEquals(organization1.getCode(), uorg.getCode());
			Assert.assertEquals(organization1.getName(), uorg.getName());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, uorg.getRelationType());
			uorg = uorgList.get(1);
			Assert.assertEquals(organization2.getCode(), uorg.getCode());
			Assert.assertEquals(organization2.getName(), uorg.getName());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, uorg.getRelationType());
			
			
			users = api.listOrganizationUser(organization1.getUid(), null);
			Assert.assertEquals(52, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis01", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, user1.getRelationType());
			user1 = users.getItems().get(2);
			Assert.assertEquals("dennis02", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
			user1 = users.getItems().get(3);
			Assert.assertEquals("dennis03", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

			api.removeOrganizationUser(organization1.getUid(),
					Collections.asList(accountMap.get("dennis01").getUid(), accountMap.get("dennis03").getUid()));
			// not exist
			api.removeOrganizationUser(organization1.getUid(),
					Collections.asList(accountMap.get("dennis05").getUid()));
			users = api.listOrganizationUser(organization1.getUid(), null);
			Assert.assertEquals(50, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis00", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis02", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

			api.setOrganizationUser(organization1.getUid(),
					Collections.asList(new AOrganizationUserRelation().userUid(accountMap.get("dennis01").getUid()).type(OrganizationUserRelationType.SUPERVISOR), 
							new AOrganizationUserRelation().userUid(accountMap.get("dennis11").getUid()).type(OrganizationUserRelationType.MEMBER)));
			users = api.listOrganizationUser(organization1.getUid(), null);
			Assert.assertEquals(2, users.getItems().size());
			user1 = users.getItems().get(0);
			Assert.assertEquals("dennis01", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, user1.getRelationType());
			user1 = users.getItems().get(1);
			Assert.assertEquals("dennis11", user1.getAccount());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

			// delete 01 & 11
			userApi.deleteUser(accountMap.get("dennis01").getUid(), false);
			userApi.deleteUser(accountMap.get("dennis11").getUid(), false);
			users = api.listOrganizationUser(organization1.getUid(), null);
			Assert.assertEquals(0, users.getItems().size());
			users = api.listOrganizationUser(organization2.getUid(), null);
			Assert.assertEquals(48, users.getItems().size());

			for (AOrganization organization : api.listOrganization(null).getItems()) {
				if (organization.getCode().equals("authrole")) {
					continue;
				}
				api.deleteOrganization(organization.getUid(), false);
			}
			Assert.assertEquals(0, api.listOrganization(null).getItems().size());

			for (AUser user : userApi.listUser(null).getItems()) {
				if (user.getAccount().equals("authuser")) {
					continue;
				}
				userApi.deleteUser(user.getUid(), false);
			}
			Assert.assertEquals(1, userApi.listUser(null).getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
