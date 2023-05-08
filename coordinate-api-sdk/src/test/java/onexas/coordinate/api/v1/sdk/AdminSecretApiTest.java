package onexas.coordinate.api.v1.sdk;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.ASecret;
import onexas.coordinate.api.v1.sdk.model.ASecretCreate;
import onexas.coordinate.api.v1.sdk.model.ASecretFilter;
import onexas.coordinate.api.v1.sdk.model.ASecretListPage;
import onexas.coordinate.api.v1.sdk.model.ASecretUpdate;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.service.SecretService;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AdminSecretApiTest extends CoordinateApiSDKTestBase {

	@Autowired
	SecretService secretService;
	
	@Test
	public void testNoPermission() {
		try {
			ApiClient client = getApiClientWithAuthCreate("someone","1234", "somerole");

			CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(client);
			try {
				api.listSecret(null);
				Assert.fail();
			} catch (ApiException x) {
				Assert.assertEquals(403, x.getCode());
			}

			client = getApiClientWithAuthCreate(new PrincipalPermission(onexas.coordinate.api.v1.admin.AdminSecretApi.API_PERMISSION_TARGET,
					onexas.coordinate.api.v1.admin.AdminSecretApi.ACTION_VIEW));
			api = new CoordinateAdminSecretApi(client);
			api.listSecret(null);

			try {
				ASecretCreate secretCreate = new ASecretCreate();
				secretCreate.setDescription("Dennis");
				secretCreate.setCode("dennis");
				secretCreate.setContent("abc@def.com");
				api.createSecret(secretCreate);
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
			ApiClient client = getApiClientWithAuthCreate("authsecret", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));

			CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(client);
			List<ASecret> list = api.listSecret(null).getItems();
			// the auth secret
			Assert.assertEquals(0, list.size());

			ASecretCreate secretCreate = new ASecretCreate();
			secretCreate.setDescription("Dennis");
			secretCreate.setCode("dennis");
			secretCreate.setContent("abc@def.com");

			ASecret secret1 = api.createSecret(secretCreate);
			String uid = secret1.getUid();
			Assert.assertNotNull(secret1.getUid());
			Assert.assertEquals(secretCreate.getCode(), secret1.getCode());
			Assert.assertEquals(secretCreate.getDescription(), secret1.getDescription());
			Assert.assertEquals(secretCreate.getContent(), secretService.getContent(secret1.getUid()));

			secret1 = api.getSecret(uid, false);
			Assert.assertEquals(uid, secret1.getUid());
			Assert.assertEquals(secretCreate.getCode(), secret1.getCode());
			Assert.assertEquals(secretCreate.getDescription(), secret1.getDescription());
			Assert.assertEquals(secretCreate.getContent(), secretService.getSecret(secret1.getCode()));

			// update
			ASecretUpdate update = new ASecretUpdate();
			update.setContent("xyz@g.f.h");
			update.setDescription("XYZ");
			ASecret secret2 = api.updateSecret(uid, update);
			Assert.assertEquals(uid, secret2.getUid());
			Assert.assertEquals(update.getDescription(), secret2.getDescription());
			Assert.assertEquals(update.getContent(), secretService.getSecret(secret2.getCode()));

			secret1 = api.getSecret(uid, true);
			Assert.assertEquals(uid, secret2.getUid());
			Assert.assertEquals(secret2.getCode(), secret1.getCode());
			Assert.assertEquals(secret2.getDescription(), secret1.getDescription());
			Assert.assertEquals(update.getContent(), secretService.getSecret(secret1.getCode()));
			
			
			ASecretUpdate update2 = new ASecretUpdate();
			update2.setDescription("ABC");
			secret2 = api.updateSecret(uid, update2);
			Assert.assertEquals(update2.getDescription(), secret2.getDescription());
			Assert.assertEquals(update.getContent(), secretService.getSecret(secret2.getCode()));

			secret1 = api.getSecret(uid, true);
			Assert.assertEquals(uid, secret2.getUid());
			Assert.assertEquals(secret2.getCode(), secret1.getCode());
			Assert.assertEquals(secret2.getDescription(), secret1.getDescription());
			Assert.assertEquals(update.getContent(), secretService.getSecret(secret1.getCode()));

			list = api.listSecret(null).getItems();
			Assert.assertEquals(1, list.size());

			secret1 = list.get(0);
			Assert.assertEquals(uid, secret2.getUid());
			Assert.assertEquals(secret2.getCode(), secret1.getCode());
			Assert.assertEquals(secret2.getDescription(), secret1.getDescription());

			api.deleteSecret(uid, true);

			secret1 = api.getSecret(uid, true);
			Assert.assertNull(secret1);

			try {
				api.getSecret(uid, false);
				Assert.fail();
			} catch (ApiException e) {
				Assert.assertEquals(404, e.getCode());
			}

			list = api.listSecret(null).getItems();
			Assert.assertEquals(0, list.size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

	@Test
	public void testFilterPage() {
		try {
			ApiClient client = getApiClientWithAuthCreate("authsecret", Strings.randomPassword(10), "authrole",
					new PrincipalPermission("*", "*"));

			CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(client);

			ASecretListPage page = api.listSecret(null);
			Assert.assertEquals(0, page.getItems().size());

			for (int i = 0; i < 100; i++) {
				ASecretCreate secretCreate = new ASecretCreate();
				String p = (i <= 9 ? "0" : "") + i;
				switch (i % 2) {
				case 0:
					secretCreate.setCode("dennis" + p);
					secretCreate.setContent("dennis" + p + "@abc.com");
					secretCreate.setDescription("Dennis" + p);
					break;
				case 1:
					secretCreate.setCode("alice" + p);
					secretCreate.setContent("alice" + p + "@def.com");
					secretCreate.setDescription("Alice" + p);
					break;
				}
				api.createSecret(secretCreate);
			}

			// test filter, page
			page = api.listSecret(new ASecretFilter().code("dennis00"));
			Assert.assertEquals(1, page.getItems().size());
			Assert.assertEquals((Long)1L, page.getItemTotal());
			ASecret secret00 = page.getItems().get(0);
			Assert.assertEquals("dennis00", secret00.getCode());
			Assert.assertEquals("dennis00@abc.com", secretService.getContent(secret00.getUid()));


			page = api.listSecret(new ASecretFilter().code("alice").strContaining(true).strIgnoreCase(true)
					.pageIndex(1).pageSize(10));
			Assert.assertEquals(10, page.getItems().size());
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(1, page.getPageIndex().intValue());
			Assert.assertEquals(5, page.getPageTotal().intValue());

			ASecret secret21 = page.getItems().get(0);
			Assert.assertEquals("alice21", secret21.getCode());
			Assert.assertEquals("alice21@def.com", secretService.getContent(secret21.getUid()));

			ASecret secret39 = page.getItems().get(9);
			Assert.assertEquals("alice39", secret39.getCode());
			Assert.assertEquals("alice39@def.com", secretService.getContent(secret39.getUid()));

			page = api.listSecret(new ASecretFilter().code("alice").strContaining(true).strIgnoreCase(true)
					.pageIndex(3).pageSize(15));
			Assert.assertEquals((Long)50L, page.getItemTotal());
			Assert.assertEquals(5, page.getItems().size());
			Assert.assertEquals(3, page.getPageIndex().intValue());
			Assert.assertEquals(4, page.getPageTotal().intValue());

			ASecret secret91 = page.getItems().get(0);
			Assert.assertEquals("alice91", secret91.getCode());
			Assert.assertEquals("alice91@def.com", secretService.getContent(secret91.getUid()));

			ASecret secret99 = page.getItems().get(4);
			Assert.assertEquals("alice99", secret99.getCode());
			Assert.assertEquals("alice99@def.com", secretService.getContent(secret99.getUid()));

			for (ASecret secret : api.listSecret(null).getItems()) {
				api.deleteSecret(secret.getUid(), false);
			}
			page = api.listSecret(null);
			Assert.assertEquals(0, page.getItems().size());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
}
