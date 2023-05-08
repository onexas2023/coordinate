package onexas.coordinate.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserFilter;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.model.UserOrganizationRelation;
import onexas.coordinate.model.UserUpdate;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface UserService {
	
	public static final String PROP_CAT_PROFILE = "profile";
	public static final String PROP_CAT_SYSTEM = "system";
	
	public ListPage<User> list(UserFilter filter);
	
	public ListPage<User> list(UserFilter filter, Boolean disabled);

	public User get(String uid);
	
	public User find(String uid);
	
	public User findByAccountDomain(String account, String domainCode);
	
	public User findByAliasUid(String aliasUid);

	public User create(UserCreate userCreate);
	
	public User update(String uid, UserUpdate userUpdate);

	public void delete(String uid, boolean quiet);

	public boolean verifyPassword(String uid, String password);
	public boolean verifyPasswordByAccountDomain(String account, String domainCode, String password);
	
	
	public Map<String, String> getProperties(String uid, String category);
	public void setProperties(String uid, Map<String, String> properties, String category);
	public void setProperty(String uid, String name, String value, String category);
	public void deleteProperties(String uid, Set<String> name);
	
	public List<Role> listRole(String uid);
	public User addRoles(String uid, Set<String> roleUids);
	public User removeRoles(String uid, Set<String> roleUid);
	public User setRoles(String uid, Set<String> roleUids);
	
	
	public List<UserOrganization> listOrganization(String uid);
	public User addOrganizations(String uid, Set<UserOrganizationRelation> organizationRelations);
	public User removeOrganizations(String uid, Set<String> organizationUid);
	public User setOrganizations(String uid, Set<UserOrganizationRelation> organizationRelations);

	public long count();

	public User createByDomainUser(DomainUser domainUser);
	public boolean verifyDomainUserIdentity(DomainUser domainUser);

}
