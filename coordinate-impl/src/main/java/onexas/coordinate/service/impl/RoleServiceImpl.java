package onexas.coordinate.service.impl;

import static onexas.coordinate.service.GlobalCacheEvictService.UNLESS_RESULT_NULL;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_ROLE;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_ROLE_BYCODE;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_ROLE_PERMISSIONS;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_USER_ROLES;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.Permission;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.RoleFilter;
import onexas.coordinate.model.RoleUpdate;
import onexas.coordinate.model.RoleUserFilter;
import onexas.coordinate.model.User;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.GlobalCacheEvictService;
import onexas.coordinate.service.LogService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.event.BeforeDeleteRoleEvent;
import onexas.coordinate.service.event.DeletedRoleEvent;
import onexas.coordinate.service.impl.dao.RoleEntityRepo;
import onexas.coordinate.service.impl.dao.RoleUserRelationEntityDao;
import onexas.coordinate.service.impl.dao.RoleUserRelationEntityRepo;
import onexas.coordinate.service.impl.dao.UserEntityRepo;
import onexas.coordinate.service.impl.entity.RoleEntity;
import onexas.coordinate.service.impl.entity.RoleUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "RoleServiceImpl")
public class RoleServiceImpl implements RoleService {

	private static final int UID_LOOP = 2;

	@Autowired
	RoleEntityRepo roleRepo;

	@Autowired
	UserEntityRepo userRepo;

	@Autowired
	RoleUserRelationEntityRepo roleUserRelationRepo;

	@Autowired
	RoleUserRelationEntityDao roleUserRelationDao;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	GlobalCacheEvictService cacheEvictService;

	@Autowired
	LogService logService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	AsyncExService asyncExService;

