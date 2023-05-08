package onexas.coordinate.api.v1.sdk;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.AuthenticationRequest;
import onexas.coordinate.api.v1.sdk.model.UPrincipalPermission;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserUpdate;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AuthenticationApiTest extends CoordinateApiSDKTestBase {

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Test
	public void testSimple() {
		try {
			ApiClient client = getApiClient();

			CoordinateAuthApi api = new CoordinateAuthApi(client);
			try {
				api.authenticate(new AuthenticationRequest());
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(400, x.getCode());
			}
			try {
				api.authenticate(new AuthenticationRequest().account("dennis").password("1234"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}
			try {
				api.authenticate(new AuthenticationRequest().account("dennis").password("1234").domain("xyz.com"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			try {
				api.authenticate(new AuthenticationRequest().token("12345678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}

			User user1 = userService.create(new UserCreate().withAccount("dennis").withPassword("1234")
					.withDomain(Domain.LOCAL).withDisplayName("Dennis"));
			User user2 = userService.create(new UserCreate().withAccount("alice").withPassword("5678")
					.withDomain(Domain.LOCAL).withDisplayName("Alice").withDisabled(true));

			Authentication auth1 = api.authenticate(new AuthenticationRequest().account("dennis").password("1234"));
			Assert.assertNotNull(auth1.getToken());
			Assert.assertEquals(user1.getDisplayName(), auth1.getDisplayName());
			Assert.assertEquals(0, auth1.getPermissions().size());

			auth1 = api.authenticate(new AuthenticationRequest().token(auth1.getToken()));
			Assert.assertNotNull(auth1.getToken());
			Assert.assertEquals(user1.getDisplayName(), auth1.getDisplayName());
			Assert.assertEquals(0, auth1.getPermissions().size());
			Assert.assertEquals(user1.getAliasUid(), auth1.getAliasUid());

			try {
				api.authenticate(new AuthenticationRequest().account("alice").password("5678"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			userService.update(user1.getUid(), new UserUpdate().withDisabled(true));
			userService.update(user2.getUid(), new UserUpdate().withDisabled(false));

			Authentication auth2 = api.authenticate(new AuthenticationRequest().account("alice").password("5678"));
			Assert.assertNotNull(auth2.getToken());
			Assert.assertEquals(user2.getDisplayName(), auth2.getDisplayName());
			Assert.assertEquals(0, auth2.getPermissions().size());
			Assert.assertEquals(user2.getAliasUid(), auth2.getAliasUid());

			try {
				api.authenticate(new AuthenticationRequest().account("dennis").password("1234"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}
			// token was asyncally delete after user disabled
			try {
				api.authenticate(new AuthenticationRequest().token(auth1.getToken()));
				Assert.fail();
			} catch (ApiException x) {
				//depends on delete or not asyncally
				Assert.assertTrue(401 == x.getCode() || 403 == x.getCode());
			}

			userService.update(user1.getUid(), new UserUpdate().withDisabled(false));

			// role permission
			Role admins = roleService.create(new RoleCreate().withCode("adms").withName("Admins"));
			Role users1 = roleService.create(new RoleCreate().withCode("usr1").withName("Users1"));
			Role users2 = roleService.create(new RoleCreate().withCode("usr2").withName("Users2"));
			permissionService.create(admins.getUid(), "*", "*", null);
			permissionService.create(users1.getUid(), "fn1", "view", null);
			permissionService.create(users1.getUid(), "fn2", "edit", null);
			permissionService.create(users2.getUid(), "fn3", "*", null);
			userService.addRoles(user1.getUid(), Collections.asSet(admins.getUid()));
			userService.addRoles(user2.getUid(), Collections.asSet(users1.getUid(), users2.getUid()));

			// re auth auth1
			auth1 = api.authenticate(new AuthenticationRequest().account("dennis").password("1234"));
			auth2 = api.authenticate(new AuthenticationRequest().token(auth2.getToken()));

			List<UPrincipalPermission> permissions1 = auth1.getPermissions();
			Assert.assertEquals(1, permissions1.size());
			UPrincipalPermission permission = permissions1.get(0);
			Assert.assertEquals("*", permission.getTarget());
			Assert.assertEquals("*", permission.getAction());

			List<UPrincipalPermission> permissions2 = auth2.getPermissions();
			Assert.assertEquals(3, permissions2.size());
			permission = permissions2.get(0);
			Assert.assertEquals("fn1", permission.getTarget());
			Assert.assertEquals("view", permission.getAction());

			permission = permissions2.get(1);
			Assert.assertEquals("fn2", permission.getTarget());
			Assert.assertEquals("edit", permission.getAction());

			permission = permissions2.get(2);
			Assert.assertEquals("fn3", permission.getTarget());
			Assert.assertEquals("*", permission.getAction());

			userService.delete(user1.getUid(), false);
			userService.delete(user2.getUid(), false);
			roleService.delete(admins.getUid(), false);
			roleService.delete(users1.getUid(), false);
			roleService.delete(users2.getUid(), false);

			try {
				api.authenticate(new AuthenticationRequest().account("dennis").password("1234"));
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(401, x.getCode());
			}
			try {
				api.authenticate(new AuthenticationRequest().token(auth1.getToken()));
				Assert.fail();
			} catch (ApiException x) {
				//depends on delete or not asyncally
				Assert.assertTrue(401 == x.getCode() || 403 == x.getCode());
			}
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
