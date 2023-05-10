package onexas.coordinate.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.common.util.Securitys;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.model.UserFilter;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.model.UserOrganizationRelation;
import onexas.coordinate.model.UserUpdate;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.LogService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.service.event.BeforeCreateUserEvent;
import onexas.coordinate.service.event.BeforeDeleteUserEvent;
import onexas.coordinate.service.event.DeletedUserEvent;
import onexas.coordinate.service.event.DisabledUserEvent;
import onexas.coordinate.service.impl.dao.OrganizationEntityRepo;
import onexas.coordinate.service.impl.dao.OrganizationUserRelationEntityRepo;
import onexas.coordinate.service.impl.dao.PropertyEntityRepo;
import onexas.coordinate.service.impl.dao.RoleEntityRepo;
import onexas.coordinate.service.impl.dao.RoleUserRelationEntityRepo;
import onexas.coordinate.service.impl.dao.UserEntityDao;
import onexas.coordinate.service.impl.dao.UserEntityRepo;
import onexas.coordinate.service.impl.dao.UserRecordEntityRepo;
import onexas.coordinate.service.impl.entity.OrganizationEntity;
import onexas.coordinate.service.impl.entity.OrganizationUserRelationEntity;
import onexas.coordinate.service.impl.entity.PropertyEntity;
import onexas.coordinate.service.impl.entity.RoleEntity;
import onexas.coordinate.service.impl.entity.RoleUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;
import onexas.coordinate.service.impl.entity.UserRecordEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "UserServiceImpl")
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private static final int UID_LOOP = 2;
	private static final String PROP_PREFIX = "user-";

	@Autowired
	UserEntityRepo userRepo;

	@Autowired
	UserEntityDao userDao;

	@Autowired
	UserRecordEntityRepo userRecordRepo;

	@Autowired
	RoleEntityRepo roleRepo;

	@Autowired
	RoleUserRelationEntityRepo roleUserRelationRepo;

	@Autowired
	PropertyEntityRepo propertyRepo;

	@Autowired
	OrganizationEntityRepo organizationRepo;

	@Autowired
	OrganizationUserRelationEntityRepo organizationUserRelationRepo;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	DomainService domainService;

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	@Autowired
	LogService logService;

	@Autowired
	AsyncExService asyncExService;

	protected String getPropertyObjUid(String userUid) {
		return PROP_PREFIX + userUid;
	}

	@Override
	public ListPage<User> list(UserFilter filter) {
		return list(filter, null);
	}

	@Override
	public ListPage<User> list(UserFilter filter, Boolean disabled) {
		List<UserEntity> list = new LinkedList<>();

		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;

		Sort sort = Sort.by(direction, "account");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			if (disabled == null) {
				list = userRepo.findAll(sort);
			} else {
				UserEntity ex = new UserEntity();
				ex.setDisabled(disabled);

				ExampleMatcher matcher = ExampleMatcher.matchingAll();
				Example<UserEntity> example = Example.of(ex, matcher);

				list = userRepo.findAll(example, sort);
			}

			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField },
						new String[] { "account", "email", "displayName", "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			Page<UserEntity> page = userDao.findAll(filter, disabled, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}

		return new ListPage<>(Jsons.transform(list, new TypeReference<List<User>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public User get(String uid) {
		Optional<UserEntity> o = userRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), User.class);
		}
		throw new NotFoundException("user {} not found", uid);
	}

	@Override
	public User find(String uid) {
		Optional<UserEntity> o = userRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), User.class);
		}
		return null;
	}

	@Override
	public User findByAccountDomain(String account, String domainCode) {
		if (Strings.isBlank(domainCode)) {
			domainCode = Domain.LOCAL;
		}
		Optional<UserEntity> o = userRepo.findByAccountDomain(account, domainCode);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), User.class);
		}
		return null;
	}

	@Override
	public User findByAliasUid(String aliasUid) {
		Optional<UserEntity> o = userRepo.findByAliasUid(aliasUid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), User.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User create(UserCreate userCreate) {

		String domain = userCreate.getDomain();
		if (Strings.isBlank(domain)) {
			domain = Domain.LOCAL;
		}

		if (userRepo.existsByUniqueConstraintAccountDomain(userCreate.getAccount(), domain)) {
			throw new BadArgumentException("the user account {} @ {} is already existed ", userCreate.getAccount(),
					domain);
		}

		UserEntity e = Jsons.transform(userCreate, UserEntity.class);

		e.setDomain(domain);

		if (e.getDisplayName() == null) {
			e.setDisplayName(e.getAccount());
		}

		if (e.getDisabled() == null) {
			e.setDisabled(false);
		}
		e.setUid(Strings.randomUid(UID_LOOP));
		e.setAliasUid(Strings.randomUid());
		e.setPassword(getPasswordMd5(userCreate.getPassword()));

		User before = Jsons.transform(e, User.class);
		eventPublisher.publishEvent(new BeforeCreateUserEvent(before));

		e = userRepo.save(e);

		UserRecordEntity record = new UserRecordEntity();
		syncRecord(e, record);
		record = userRecordRepo.save(record);

		logService.info(getClass(), e.getUid(), User.class, null, null, "Created user {}/{}", e.getAccount(),
				e.getDisplayName());

		return Jsons.transform(e, User.class);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User update(String uid, UserUpdate userUpdate) {

		Optional<UserEntity> o = userRepo.findById(uid);
		if (o.isPresent()) {
			UserEntity e = o.get();
			Boolean disabled = e.getDisabled();
			boolean fireDisabled = false;
			// account, domain are not updateable.
			if (userUpdate.getPassword() != null) {
				if(!Domain.LOCAL.equals(e.getDomain())){
					throw new BadArgumentException("can't update non-local domain user's password");
				}
				e.setPassword(getPasswordMd5(userUpdate.getPassword()));
			}
			if (userUpdate.getDisplayName() != null
					&& !Objects.equals(userUpdate.getDisplayName(), e.getDisplayName())) {
				e.setDisplayName(userUpdate.getDisplayName());

				Optional<UserRecordEntity> r = userRecordRepo.findById(e.getUid());
				UserRecordEntity record = null;
				if (r.isPresent()) {
					record = r.get();
				} else {
					record = new UserRecordEntity();
				}
				syncRecord(e, record);
				record = userRecordRepo.save(record);
			}
			if (userUpdate.getEmail() != null) {
				e.setEmail(userUpdate.getEmail());
			}
			if (userUpdate.getDisabled() != null) {
				e.setDisabled(userUpdate.getDisabled());
			}

			if (Boolean.FALSE.equals(disabled) && Boolean.TRUE.equals(e.getDisabled())) {
				fireDisabled = true;
			}

			e = userRepo.save(e);

			logService.info(getClass(), e.getUid(), User.class, null, null, "Updated user {}", e.getDisplayName());

			User user = Jsons.transform(e, User.class);
			if (fireDisabled) {
				eventPublisher.publishEvent(new DisabledUserEvent(user));
			}
			return user;
		} else {
			throw new BadArgumentException("user {} not found", uid);
		}
	}

	private void syncRecord(UserEntity e, UserRecordEntity record) {
		record.setUid(e.getUid());
		record.setCreatedDateTime(e.getCreatedDateTime());
		record.setAccount(e.getAccount());
		record.setAliasUid(e.getAliasUid());
		record.setDisplayName(e.getDisplayName());
		record.setDomain(e.getDomain());
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String uid, boolean quiet) {
		Optional<UserEntity> o = userRepo.findById(uid);

		if (!o.isPresent()) {
			if (!quiet) {
				throw new NotFoundException("user {} not found", uid);
			}
		} else {
			UserEntity e = o.get();
			User user = Jsons.transform(e, User.class);

			eventPublisher.publishEvent(new BeforeDeleteUserEvent(user));

			propertyRepo.deleteByObjUid(getPropertyObjUid(uid));
			roleUserRelationRepo.deleteByUserUid(uid);
			organizationUserRelationRepo.deleteByUserUid(uid);

			Optional<UserRecordEntity> r = userRecordRepo.findById(e.getUid());
			UserRecordEntity record = null;
			if (r.isPresent()) {
				record = r.get();
			} else {
				record = new UserRecordEntity();
			}
			syncRecord(e, record);
			record.setDeletedDateTime(System.currentTimeMillis());
			record = userRecordRepo.save(record);

			userRepo.deleteById(uid);
			userRepo.flush();

			logService.info(getClass(), user.getUid(), User.class, null, null, "Deleted user {}",
					user.getDisplayName());

			asyncExService.asyncRunAfterTxCommit(() -> {
				eventPublisher.publishEvent(new DeletedUserEvent(user));
			});
		}
	}

	@Override
	public boolean verifyPassword(String uid, String password) {
		Optional<UserEntity> o = userRepo.findById(uid);// check
		if (o.isPresent()) {
			return o.get().getPassword().equals(getPasswordMd5(password));
		}
		return false;
	}

	@Override
	public boolean verifyPasswordByAccountDomain(String account, String domainCode, String password) {
		Optional<UserEntity> o = userRepo.findByAccountDomain(account, domainCode);// check
		if (o.isPresent()) {
			UserEntity u = o.get();
			return u.getPassword().equals(getPasswordMd5(password));
		}
		return false;
	}

	@Override
	public Map<String, String> getProperties(String uid, String category) {
		Map<String, String> m = new LinkedHashMap<>();
		if (category == null) {
			for (PropertyEntity e : propertyRepo.findAllByObjUid(getPropertyObjUid(uid))) {
				m.put(e.getName(), e.getValue());
			}
		} else {
			for (PropertyEntity e : propertyRepo.findAllByObjUid(getPropertyObjUid(uid), category)) {
				m.put(e.getName(), e.getValue());
			}
		}
		return m;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void setProperties(String uid, Map<String, String> properties, String category) {
		User user = get(uid);// check

		properties = new LinkedHashMap<>(properties);
		for (PropertyEntity e : propertyRepo.findAllByObjUid(getPropertyObjUid(uid))) {
			String n = e.getName();
			if (properties.containsKey(n)) {
				String v = properties.get(n);
				if (v == null) {
					propertyRepo.delete(e);
				} else {
					e.setValue(v);
					e.setCategory(category);
				}
				properties.remove(n);
			}
		}
		for (String n : properties.keySet()) {
			String v = properties.get(n);
			if (v != null) {
				PropertyEntity e = new PropertyEntity();
				e.setObjUid(getPropertyObjUid(uid));
				e.setName(n);
				e.setValue(v);
				e.setCategory(category);
				propertyRepo.save(e);
			}
		}

		logService.info(getClass(), user.getUid(), User.class, null, null, "Updated properties of user {}",
				user.getDisplayName());

	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void setProperty(String uid, String name, String value, String category) {
		Map<String, String> m = new HashMap<>();
		m.put(name, value);
		setProperties(uid, m, category);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void deleteProperties(String uid, Set<String> name) {
		User user = get(uid);// check

		for (String n : name) {
			propertyRepo.deleteById(new PropertyEntity.PK(getPropertyObjUid(uid), n));
		}
		propertyRepo.flush();

		logService.info(getClass(), user.getUid(), User.class, null, null, "Deleted properties of user {}",
				user.getDisplayName());
	}

	// role

	@Override
	public List<Role> listRole(String uid) {
		List<RoleEntity> list = new LinkedList<>();
		for (RoleEntity e : roleUserRelationRepo.findAllRolesByUserUid(uid)) {
			list.add(e);
		}
		return Jsons.transform(list, new TypeReference<List<Role>>() {
		});
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User addRoles(String uid, Set<String> roleUids) {
		User user = get(uid);// check
		for (String roleUid : roleUids) {
			if (!roleUserRelationRepo.existsById(new RoleUserRelationEntity.PK(roleUid, uid))) {
				if (roleRepo.existsById(roleUid)) {
					roleUserRelationRepo.save(new RoleUserRelationEntity(roleUid, uid));
					RoleEntity role = roleRepo.getOne(roleUid);
					logService.info(getClass(), user.getUid(), User.class, role.getUid(), Role.class,
							"Added role {} to user {}", role.getCode(), user.getDisplayName());
				} else {
					throw new BadArgumentException("role {} not found", roleUid);
				}

			}
		}

		return user;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User removeRoles(String uid, Set<String> roleUids) {
		User user = get(uid);// check
		for (String roleUid : roleUids) {
			if (roleUserRelationRepo.existsById(new RoleUserRelationEntity.PK(roleUid, uid))) {
				roleUserRelationRepo.deleteById(new RoleUserRelationEntity.PK(roleUid, uid));
				RoleEntity role = roleRepo.getOne(roleUid);
				logService.info(getClass(), user.getUid(), User.class, role.getUid(), Role.class,
						"Removed role {} from user {}", role.getCode(), user.getDisplayName());
			}
		}
		return user;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User setRoles(String uid, Set<String> roleUids) {
		User user = get(uid);// check
		roleUserRelationRepo.deleteByUserUid(uid);
		roleUserRelationRepo.flush();

		logService.info(getClass(), user.getUid(), User.class, null, null, "Removed all role from user {}",
				user.getDisplayName());

		for (String roleUid : roleUids) {
			if (roleRepo.existsById(roleUid)) {
				roleUserRelationRepo.save(new RoleUserRelationEntity(roleUid, uid));
				RoleEntity role = roleRepo.getOne(roleUid);
				logService.info(getClass(), user.getUid(), User.class, role.getUid(), Role.class,
						"Added role {} to user {}", role.getCode(), user.getDisplayName());
			} else {
				throw new BadArgumentException("role {} not found", roleUid);
			}
		}

		return user;
	}

	// organization

	@Override
	public List<UserOrganization> listOrganization(String uid) {

		Map<String, OrganizationUserRelationType> typeMap = new LinkedHashMap<>();
		for (OrganizationUserRelationEntity e : organizationUserRelationRepo.findAllByUserUid(uid)) {
			typeMap.put(e.getOrganizationUid(), e.getType());
		}

		List<UserOrganization> list = new LinkedList<>();
		for (OrganizationEntity e : organizationUserRelationRepo.findAllOrganizationsByUserUid(uid)) {
			UserOrganization u = Jsons.transform(e, UserOrganization.class);
			u.setRelationType(typeMap.get(e.getUid()));
			list.add(u);
		}
		return list;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User addOrganizations(String uid, Set<UserOrganizationRelation> organizationRelations) {
		User user = get(uid);// check
		for (UserOrganizationRelation r : organizationRelations) {
			Optional<OrganizationUserRelationEntity> o = organizationUserRelationRepo
					.findById(new OrganizationUserRelationEntity.PK(r.getOrganizationUid(), uid));

			if (o.isPresent()) {
				o.get().setType(r.getType());
			} else {
				String organizationUid = r.getOrganizationUid();
				if (organizationRepo.existsById(organizationUid)) {
					organizationUserRelationRepo
							.save(new OrganizationUserRelationEntity(organizationUid, uid, r.getType()));
					OrganizationEntity organization = organizationRepo.getOne(organizationUid);
					logService.info(getClass(), user.getUid(), User.class, organization.getUid(), Organization.class,
							"Added organization {} to user {}", organization.getCode(), user.getDisplayName());

				} else {
					throw new BadArgumentException("organization {} not found", r.getOrganizationUid());
				}

			}
		}

		organizationUserRelationRepo.flush();

		return user;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User removeOrganizations(String uid, Set<String> organizationUids) {
		User user = get(uid);// check
		for (String organizationUid : organizationUids) {
			if (organizationUserRelationRepo.existsById(new OrganizationUserRelationEntity.PK(organizationUid, uid))) {
				organizationUserRelationRepo.deleteById(new OrganizationUserRelationEntity.PK(organizationUid, uid));
				OrganizationEntity organization = organizationRepo.getOne(organizationUid);
				logService.info(getClass(), user.getUid(), User.class, organization.getUid(), Organization.class,
						"Removed organization {} from user {}", organization.getCode(), user.getDisplayName());
			}
		}
		return user;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User setOrganizations(String uid, Set<UserOrganizationRelation> organizationRelations) {
		User user = get(uid);// check
		organizationUserRelationRepo.deleteByUserUid(uid);
		organizationUserRelationRepo.flush();

		logService.info(getClass(), user.getUid(), User.class, null, null, "Removed all organization from user {}",
				user.getDisplayName());

		for (UserOrganizationRelation r : organizationRelations) {
			String organizationUid = r.getOrganizationUid();
			if (organizationRepo.existsById(organizationUid)) {
				organizationUserRelationRepo
						.save(new OrganizationUserRelationEntity(organizationUid, uid, r.getType()));
				OrganizationEntity organization = organizationRepo.getOne(organizationUid);
				logService.info(getClass(), user.getUid(), User.class, organization.getUid(), Organization.class,
						"Added organization {} to user {}", organization.getCode(), user.getDisplayName());
			} else {
				throw new BadArgumentException("organization {} not found", r.getOrganizationUid());
			}
		}

		organizationUserRelationRepo.flush();

		return user;
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	protected static String getPasswordMd5(String password) {
		return Securitys.md5String(AppContext.config().getString("passwordSalt", "betterthannothing") + password);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public User createByDomainUser(DomainUser domainUser) {

		UserEntity e = new UserEntity();
		e.setUid(Strings.randomUid(UID_LOOP));
		e.setAliasUid(Strings.randomUid());
		e.setAccount(domainUser.getAccount());
		e.setDomain(domainUser.getDomain());
		e.setPassword(getPasswordMd5(Strings.randomPassword(20)));
		e.setDisplayName(domainUser.getDisplayName());
		e.setEmail(domainUser.getEmail());
		e.setDisabled(false);

		e.setDomainUserIdentity(domainUser.getIdentity());

		
		User before = Jsons.transform(e, User.class);
		eventPublisher.publishEvent(new BeforeCreateUserEvent(before));
		
		e = userRepo.save(e);

		UserRecordEntity record = new UserRecordEntity();
		syncRecord(e, record);
		record = userRecordRepo.save(record);

		logService.info(getClass(), e.getUid(), User.class, null, null, "Created user {}/{}/{}", e.getAccount(),
				e.getDisplayName(), e.getDomain());

		return Jsons.transform(e, User.class);
	}

	@Override
	public boolean verifyDomainUserIdentity(DomainUser domainUser) {
		if (Domain.LOCAL.equals(domainUser.getDomain())) {
			// no need to verify local domain (optimal)
			return true;
		}
		Optional<UserEntity> o = userRepo.findByAccountDomain(domainUser.getAccount(), domainUser.getDomain());
		if (o.isPresent()) {
			boolean r = Objects.equals(o.get().getDomainUserIdentity(), domainUser.getIdentity());
			if (!r) {
				logger.warn("domain user identity inconsistent {}!={}", o.get().getDomainUserIdentity(),
						domainUser.getIdentity());
			}
			return r;
		}
		return false;
	}
}