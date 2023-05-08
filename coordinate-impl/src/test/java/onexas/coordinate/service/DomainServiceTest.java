package onexas.coordinate.service;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.util.Yamls;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainCreate;
import onexas.coordinate.model.DomainUpdate;
import onexas.coordinate.model.MapDomainConfig;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainServiceTest extends CoordinateImplTestBase {

	@Autowired
	DomainService service;

	@Test
	public void testSimple() {
		List<Domain> list = service.list();
		Assert.assertEquals(1, list.size());
		Domain domain = list.get(0);
		Assert.assertEquals(Domain.LOCAL, domain.getCode());
		Assert.assertEquals("Local", domain.getName());
		Assert.assertEquals(Boolean.FALSE, domain.getDisabled());
		Assert.assertNotNull(service.getConfig(Domain.LOCAL));

		domain = service.get(Domain.LOCAL);
		Assert.assertEquals(Domain.LOCAL, domain.getCode());
		Assert.assertEquals("Local", domain.getName());
		Assert.assertEquals(null, domain.getDescription());
		Assert.assertEquals(Boolean.FALSE, domain.getDisabled());
		Assert.assertNotNull(service.getConfig(Domain.LOCAL));

		Domain domain1 = service.create(
				new DomainCreate().withCode("xyz").withName("Xyz").withProvider("ldap").withDescription("the xyz")
						.withConfigYaml(Yamls.yamlify(new MapDomainConfig().with("host", "xyz.ldap.com"))));

		Assert.assertEquals("xyz", domain1.getCode());
		Assert.assertEquals("Xyz", domain1.getName());
		Assert.assertEquals(Boolean.FALSE, domain1.getDisabled());
		Assert.assertEquals("the xyz", domain1.getDescription());
		Assert.assertEquals("ldap", domain1.getProvider());
		Assert.assertEquals("xyz.ldap.com",
				service.getConfig(domain1.getCode()).toObject(TestConfigAttrs.class).getHost());

		domain1 = service.get("xyz");
		Assert.assertEquals("xyz", domain1.getCode());
		Assert.assertEquals("Xyz", domain1.getName());
		Assert.assertEquals(Boolean.FALSE, domain1.getDisabled());
		Assert.assertEquals("the xyz", domain1.getDescription());
		Assert.assertEquals("ldap", domain1.getProvider());
		Assert.assertEquals("xyz.ldap.com",
				service.getConfig(domain1.getCode()).toObject(TestConfigAttrs.class).getHost());

		Domain domain2 = service.create(new DomainCreate().withCode("abc").withName("Abc").withDisabled(Boolean.TRUE)
				.withProvider("cas").withConfigYaml(Yamls.yamlify(new MapDomainConfig().with("host", "abc.cas.com"))));

		Assert.assertEquals("abc", domain2.getCode());
		Assert.assertEquals("Abc", domain2.getName());
		Assert.assertEquals(Boolean.TRUE, domain2.getDisabled());
		Assert.assertEquals("cas", domain2.getProvider());
		Assert.assertEquals("abc.cas.com",
				service.getConfig(domain2.getCode()).toObject(Map.class).get("host"));

		domain2 = service.find("abc");
		Assert.assertEquals("abc", domain2.getCode());
		Assert.assertEquals("Abc", domain2.getName());
		Assert.assertEquals(Boolean.TRUE, domain2.getDisabled());
		Assert.assertEquals("cas", domain2.getProvider());
		Assert.assertEquals("abc.cas.com",
				service.getConfig(domain2.getCode()).toObject(Map.class).get("host"));

		Assert.assertNull(service.find("ykk"));

		list = service.list();
		Assert.assertEquals(3, list.size());

		domain = list.get(0);
		Assert.assertEquals(Domain.LOCAL, domain.getCode());
		Assert.assertEquals("Local", domain.getName());
		domain = list.get(1);
		Assert.assertEquals("abc", domain.getCode());
		Assert.assertEquals("Abc", domain.getName());
		Assert.assertEquals("cas", domain.getProvider());
		Assert.assertEquals("abc.cas.com", service.getConfig(domain.getCode()).toObject(Map.class).get("host"));
		domain = list.get(2);
		Assert.assertEquals("xyz", domain.getCode());
		Assert.assertEquals("Xyz", domain.getName());
		Assert.assertEquals("ldap", domain.getProvider());
		Assert.assertEquals("xyz.ldap.com",
				service.getConfig(domain.getCode()).toObject(TestConfigAttrs.class).getHost());

		// update
		Domain domain0 = service.update(Domain.LOCAL, new DomainUpdate().withName("MainDomain")
				.withConfigYaml(Yamls.yamlify(new MapDomainConfig())));
		domain1 = service.update("xyz", new DomainUpdate().withName("Xyz1").withDescription("the xyz1"));
		domain2 = service.update("abc", new DomainUpdate().withName("Abc1").withDisabled(Boolean.FALSE)
				.withConfigYaml(Yamls.yamlify(new MapDomainConfig().with("host", "abc1.cas.com"))));

		Assert.assertEquals(Domain.LOCAL, domain0.getCode());
		Assert.assertEquals("MainDomain", domain0.getName());
		Assert.assertEquals(Boolean.FALSE, domain0.getDisabled());
		Assert.assertEquals(Domain.LOCAL, domain0.getProvider());

		Assert.assertEquals("abc", domain2.getCode());
		Assert.assertEquals("Abc1", domain2.getName());
		Assert.assertEquals(Boolean.FALSE, domain2.getDisabled());
		Assert.assertEquals("cas", domain2.getProvider());
		Assert.assertEquals("abc1.cas.com",
				service.getConfig(domain2.getCode()).toObject(Map.class).get("host"));

		Assert.assertEquals("xyz", domain1.getCode());
		Assert.assertEquals("Xyz1", domain1.getName());
		Assert.assertEquals(Boolean.FALSE, domain1.getDisabled());
		Assert.assertEquals("the xyz1", domain1.getDescription());
		Assert.assertEquals("ldap", domain1.getProvider());
		Assert.assertEquals("xyz.ldap.com",
				service.getConfig(domain1.getCode()).toObject(TestConfigAttrs.class).getHost());

		list = service.list();
		Assert.assertEquals(3, list.size());
		domain = list.get(0);
		Assert.assertEquals(Domain.LOCAL, domain.getCode());
		Assert.assertEquals("MainDomain", domain.getName());
		Assert.assertEquals(Boolean.FALSE, domain.getDisabled());
		Assert.assertEquals(Domain.LOCAL, domain.getProvider());
		domain = list.get(1);
		Assert.assertEquals("abc", domain.getCode());
		Assert.assertEquals("Abc1", domain.getName());
		Assert.assertEquals(Boolean.FALSE, domain.getDisabled());
		Assert.assertEquals("cas", domain.getProvider());
		Assert.assertEquals("abc1.cas.com",
				service.getConfig(domain.getCode()).toObject(Map.class).get("host"));
		domain = list.get(2);
		Assert.assertEquals("xyz", domain.getCode());
		Assert.assertEquals("Xyz1", domain.getName());
		Assert.assertEquals(Boolean.FALSE, domain.getDisabled());
		Assert.assertEquals("ldap", domain.getProvider());
		Assert.assertEquals("xyz.ldap.com",
				service.getConfig(domain.getCode()).toObject(TestConfigAttrs.class).getHost());

		service.delete(domain1.getCode(), false);
		service.delete(domain2.getCode(), false);

		service.delete(domain1.getCode(), true);

		try {
			service.delete(Domain.LOCAL, false);
			Assert.fail();
		} catch (IntegrityViolationException x) {
		}
		service.delete(Domain.LOCAL, true);

		list = service.list();
		Assert.assertEquals(1, list.size());
	}

	static public class TestConfigAttrs {
		String host;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}
	}

}
