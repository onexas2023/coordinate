package onexas.coordinate.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.RoleFilter;
import onexas.coordinate.model.RoleUpdate;
import onexas.coordinate.model.RoleUserFilter;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */	
public class RoleServiceTest extends CoordinateImplTestBase {

	@Autowired
	RoleService service;

	@Autowired
	UserService userService;

	@Test
	public void testSimple() {
		ListPage<Role> page = service.list(null);
		Assert.assertEquals(0, page.size());

		RoleCreate roleCreate = new RoleCreate();
		roleCreate.setCode("admin");
		roleCreate.setName("Administrator");
		roleCreate.setDescription("administrator role");

		Role role1 = service.create(roleCreate);
		String uid = role1.getUid();
		Assert.assertNotNull(role1.getUid());
		Assert.assertEquals(roleCreate.getCode(), role1.getCode());
		Assert.assertEquals(roleCreate.getName(), role1.getName());
		Assert.assertEquals(roleCreate.getDescription(), role1.getDescription());

		role1 = service.get(uid);
		Assert.assertEquals(uid, role1.getUid());
		Assert.assertEquals(roleCreate.getCode(), role1.getCode());
		Assert.assertEquals(roleCreate.getName(), role1.getName());
		Assert.assertEquals(roleCreate.getDescription(), role1.getDescription());

		// update
		RoleUpdate update = new RoleUpdate();
		update.setName("Administrators");

		Role role2 = service.update(uid, update);

		role1 = service.find(uid);
		Assert.assertEquals(uid, role2.getUid());
		Assert.assertEquals(role2.getCode(), role1.getCode());
		Assert.assertEquals(role2.getName(), role1.getName());
		Assert.assertEquals(role2.getDescription(), role1.getDescription());

		role1 = service.findByCode(roleCreate.getCode());
		Assert.assertEquals(uid, role2.getUid());
		Assert.assertEquals(role2.getCode(), role1.getCode());
		Assert.assertEquals(role2.getName(), role1.getName());
		Assert.assertEquals(role2.getDescription(), role1.getDescription());

		page = service.list(null);
		Assert.assertEquals(1, page.size());

		role1 = page.getItems().get(0);
		Assert.assertEquals(uid, role2.getUid());
		Assert.assertEquals(role2.getCode(), role1.getCode());
		Assert.assertEquals(role2.getName(), role1.getName());
		Assert.assertEquals(role2.getDescription(), role1.getDescription());

		service.delete(uid, true);

		role1 = service.find(uid);
		Assert.assertNull(role1);

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
		ListPage<Role> page = service.list(null);
		Assert.assertEquals(0, page.size());

		for (int i = 0; i < 100; i++) {
			RoleCreate roleCreate = new RoleCreate();
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
			service.create(roleCreate);
		}

		// test filter, page
		page = service.list(new RoleFilter().withCode("r-a00"));
		Assert.assertEquals(1, page.size());
		Role role00 = page.getItems().get(0);
		Assert.assertEquals("r-a00", role00.getCode());
		Assert.assertEquals("RoleA00", role00.getName());

		page = service.list(new RoleFilter().withCode("r-a").withStrContaining(true).withStrIgnoreCase(true)
				.withSortField("name").withSortDesc(true));
		Assert.assertEquals(50, page.size());
		role00 = page.getItems().get(49);
		Assert.assertEquals("r-a00", role00.getCode());
		Assert.assertEquals("RoleA00", role00.getName());

		Role role98 = page.getItems().get(0);
		Assert.assertEquals("r-a98", role98.getCode());
		Assert.assertEquals("RoleA98", role98.getName());

		page = service.list(new RoleFilter().withCode("r-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		Role role21 = page.getItems().get(0);
		Assert.assertEquals("r-b21", role21.getCode());

		Role role39 = page.getItems().get(9);
		Assert.assertEquals("r-b39", role39.getCode());

		page = service.list(new RoleFilter().withCode("r-b").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		Role role91 = page.getItems().get(0);
		Assert.assertEquals("r-b91", role91.getCode());

		Role role99 = page.getItems().get(4);
		Assert.assertEquals("r-b99", role99.getCode());

		for (Role role : service.list(null).getItems()) {
			service.delete(role.getUid(), false);
		}
		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testRoleUser() {
		Assert.assertEquals(0, service.list(null).size());

		Role role1 = service.create(new RoleCreate().withCode("r1").withName("Role1"));
		Role role2 = service.create(new RoleCreate().withCode("r2").withName("Role2"));

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
				service.addUsers(role1.getUid(), Collections.asSet(user.getUid()));
				break;
			case 1:
				service.addUsers(role2.getUid(), Collections.asSet(user.getUid()));
				break;
			}

		}
		Assert.assertEquals(100, userService.list(null).size());

		ListPage<User> users = service.listUser(role1.getUid(), null);
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(50, users.size());
		User user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		user1 = users.getItems().get(49);
		Assert.assertEquals("dennis98", user1.getAccount());

		users = service.listUser(role2.getUid(), null);
		Assert.assertEquals(1, users.getPageTotal().intValue());
		Assert.assertEquals(50, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis01", user1.getAccount());
		user1 = users.getItems().get(49);
		Assert.assertEquals("dennis99", user1.getAccount());

		users = service.listUser(role2.getUid(), new RoleUserFilter().withPageSize(10).withPageIndex(1));
		Assert.assertEquals(5, users.getPageTotal().intValue());
		Assert.assertEquals(10, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis21", user1.getAccount());
		user1 = users.getItems().get(9);
		Assert.assertEquals("dennis39", user1.getAccount());

		users = service.listUser(role2.getUid(), new RoleUserFilter().withPageSize(10).withPageIndex(2));
		Assert.assertEquals(5, users.getPageTotal().intValue());
		Assert.assertEquals(10, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis41", user1.getAccount());
		user1 = users.getItems().get(9);
		Assert.assertEquals("dennis59", user1.getAccount());

		service.addUsers(role1.getUid(), Collections.asSet(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
				userService.findByAccountDomain("dennis03", Domain.LOCAL).getUid()));
		// repeat
		service.addUsers(role1.getUid(), Collections.asSet(userService.findByAccountDomain("dennis02", Domain.LOCAL).getUid()));
		users = service.listUser(role1.getUid(), null);
		Assert.assertEquals(52, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis01", user1.getAccount());
		user1 = users.getItems().get(2);
		Assert.assertEquals("dennis02", user1.getAccount());
		user1 = users.getItems().get(3);
		Assert.assertEquals("dennis03", user1.getAccount());

		service.removeUsers(role1.getUid(), Collections.asSet(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
				userService.findByAccountDomain("dennis03", Domain.LOCAL).getUid()));
		// not exist
		service.removeUsers(role1.getUid(), Collections.asSet(userService.findByAccountDomain("dennis05", Domain.LOCAL).getUid()));
		users = service.listUser(role1.getUid(), null);
		Assert.assertEquals(50, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis00", user1.getAccount());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis02", user1.getAccount());

		service.setUsers(role1.getUid(), Collections.asSet(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(),
				userService.findByAccountDomain("dennis11", Domain.LOCAL).getUid()));
		users = service.listUser(role1.getUid(), null);
		Assert.assertEquals(2, users.size());
		user1 = users.getItems().get(0);
		Assert.assertEquals("dennis01", user1.getAccount());
		user1 = users.getItems().get(1);
		Assert.assertEquals("dennis11", user1.getAccount());

		// delete 01 & 11
		userService.delete(userService.findByAccountDomain("dennis01", Domain.LOCAL).getUid(), false);
		userService.delete(userService.findByAccountDomain("dennis11", Domain.LOCAL).getUid(), false);
		users = service.listUser(role1.getUid(), null);
		Assert.assertEquals(0, users.size());
		users = service.listUser(role2.getUid(), null);
		Assert.assertEquals(48, users.size());

		for (Role role : service.list(null).getItems()) {
			service.delete(role.getUid(), false);
		}
		Assert.assertEquals(0, service.list(null).size());

		for (User user : userService.list(null).getItems()) {
			userService.delete(user.getUid(), false);
		}
		Assert.assertEquals(0, userService.list(null).size());
	}
}
