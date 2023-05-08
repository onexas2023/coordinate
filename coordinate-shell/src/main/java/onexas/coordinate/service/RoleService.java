package onexas.coordinate.service;

import java.util.List;
import java.util.Set;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.RoleFilter;
import onexas.coordinate.model.RoleUpdate;
import onexas.coordinate.model.RoleUserFilter;
import onexas.coordinate.model.User;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface RoleService {
	
	public ListPage<Role> list(RoleFilter filter);

	public Role get(String uid);
	
	public Role find(String uid);
	
	public Role findByCode(String code);

	public Role create(RoleCreate roleCreate);
	
	public Role update(String uid, RoleUpdate roleUpdate);

	public void delete(String uid, boolean quiet);
	
	
	public ListPage<User> listUser(String uid, RoleUserFilter filter);
	public Role addUsers(String uid, Set<String> userUids);
	public Role removeUsers(String uid, Set<String> userUids);
	public Role setUsers(String uid, Set<String> userUids);

	public long count();
	
	public List<PrincipalPermission> listPermission(String uid);
	public Role addPermissions(String uid, Set<PrincipalPermission> rolePermissionList);
	public Role setPermissions(String uid, Set<PrincipalPermission> rolePermissionList);
	public Role removePermissions(String uid, Set<PrincipalPermission> rolePermissionList);
}
