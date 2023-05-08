package onexas.coordinate.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationCreate;
import onexas.coordinate.model.OrganizationFilter;
import onexas.coordinate.model.OrganizationUpdate;
import onexas.coordinate.model.OrganizationUser;
import onexas.coordinate.model.OrganizationUserFilter;
import onexas.coordinate.model.OrganizationUserRelation;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationServiceTest extends CoordinateImplTestBase {

	@Autowired
	OrganizationService service;

	@Autowired
	UserService userService;

	@Test
	public void testSimple() {
		ListPage<Organization> page = service.list(null);
		Assert.assertEquals(0, page.size());

		OrganizationCreate organizationCreate = new OrganizationCreate();
		organizationCreate.setCode("zz");
		organizationCreate.setName("one.com");
		organizationCreate.setDescription("one organization");

		Organization organization1 = service.create(organizationCreate);
		String uid = organization1.getUid();
		Assert.assertNotNull(organization1.getUid());
		Assert.assertEquals(organizationCreate.getCode(), organization1.getCode());
		Assert.assertEquals(organizationCreate.getName(), organization1.getName());
		Assert.assertEquals(organizationCreate.getDescription(), organization1.getDescription());

		organization1 = service.get(uid);
		Assert.assertEquals(uid, organization1.getUid());
		Assert.assertEquals(organizationCreate.getCode(), organization1.getCode());
		Assert.assertEquals(organizationCreate.getName(), organization1.getName());
		Assert.assertEquals(organizationCreate.getDescription(), organization1.getDescription());

		// update
		OrganizationUpdate update = new OrganizationUpdate();
		update.setName("depart.one.com");

		Organization organization2 = service.update(uid, update);

		organization1 = service.find(uid);
		Assert.assertEquals(uid, organization2.getUid());
		Assert.assertEquals(organization2.getCode(), organization1.getCode());
		Assert.assertEquals(organization2.getName(), organization1.getName());
		Assert.assertEquals(organization2.getDescription(), organization1.getDescription());

		organization1 = service.findByCode(organizationCreate.getCode());
		Assert.assertEquals(uid, organization2.getUid());
		Assert.assertEquals(organization2.getCode(), organization1.getCode());
		Assert.assertEquals(organization2.getName(), organization1.getName());
		Assert.assertEquals(organization2.getDescription(), organization1.getDescription());

		page = service.list(null);
		Assert.assertEquals(1, page.size());

		organization1 = page.getItems().get(0);
		Assert.assertEquals(uid, organization2.getUid());
		Assert.assertEquals(organization2.getCode(), organization1.getCode());
		Assert.assertEquals(organization2.getName(), organization1.getName());
		Assert.assertEquals(organization2.getDescription(), organization1.getDescription());

		service.delete(uid, true);

		organization1 = service.find(uid);
		Assert.assertNull(organization1);

		try {
			service.get(uid);
			Assert.fail();
		} catch (NotFoundException e) {
		}

		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testFilterPage() {
		ListPage<Organization> page = service.list(null);
		Assert.assertEquals(0, page.size());

		for (int i = 0; i < 100; i++) {
			OrganizationCreate organizationCreate = new OrganizationCreate();
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
			service.create(organizationCreate);
		}

		// test filter, page
		page = service.list(new OrganizationFilter().withCode("org-a00"));
		Assert.assertEquals(1, page.size());
		Organization organization00 = page.getItems().get(0);
		Assert.assertEquals("org-a00", organization00.getCode());
		Assert.assertEquals("OrganizationA00", organization00.getName());

		page = service.list(new OrganizationFilter().withCode("org-a").withStrContaining(true).withStrIgnoreCase(true)
				.withSortField("name").withSortDesc(true));
		Assert.assertEquals(50, page.size());
		organization00 = page.getItems().get(49);
		Assert.assertEquals("org-a00", organization00.getCode());
		Assert.assertEquals("OrganizationA00", organization00.getName());

		Organization organization98 = page.getItems().get(0);
		Assert.assertEquals("org-a98", organization98.getCode());
		Assert.assertEquals("OrganizationA98", organization98.getName());

		page = service.list(new OrganizationFilter().withCode("org-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		Organization organization21 = page.getItems().get(0);
		Assert.assertEquals("org-b21", organization21.getCode());

		Organization organization39 = page.getItems().get(9);
		Assert.assertEquals("org-b39", organization39.getCode());

		page = service.list(new OrganizationFilter().withCode("org-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		Organization organization91 = page.getItems().get(0);
		Assert.assertEquals("org-b91", organization91.getCode());

		Organization organization99 = page.getItems().get(4);
		Assert.assertEquals("org-b99", organization99.getCode());

		for (Organization organization : service.list(null).getItems()) {
			service.delete(organization.getUid(), false);
		}
		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testOrganizationUser() {
		Assert.assertEquals(0, service.list(null).size());

		Organization organization1 = service
				.create(new OrganizationCreate().withCode("org1").withName("Organization1"));
		Organization organization2 = service
				.create(new OrganizationCreate().withCode("org2").withName("Organization2"));

		Assert.assertEquals(0, userService.list(null).size());

		for (int i = 0; i < 100; i++) {
			UserCreate userCreate = new UserCreate();
			String p = (i <= 9 ? "0" : "") + i;
			userCreate.setAccount("dennis" + p);
			userCreate.setDisplayName("Dennis");
			userCreate.setDomain(Domain.LOCAL);
			userCreate.setEmail("dennis" + p + "@abc.com");
			userCreate.setPassword("1234");

			User user = userService.create(userCreate);

			switch (i % 2) {
			case 0:
				service.addUsers(organization1.getUid(), Collections
						.asSet(new OrganizationUserRelation(user.getUid(), OrganizationUserRelationType.MEMBER)));
				break;
			case 1:
				service.addUsers(organization2.getUid(), Collections
						.asSet(new OrganizationUserRelation(user.getUid(), OrganizationUserRelationType.ADVANCED_MEMBER)));
				break;
			}

		}
		Assert.assertEquals(100, userService.list(null).size());

		UserOrganization uo = service.findUserOrganization(organization1.getUid(), Strings.randomUid());
		Assert.assertNull(uo);

		ListPage<OrganizationUser> users = service.listUser(organization1.getUid(), null);
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(50, users.size());
		OrganizationUser user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

		uo = service.findUserOrganization(organization1.getUid(), user1.getUid());
		Assert.assertEquals(organization1.getUid(), uo.getUid());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, uo.getRelationType());

		user1 = users.getItems().get(49);
		Assert.assertEquals("dennis98", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00"));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(1, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis000"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());

		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("Dennis00"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00").withEmail("dennis00@abc.com").withDisplayName("Dennis"));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(1, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00").withEmail("dennis00@abc.com").withDisplayName("DennisAA"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00").withEmail("dennis00@abc.comAA").withDisplayName("Dennis"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00AA").withEmail("dennis00@abc.com").withDisplayName("Dennis"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00").withEmail("dennis00@abc.comAA").withDisplayName("DennisAA").withMatchAny(true));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(1, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00AA").withEmail("dennis00@abc.comAA").withDisplayName("Dennis").withMatchAny(true));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(50, users.size());
		user1 = users.getItems().get(49);
		Assert.assertEquals("dennis98", user1.getAccount());
		
		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis00AA").withEmail("dennis00@abc.com").withDisplayName("DennisAA").withMatchAny(true));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(1, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		
		
		
		
		

		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis0")
				.withStrContaining(Boolean.TRUE).withSortField("account"));
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(5, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());
		user1 = users.getItems().get(4);
		Assert.assertEquals("dennis08", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

		users = service.listUser(organization1.getUid(), new OrganizationUserFilter().withAccount("dennis000"));
		Assert.assertEquals(0, users.getPageTotal().intValue());
		Assert.assertEquals(0, users.size());

		uo = service.findUserOrganization(organization1.getUid(), user1.getUid());
		Assert.assertEquals(organization1.getUid(), uo.getUid());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, uo.getRelationType());

		users = service.listUser(organization2.getUid(), null);
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(50, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis01", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

		uo = service.findUserOrganization(organization2.getUid(), user1.getUid());
		Assert.assertEquals(organization2.getUid(), uo.getUid());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, uo.getRelationType());

		user1 = users.getItems().get(49);
		Assert.assertEquals("dennis99", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

		users = service.listUser(organization2.getUid(),
				new OrganizationUserFilter().withPageSize(10).withPageIndex(1));
		Assert.assertEquals(5, users.getPageTotal().intValue());
		Assert.assertEquals(10, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis21", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
		user1 = users.getItems().get(9);
		Assert.assertEquals("dennis39", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

		users = service.listUser(organization2.getUid(),
				new OrganizationUserFilter().withPageSize(10).withPageIndex(2));
		Assert.assertEquals(5, users.getPageTotal().intValue());
		Assert.assertEquals(10, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis41", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
		user1 = users.getItems().get(9);
		Assert.assertEquals("dennis59", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());

		service.addUsers(organization1.getUid(),
				Collections.asSet(
						new OrganizationUserRelation(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
								OrganizationUserRelationType.SUPERVISOR),
						new OrganizationUserRelation(userService.findByAccountDomain("dennis03", Domain.LOCAL).getUid(),
								OrganizationUserRelationType.MEMBER)));
		// repeat
		service.addUsers(organization1.getUid(),
				Collections.asSet(
						new OrganizationUserRelation(userService.findByAccountDomain("dennis02", Domain.LOCAL).getUid(),
								OrganizationUserRelationType.ADVANCED_MEMBER)));
		users = service.listUser(organization1.getUid(), null);
		Assert.assertEquals(52, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis01", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, user1.getRelationType());
		user1 = users.getItems().get(2);
		Assert.assertEquals("dennis02", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
		user1 = users.getItems().get(3);
		Assert.assertEquals("dennis03", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.MEMBER, user1.getRelationType());

		service.removeUsers(organization1.getUid(),
				Collections.asSet(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
						userService.findByAccountDomain("dennis03", Domain.LOCAL).getUid()));
		// not exist
		service.removeUsers(organization1.getUid(),
				Collections.asSet(userService.findByAccountDomain("dennis05", Domain.LOCAL).getUid()));
		users = service.listUser(organization1.getUid(), null);
		Assert.assertEquals(50, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis02", user1.getAccount());

		service.setUsers(organization1.getUid(),
				Collections.asSet(
						new OrganizationUserRelation(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
								OrganizationUserRelationType.ADVANCED_MEMBER),
						new OrganizationUserRelation(userService.findByAccountDomain("dennis11", Domain.LOCAL).getUid(),
								OrganizationUserRelationType.SUPERVISOR)));
		users = service.listUser(organization1.getUid(), null);
		Assert.assertEquals(2, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis01", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.ADVANCED_MEMBER, user1.getRelationType());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis11", user1.getAccount());
		Assert.assertEquals(OrganizationUserRelationType.SUPERVISOR, user1.getRelationType());
		// delete 01 & 11
		userService.delete(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(), false);
		userService.delete(userService.findByAccountDomain("dennis11", Domain.LOCAL).getUid(), false);
		users = service.listUser(organization1.getUid(), null);
		Assert.assertEquals(0, users.size());
		users = service.listUser(organization2.getUid(), null);
		Assert.assertEquals(48, users.size());

		for (Organization organization : service.list(null).getItems()) {
			service.delete(organization.getUid(), false);
		}
		Assert.assertEquals(0, service.list(null).size());

		for (User user : userService.list(null).getItems()) {
			userService.delete(user.getUid(), false);
		}
		Assert.assertEquals(0, userService.list(null).size());
	}
}
