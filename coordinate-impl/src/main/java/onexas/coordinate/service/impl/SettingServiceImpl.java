package onexas.coordinate.service.impl;

import static onexas.coordinate.service.GlobalCacheEvictService.UNLESS_RESULT_NULL;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_SETTING;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.ServerSetting;
import onexas.coordinate.model.ServerSettingUpdate;
import onexas.coordinate.service.GlobalCacheEvictService;
import onexas.coordinate.service.SettingService;
import onexas.coordinate.service.impl.dao.PropertyEntityRepo;
import onexas.coordinate.service.impl.entity.PropertyEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "SettingServiceImpl")
public class SettingServiceImpl implements SettingService {

	private static final String SERVER_SETTING_PROP = "server-setting";

	private static Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Autowired
	PropertyEntityRepo propertyRepo;

	@Autowired
	GlobalCacheEvictService cacheEvictService;

//	@Value("${coordinate.server-setting.locale:#{T(java.util.Locale).getDefault()}}")
//	Locale locale;

	@Value("${coordinate.server-setting.admin-email:#{null}}")
	String adminEmail;

	@Value("${coordinate.server-setting.console-url:#{null}}")
	String consoleUrl;

	@Value("${coordinate.server-setting.api-base-url:#{null}}")
	String apiBaseUrl;

	@Value("${coordinate.server-setting.api-internal-base-url:#{null}}")
	String apiInternalBaseUrl;

	private ServerSetting getDefaultServerSetting() {
		ServerSetting setting = new ServerSetting();
		setting.setAdminEmail(adminEmail == null ? "admin@bar.foo.com" : adminEmail);
		setting.setConsoleUrl(consoleUrl == null ? "http://bar.foo.com:8080" : consoleUrl);
		setting.setApiBaseUrl(apiBaseUrl == null ? "http://bar.foo.com:8088" : apiBaseUrl);
		setting.setApiInternalBaseUrl(
				apiInternalBaseUrl == null ? "http://internal.bar.foo.com:8088" : apiInternalBaseUrl);
		return setting;
	}

	@Override
	@Cacheable(key="'server-setting'", cacheNames = CACHE_NAME_SETTING, unless = UNLESS_RESULT_NULL)
	public ServerSetting getServerSetting() {
		Optional<PropertyEntity> o = propertyRepo
				.findById(new PropertyEntity.PK(Strings.toUid(0), SERVER_SETTING_PROP));
		ServerSetting setting = getDefaultServerSetting();
		if (o.isPresent()) {
			try {
				ServerSetting s = Jsons.objectify(o.get().getValue(), ServerSetting.class);
				if (s != null) {
					if (s.getAdminEmail() != null) {
						setting.setAdminEmail(s.getAdminEmail());
					}
					if (s.getConsoleUrl() != null) {
						setting.setConsoleUrl(s.getConsoleUrl());
					}
					if (s.getApiBaseUrl() != null) {
						setting.setApiBaseUrl(s.getApiBaseUrl());
					}
					if (s.getApiInternalBaseUrl() != null) {
						setting.setApiInternalBaseUrl(s.getApiInternalBaseUrl());
					}
				}
			} catch (Exception x) {
				// eat, just in case
			}
		}
		return setting;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ServerSetting updateServerSetting(ServerSettingUpdate serverSettingUpdate) {
		ServerSetting setting = getServerSetting();
		if (serverSettingUpdate.getAdminEmail() != null
				&& !Objects.equal(serverSettingUpdate.getAdminEmail(), setting.getAdminEmail())) {
			setting.setAdminEmail(serverSettingUpdate.getAdminEmail());
			logger.info("Set server admin email to {}", setting.getAdminEmail());
		}
		if (serverSettingUpdate.getConsoleUrl() != null
				&& !Objects.equal(serverSettingUpdate.getConsoleUrl(), setting.getConsoleUrl())) {
			setting.setConsoleUrl(serverSettingUpdate.getConsoleUrl());
			logger.info("Set console url to {}", setting.getConsoleUrl());
		}
		if (serverSettingUpdate.getApiBaseUrl() != null
				&& !Objects.equal(serverSettingUpdate.getApiBaseUrl(), setting.getApiBaseUrl())) {
			setting.setApiBaseUrl(serverSettingUpdate.getApiBaseUrl());
			logger.info("Set api base url to {}", setting.getApiBaseUrl());
		}
		if (serverSettingUpdate.getApiInternalBaseUrl() != null
				&& !Objects.equal(serverSettingUpdate.getApiInternalBaseUrl(), setting.getApiInternalBaseUrl())) {
			setting.setApiInternalBaseUrl(serverSettingUpdate.getApiInternalBaseUrl());
			logger.info("Set api internal base url to {}", setting.getApiInternalBaseUrl());
		}

		Optional<PropertyEntity> o = propertyRepo
				.findById(new PropertyEntity.PK(Strings.toUid(0), SERVER_SETTING_PROP));
		if (o.isPresent()) {
			o.get().setValue(Jsons.jsonify(setting));
			propertyRepo.flush();
		} else {
			PropertyEntity e = new PropertyEntity();
			e.setObjUid(Strings.toUid(0));
			e.setName(SERVER_SETTING_PROP);
			e.setValue(Jsons.jsonify(setting));
			propertyRepo.save(e);
		}
		cacheEvictService.evict("server-setting",CACHE_NAME_SETTING);
		return setting;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public synchronized ServerSetting resetServerSetting() {
		Optional<PropertyEntity> o = propertyRepo
				.findById(new PropertyEntity.PK(Strings.toUid(0), SERVER_SETTING_PROP));
		if (o.isPresent()) {
			propertyRepo.delete(o.get());
		}
		cacheEvictService.evict("server-setting",CACHE_NAME_SETTING);
		return getDefaultServerSetting();
	}

}