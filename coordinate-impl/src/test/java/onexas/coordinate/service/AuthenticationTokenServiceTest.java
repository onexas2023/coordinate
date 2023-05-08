package onexas.coordinate.service;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.AuthenticationTokenCreate;
import onexas.coordinate.model.Domain;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AuthenticationTokenServiceTest extends CoordinateImplTestBase {

	@Autowired
	AuthenticationTokenService service;

	@Test
	public void testSimple() {
		Assert.assertEquals(0, service.count());

		long now1 = System.currentTimeMillis();
		AuthenticationToken token = service.create(
				new AuthenticationTokenCreate().withAccount("dennis").withDomain(Domain.LOCAL).withAliasUid("abcd").withClientIp("1.2.3.4"));
		Assert.assertEquals(1, service.count());

		Assert.assertNotNull(token.getToken());
		Assert.assertTrue(token.getTimeoutAt() > now1);
		Assert.assertEquals("dennis", token.getAccount());
		Assert.assertEquals("abcd", token.getAliasUid());
		Assert.assertEquals(Domain.LOCAL, token.getDomain());
		Assert.assertEquals("1.2.3.4", token.getClientIp());

		AuthenticationToken token1 = service.find(token.getToken());
		Assert.assertEquals(token.getToken(), token1.getToken());
		Assert.assertEquals(token.getAccount(), token1.getAccount());
		Assert.assertEquals(token.getDomain(), token1.getDomain());
		Assert.assertEquals(token.getClientIp(), token1.getClientIp());
		Assert.assertEquals(token.getTimeoutAt(), token1.getTimeoutAt());

		token = service
				.create(new AuthenticationTokenCreate().withAccount("alice").withDomain("xyz").withAliasUid("zzz").withClientIp("5.6.7.8"));
		Assert.assertEquals(2, service.count());

		Assert.assertNotNull(token.getToken());
		Assert.assertTrue(token.getTimeoutAt() > now1);
		Assert.assertEquals("alice", token.getAccount());
		Assert.assertEquals("zzz", token.getAliasUid());
		Assert.assertEquals("xyz", token.getDomain());
		Assert.assertEquals("5.6.7.8", token.getClientIp());

		AuthenticationToken token2 = service.find(token.getToken());
		Assert.assertEquals(token.getToken(), token2.getToken());
		Assert.assertEquals(token.getAccount(), token2.getAccount());
		Assert.assertEquals(token.getDomain(), token2.getDomain());
		Assert.assertEquals(token.getClientIp(), token2.getClientIp());
		Assert.assertEquals(token.getTimeoutAt(), token2.getTimeoutAt());

		service.delete(token1.getToken(), true);
		Assert.assertEquals(1, service.count());
		Assert.assertNull(service.find(token1.getToken()));

		service.delete(token2.getToken(), true);
		Assert.assertEquals(0, service.count());
		Assert.assertNull(service.find(token2.getToken()));

	}

	@Test
	public void testProperty() {
		Assert.assertEquals(0, service.count());

		AuthenticationToken token1 = service.create(
				new AuthenticationTokenCreate().withAccount("dennis").withDomain(Domain.LOCAL).withAliasUid("aaaa").withClientIp("1.2.3.4"));
		AuthenticationToken token2 = service
				.create(new AuthenticationTokenCreate().withAccount("alice").withDomain("xyz").withAliasUid("bbbb").withClientIp("5.6.7.8"));

		Map<String, String> props = service.getProperties(token1.getToken());
		Assert.assertEquals(0, props.size());

		service.setProperty(token1.getToken(), "key1", "abc");
		props = service.getProperties(token1.getToken());
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("abc", props.get("key1"));

		props.put("key1", "a");
		props.put("key2", "b");
		props.put("key3", "c");
		service.setProperties(token1.getToken(), props);
		props = service.getProperties(token1.getToken());
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("a", props.get("key1"));
		Assert.assertEquals("b", props.get("key2"));
		Assert.assertEquals("c", props.get("key3"));

		service.deleteProperties(token1.getToken(), Collections.asSet("key1", "key2"));
		props = service.getProperties(token1.getToken());
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("c", props.get("key3"));

		props = service.getProperties(token2.getToken());
		Assert.assertEquals(0, props.size());

		service.delete(token1.getToken(), true);
		service.delete(token2.getToken(), true);

		try {
			service.getProperties(token1.getToken());
			Assert.fail("not here");
		} catch (NotFoundException x) {
		}

		try {
			service.getProperties(token2.getToken());
			Assert.fail("not here");
		} catch (NotFoundException x) {
		}

	}

	@Test
	public void testPrune() {
		Assert.assertEquals(0, service.count());
		AuthenticationToken token = null;
		for (int i = 0; i < 10; i++) {
			token = service.create(new AuthenticationTokenCreate().withAccount("dennis" + i).withDomain(Domain.LOCAL).withAliasUid("aaaa."+i)
					.withClientIp("1.2.3." + i));
		}
		Assert.assertEquals(10, service.count());
		
		long time1 = token.getTimeoutAt()+1000;
		
		sleep3();
		
		for (int i = 0; i < 10; i++) {
			token = service.create(new AuthenticationTokenCreate().withAccount("dennis" + i).withDomain(Domain.LOCAL).withAliasUid("bbbb."+i)
					.withClientIp("1.2.3." + i));
		}
		Assert.assertEquals(20, service.count());
		long time2 = token.getTimeoutAt()+1000;
		
		service.prune(time1);
		Assert.assertEquals(10, service.count());
		service.prune(time2);
		Assert.assertEquals(0, service.count());
	}

}