	@Override
	public ListPage<Role> list(RoleFilter filter) {
		List<RoleEntity> list = new LinkedList<>();

		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;

		Sort sort = Sort.by(direction, "code");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			list = roleRepo.findAll(sort);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField }, new String[] { "code", "name", "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			RoleEntity ex = Jsons.transform(filter, RoleEntity.class);
			ExampleMatcher matcher = Boolean.TRUE.equals(filter.getMatchAny()) ? ExampleMatcher.matchingAny()
					: ExampleMatcher.matchingAll();
			matcher = matcher.withIgnoreNullValues();

			if (Boolean.TRUE.equals(filter.getStrIgnoreCase())) {
				matcher = matcher.withIgnoreCase();
			}
			if (Boolean.TRUE.equals(filter.getStrContaining())) {
				matcher = matcher.withStringMatcher(StringMatcher.CONTAINING);
			}
			Example<RoleEntity> example = Example.of(ex, matcher);

			Page<RoleEntity> page = roleRepo.findAll(example, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<Role>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_ROLE, unless = UNLESS_RESULT_NULL)
	public Role get(String uid) {
		Optional<RoleEntity> o = roleRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Role.class);
		}
		throw new NotFoundException("role {} not found", uid);
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_ROLE, unless = UNLESS_RESULT_NULL)
	public Role find(String uid) {
		Optional<RoleEntity> o = roleRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Role.class);
		}
		return null;
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_ROLE_BYCODE, unless = UNLESS_RESULT_NULL)
	public Role findByCode(String code) {
		Optional<RoleEntity> o = roleRepo.findByCode(code);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Role.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role create(RoleCreate roleCreate) {
		if (roleRepo.existsByUniqueConstraintCode(roleCreate.getCode())) {
			throw new BadArgumentException("the code {} of role is already existed ", roleCreate.getCode());
		}

		RoleEntity e = Jsons.transform(roleCreate, RoleEntity.class);
		e.setUid(Strings.randomUid(UID_LOOP));
		if (e.getName() == null) {
			e.setName(e.getCode());
		}
		e = roleRepo.save(e);

		logService.info(getClass(), e.getUid(), Role.class, null, null, "Created role {}/{}", e.getCode(), e.getName());

		return Jsons.transform(e, Role.class);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role update(String uid, RoleUpdate roleUpdate) {

		Optional<RoleEntity> o = roleRepo.findById(uid);// check
		if (o.isPresent()) {
			RoleEntity e = o.get();
			// code are not updateable.

			if (roleUpdate.getName() != null) {
				e.setName(roleUpdate.getName());
			}
			if (roleUpdate.getDescription() != null) {
				e.setDescription(roleUpdate.getDescription());
			}

			e = roleRepo.save(e);

			logService.info(getClass(), e.getUid(), Role.class, null, null, "Update role {}", e.getCode());

			cacheEvictService.evict(uid, CACHE_NAME_ROLE, CACHE_NAME_ROLE_PERMISSIONS);
			cacheEvictService.evict(e.getCode(), CACHE_NAME_ROLE_BYCODE);
			cacheEvictService.clear(CACHE_NAME_USER_ROLES);

			return Jsons.transform(e, Role.class);
		} else {
			throw new BadArgumentException("role {} not found", uid);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String uid, boolean quiet) {
		Role role;
		if ((role = find(uid)) == null) {
			if (!quiet) {
				throw new NotFoundException("role {} not found", uid);
			}
		} else {
			eventPublisher.publishEvent(new BeforeDeleteRoleEvent(role));

			roleUserRelationRepo.deleteByRoleUid(uid);
			roleRepo.deleteById(uid);

			permissionService.deleteByPricipal(uid);

			logService.info(getClass(), role.getUid(), Role.class, null, null, "Deleted role {}", role.getCode());

			cacheEvictService.evict(uid, CACHE_NAME_ROLE, CACHE_NAME_ROLE_PERMISSIONS);
			cacheEvictService.evict(role.getCode(), CACHE_NAME_ROLE_BYCODE);
			cacheEvictService.clear(CACHE_NAME_USER_ROLES);

			asyncExService.asyncRunAfterTxCommit(() -> {
				eventPublisher.publishEvent(new DeletedRoleEvent(role));
			});
		}
	}

	@Override
	public ListPage<User> listUser(String uid, RoleUserFilter filter) {
		List<UserEntity> list = new LinkedList<>();

		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;
		Sort sort = Sort.by(direction, "account");

		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			list = roleUserRelationRepo.findAllUsersByRoleUid(uid);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField },
						new String[] { "account", "email", "displayName", "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			Page<UserEntity> page = roleUserRelationDao.findAllUsersByRoleUid(uid, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<User>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role addUsers(String uid, Set<String> userUids) {
		Role role = get(uid);// check
		for (String userUid : userUids) {
			if (!roleUserRelationRepo.existsById(new RoleUserRelationEntity.PK(uid, userUid))) {
				if (userRepo.existsById(userUid)) {
					roleUserRelationRepo.save(new RoleUserRelationEntity(uid, userUid));
					UserEntity user = userRepo.getOne(userUid);
					logService.info(getClass(), role.getUid(), Role.class, user.getUid(), User.class,
							"Added user {} to role {}", user.getDisplayName(), role.getCode());
				} else {
					throw new BadArgumentException("user {} not found", userUid);
				}
			}
		}

		cacheEvictService.clear(CACHE_NAME_USER_ROLES);
		return role;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role removeUsers(String uid, Set<String> userUids) {
		Role role = get(uid);// check
		for (String userUid : userUids) {
			if (roleUserRelationRepo.existsById(new RoleUserRelationEntity.PK(uid, userUid))) {
				roleUserRelationRepo.deleteById(new RoleUserRelationEntity.PK(uid, userUid));
				UserEntity user = userRepo.getOne(userUid);
				logService.info(getClass(), role.getUid(), Role.class, user.getUid(), User.class,
						"Removed user {} from role {}", user.getDisplayName(), role.getCode());
			}
		}
		cacheEvictService.clear(CACHE_NAME_USER_ROLES);
		return role;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role setUsers(String uid, Set<String> userUids) {
		Role role = get(uid);// check
		roleUserRelationRepo.deleteByRoleUid(uid);
		roleUserRelationRepo.flush();

		logService.info(getClass(), role.getUid(), Role.class, null, null, "Removed all user from role {}",
				role.getCode());

		for (String userUid : userUids) {
			if (userRepo.existsById(userUid)) {
				roleUserRelationRepo.save(new RoleUserRelationEntity(uid, userUid));
				UserEntity user = userRepo.getOne(userUid);
				logService.info(getClass(), role.getUid(), Role.class, user.getUid(), User.class,
						"Added user {} to role {}", user.getDisplayName(), role.getCode());

			} else {
				throw new BadArgumentException("user {} not found", userUid);
			}
		}

		cacheEvictService.clear(CACHE_NAME_USER_ROLES);
		return role;
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_ROLE_PERMISSIONS, unless = UNLESS_RESULT_NULL)
	public List<PrincipalPermission> listPermission(String uid) {
		get(uid);// check
		List<PrincipalPermission> list = new LinkedList<>();
		for (Permission p : permissionService.listByPrincipal(uid)) {
			PrincipalPermission rp = new PrincipalPermission();
			rp.setTarget(p.getTarget());
			rp.setAction(p.getAction());
			list.add(rp);
		}
		return list;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role addPermissions(String uid, Set<PrincipalPermission> rolePermissionList) {
		Role role = get(uid);// check

		Set<PrincipalPermission> toAdd = new HashSet<>(rolePermissionList);
		for (Permission p : permissionService.listByPrincipal(uid)) {
			PrincipalPermission rp = new PrincipalPermission();
			rp.setTarget(p.getTarget());
			rp.setAction(p.getAction());
			toAdd.remove(rp);
		}
		for (PrincipalPermission rp : toAdd) {
			permissionService.create(uid, rp.getTarget(), rp.getAction(), "role");
			logService.info(getClass(), uid, Role.class, null, null, "Added permision {}:{} to role {}", rp.getTarget(),
					rp.getAction(), role.getCode());
		}

		cacheEvictService.evict(uid, CACHE_NAME_ROLE_PERMISSIONS);

		return role;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role setPermissions(String uid, Set<PrincipalPermission> rolePermissionList) {
		Role role = get(uid);// check

		permissionService.deleteByPricipal(uid);
		logService.info(getClass(), uid, Role.class, null, null, "Removed all permission of role {}", role.getCode());
		for (PrincipalPermission rp : new LinkedHashSet<>(rolePermissionList)) {
			permissionService.create(uid, rp.getTarget(), rp.getAction(), "role");
			logService.info(getClass(), uid, Role.class, null, null, "Added permision {}:{} to role {}", rp.getTarget(),
					rp.getAction(), role.getCode());
		}

		cacheEvictService.evict(uid, CACHE_NAME_ROLE_PERMISSIONS);

		return role;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Role removePermissions(String uid, Set<PrincipalPermission> rolePermissionList) {
		Role role = get(uid);// check

		Set<PrincipalPermission> toRemove = new HashSet<>(rolePermissionList);

		for (Permission p : permissionService.listByPrincipal(uid)) {
			PrincipalPermission rp = new PrincipalPermission();
			rp.setTarget(p.getTarget());
			rp.setAction(p.getAction());
			if (toRemove.contains(rp)) {
				permissionService.delete(p.getUid(), true);
				logService.info(getClass(), uid, Role.class, null, null, "Deleted permision {}:{} of role {}",
						rp.getTarget(), rp.getAction(), role.getCode());
			}
		}

		cacheEvictService.evict(uid, CACHE_NAME_ROLE_PERMISSIONS);

		return role;
	}

	@Override
	public long count() {
		return roleRepo.count();
	}
}