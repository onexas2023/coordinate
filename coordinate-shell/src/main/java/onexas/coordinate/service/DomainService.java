package onexas.coordinate.service;

import java.util.List;

import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainCreate;
import onexas.coordinate.model.DomainUpdate;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface DomainService {
	
	public List<Domain> list();
	
	public Domain get(String code);
	
	public Domain find(String code);
	
	public Domain create(DomainCreate domainCreate);
	
	public Domain update(String domainCode, DomainUpdate domainUpdate);
	
	public void delete(String domainCode, boolean quiet);

	public DomainConfig getConfig(String domainCode);
	
	public String getConfigYaml(String domainCode);
	
}
