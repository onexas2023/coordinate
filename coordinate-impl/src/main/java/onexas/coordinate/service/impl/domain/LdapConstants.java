package onexas.coordinate.service.impl.domain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LdapConstants {
	
	public static final String CONFIG_SERVER = "server";
	public static final String CONFIG_PORT = "port";
	public static final int DEFAULT_PORT = 389;
	
	public static final String CONFIG_SSL = "ssl";
	public static final boolean DEFAULT_SSL = false;
	
	public static final String CONFIG_BIND_DN = "bind-dn";
	
	public static final String CONFIG_PASSWORD = "password";
	public static final String CONFIG_PASSWORD_SECRET = "password-secret";
	
	public static final String CONFIG_SEARCH_BASE_DN = "search-base-dn";
	
	public static final String CONFIG_AUTHENTICATOR_FILTER = "authenticator-filter";
	public static final String DEFAULT_AUTHENTICATOR_FILTER = "(&(objectClass=person)(uid={}))";
	
	public static final String CONFIG_USER_ACCOUNT_ATTRIBUTE = "user-account-attr";
	public static final String DEFAULT_USER_ACCOUNT_ATTRIBUTE = "uid";
	
	public static final String CONFIG_USER_DISPLAY_NAME_ATTRIBUTE = "user-display-name-attr";
	public static final String DEFAULT_USER_DISPLAY_NAME_ATTRIBUTE = "cn";
	
	public static final String CONFIG_USER_EMAIL_ATTRIBUTE = "user-email-attr";
	public static final String DEFAULT_USER_EMAIL_ATTRIBUTE = "mail";
	
	public static final String CONFIG_SEARCH_CRITERIA_FILTER = "search-criteria-filter";
	public static final String DEFAULT_SEARCH_CRITERIA_FILTER = "(&(objectClass=person)(|(cn=*{}*)(uid=*{}*)))";
	
	public static final String CONFIG_SEARCH_NO_CRITERIA_FILTER = "search-no-criteria-filter";
	public static final String DEFAULT_SEARCH_NO_CRITERIA_FILTER = "(&(objectClass=person))";
}
