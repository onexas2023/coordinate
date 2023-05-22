package onexas.coordinate.service;

import java.util.Map;
import java.util.Set;

import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.AuthenticationTokenCreate;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface AuthenticationTokenService {

	AuthenticationToken create(AuthenticationTokenCreate authCreate);

	AuthenticationToken find(String token);

	AuthenticationToken extend(String token);
	
	boolean shouldExtend(long timeoutAt);

	void delete(String token, boolean quiet);
	
	long count();

	public Map<String, String> getProperties(String token);

	public void setProperties(String token, Map<String, String> properties);

	public void setProperty(String token, String name, String value);

	public void deleteProperties(String token, Set<String> name);

	void prune(Long time);

}
