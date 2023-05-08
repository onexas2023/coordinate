package onexas.coordinate.service.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.common.util.ValueII;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.Organization;
import onexas.coordinate.model.OrganizationCreate;
import onexas.coordinate.model.OrganizationFilter;
import onexas.coordinate.model.OrganizationUpdate;
import onexas.coordinate.model.OrganizationUser;
import onexas.coordinate.model.OrganizationUserFilter;
import onexas.coordinate.model.OrganizationUserRelation;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserOrganization;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.LogService;
import onexas.coordinate.service.OrganizationService;
import onexas.coordinate.service.event.BeforeDeleteOrganizationEvent;
import onexas.coordinate.service.event.DeletedOrganizationEvent;
import onexas.coordinate.service.impl.dao.OrganizationEntityRepo;
import onexas.coordinate.service.impl.dao.OrganizationRecordEntityRepo;
import onexas.coordinate.service.impl.dao.OrganizationUserRelationEntityDao;
import onexas.coordinate.service.impl.dao.OrganizationUserRelationEntityRepo;
import onexas.coordinate.service.impl.dao.UserEntityRepo;
import onexas.coordinate.service.impl.entity.OrganizationEntity;
import onexas.coordinate.service.impl.entity.OrganizationRecordEntity;
import onexas.coordinate.service.impl.entity.OrganizationUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "OrganizationServiceImpl")
public class OrganizationServiceImpl implements OrganizationService {

	private static final int UID_LOOP = 2;

	@Autowired
	OrganizationEntityRepo organizationRepo;

	@Autowired
	UserEntityRepo userRepo;

	@Autowired
	OrganizationUserRelationEntityRepo organizationUserRelationRepo;

	@Autowired
	OrganizationUserRelationEntityDao organizationUserRelationDao;

	@Autowired
	OrganizationRecordEntityRepo organizationRecordRepo;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	LogService logService;
	
	@Autowired
	AsyncExService asyncExService;

