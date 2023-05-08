package onexas.coordinate.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.SecretProvider;
import onexas.coordinate.model.Secret;
import onexas.coordinate.model.SecretCreate;
import onexas.coordinate.model.SecretFilter;
import onexas.coordinate.model.SecretUpdate;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretServiceTest extends CoordinateImplTestBase {

	@Autowired
	SecretService service;
	
	

	@Test
	public void testSimple() {
		
		SecretProvider provider = AppContext.bean(SecretProvider.class);
		
		ListPage<Secret> page = service.list(null);
		Assert.assertEquals(0, page.size());

		SecretCreate secretCreate = new SecretCreate();
		secretCreate.setCode("dennis");
		secretCreate.setDescription("Dennix");
		secretCreate.setContent("");

		Secret secret1 = service.create(secretCreate);
		String uid = secret1.getUid();
		Assert.assertNotNull(secret1.getUid());
		Assert.assertEquals(secretCreate.getCode(), secret1.getCode());
		Assert.assertEquals(secretCreate.getDescription(), secret1.getDescription());
		Assert.assertNotNull(secret1.getFingerprint());
		Assert.assertEquals(secretCreate.getContent(), service.getContent(secret1.getUid()));
		Assert.assertEquals(secretCreate.getContent(), service.getSecret(secret1.getCode()));
		Assert.assertEquals(secretCreate.getContent(), provider.getSecret(secret1.getCode()));


		secret1 = service.get(uid);
		Assert.assertEquals(uid, secret1.getUid());
		Assert.assertEquals(secretCreate.getCode(), secret1.getCode());
		Assert.assertEquals(secretCreate.getDescription(), secret1.getDescription());
		Assert.assertNotNull(secret1.getFingerprint());
		Assert.assertEquals(secretCreate.getContent(), service.getContent(secret1.getUid()));
		Assert.assertEquals(secretCreate.getContent(), service.getSecret(secret1.getCode()));
		Assert.assertEquals(secretCreate.getContent(), provider.getSecret(secret1.getCode()));

		
		
		// update
		SecretUpdate update = new SecretUpdate();
		update.setDescription("Dennis");
		update.setContent(" xyz@g.f.h ");
		
		Secret secret2 = service.update(uid, update);

		Assert.assertEquals(secret1.getUid(), secret2.getUid());
		Assert.assertEquals(secret1.getCode(), secret2.getCode());
		Assert.assertNotNull(secret2.getFingerprint());
		Assert.assertEquals(update.getDescription(), secret2.getDescription());
		Assert.assertEquals(update.getContent(), service.getContent(secret2.getUid()));
		Assert.assertEquals(update.getContent(), service.getSecret(secret2.getCode()));
		Assert.assertEquals(update.getContent(), provider.getSecret(secret2.getCode()));


		secret1 = service.find(uid);
		Assert.assertEquals(uid, secret2.getUid());
		Assert.assertEquals(secret2.getCode(), secret1.getCode());
		Assert.assertEquals(secret2.getDescription(), secret1.getDescription());
		Assert.assertNotNull(secret1.getFingerprint());

		page = service.list(null);
		Assert.assertEquals(1, page.size());

		secret1 = page.getItems().get(0);
		Assert.assertEquals(uid, secret2.getUid());
		Assert.assertEquals(secret2.getCode(), secret1.getCode());
		Assert.assertEquals(secret2.getDescription(), secret1.getDescription());
		

		service.delete(uid, true);

		secret1 = service.find(uid);
		Assert.assertNull(secret1);

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
		ListPage<Secret> page = service.list(null);
		Assert.assertEquals(0, page.size());

		for (int i = 0; i < 100; i++) {
			SecretCreate secretCreate = new SecretCreate();
			String p = (i <= 9 ? "0" : "") + i;
			switch (i % 2) {
			case 0:
				secretCreate.setCode("dennis" + p);
				secretCreate.setDescription("Dennis");
				secretCreate.setContent("dennis" + p + "@abc.com");
				break;
			case 1:
				secretCreate.setCode("alice" + p);
				secretCreate.setDescription("Alice");
				secretCreate.setContent("alice" + p + "@def.com");
				break;
			}
			service.create(secretCreate);
		}

		// test filter, page
		page = service.list(new SecretFilter().withCode("dennis00"));
		Assert.assertEquals(1, page.size());
		Secret secret00 = page.getItems().get(0);
		Assert.assertEquals("dennis00", secret00.getCode());
		Assert.assertEquals("dennis00@abc.com", service.getContent(secret00.getUid()));

		page = service.list(new SecretFilter().withCode("alice").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(1).withPageSize(10));
		Assert.assertEquals(10, page.size());
		Assert.assertEquals(1, page.getPageIndex().intValue());
		Assert.assertEquals(10, page.getPageSize().intValue());
		Assert.assertEquals(5, page.getPageTotal().intValue());

		Secret secret21 = page.getItems().get(0);
		Assert.assertEquals("alice21", secret21.getCode());
		Assert.assertEquals("alice21@def.com", service.getSecret(secret21.getCode()));

		Secret secret39 = page.getItems().get(9);
		Assert.assertEquals("alice39", secret39.getCode());
		Assert.assertEquals("alice39@def.com", service.getSecret(secret39.getCode()));

		page = service.list(new SecretFilter().withCode("alice").withStrContaining(true).withStrIgnoreCase(true)
				.withPageIndex(3).withPageSize(15));
		Assert.assertEquals(5, page.size());
		Assert.assertEquals(3, page.getPageIndex().intValue());
		Assert.assertEquals(15, page.getPageSize().intValue());
		Assert.assertEquals(4, page.getPageTotal().intValue());

		Secret secret91 = page.getItems().get(0);
		Assert.assertEquals("alice91", secret91.getCode());
		Assert.assertEquals("alice91@def.com", service.getSecret(secret91.getCode()));

		Secret secret99 = page.getItems().get(4);
		Assert.assertEquals("alice99", secret99.getCode());
		Assert.assertEquals("alice99@def.com", service.getSecret(secret99.getCode()));

		for (Secret secret : service.list(null).getItems()) {
			service.delete(secret.getUid(), false);
		}
		page = service.list(null);
		Assert.assertEquals(0, page.size());

	}
}
