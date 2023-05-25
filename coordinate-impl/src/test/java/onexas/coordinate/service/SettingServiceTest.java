package onexas.coordinate.service;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.ServerSetting;
import onexas.coordinate.model.ServerSettingUpdate;
import onexas.coordinate.service.test.CoordinateImplTestBase;


/**
 * 
 * @author Dennis Chen
 *
 */
@ActiveProfiles(profiles = Env.PROFILE_JOB_NODE)
public class SettingServiceTest extends CoordinateImplTestBase {

	@Autowired
	SettingService settingService;


	@Test
	public void testServerSetting() {
		
		String defaultAdminEmail = "admin@bar.foo.com";
		String defaultConsoleUrl = "http://bar.foo.com:8080";
		String defaultApiBaseUrl = "http://bar.foo.com:8088";
		String defaultApiInternalBaseUrl = "http://internal.bar.foo.com:8088";
		
		String adminEmail1 = "dennis@somewhere";
		String consoleUrl = "http://xxx.foo.com:8080";
		String apiBaseUrl = "http://xxx.foo.com:8088";
		String apiInternalBaseUrl = "http://internal.xxx.foo.com:8088";
		
		ServerSetting setting = settingService.getServerSetting();
		System.out.println("1>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());
		Assert.assertEquals(defaultConsoleUrl, setting.getConsoleUrl());
		Assert.assertEquals(defaultApiBaseUrl, setting.getApiBaseUrl());
		Assert.assertEquals(defaultApiInternalBaseUrl, setting.getApiInternalBaseUrl());
		
		setting = settingService.updateServerSetting(new ServerSettingUpdate().withAdminEmail(adminEmail1)
				.withConsoleUrl(consoleUrl).withApiBaseUrl(apiBaseUrl).withApiInternalBaseUrl(apiInternalBaseUrl));
		System.out.println("2>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(adminEmail1, setting.getAdminEmail());
		Assert.assertEquals(consoleUrl, setting.getConsoleUrl());
		Assert.assertEquals(apiBaseUrl, setting.getApiBaseUrl());
		Assert.assertEquals(apiInternalBaseUrl, setting.getApiInternalBaseUrl());
		
		setting = settingService.getServerSetting();
		System.out.println("2.5>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(adminEmail1, setting.getAdminEmail());
		Assert.assertEquals(adminEmail1, setting.getAdminEmail());
		Assert.assertEquals(consoleUrl, setting.getConsoleUrl());
		Assert.assertEquals(apiBaseUrl, setting.getApiBaseUrl());
		Assert.assertEquals(apiInternalBaseUrl, setting.getApiInternalBaseUrl());
		
		
		setting = settingService.getServerSetting();
		System.out.println("3>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(adminEmail1, setting.getAdminEmail());
		
		setting = settingService.resetServerSetting();
		System.out.println("4>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());
		
		setting = settingService.getServerSetting();
		System.out.println("5>>>>>>>>>>>>>>>>>"+Jsons.jsonify(setting));
		Assert.assertEquals(defaultAdminEmail, setting.getAdminEmail());
		Assert.assertEquals(defaultConsoleUrl, setting.getConsoleUrl());
		Assert.assertEquals(defaultApiBaseUrl, setting.getApiBaseUrl());
		Assert.assertEquals(defaultApiInternalBaseUrl, setting.getApiInternalBaseUrl());
	}

	
}
