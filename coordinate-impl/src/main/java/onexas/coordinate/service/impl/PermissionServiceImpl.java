package onexas.coordinate.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Permission;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.impl.dao.PermissionEntityRepo;
import onexas.coordinate.service.impl.entity.PermissionEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "PermissionServiceImpl")
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	PermissionEntityRepo permissionRepo;

	@Override
	public List<Permission> listByPrincipal(String principal) {
		if (Permission.ANY_RPINCIPAL.equals(principal)) {
			throw new BadArgumentException("doesn't support list by any principal");
		}
		List<Permission> list = new LinkedList<>();
		for (PermissionEntity e : permissionRepo.findAllByPrincipal(principal)) {
			list.add(Jsons.transform(e, Permission.class));
		}
		return list;
	}

	@Override
	public List<Permission> listByTarget(String target) {
		if (Permission.ANY_TARGET.equals(target)) {
			throw new BadArgumentException("doesn't support list by any target");
		}
		List<Permission> list = new LinkedList<>();
		for (PermissionEntity e : permissionRepo.findAllByTarget(target)) {
			list.add(Jsons.transform(e, Permission.class));
		}
		return list;
	}

	@Override
	public Permission get(String uid) {
		Optional<PermissionEntity> o = permissionRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Permission.class);
		}
		throw new NotFoundException("permission {} not found", uid);
	}

	@Override
	public Permission find(String uid) {
		Optional<PermissionEntity> o = permissionRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Permission.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Permission create(String principal, String target, String action, String mark) {
		if (Permission.ANY_RPINCIPAL.equals(principal)) {
			throw new BadArgumentException("doesn't support to create an any principal permission");
		}

		if (permissionRepo.existsByUniqueConstraintCode(principal, target, action)) {
			throw new BadArgumentException("[{},{},{}] is already existed", principal, target, action);
		}

		PermissionEntity e = new PermissionEntity();
		e.setPrincipal(principal);
		e.setTarget(target);
		e.setAction(action);
		e.setUid(Strings.randomUid());
		e.setRemark(mark);

		e = permissionRepo.save(e);

		return Jsons.transform(e, Permission.class);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String uid, boolean quiet) {
		if (find(uid) == null) {
			if (!quiet) {
				throw new NotFoundException("permission {} not found", uid);
			}
		} else {
			permissionRepo.deleteById(uid);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void deleteByPricipal(String principal) {
		permissionRepo.deleteByPrincipal(principal);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void deleteByTarget(String target) {
		permissionRepo.deleteByTarget(target);
	}

	@Override
	public boolean isPermitted(String principal, String target, String action) {
		if (Permission.ANY_RPINCIPAL.equals(principal)) {
			throw new BadArgumentException("bad principal:" + principal);
		}
		if (Permission.ANY_TARGET.equals(target)) {
			throw new BadArgumentException("bad target:" + target);
		}
		if (Permission.ANY_TARGET.equals(target)) {
			throw new BadArgumentException("bad action:" + action);
		}
		List<PermissionEntity> list = permissionRepo.findAllByPrincipalTargetWithAny(principal, target);
		for (Permission p : list) {
			if (Permission.ANY_ACTION.equals(p.getAction()) || action.equals(p.getAction())) {
				return true;
			}
		}
		return false;
	}

}