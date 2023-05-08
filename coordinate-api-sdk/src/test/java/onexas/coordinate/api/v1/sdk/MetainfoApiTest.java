package onexas.coordinate.api.v1.sdk;

import static onexas.coordinate.model.Domain.LOCAL;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import onexas.api.invoker.ApiClient;
import onexas.api.invoker.ApiException;
import onexas.coordinate.api.test.CoordinateApiSDKTestBase;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.api.v1.sdk.model.UMetainfo;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MetainfoApiTest extends CoordinateApiSDKTestBase {

	@Test
	public void testDomain() {
		try {
			ApiClient client = getApiClient();

			CoordinateMetainfoApi api = new CoordinateMetainfoApi(client);

			List<UDomain> list = api.listDomain();
			Assert.assertEquals(1, list.size());
			UDomain domain = list.get(0);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("Local", domain.getName());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}
	@Test
	public void testPublicMetainfo() {
		try {
			ApiClient client = getApiClient();

			CoordinateMetainfoApi api = new CoordinateMetainfoApi(client);

			UMetainfo Metainfo = api.getMetainfo();
			List<UDomain> domains = Metainfo.getDomains();
			Assert.assertEquals(1, domains.size());
			UDomain domain = domains.get(0);
			Assert.assertEquals(LOCAL, domain.getCode());
			Assert.assertEquals("Local", domain.getName());
		} catch (ApiException x) {
			throw new RuntimeException(Strings.format("{}:{}", x.getCode(), x.getResponseBody()), x);
		}
	}

}
