package onexas.coordinate.service.domain;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.DomainUserFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface DomainUserFinder {

	ListPage<DomainUser> list(DomainUserFilter filter);
	
	DomainUser get(String identity);
	
	DomainUser find(String identity);

	DomainUser findByAccount(String account);
	
}