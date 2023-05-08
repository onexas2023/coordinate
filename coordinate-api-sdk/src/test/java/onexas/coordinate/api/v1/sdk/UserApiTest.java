package onexas.coordinate.api.v1.sdk;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.UUser;
import onexas.coordinate.api.v1.sdk.model.UUserFilter;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.PrincipalPermission;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone", "1234", "somerole");

			CoordinateUserApi api = new CoordinateUserApi(client);
			try {
				api.listUser(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(
					new PrincipalPermission(onexas.coordinate.api.v1.UserApi.API_PERMISSION_TARGET,
							onexas.coordinate.api.v1.UserApi.ACTION_VIEW));
			api = new CoordinateUserApi(client);
			api.listUser(null);

		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testList() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authuser", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));

			CoordinateAdminUserApi adminApi = new CoordinateAdminUserApi(client);
			Assert.assertEquals(1, adminApi.listUser(null).getItems().size());

			CoordinateUserApi api = new CoordinateUserApi(client);
			List<UUser> list = api.listUser(null).getItems();
			// the auth user
			Assert.assertEquals(0, list.size());

			try {
				list = api.listUser(new UUserFilter()).getItems();
			} catch (ApiException x) {
				Assert.assertEquals(400, x.getCode());
			}

			AUserCreate userCreate = new AUserCreate().account("adennis1").displayName("Dennis1").password("1234");
			AUser user1 = adminApi.createUser(userCreate);

			userCreate = new AUserCreate().account("bdennis2").displayName("Dennis2").password("1234").disabled(true);
			AUser user2 = adminApi.createUser(userCreate);

			userCreate = new AUserCreate().account("cdennis3").displayName("Dennis3").password("1234");
			AUser user3 = adminApi.createUser(userCreate);

			list = api.listUser(new UUserFilter().criteria("dennis").strContaining(Boolean.TRUE)).getItems();
			Assert.assertEquals(2, list.size());
			UUser user = list.get(0);
			Assert.assertEquals(user1.getAliasUid(), user.getAliasUid());
			Assert.assertEquals(user1.getDisplayName(), user.getDisplayName());
			Assert.assertEquals(user1.getDomain(), user.getDomain());
			user = list.get(1);
			Assert.assertEquals(user3.getAliasUid(), user.getAliasUid());
			Assert.assertEquals(user3.getDisplayName(), user.getDisplayName());
			Assert.assertEquals(user3.getDomain(), user.getDomain());

			list = api.listUser(new UUserFilter().criteria("dennis3").strContaining(Boolean.TRUE)).getItems();
			Assert.assertEquals(1, list.size());
			user = list.get(0);
			Assert.assertEquals(user3.getAliasUid(), user.getAliasUid());
			Assert.assertEquals(user3.getDisplayName(), user.getDisplayName());
			Assert.assertEquals(user3.getDomain(), user.getDomain());

			adminApi.deleteUser(user1.getUid(), true);
			adminApi.deleteUser(user2.getUid(), true);
			adminApi.deleteUser(user3.getUid(), true);

			list = api.listUser(new UUserFilter().criteria("dennis").strContaining(Boolean.TRUE)).getItems();
			Assert.assertEquals(0, list.size());

			Assert.assertEquals(1, adminApi.listUser(null).getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
