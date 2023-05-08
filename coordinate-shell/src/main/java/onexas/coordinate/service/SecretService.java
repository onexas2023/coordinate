package onexas.coordinate.service;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.SecretProvider;
import onexas.coordinate.model.Secret;
import onexas.coordinate.model.SecretCreate;
import onexas.coordinate.model.SecretFilter;
import onexas.coordinate.model.SecretUpdate;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface SecretService extends SecretProvider{
		
	public ListPage<Secret> list(SecretFilter filter);
	
	public Secret get(String uid);
	
	public Secret find(String uid);

	public Secret create(SecretCreate secretCreate);
	
	public Secret update(String uid, SecretUpdate secretUpdate);

	public void delete(String uid, boolean quiet);

	public long count();
	
	public String getContent(String uid);
	

}