	@Override
	public ListPage<Organization> list(OrganizationFilter filter) {
		List<OrganizationEntity> list = new LinkedList<>();

		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;

		Sort sort = Sort.by(direction, "code");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;

		Long itemTotal;
		if (filter == null) {
			list = organizationRepo.findAll(sort);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField }, new String[] { "code", "name", "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			OrganizationEntity ex = Jsons.transform(filter, OrganizationEntity.class);
			ExampleMatcher matcher = Boolean.TRUE.equals(filter.getMatchAny()) ? ExampleMatcher.matchingAny()
					: ExampleMatcher.matchingAll();
			matcher = matcher.withIgnoreNullValues();

			if (Boolean.TRUE.equals(filter.getStrIgnoreCase())) {
				matcher = matcher.withIgnoreCase();
			}
			if (Boolean.TRUE.equals(filter.getStrContaining())) {
				matcher = matcher.withStringMatcher(StringMatcher.CONTAINING);
			}
			Example<OrganizationEntity> example = Example.of(ex, matcher);

			Page<OrganizationEntity> page = organizationRepo.findAll(example, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<Organization>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public Organization get(String uid) {
		Optional<OrganizationEntity> o = organizationRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Organization.class);
		}
		throw new NotFoundException("organization {} not found", uid);
	}

	@Override
	public Organization find(String uid) {
		Optional<OrganizationEntity> o = organizationRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Organization.class);
		}
		return null;
	}

	@Override
	public Organization findByCode(String code) {
		Optional<OrganizationEntity> o = organizationRepo.findByCode(code);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Organization.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Organization create(OrganizationCreate organizationCreate) {
		if (organizationRepo.existsByUniqueConstraintCode(organizationCreate.getCode())) {
			throw new BadArgumentException("the code {} of organization is already existed ",
					organizationCreate.getCode());
		}

		OrganizationEntity e = Jsons.transform(organizationCreate, OrganizationEntity.class);
		e.setUid(Strings.randomUid(UID_LOOP));
		if (e.getName() == null) {
			e.setName(e.getCode());
		}
		e = organizationRepo.save(e);

		OrganizationRecordEntity record = new OrganizationRecordEntity();
		syncRecord(e, record);
		record = organizationRecordRepo.save(record);

		logService.info(getClass(), e.getUid(), Organization.class, null, null, "Created organization {}/{}",
				e.getCode(), e.getName());

		return Jsons.transform(e, Organization.class);
	}

	private void syncRecord(OrganizationEntity e, OrganizationRecordEntity record) {
		record.setUid(e.getUid());
		record.setCreatedDateTime(e.getCreatedDateTime());
		record.setCode(e.getCode());
		record.setName(e.getName());
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Organization update(String uid, OrganizationUpdate organizationUpdate) {

		Optional<OrganizationEntity> o = organizationRepo.findById(uid);
		if (o.isPresent()) {
			OrganizationEntity e = o.get();
			// code are not updateable.
			if (organizationUpdate.getName() != null && !Objects.equals(organizationUpdate.getName(), e.getName())) {
				e.setName(organizationUpdate.getName());

				Optional<OrganizationRecordEntity> r = organizationRecordRepo.findById(e.getUid());
				OrganizationRecordEntity record = null;
				if (r.isPresent()) {
					record = r.get();
				} else {
					record = new OrganizationRecordEntity();
				}
				syncRecord(e, record);
				record = organizationRecordRepo.save(record);
			}

			if (organizationUpdate.getDescription() != null) {
				e.setDescription(organizationUpdate.getDescription());
			}

			e = organizationRepo.save(e);

			logService.info(getClass(), e.getUid(), Organization.class, null, null, "Updated organization {}",
					e.getCode());

			return Jsons.transform(e, Organization.class);
		} else {
			throw new BadArgumentException("organization {} not found", uid);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String uid, boolean quiet) {
		Optional<OrganizationEntity> o = organizationRepo.findById(uid);
		if (!o.isPresent()) {
			if (!quiet) {
				throw new NotFoundException("organization {} not found", uid);
			}
		} else {
			OrganizationEntity e = o.get();
			Organization organization = Jsons.transform(o.get(), Organization.class);

			eventPublisher.publishEvent(new BeforeDeleteOrganizationEvent(organization));

			Optional<OrganizationRecordEntity> r = organizationRecordRepo.findById(e.getUid());
			OrganizationRecordEntity record = null;
			if (r.isPresent()) {
				record = r.get();
			} else {
				record = new OrganizationRecordEntity();
			}
			syncRecord(e, record);
			record.setDeletedDateTime(System.currentTimeMillis());
			record = organizationRecordRepo.save(record);

			organizationUserRelationRepo.deleteByOrganizationUid(uid);
			organizationRepo.deleteById(uid);

			logService.info(getClass(), organization.getUid(), Organization.class, null, null,
					"Deleted organization {}", organization.getCode());

			asyncExService.asyncRunAfterTxCommit(() -> {
				eventPublisher.publishEvent(new DeletedOrganizationEvent(organization));
			});
		}
	}

	@Override
	public ListPage<OrganizationUser> listUser(String uid, OrganizationUserFilter filter) {
		List<OrganizationUser> list = new LinkedList<>();
		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;
		Sort sort = Sort.by(direction, "account");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		if (filter == null) {
			Map<String, OrganizationUserRelationType> typeMap = new LinkedHashMap<>();
			for (OrganizationUserRelationEntity e : organizationUserRelationRepo.findAllByOrganizationUid(uid)) {
				typeMap.put(e.getUserUid(), e.getType());
			}
			for (UserEntity e : organizationUserRelationRepo.findAllUsersByOrganizationUid(uid)) {
				OrganizationUser u = Jsons.transform(e, OrganizationUser.class);
				u.setRelationType(typeMap.get(u.getUid()));
				list.add(u);
			}
			return new ListPage<OrganizationUser>(list);
		}
		String sortField = filter.getSortField();

		if (sortField != null) {
			Fields.checkFieldsIn(new String[] { sortField },
					new String[] { "account", "email", "displayName", "createdDateTime" });
			sort = Sort.by(direction, sortField);
		}
		Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

		Page<ValueII<UserEntity, OrganizationUserRelationType>> page = organizationUserRelationDao
				.findAllUsersByOrganizationUid(uid, filter, pageable);
		Integer pageTotal = page.getTotalPages();
		Long itemTotal = page.getTotalElements();

		for (ValueII<UserEntity, OrganizationUserRelationType> e : page.getContent()) {
			OrganizationUser u;
			list.add(u = Jsons.transform(e.getValue1(), OrganizationUser.class));
			u.setRelationType(e.getValue2());
		}
		return new ListPage<>(list, pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Organization addUsers(String uid, Set<OrganizationUserRelation> userRelations) {
		Organization organization = get(uid);// check

		Map<String, OrganizationUserRelation> m = new LinkedHashMap<>();
		for (OrganizationUserRelation r : userRelations) {
			m.put(r.getUserUid(), r);
		}

		for (OrganizationUserRelation r : m.values()) {
			Optional<OrganizationUserRelationEntity> o = organizationUserRelationRepo
					.findById(new OrganizationUserRelationEntity.PK(uid, r.getUserUid()));
			if (o.isPresent()) {
				o.get().setType(r.getType());
			} else {
				if (userRepo.existsById(r.getUserUid())) {
					organizationUserRelationRepo
							.save(new OrganizationUserRelationEntity(uid, r.getUserUid(), r.getType()));

					UserEntity user = userRepo.getOne(r.getUserUid());

					logService.info(getClass(), organization.getUid(), Organization.class, user.getUid(), User.class,
							"Added user {} to organization {}", user.getDisplayName(), organization.getCode());
				} else {
					throw new BadArgumentException("user {} not found", r.getUserUid());
				}
			}
		}
		organizationUserRelationRepo.flush();
		return organization;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Organization removeUsers(String uid, Set<String> userUids) {
		Organization organization = get(uid);// check
		for (String userUid : userUids) {
			if (organizationUserRelationRepo.existsById(new OrganizationUserRelationEntity.PK(uid, userUid))) {
				organizationUserRelationRepo.deleteById(new OrganizationUserRelationEntity.PK(uid, userUid));
				UserEntity user = userRepo.getOne(userUid);
				logService.info(getClass(), organization.getUid(), Organization.class, user.getUid(), User.class,
						"Removed user {} from organization {}", user.getDisplayName(), organization.getCode());
			}
		}
		organizationUserRelationRepo.flush();
		return organization;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Organization setUsers(String uid, Set<OrganizationUserRelation> userRelations) {
		Organization organization = get(uid);// check

		Map<String, OrganizationUserRelation> m = new LinkedHashMap<>();
		for (OrganizationUserRelation r : userRelations) {
			m.put(r.getUserUid(), r);
		}

		organizationUserRelationRepo.deleteByOrganizationUid(uid);
		logService.info(getClass(), organization.getUid(), Organization.class, null, null,
				"Removed all user from organization {}", organization.getCode());
		organizationUserRelationRepo.flush();
		for (OrganizationUserRelation r : m.values()) {
			if (userRepo.existsById(r.getUserUid())) {
				organizationUserRelationRepo.save(new OrganizationUserRelationEntity(uid, r.getUserUid(), r.getType()));
				UserEntity user = userRepo.getOne(r.getUserUid());
				logService.info(getClass(), organization.getUid(), Organization.class, user.getUid(), User.class,
						"Added user {} to organization {}", user.getDisplayName(), organization.getCode());
			} else {
				throw new BadArgumentException("user {} not found", r.getUserUid());
			}
		}
		organizationUserRelationRepo.flush();
		return organization;
	}

	@Override
	public UserOrganization findUserOrganization(String uid, String userUid) {

		Optional<OrganizationUserRelationEntity> o = organizationUserRelationRepo
				.findById(new OrganizationUserRelationEntity.PK(uid, userUid));
		if (!o.isPresent()) {
			return null;
		}
		OrganizationUserRelationEntity e = o.get();
		OrganizationEntity p = organizationRepo.findById(e.getOrganizationUid()).get();
		UserOrganization u = Jsons.transform(p, UserOrganization.class);
		u.setRelationType(e.getType());
		return u;
	}
}