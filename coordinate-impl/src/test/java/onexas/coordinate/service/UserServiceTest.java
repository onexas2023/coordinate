package onexas.coordinate.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationCreate;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserFilter;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.model.UserOrganizationRelation;
import onexas.coordinate.model.UserUpdate;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserServiceTest extends CoordinateImplTestBase {

	@Autowired
	UserService service;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	OrganizationService organizationService;
	
	@Autowired
	DomainService domainService;

	@Test
	public void testSimple() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate = new UserCreate();
		userCreate.setAccount("dennis");
		userCreate.setDomain(Domain.LOCAL);
		userCreate.setDisplayName("Dennix");
		userCreate.setEmail("abc@def.com");
		userCreate.setPassword("1234");

		User user1 = service.create(userCreate);
		String uid = user1.getUid();
		Assert.assertNotNull(user1.getUid());
		Assert.assertNotNull(user1.getAliasUid());
		Assert.assertEquals(userCreate.getAccount(), user1.getAccount());
		Assert.assertEquals(userCreate.getDisplayName(), user1.getDisplayName());
		Assert.assertEquals(userCreate.getEmail(), user1.getEmail());
		Assert.assertEquals(userCreate.getDomain(), user1.getDomain());
		Assert.assertFalse(user1.getDisabled());

		Assert.assertTrue(service.verifyPassword(uid, "1234"));
		Assert.assertTrue(service.verifyPasswordByAccountDomain(userCreate.getAccount(), Domain.LOCAL, "1234"));

		user1 = service.get(uid);
		Assert.assertEquals(uid, user1.getUid());
		Assert.assertEquals(userCreate.getAccount(), user1.getAccount());
		Assert.assertEquals(userCreate.getDisplayName(), user1.getDisplayName());
		Assert.assertEquals(userCreate.getEmail(), user1.getEmail());
		Assert.assertEquals(userCreate.getDomain(), user1.getDomain());
		Assert.assertFalse(user1.getDisabled());

		User user = service.findByAliasUid(user1.getAliasUid());
		Assert.assertEquals(user1.getUid(), user.getUid());
		Assert.assertEquals(user1.getAliasUid(), user.getAliasUid());
		Assert.assertEquals(user1.getAccount(), user.getAccount());
		Assert.assertEquals(user1.getDisplayName(), user.getDisplayName());
		Assert.assertEquals(user1.getEmail(), user.getEmail());
		Assert.assertEquals(user1.getDomain(), user.getDomain());
		
		// update
		UserUpdate update = new UserUpdate();
		update.setDisplayName("Dennis");
		update.setEmail("xyz@g.f.h");
		update.setPassword("5678");
		User user2 = service.update(uid, update);

		Assert.assertTrue(service.verifyPassword(uid, "5678"));
		Assert.assertFalse(service.verifyPassword(uid, "12345678"));
		Assert.assertTrue(service.verifyPasswordByAccountDomain(userCreate.getAccount(),Domain.LOCAL, "5678"));
		Assert.assertFalse(service.verifyPasswordByAccountDomain(userCreate.getAccount(),Domain.LOCAL, "12345678"));

		user1 = service.find(uid);
		Assert.assertEquals(uid, user2.getUid());
		Assert.assertEquals(user2.getAccount(), user1.getAccount());
		Assert.assertEquals(user2.getDisplayName(), user1.getDisplayName());
		Assert.assertEquals(user2.getEmail(), user1.getEmail());
		Assert.assertEquals(user2.getDomain(), user1.getDomain());
		Assert.assertEquals(user2.getDisabled(), user1.getDisabled());

		user1 = service.findByAccountDomain(userCreate.getAccount(), Domain.LOCAL);
		Assert.assertEquals(uid, user2.getUid());
		Assert.assertEquals(user2.getAccount(), user1.getAccount());
		Assert.assertEquals(user2.getDisplayName(), user1.getDisplayName());
		Assert.assertEquals(user2.getEmail(), user1.getEmail());
		Assert.assertEquals(user2.getDomain(), user1.getDomain());
		Assert.assertEquals(user2.getDisabled(), user1.getDisabled());

		page = service.list(null);
		Assert.assertEquals(1, page.size());

		user1 = page.getItems().get(0);
		Assert.assertEquals(uid, user2.getUid());
		Assert.assertEquals(user2.getAccount(), user1.getAccount());
		Assert.assertEquals(user2.getDisplayName(), user1.getDisplayName());
		Assert.assertEquals(user2.getEmail(), user1.getEmail());
		Assert.assertEquals(user2.getDomain(), user1.getDomain());
		Assert.assertEquals(user2.getDisabled(), user1.getDisabled());

		service.delete(uid, true);

		user1 = service.find(uid);
		Assert.assertNull(user1);

		try {
			service.get(uid);
			Assert.fail();
		} catch (NotFoundException e) {
		}

		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}
	
	//"acount change to lower case only"
