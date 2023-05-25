package onexas.coordinate.service.impl;

import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Constants {
	
	public static final String CACHE_NAME_AUTHTOKEN = Env.NS_CACHE + "authtoken";

	public static final String CACHE_NAME_USER = Env.NS_CACHE + "user";
	public static final String CACHE_NAME_USER_BYACCOUNTDOMAIN = CACHE_NAME_USER + ".byAccountDomain";
	public static final String CACHE_NAME_USER_BYALIASUID = CACHE_NAME_USER + ".byAliasUid";
	public static final String CACHE_NAME_USER_ROLES = CACHE_NAME_USER + ".roles";
	
	public static final String CACHE_NAME_DOMAIN = Env.NS_CACHE + "domain";
	public static final String CACHE_NAME_DOMAIN_CONFIG = CACHE_NAME_DOMAIN + ".config";
	public static final String CACHE_NAME_DOMAIN_CONFIGYAML = CACHE_NAME_DOMAIN + ".configYaml";
	
	public static final String CACHE_NAME_ROLE = Env.NS_CACHE + "role";
	public static final String CACHE_NAME_ROLE_BYCODE = CACHE_NAME_ROLE + ".byCode";
	public static final String CACHE_NAME_ROLE_PERMISSIONS = CACHE_NAME_ROLE + ".permissions";
	
	public static final String CACHE_NAME_SETTING = Env.NS_CACHE + "setting";
}
