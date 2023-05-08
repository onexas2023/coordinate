package onexas.coordinate.api.v1.sdk;

import static onexas.coordinate.model.Domain.LOCAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.ADomain;
import onexas.coordinate.api.v1.sdk.model.ADomainCreate;
import onexas.coordinate.api.v1.sdk.model.ADomainUpdate;
import onexas.coordinate.api.v1.sdk.model.ADomainUser;
import onexas.coordinate.api.v1.sdk.model.ADomainUserFilter;
import onexas.coordinate.api.v1.sdk.model.ADomainUserListPage;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.SchemaMap;
import onexas.coordinate.common.util.Yamls;
import onexas.coordinate.model.MapDomainConfig;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminDomainApiTest extends CoordinateApiSDKTestBase {

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "somerole");

			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(client);
			try {
				api.listDomain();
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(
					new PrincipalPermission(onexas.coordinate.api.v1.admin.AdminDomainApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.admin.AdminDomainApi.ACTION_VIEW));
			api = new CoordinateAdminDomainApi(client);
			api.listDomain();

			try {
				Map<String, Object> attr = new HashMap<>();
				attr.put("host", "xyz.ldap.com");
				api.createDomain(new ADomainCreate().code("xyz").name("xyz").configYaml(Yamls.yamlify(attr)));

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
			ApiClient client = getApiClientWithAuthCreate();

			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(client);

			List<ADomain> list = api.listDomain();
			Assert.assertEquals(1, list.size());
			ADomain domain = list.get(0);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("Local", domain.getName());
			Assert.assertEquals(LOCAL, domain.getProvider());

			// through api, "" become null
			Assert.assertEquals(null, api.getDomainConfigYaml(LOCAL));
			Assert.assertNotNull(api.getDomainConfig(LOCAL));

			domain = api.getDomain(LOCAL, false);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("Local", domain.getName());
			Assert.assertEquals(LOCAL, domain.getProvider());

			// through api, "" become null
			Assert.assertEquals(null, api.getDomainConfigYaml(LOCAL));
			Assert.assertNotNull(api.getDomainConfig(LOCAL));

			Map<String, Object> attr = new HashMap<>();
			attr.put("host", "xyz.ldap.com");
			ADomain domain1 = api.createDomain(
					new ADomainCreate().code("xyz").name("xyz").provider("ldap").configYaml(Yamls.yamlify(attr)));

			Assert.assertEquals("xyz", domain1.getCode());
			Assert.assertEquals("xyz", domain1.getName());
			Assert.assertNotNull(api.getDomainConfigYaml(domain1.getCode()));
			Assert.assertEquals("ldap", domain1.getProvider());
			Assert.assertEquals("xyz.ldap.com", api.getDomainConfig(domain1.getCode()).get("host"));

			domain1 = api.getDomain("xyz", false);
			Assert.assertEquals("xyz", domain1.getCode());
			Assert.assertEquals("xyz", domain1.getName());
			Assert.assertNotNull(api.getDomainConfigYaml(domain1.getCode()));
			Assert.assertEquals("ldap", domain1.getProvider());
			Assert.assertEquals("xyz.ldap.com", api.getDomainConfig(domain1.getCode()).get("host"));

			attr = new HashMap<>();
			attr.put("host", "abc.cas.com");
			ADomain domain2 = api.createDomain(
					new ADomainCreate().code("abc").name("abc").provider("cas").configYaml(Yamls.yamlify(attr)));

			Assert.assertEquals("abc", domain2.getCode());
			Assert.assertEquals("abc", domain2.getName());
			Assert.assertNotNull(api.getDomainConfigYaml(domain2.getCode()));
			Assert.assertEquals("cas", domain2.getProvider());
			Assert.assertEquals("abc.cas.com", api.getDomainConfig(domain2.getCode()).get("host"));

			domain2 = api.getDomain("abc", false);
			Assert.assertEquals("abc", domain2.getCode());
			Assert.assertEquals("abc", domain2.getName());
			Assert.assertNotNull(api.getDomainConfigYaml(domain2.getCode()));
			Assert.assertEquals("cas", domain2.getProvider());
			Assert.assertEquals("abc.cas.com", api.getDomainConfig(domain2.getCode()).get("host"));

			Assert.assertNull(api.getDomain("ykk", true));

			list = api.listDomain();
			Assert.assertEquals(3, list.size());

			domain = list.get(0);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("Local", domain.getName());
			domain = list.get(1);
			Assert.assertEquals("abc", domain.getCode());
			Assert.assertEquals("abc", domain.getName());
			Assert.assertEquals("cas", domain.getProvider());
			Assert.assertEquals("abc.cas.com", api.getDomainConfig(domain.getCode()).get("host"));
			domain = list.get(2);
			Assert.assertEquals("xyz", domain.getCode());
			Assert.assertEquals("xyz", domain.getName());
			Assert.assertEquals("ldap", domain.getProvider());
			Assert.assertEquals("xyz.ldap.com", api.getDomainConfig(domain1.getCode()).get("host"));

			// update
			attr = new HashMap<>();
			attr.put("host", "abc1.cas.com");
			ADomain domain0 = api.updateDomain(LOCAL, new ADomainUpdate().name("MainDomain")
					.configYaml(Yamls.yamlify(new MapDomainConfig())));
			domain1 = api.updateDomain("xyz", new ADomainUpdate().name("XYZ1"));
			domain2 = api
					.updateDomain("abc", new ADomainUpdate().name("ABC1").configYaml(Yamls.yamlify(attr)));

			Assert.assertEquals(LOCAL, domain0.getCode());
			Assert.assertEquals("MainDomain", domain0.getName());
			Assert.assertEquals(LOCAL, domain0.getProvider());

			Assert.assertEquals("abc", domain2.getCode());
			Assert.assertEquals("ABC1", domain2.getName());
			Assert.assertEquals("cas", domain2.getProvider());
			Assert.assertEquals("abc1.cas.com", api.getDomainConfig(domain2.getCode()).get("host"));

			Assert.assertEquals("xyz", domain1.getCode());
			Assert.assertEquals("XYZ1", domain1.getName());
			Assert.assertEquals("ldap", domain1.getProvider());
			Assert.assertEquals("xyz.ldap.com", api.getDomainConfig(domain1.getCode()).get("host"));

			list = api.listDomain();
			Assert.assertEquals(3, list.size());
			domain = list.get(0);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("MainDomain", domain.getName());
			Assert.assertEquals(LOCAL, domain.getProvider());
			domain = list.get(1);
			Assert.assertEquals("abc", domain.getCode());
			Assert.assertEquals("ABC1", domain.getName());
			Assert.assertEquals("cas", domain.getProvider());
			Assert.assertEquals("abc1.cas.com", api.getDomainConfig(domain.getCode()).get("host"));
			domain = list.get(2);
			Assert.assertEquals("xyz", domain.getCode());
			Assert.assertEquals("XYZ1", domain.getName());
			Assert.assertEquals("ldap", domain.getProvider());
			Assert.assertEquals("xyz.ldap.com", api.getDomainConfig(domain1.getCode()).get("host"));

			api.deleteDomain(domain1.getCode(), false);
			api.deleteDomain(domain2.getCode(), false);

			api.deleteDomain(domain1.getCode(), true);

			try {
				api.deleteDomain(LOCAL, false);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(400, x.getCode());
			}
			api.deleteDomain(LOCAL, true);

			list = api.listDomain();
			Assert.assertEquals(1, list.size());

			// set back to prevent other test(metadata test) error
			api.updateDomain(LOCAL, new ADomainUpdate().name("Local"));
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testFilterPage() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));

			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(client);
			CoordinateAdminUserApi userApi = new CoordinateAdminUserApi(client);
			ADomainUserListPage page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL, null);
			Assert.assertEquals(1, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				AUserCreate userCreate = new AUserCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					userCreate.setAccount("dennis" + p);
					userCreate.setEmail("dennis" + p + "@abc.com");
					userCreate.setDomain(onexas.coordinate.model.Domain.LOCAL);
					userCreate.setDisplayName("Dennis" + p);
					userCreate.setPassword("1234");
					break;
				case 1:
					userCreate.setAccount("alice" + p);
					userCreate.setEmail("alice" + p + "@def.com");
					userCreate.setDomain(onexas.coordinate.model.Domain.LOCAL);
					userCreate.setDisplayName("Alice" + p);
					userCreate.setPassword("5678");
					break;
				}
				userApi.createUser(userCreate);
			}

			// test filter, page
			page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL,
					new ADomainUserFilter().criteria("dennis00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long) 1L, page.getItemTotal());
			ADomainUser user00 = page.getItems().get(0);
			Assert.assertEquals("dennis00", user00.getAccount());
			Assert.assertEquals("dennis00@abc.com", user00.getEmail());

			page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL,
					new ADomainUserFilter().criteria("dennis").sortField("email").sortDesc(true));
			Assert.assertEquals(50, page.getItems().size());
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			user00 = page.getItems().get(49);
			Assert.assertEquals("dennis00", user00.getAccount());
			Assert.assertEquals("dennis00@abc.com", user00.getEmail());

			ADomainUser user98 = page.getItems().get(0);
			Assert.assertEquals("dennis98", user98.getAccount());
			Assert.assertEquals("dennis98@abc.com", user98.getEmail());

			page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL,
					new ADomainUserFilter().criteria("alice").pageIndex(1).pageSize(10));
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			ADomainUser user21 = page.getItems().get(0);
			Assert.assertEquals("alice21", user21.getAccount());

			ADomainUser user39 = page.getItems().get(9);
			Assert.assertEquals("alice39", user39.getAccount());

			page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL,
					new ADomainUserFilter().criteria("alice").pageIndex(3).pageSize(15));
			Assert.assertEquals((Long) 50L, page.getItemTotal());
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			ADomainUser user91 = page.getItems().get(0);
			Assert.assertEquals("alice91", user91.getAccount());

			ADomainUser user99 = page.getItems().get(4);
			Assert.assertEquals("alice99", user99.getAccount());

			for (AUser user : userApi.listUser(null).getItems()) {
				if (user.getAccount().equals("authuser")) {
					continue;
				}
				userApi.deleteUser(user.getUid(), false);
			}
			page = api.listDomainUser(onexas.coordinate.model.Domain.LOCAL, null);
			Assert.assertEquals(1, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNestedConfig() {
		try {
			ApiClient client = getApiClientWithAuthCreate();

			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(client);

			SchemaMap<Object> config = new SchemaMap<Object>().with("a", new SchemaMap<Object>().with("b", "c"));
			String yaml = Yamls.yamlify(config);
			System.out.println(">>>" + yaml);
			ADomain domain1 = api
					.createDomain(new ADomainCreate().code("xyz").name("xyz").provider("ldap").configYaml(yaml));

			Map<String, Object> m = api.getDomainConfig(domain1.getCode());
			Assert.assertEquals("c", ((Map<String, Object>) m.get("a")).get("b"));
			api.deleteDomain(domain1.getCode(), false);

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