//	@Test
	@Deprecated
	public void testCaseScensitive() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate1 = new UserCreate();
		userCreate1.setAccount("dennis");
		userCreate1.setDomain(Domain.LOCAL);
		userCreate1.setDisplayName("Dennix");
		userCreate1.setEmail("abc@def.com");
		userCreate1.setPassword("1234");

		User user1 = service.create(userCreate1);
		
		User user11 = service.findByAccountDomain("dennis", Domain.LOCAL);
		User user21 = service.findByAccountDomain("deNNis", Domain.LOCAL);
		
		Assert.assertNotNull(user11);
		Assert.assertNull(user21);
		
		UserCreate userCreate2 = new UserCreate();
		userCreate2.setAccount("deNNis");
		userCreate2.setDomain(Domain.LOCAL);
		userCreate2.setDisplayName("DeNNix");
		userCreate2.setEmail("xyz@def.com");
		userCreate2.setPassword("5678");

		User user2 = service.create(userCreate2);
		
		
		user11 = service.findByAccountDomain("dennis", Domain.LOCAL);
		user21 = service.findByAccountDomain("deNNis", Domain.LOCAL);

		Assert.assertNotNull(user11);
		Assert.assertNotNull(user21);
		
		Assert.assertNotEquals(user11.getUid(), user21.getUid());
		
		Assert.assertTrue(service.verifyPassword(user11.getUid(), "1234"));
		Assert.assertTrue(service.verifyPassword(user21.getUid(), "5678"));
		
		
		service.delete(user1.getUid(), true);
		service.delete(user2.getUid(), true);

		user1 = service.find(user1.getUid());
		Assert.assertNull(user1);
		user2 = service.find(user2.getUid());
		Assert.assertNull(user2);
		
		page = service.list(null);
		Assert.assertEquals(0, page.size());
	}

	@Test
	public void testFilterPage() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		for (int i = 0; i < 100; i++) {
			UserCreate userCreate = new UserCreate();
			String p = (i <= 9 ? "0" : "") + i;
			switch (i % 2) {
			case 0:
				userCreate.setAccount("dennis" + p);
				userCreate.setDisplayName("Dennis");
				userCreate.setDomain(Domain.LOCAL);
				userCreate.setEmail("dennis" + p + "@abc.com");
				userCreate.setPassword("1234");
				break;
			case 1:
				userCreate.setAccount("alice" + p);
				userCreate.setDisplayName("Alice");
				userCreate.setDomain(Domain.LOCAL);
				userCreate.setEmail("alice" + p + "@def.com");
				userCreate.setPassword("5678");
				break;
			}
			service.create(userCreate);
		}

		// test filter, page
		page = service.list(new UserFilter().withAccount("dennis00"));
		Assert.assertEquals(1, page.size());
		User user00 = page.getItems().get(0);
		Assert.assertEquals("dennis00", user00.getAccount());
		Assert.assertEquals("dennis00@abc.com", user00.getEmail());

		page = service.list(new UserFilter().withAccount("dennis").withStrContaining(true).withStrIgnoreCase(true)
				.withSortField("email").withSortDesc(true));
		Assert.assertEquals(50, page.size());
		user00 = page.getItems().get(49);
		Assert.assertEquals("dennis00", user00.getAccount());
		Assert.assertEquals("dennis00@abc.com", user00.getEmail());

		User user98 = page.getItems().get(0);
		Assert.assertEquals("dennis98", user98.getAccount());
		Assert.assertEquals("dennis98@abc.com", user98.getEmail());

		page = service.list(new UserFilter().withAccount("alice").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		User user21 = page.getItems().get(0);
		Assert.assertEquals("alice21", user21.getAccount());

		User user39 = page.getItems().get(9);
		Assert.assertEquals("alice39", user39.getAccount());

		page = service.list(new UserFilter().withAccount("alice").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		User user91 = page.getItems().get(0);
		Assert.assertEquals("alice91", user91.getAccount());

		User user99 = page.getItems().get(4);
		Assert.assertEquals("alice99", user99.getAccount());

		for (User user : service.list(null).getItems()) {
			service.delete(user.getUid(), false);
		}
		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testProperty() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate = new UserCreate();
		userCreate.setAccount("dennis");
		userCreate.setDomain(Domain.LOCAL);
		userCreate.setDisplayName("Dennis");
		userCreate.setEmail("dennis@abc.com");
		userCreate.setPassword("1234");

		User user1 = service.create(userCreate);
		String uid = user1.getUid();

		Map<String, String> props = service.getProperties(uid, null);

		Assert.assertEquals(0, props.size());

		service.setProperty(uid, "abc", "xyz", null);

		props = service.getProperties(uid, null);
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("xyz", props.get("abc"));

		props = service.getProperties(uid, "cat1");
		Assert.assertEquals(0, props.size());
		
		String prop = service.findProperty(uid, "abc");
		Assert.assertEquals("xyz", prop);
		prop = service.findProperty(uid, "def");
		Assert.assertNull(prop);
		
		

		// update
		service.setProperty(uid, "abc", "xyz2", "cat1");
		service.setProperty(uid, "def", "ykk", "cat2");

		props = service.getProperties(uid, null);
		Assert.assertEquals(2, props.size());
		Assert.assertEquals("xyz2", props.get("abc"));
		Assert.assertEquals("ykk", props.get("def"));
		
		prop = service.findProperty(uid, "abc");
		Assert.assertEquals("xyz2", prop);
		prop = service.findProperty(uid, "def");
		Assert.assertEquals("ykk", prop);
		
		props = service.getProperties(uid, "cat1");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("xyz2", props.get("abc"));

		props = service.getProperties(uid, "cat2");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("ykk", props.get("def"));

		// remove
		service.setProperty(uid, "abc", null, null);
		
		
		prop = service.findProperty(uid, "abc");
		Assert.assertNull(prop);
		prop = service.findProperty(uid, "def");
		Assert.assertEquals("ykk", prop);
		
		props = service.getProperties(uid, null);
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("ykk", props.get("def"));

		props = service.getProperties(uid, "cat1");
		Assert.assertEquals(0, props.size());

		props = service.getProperties(uid, "cat2");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("ykk", props.get("def"));

		// delete user
		service.delete(uid, true);
		
		prop = service.findProperty(uid, "abc");
		Assert.assertNull(prop);
		prop = service.findProperty(uid, "def");
		Assert.assertNull(prop);

		props = service.getProperties(uid, null);
		Assert.assertEquals(0, props.size());

		user1 = service.find(uid);
		Assert.assertNull(user1);
		page = service.list(null);
		Assert.assertEquals(0, page.size());
	}

	@Test
	public void testUserRole() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate = new UserCreate();
		userCreate.setAccount("dennis");
		userCreate.setDisplayName("Dennis");
		userCreate.setDomain(Domain.LOCAL);
		userCreate.setEmail("dennis@abc.com");
		userCreate.setPassword("1234");

		User user1 = service.create(userCreate);
		String uid = user1.getUid();

		Role role1 = roleService.create(new RoleCreate().withCode("r1").withName("Admin"));
		Role role2 = roleService.create(new RoleCreate().withCode("r2").withName("Users"));
		Role role3 = roleService.create(new RoleCreate().withCode("r3").withName("Adv"));
		
		List<Role> roles = service.listRole(uid);
		Assert.assertEquals(0, roles.size());
		
		service.addRoles(user1.getUid(), Collections.asSet(role1.getUid(), role2.getUid()));
		roles = service.listRole(uid);
		Assert.assertEquals(2, roles.size());
		
		List<String> roleCodes = roles.stream().map((r)->{return r.getCode();}).collect(Collectors.toList());
		Assert.assertTrue(roleCodes.contains(role1.getCode()));
		Assert.assertTrue(roleCodes.contains(role2.getCode()));
		
		service.addRoles(user1.getUid(), Collections.asSet(role1.getUid()));//repeat, shouldn't cause any error
		roles = service.listRole(uid);
		Assert.assertEquals(2, roles.size());
		roleCodes = roles.stream().map((r)->{return r.getCode();}).collect(Collectors.toList());
		Assert.assertTrue(roleCodes.contains(role1.getCode()));
		Assert.assertTrue(roleCodes.contains(role2.getCode()));
		
		service.setRoles(user1.getUid(), Collections.asSet(role1.getUid(),role3.getUid()));
		roles = service.listRole(uid);
		Assert.assertEquals(2, roles.size());
		roleCodes = roles.stream().map((r)->{return r.getCode();}).collect(Collectors.toList());
		Assert.assertTrue(roleCodes.contains(role1.getCode()));
		Assert.assertTrue(roleCodes.contains(role3.getCode()));
		
		service.removeRoles(user1.getUid(), Collections.asSet(role3.getUid()));
		service.removeRoles(user1.getUid(), Collections.asSet(role2.getUid()));//not exist role, shouldn't cause any error
		roles = service.listRole(uid);
		Assert.assertEquals(1, roles.size());
		roleCodes = roles.stream().map((r)->{return r.getCode();}).collect(Collectors.toList());
		Assert.assertTrue(roleCodes.contains(role1.getCode()));
		
		roleService.delete(role1.getUid(), true);
		roles = service.listRole(uid);
		Assert.assertEquals(0, roles.size());
		
		// delete user
		service.delete(uid, true);
		user1 = service.find(uid);
		Assert.assertNull(user1);
		
		//delete role
		roleService.delete(role2.getUid(), true);
		roleService.delete(role3.getUid(), true);
		Assert.assertEquals(0, roleService.list(null).size());
	}
	
	@Test
	public void testUserOrganization() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate = new UserCreate();
		userCreate.setAccount("dennis");
		userCreate.setDisplayName("Dennis");
		userCreate.setDomain(Domain.LOCAL);
		userCreate.setEmail("dennis@abc.com");
		userCreate.setPassword("1234");

		User user1 = service.create(userCreate);
		String uid = user1.getUid();

		Organization organization1 = organizationService.create(new OrganizationCreate().withCode("org1").withName("Admin"));
		Organization organization2 = organizationService.create(new OrganizationCreate().withCode("org2").withName("Users"));
		Organization organization3 = organizationService.create(new OrganizationCreate().withCode("org3").withName("Adv"));
		
		List<UserOrganization> organizations = service.listOrganization(uid);
		Assert.assertEquals(0, organizations.size());
		
		service.addOrganizations(user1.getUid(), Collections.asSet( new UserOrganizationRelation(organization1.getUid(), OrganizationUserRelationType.MEMBER), 
				new UserOrganizationRelation(organization2.getUid(),OrganizationUserRelationType.ADVANCED_MEMBER)));
		organizations = service.listOrganization(uid);
		Assert.assertEquals(2, organizations.size());
		
		UserOrganization organization = organizations.get(0);
		Assert.assertEquals(organization1.getCode(), organization.getCode());
		Assert.assertEquals(organization1.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, organization.getRelationType());
		organization = organizations.get(1);
		Assert.assertEquals(organization2.getCode(), organization.getCode());
		Assert.assertEquals(organization2.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, organization.getRelationType());
		
		service.addOrganizations(user1.getUid(), Collections.asSet(new UserOrganizationRelation(organization1.getUid(),OrganizationUserRelationType.ADVANCED_MEMBER)));//repeat, shouldn't cause any error
		organizations = service.listOrganization(uid);
		Assert.assertEquals(2, organizations.size());
		organization = organizations.get(0);
		Assert.assertEquals(organization1.getCode(), organization.getCode());
		Assert.assertEquals(organization1.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, organization.getRelationType());
		organization = organizations.get(1);
		Assert.assertEquals(organization2.getCode(), organization.getCode());
		Assert.assertEquals(organization2.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, organization.getRelationType());
		
		service.setOrganizations(user1.getUid(), Collections.asSet(new UserOrganizationRelation(organization1.getUid(),OrganizationUserRelationType.SUPERVISOR),
				new UserOrganizationRelation(organization3.getUid(),OrganizationUserRelationType.MEMBER)));
		organizations = service.listOrganization(uid);
		Assert.assertEquals(2, organizations.size());
		organization = organizations.get(0);
		Assert.assertEquals(organization1.getCode(), organization.getCode());
		Assert.assertEquals(organization1.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, organization.getRelationType());
		organization = organizations.get(1);
		Assert.assertEquals(organization3.getCode(), organization.getCode());
		Assert.assertEquals(organization3.getName(), organization.getName());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, organization.getRelationType());
		
		service.removeOrganizations(user1.getUid(), Collections.asSet(organization3.getUid()));
		//not exist organization, shouldn't cause any error
		service.removeOrganizations(user1.getUid(), Collections.asSet(organization2.getUid()));
		organizations = service.listOrganization(uid);
		Assert.assertEquals(1, organizations.size());
		Assert.assertTrue(organizations.stream().anyMatch((e) -> {
			return e.getUid().equals(organization1.getUid());
		}));
		
		organizationService.delete(organization1.getUid(), true);
		organizations = service.listOrganization(uid);
		Assert.assertEquals(0, organizations.size());
		
		// delete user
		service.delete(uid, true);
		user1 = service.find(uid);
		Assert.assertNull(user1);
		
		//delete organization
		organizationService.delete(organization2.getUid(), true);
		organizationService.delete(organization3.getUid(), true);
		Assert.assertEquals(0, organizationService.list(null).size());
	}
	
	@Test
	public void testListDisabled() {
		ListPage<User> page = service.list(null);
		Assert.assertEquals(0, page.size());

		UserCreate userCreate = new UserCreate().withAccount("adennis1").withDisplayName("Dennis1").withPassword("1234");
		User user1 = service.create(userCreate);
		userCreate = new UserCreate().withAccount("bdennis2").withDisplayName("Dennis2").withPassword("1234").withDisabled(Boolean.TRUE);
		User user2 = service.create(userCreate);
		userCreate = new UserCreate().withAccount("cdennis3").withDisplayName("Dennis3").withPassword("1234");
		User user3 = service.create(userCreate);
		
		page = service.list(null, null);
		Assert.assertEquals(3, page.size());
		User user = page.getItems().get(0);
		Assert.assertEquals("adennis1", user.getAccount());
		user = page.getItems().get(1);
		Assert.assertEquals("bdennis2", user.getAccount());
		user = page.getItems().get(2);
		Assert.assertEquals("cdennis3", user.getAccount());
		
		
		page = service.list(null, false);
		Assert.assertEquals(2, page.size());
		user = page.getItems().get(0);
		Assert.assertEquals("adennis1", user.getAccount());
		user = page.getItems().get(1);
		Assert.assertEquals("cdennis3", user.getAccount());

		page = service.list(null, true);
		Assert.assertEquals(1, page.size());
		user = page.getItems().get(0);
		
		
		page = service.list(new UserFilter().withAccount("dennis").withStrContaining(Boolean.TRUE), false);
		Assert.assertEquals(2, page.size());
		user = page.getItems().get(0);
		Assert.assertEquals("adennis1", user.getAccount());
		user = page.getItems().get(1);
		Assert.assertEquals("cdennis3", user.getAccount());
		
		page = service.list(new UserFilter().withAccount("dennis1").withStrContaining(Boolean.TRUE), false);
		Assert.assertEquals(1, page.size());
		user = page.getItems().get(0);
		Assert.assertEquals("adennis1", user.getAccount());
		
		
		page = service.list(new UserFilter().withAccount("dennis").withStrContaining(Boolean.TRUE), true);
		Assert.assertEquals(1, page.size());
		user = page.getItems().get(0);
		Assert.assertEquals("bdennis2", user.getAccount());
		
		page = service.list(new UserFilter().withAccount("dennis1").withStrContaining(Boolean.TRUE), true);
		Assert.assertEquals(0, page.size());

		service.delete(user1.getUid(), true);
		service.delete(user2.getUid(), true);
		service.delete(user3.getUid(), true);


		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}
}
