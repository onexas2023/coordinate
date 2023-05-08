package onexas.coordinate.api.v1.sdk;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationCreate;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserRelation;
import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleCreate;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.OrganizationUserRelationType;
import onexas.coordinate.api.v1.sdk.model.UOrganizationUser;
import onexas.coordinate.api.v1.sdk.model.UOrganizationUserFilter;
import onexas.coordinate.api.v1.sdk.model.UOrganizationUserRelation;
import onexas.coordinate.api.v1.sdk.model.UUserOrganization;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testSimple() {
		try {
			ApiClient adminClient = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new onexas.coordinate.model.PrincipalPermission("*", "*"));

			CoordinateAdminOrganizationApi organizationApi = new CoordinateAdminOrganizationApi(adminClient);
			CoordinateAdminUserApi userApi = new CoordinateAdminUserApi(adminClient);
			CoordinateAdminRoleApi roleApi = new CoordinateAdminRoleApi(adminClient);
			Assert.assertEquals(0, organizationApi.listOrganization(null).getItems().size());

			AOrganizationCreate organizationCreate = new AOrganizationCreate().code("orgx").name("OrganizationX");
			AOrganization organization1 = organizationApi.createOrganization(organizationCreate);
			organizationCreate = new AOrganizationCreate().code("orgy").name("OrganizationY");
			AOrganization organization2 = organizationApi.createOrganization(organizationCreate);
			organizationCreate = new AOrganizationCreate().code("orgz").name("OrganizationZ");
			AOrganization organization3 = organizationApi.createOrganization(organizationCreate);

			
			ARole role = roleApi.createRole(new ARoleCreate().code("role1"));
			
			AUserCreate userCreate = new AUserCreate().account("user1").displayName("User1").password("1234");
			AUser user1 = userApi.createUser(userCreate);
			userCreate = new AUserCreate().account("user2").displayName("User2").password("1234");
			AUser user2 = userApi.createUser(userCreate);
			userCreate = new AUserCreate().account("user3").displayName("User3").password("1234");
			AUser user3 = userApi.createUser(userCreate);
			userCreate = new AUserCreate().account("user4").displayName("User4").password("1234");
			AUser user4 = userApi.createUser(userCreate);

			ApiClient client1 = getApiClientWithAuth("user1", "1234");
			ApiClient client2 = getApiClientWithAuth("user2", "1234");
			ApiClient client3 = getApiClientWithAuth("user3", "1234");
			ApiClient client4 = getApiClientWithAuth("user4", "1234");

			CoordinateOrganizationApi api1 = new CoordinateOrganizationApi(client1);
			CoordinateOrganizationApi api2 = new CoordinateOrganizationApi(client2);
			CoordinateOrganizationApi api3 = new CoordinateOrganizationApi(client3);
			CoordinateOrganizationApi api4 = new CoordinateOrganizationApi(client4);
			
			
			UUserOrganization org;
			
			try {
				org = api1.getOrganization("aaa", null);
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			
			roleApi.addRolePermission(role.getUid(), Collections.asList(new APrincipalPermission().target("coordinate-organization").action("view")));
			roleApi.addRoleUser(role.getUid(), Collections.asList(user1.getUid(),user2.getUid(),user3.getUid(),user4.getUid()));
			
			try {
				org = api1.getOrganization("aaa", null);
			} catch (ApiException x) {
				Assert.assertEquals(404, x.getCode());
			}
					
			org = api1.getOrganization("aaa", Boolean.TRUE);
			Assert.assertNull(org);
			try {
				org = api1.getOrganization("orgx", null);
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			// case 1.
			organizationApi.addOrganizationUser(organization1.getUid(),
					Collections.asList(
							new AOrganizationUserRelation().userUid(user1.getUid())
									.type(OrganizationUserRelationType.ADVANCED_MEMBER),
							new AOrganizationUserRelation().userUid(user2.getUid())
									.type(OrganizationUserRelationType.SUPERVISOR),
							new AOrganizationUserRelation().userUid(user3.getUid())
									.type(OrganizationUserRelationType.MEMBER)));

			org = api1.getOrganization("orgx", null);
			Assert.assertEquals("orgx",org.getCode());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER,org.getRelationType());
			
			
			// user3,4 can't operate, user2, user1 can remove user 3,4
			try {
				api3.addOrganizationUser(organization1.getCode(), Collections.asList(new UOrganizationUserRelation()
						.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api4.removeOrganizationUsers(organization1.getCode(), Collections.asList(user1.getAliasUid()));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			api1.removeOrganizationUsers(organization1.getCode(), Collections.asList(user3.getAliasUid()));
			api2.removeOrganizationUsers(organization1.getCode(), Collections.asList(user4.getAliasUid()));

			// case 2.
			organizationApi.addOrganizationUser(organization2.getUid(),
					Collections.asList(
							new AOrganizationUserRelation().userUid(user1.getUid())
									.type(OrganizationUserRelationType.ADVANCED_MEMBER),
							new AOrganizationUserRelation().userUid(user2.getUid())
									.type(OrganizationUserRelationType.MEMBER)));
			// user2,3,4 can't operate, user1 can add user3.
			try {
				api2.setOrganizationUser(organization2.getCode(), Collections.asList(new UOrganizationUserRelation()
						.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api3.addOrganizationUser(organization2.getCode(), Collections.asList(new UOrganizationUserRelation()
						.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api4.removeOrganizationUsers(organization2.getCode(), Collections.asList(user1.getAliasUid()));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			api1.addOrganizationUser(organization2.getCode(), Collections.asList(new UOrganizationUserRelation()
					.aliasUid(user3.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
			// then user 3 can add user 4 too
			api3.addOrganizationUser(organization2.getCode(), Collections.asList(new UOrganizationUserRelation()
					.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.MEMBER)));

			// case 3
			organizationApi.addOrganizationUser(organization3.getUid(),
					Collections.asList(
							new AOrganizationUserRelation().userUid(user1.getUid())
									.type(OrganizationUserRelationType.MEMBER),
							new AOrganizationUserRelation().userUid(user2.getUid())
									.type(OrganizationUserRelationType.MEMBER),
							new AOrganizationUserRelation().userUid(user3.getUid())
									.type(OrganizationUserRelationType.MEMBER)));

			// no one can act
			// user2,3,4 can't operate, user1 can add user3.
			try {
				api2.setOrganizationUser(organization3.getCode(), Collections.asList(new UOrganizationUserRelation()
						.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api3.addOrganizationUser(organization3.getCode(), Collections.asList(new UOrganizationUserRelation()
						.aliasUid(user4.getAliasUid()).type(OrganizationUserRelationType.ADVANCED_MEMBER)));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api4.removeOrganizationUsers(organization3.getCode(), Collections.asList(user1.getAliasUid()));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			try {
				api1.removeOrganizationUsers(organization3.getCode(), Collections.asList(user2.getAliasUid()));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			// org list of user 1,2,3,4
			List<UUserOrganization> orglist = api1.listOrganization();
			Assert.assertEquals(3, orglist.size());
			Assert.assertEquals(organization1.getCode(), orglist.get(0).getCode());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER,
					orglist.get(0).getRelationType());
			Assert.assertEquals(organization2.getCode(), orglist.get(1).getCode());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER,
					orglist.get(1).getRelationType());
			Assert.assertEquals(organization3.getCode(), orglist.get(2).getCode());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER,
					orglist.get(2).getRelationType());

			orglist = api2.listOrganization();
			Assert.assertEquals(3, orglist.size());
			Assert.assertEquals(organization1.getCode(), orglist.get(0).getCode());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR,
					orglist.get(0).getRelationType());
			Assert.assertEquals(organization2.getCode(), orglist.get(1).getCode());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER,
					orglist.get(1).getRelationType());
			Assert.assertEquals(organization3.getCode(), orglist.get(2).getCode());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER,
					orglist.get(2).getRelationType());

			orglist = api3.listOrganization();
			Assert.assertEquals(2, orglist.size());
			Assert.assertEquals(organization2.getCode(), orglist.get(0).getCode());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER,
					orglist.get(0).getRelationType());
			Assert.assertEquals(organization3.getCode(), orglist.get(1).getCode());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER,
					orglist.get(1).getRelationType());

			orglist = api4.listOrganization();
			Assert.assertEquals(1, orglist.size());
			Assert.assertEquals(organization2.getCode(), orglist.get(0).getCode());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER,
					orglist.get(0).getRelationType());
			
			
			//api1 can list all member of org 1,2,3
			List<UOrganizationUser> userlist = api1.listOrganizationUser(organization1.getCode(), null).getItems();
			Assert.assertEquals(2, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, userlist.get(1).getRelationType());
			
			userlist = api1.listOrganizationUser(organization2.getCode(), null).getItems();
			Assert.assertEquals(4, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(2).getRelationType());
			Assert.assertEquals(user4.getAliasUid(), userlist.get(3).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(3).getRelationType());
			
			userlist = api1.listOrganizationUser(organization3.getCode(), null).getItems();
			Assert.assertEquals(3, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(2).getRelationType());
			
			
			//api2 can list all member of org 1,2,3
			userlist = api2.listOrganizationUser(organization1.getCode(), null).getItems();
			Assert.assertEquals(2, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, userlist.get(1).getRelationType());
			
			userlist = api2.listOrganizationUser(organization2.getCode(), null).getItems();
			Assert.assertEquals(4, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(2).getRelationType());
			Assert.assertEquals(user4.getAliasUid(), userlist.get(3).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(3).getRelationType());
			
			userlist = api2.listOrganizationUser(organization3.getCode(), null).getItems();
			Assert.assertEquals(3, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(2).getRelationType());
			
			
			//api3 can list all member of org 2,3
			try {
				userlist = api3.listOrganizationUser(organization1.getCode(), null).getItems();
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			userlist = api3.listOrganizationUser(organization2.getCode(), null).getItems();
			Assert.assertEquals(4, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(2).getRelationType());
			Assert.assertEquals(user4.getAliasUid(), userlist.get(3).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(3).getRelationType());
			
			userlist = api3.listOrganizationUser(organization3.getCode(), null).getItems();
			Assert.assertEquals(3, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(2).getRelationType());
			
			
			//api4 can list all member of org 2
			try {
				userlist = api4.listOrganizationUser(organization1.getCode(), null).getItems();
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			userlist = api4.listOrganizationUser(organization2.getCode(), null).getItems();
			Assert.assertEquals(4, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(0).getRelationType());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(1).getRelationType());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, userlist.get(2).getRelationType());
			Assert.assertEquals(user4.getAliasUid(), userlist.get(3).getAliasUid());
			Assert.assertEquals(OrganizationUserRelationType.MEMBER, userlist.get(3).getRelationType());
			
			try {
				userlist = api4.listOrganizationUser(organization3.getCode(), null).getItems();
				Assert.fail();
			}catch(ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			
			//test criteria
			userlist = api4.listOrganizationUser(organization2.getCode(), new UOrganizationUserFilter().criteria("user")).getItems();
			Assert.assertEquals(0, userlist.size());
			
			userlist = api4.listOrganizationUser(organization2.getCode(), new UOrganizationUserFilter().criteria("user1")).getItems();
			Assert.assertEquals(1, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			
			userlist = api4.listOrganizationUser(organization2.getCode(), new UOrganizationUserFilter().criteria("user").strContaining(true)).getItems();
			Assert.assertEquals(4, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			Assert.assertEquals(user3.getAliasUid(), userlist.get(2).getAliasUid());
			Assert.assertEquals(user4.getAliasUid(), userlist.get(3).getAliasUid());
			
			
			userlist = api2.listOrganizationUser(organization1.getCode(), new UOrganizationUserFilter().criteria("user").strContaining(true)).getItems();
			Assert.assertEquals(2, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			Assert.assertEquals(user2.getAliasUid(), userlist.get(1).getAliasUid());
			
			userlist = api2.listOrganizationUser(organization1.getCode(), new UOrganizationUserFilter().criteria("user1")).getItems();
			Assert.assertEquals(1, userlist.size());
			Assert.assertEquals(user1.getAliasUid(), userlist.get(0).getAliasUid());
			
			userlist = api2.listOrganizationUser(organization1.getCode(), new UOrganizationUserFilter().criteria("user3")).getItems();
			Assert.assertEquals(0, userlist.size());
			
			
			//reset
			organizationApi.deleteOrganization(organization1.getUid(), false);
			organizationApi.deleteOrganization(organization2.getUid(), false);
			organizationApi.deleteOrganization(organization3.getUid(), false);
			roleApi.deleteRole(role.getUid(), false);
			userApi.deleteUser(user1.getUid(), false);
			userApi.deleteUser(user2.getUid(), false);
			userApi.deleteUser(user3.getUid(), false);
			userApi.deleteUser(user4.getUid(), false);

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
