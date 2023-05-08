package onexas.coordinate.service;

import java.util.List;

import onexas.coordinate.model.Permission;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface PermissionService {

	public List<Permission> listByPrincipal(String principal);

	public List<Permission> listByTarget(String target);

	public Permission get(String uid);

	public Permission find(String uid);

	public Permission create(String principal, String target, String action, String remark);

	public void delete(String uid, boolean quiet);

	public void deleteByPricipal(String principal);

	public void deleteByTarget(String target);

	public boolean isPermitted(String principal, String target, String action);
}
