package onexas.coordinate.service;

import java.util.Set;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationCreate;
import onexas.coordinate.model.OrganizationFilter;
import onexas.coordinate.model.OrganizationUpdate;
import onexas.coordinate.model.OrganizationUser;
import onexas.coordinate.model.OrganizationUserFilter;
import onexas.coordinate.model.OrganizationUserRelation;
import onexas.coordinate.model.UserOrganization;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface OrganizationService {
	
	public ListPage<Organization> list(OrganizationFilter filter);

	public Organization get(String uid);
	
	public Organization find(String uid);
	
	public Organization findByCode(String code);

	public Organization create(OrganizationCreate organizationCreate);
	
	public Organization update(String uid, OrganizationUpdate organizationUpdate);

	public void delete(String uid, boolean quiet);
	
	public UserOrganization findUserOrganization(String uid, String userUid);
	
	public ListPage<OrganizationUser> listUser(String uid, OrganizationUserFilter filter);
	public Organization addUsers(String uid, Set<OrganizationUserRelation> userRelations);
	public Organization removeUsers(String uid, Set<String> userUids);
	public Organization setUsers(String uid, Set<OrganizationUserRelation> userRelations);
}
