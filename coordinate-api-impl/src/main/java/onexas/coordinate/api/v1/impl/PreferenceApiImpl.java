package onexas.coordinate.api.v1.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.PreferenceApi;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "PreferenceApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantAuthentication
public class PreferenceApiImpl extends ApiImplBase implements PreferenceApi {

	public static final String CATEGORY = "user-preference";
	public static final String PREFIX = "preference-";

	@Autowired
	UserService userService;

	@Autowired
	AuthenticationTokenService authTokenService;

	@Autowired
	RequestContext reqContext;

	public PreferenceApiImpl() {
		super(PreferenceApi.API_NAME, V1, PreferenceApi.API_URI);
	}

	@Override
	public Map<String, String> getPreferences() {
		String userUid = reqContext.grantUserUid();

		Map<String, String> properties = userService.getProperties(userUid, CATEGORY);
		properties = trimKey(properties);
		return properties;
	}

	static public Map<String, String> trimKey(Map<String, String> properties) {
		properties = properties.entrySet().stream().filter((e) -> e.getKey().startsWith(PREFIX))
				.collect(Collectors.toMap((e) -> e.getKey().substring(PREFIX.length()), Map.Entry::getValue));
		return properties;
	}

	static public Map<String, String> appendKey(Map<String, String> properties) {
		properties = properties.entrySet().stream()
				.collect(Collectors.toMap((e) -> PREFIX + e.getKey(), Map.Entry::getValue));
		return properties;
	}

	static public Set<String> appendKey(Set<String> properties) {
		properties = properties.stream().map((e) -> PREFIX + e).collect(Collectors.toSet());
		return properties;
	}

	@Override
	@GrantPermissions(@GrantPermission(target = PreferenceApi.API_PERMISSION_TARGET, action = {
			PreferenceApi.ACTION_MODIFY }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Map<String, String> updatePreferences(Map<String, String> preferenceUpdate) {
		String userUid = reqContext.grantUserUid();

		userService.setProperties(userUid, appendKey(preferenceUpdate), CATEGORY);

		Map<String, String> properties = userService.getProperties(userUid, CATEGORY);
		properties = trimKey(properties);
		return properties;
	}

	@Override
	@GrantPermissions(@GrantPermission(target = PreferenceApi.API_PERMISSION_TARGET, action = {
			PreferenceApi.ACTION_MODIFY }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public String updatePreference(String key, String value) {
		String userUid = reqContext.grantUserUid();

		userService.setProperty(userUid, PREFIX + key, value, CATEGORY);
		return value;
	}

	@Override
	@GrantPermissions(@GrantPermission(target = PreferenceApi.API_PERMISSION_TARGET, action = {
			PreferenceApi.ACTION_MODIFY }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Map<String, String> resetPreferences(Map<String, String> preferenceReset) {
		String userUid = reqContext.grantUserUid();

		Set<String> toRemove = new HashSet<>();
		Map<String, String> properties = userService.getProperties(userUid, CATEGORY);

		properties = trimKey(properties);

		for (Entry<String, String> e : properties.entrySet()) {
			if (!preferenceReset.containsKey(e.getKey())) {
				toRemove.add(e.getKey());
			}
		}
		
		preferenceReset = appendKey(preferenceReset);
		userService.setProperties(userUid, preferenceReset, CATEGORY);
		
		if (toRemove.size() > 0) {
			toRemove = appendKey(toRemove);
			userService.deleteProperties(userUid, toRemove);
		}

		properties = userService.getProperties(userUid, CATEGORY);
		properties = trimKey(properties);
		return properties;
	}

	@Override
	public String findPreference(String key) {
		String userUid = reqContext.grantUserUid();
		String preference = userService.findProperty(userUid, PREFIX + key);
		return preference;
	}

}