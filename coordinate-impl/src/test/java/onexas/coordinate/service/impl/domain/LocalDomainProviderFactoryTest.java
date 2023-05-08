package onexas.coordinate.service.impl.domain;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.DomainUserFilter;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainAuthenticator;
import onexas.coordinate.service.domain.DomainAuthenticator.Authentication;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainUserFinder;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LocalDomainProviderFactoryTest extends CoordinateImplTestBase {

	@Autowired
	UserService service;

	@Autowired
	LocalDomainProviderFactory factory;
	
	@Test
	public void testSimple() {
		Assert.assertEquals(0, service.list(null).size());

		UserCreate userCreate = new UserCreate();
		userCreate.setAccount("dennis");
		userCreate.setDomain(Domain.LOCAL);
		userCreate.setDisplayName("Dennix");
		userCreate.setEmail("abc@def.com");
		userCreate.setPassword("1234");

		User user1 = service.create(userCreate);
		String uid = user1.getUid();
		
		DomainProvider provider = factory.getProvider(Domain.LOCAL, factory.getConfigTemplate());
		
		DomainAuthenticator authenticator = provider.getAuthenticator();
		
		Authentication auth;
		try {
			auth = authenticator.authenticate("xyz", "5678");
			Assert.fail();
		}catch(UnauthenticatedException x) {
		}
		try {
			auth = authenticator.authenticate("dennis", "5678");
			Assert.fail();
		}catch(UnauthenticatedException x) {
		}
		
		auth = authenticator.authenticate("dennis", "1234");

		Assert.assertEquals(user1.getUid(), auth.getIdentity());
		
		DomainUserFinder finder = provider.getUserFinder();
		
		DomainUser duser1 = finder.find("aaaa");
		Assert.assertNull(duser1);
		
		duser1 = finder.get(user1.getUid());		
		Assert.assertEquals(user1.getUid(), duser1.getIdentity());
		Assert.assertEquals(user1.getAccount(), duser1.getAccount());
		Assert.assertEquals(user1.getDisplayName(), duser1.getDisplayName());
		Assert.assertEquals(user1.getEmail(), duser1.getEmail());
		
		duser1 = finder.find(user1.getUid());		
		Assert.assertEquals(user1.getUid(), duser1.getIdentity());
		Assert.assertEquals(user1.getAccount(), duser1.getAccount());
		Assert.assertEquals(user1.getDisplayName(), duser1.getDisplayName());
		Assert.assertEquals(user1.getEmail(), duser1.getEmail());
		
		service.delete(uid, true);

		duser1 = finder.find(user1.getUid());		
		Assert.assertNull(duser1);

		try {
			duser1 = finder.get(user1.getUid());	
			Assert.fail();
		} catch (NotFoundException e) {
		}

		ListPage<DomainUser> page = finder.list(null);
		Assert.assertEquals(0, page.size());

	}

	@Test
	public void testFilterPage() {
		Assert.assertEquals(0, service.list(null).size());

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
		DomainProvider provider = factory.getProvider(Domain.LOCAL, factory.getConfigTemplate());
		DomainUserFinder finder = provider.getUserFinder();
		// test filter, page
		ListPage<DomainUser> page = finder.list(new DomainUserFilter().withCriteria("dennis00"));
		Assert.assertEquals(1, page.size());
		DomainUser user00 = page.getItems().get(0);
		Assert.assertEquals("dennis00", user00.getAccount());
		Assert.assertEquals("Dennis", user00.getDisplayName());
		Assert.assertEquals("dennis00@abc.com", user00.getEmail());

		page = finder.list(new DomainUserFilter().withCriteria("dennis").withSortField("email").withSortDesc(true));
		Assert.assertEquals(50, page.size());
		user00 = page.getItems().get(49);
		Assert.assertEquals("dennis00", user00.getAccount());
		Assert.assertEquals("dennis00@abc.com", user00.getEmail());

		DomainUser user98 = page.getItems().get(0);
		Assert.assertEquals("dennis98", user98.getAccount());
		Assert.assertEquals("dennis98@abc.com", user98.getEmail());

		page = finder.list(new DomainUserFilter().withCriteria("alice").withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		DomainUser user21 = page.getItems().get(0);
		Assert.assertEquals("alice21", user21.getAccount());

		DomainUser user39 = page.getItems().get(9);
		Assert.assertEquals("alice39", user39.getAccount());

		page = finder.list(new DomainUserFilter().withCriteria("alice").withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		DomainUser user91 = page.getItems().get(0);
		Assert.assertEquals("alice91", user91.getAccount());

		DomainUser user99 = page.getItems().get(4);
		Assert.assertEquals("alice99", user99.getAccount());

		for (User user : service.list(null).getItems()) {
			service.delete(user.getUid(), false);
		}
		page = finder.list(null);
		Assert.assertEquals(0, page.size());

	}
}
