package onexas.coordinate.api.v1.sdk;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.service.impl.domain.MockDomainProviderFactory;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AuthenticationApiMockDomainTest extends CoordinateApiSDKTestBase {

	@Autowired
	UserService userService;
	
	@Autowired
	DomainService domainService;
	
	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	@Test
	public void testMockDomainProvider() {

		ListPage<User> page = userService.list(null);
		Assert.assertEquals(0, page.size());

		try {

			CoordinateAuthApi authApi = new CoordinateAuthApi(getApiClient());

			Authentication auth;

			try {
				auth = authApi.authenticate(
						new AuthenticationRequest().account("dennis").domain("mock-domain").password("1234"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			String domainCode = "mock1";
			MockDomainProviderFactory factory = new MockDomainProviderFactory(domainCode, "abcd", "dennis", "Dennis",
					"dennis@abc.com", "1234");

			dpfReg.registerFactory(factory);

			Domain domain = domainService.create(new DomainCreate().withCode(domainCode).withName("Mock Domain").withProvider(factory.getProviderCode())
					.withConfigYaml(factory.getConfigTemplate().toYaml()));

			try {
				auth = authApi.authenticate(
						new AuthenticationRequest().account("dennis").domain(domainCode).password("5678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			page = userService.list(null);
			Assert.assertEquals(0, page.size());

			auth = authApi.authenticate(
				new AuthenticationRequest().account("dennis").domain(domainCode).password("1234"));
			Assert.assertNotNull(auth.getToken());

			page = userService.list(null);
			Assert.assertEquals(1, page.size());

			User user = page.getItems().get(0);

			Assert.assertEquals("dennis", user.getAccount());
			Assert.assertEquals(domainCode, user.getDomain());
			Assert.assertEquals("Dennis", user.getDisplayName());
			Assert.assertEquals("dennis@abc.com", user.getEmail());

			// login test again
			try {
				auth = authApi.authenticate(
						new AuthenticationRequest().account("dennis").domain(domainCode).password("5678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			page = userService.list(null);
			Assert.assertEquals(1, page.size());

			auth = authApi.authenticate(
				new AuthenticationRequest().account("dennis").domain(domainCode).password("1234"));
			Assert.assertNotNull(auth.getToken());

			page = userService.list(null);
			Assert.assertEquals(1, page.size());

			userService.delete(user.getUid(), false);

			page = userService.list(null);
			Assert.assertEquals(0, page.size());

			try {
				auth = authApi.authenticate(
						new AuthenticationRequest().account("dennis").domain(domainCode).password("5678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			page = userService.list(null);
			Assert.assertEquals(0, page.size());
			
			domainService.delete(domain.getCode(), false);
			
			dpfReg.unregisterFactory(factory.getProviderCode());
			
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody(), x));
		}
	}
}
