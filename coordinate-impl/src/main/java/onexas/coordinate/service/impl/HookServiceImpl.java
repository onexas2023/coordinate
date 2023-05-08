package onexas.coordinate.service.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;

import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.Hook;
import onexas.coordinate.model.HookCreate;
import onexas.coordinate.model.HookFilter;
import onexas.coordinate.model.HookMatch;
import onexas.coordinate.model.HookUpdate;
import onexas.coordinate.model.User;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.HookService;
import onexas.coordinate.service.event.DeletedUserEvent;
import onexas.coordinate.service.event.TriggerHookEvent;
import onexas.coordinate.service.impl.dao.HookEntityDao;
import onexas.coordinate.service.impl.dao.HookEntityRepo;
import onexas.coordinate.service.impl.entity.HookEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "HookServiceImpl")
public class HookServiceImpl implements HookService {

	private static Logger logger = LoggerFactory.getLogger(HookServiceImpl.class);

	private static final int UID_LOOP = 3;

	@Autowired
	HookEntityRepo hookRepo;

	@Autowired
	HookEntityDao hookDao;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	AsyncExService asyncExService;

	@Override
	public ListPage<Hook> list(@Nullable HookFilter filter) {
		List<HookEntity> list = new LinkedList<>();
		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;
		Sort sort = Sort.by(direction, "createdDateTime");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			list = hookRepo.findAll(sort);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField }, new String[] { "createdDateTime", "zone" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			Page<HookEntity> page = hookDao.findAll(filter, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<Hook>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public Hook get(String uid) {
		Optional<HookEntity> o = hookRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Hook.class);
		}
		throw new NotFoundException("hook {} not found", uid);
	}

	@Override
	public Hook find(String uid) {
		Optional<HookEntity> o = hookRepo.findById(uid);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Hook.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String uid, boolean quiet) {
		if ((find(uid)) == null) {
			if (!quiet) {
				throw new NotFoundException("hook {} not found", uid);
			}
		} else {
			hookRepo.deleteById(uid);
			logger.debug("deleted hook {}", uid);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Hook create(HookCreate hookCreate) {

		HookEntity e = Jsons.transform(hookCreate, HookEntity.class);

		e.setUid(Strings.randomUid(UID_LOOP));
		e.setTrigger(0);

		e = hookRepo.save(e);

		logger.debug("created hook {}", e.getUid());

		return Jsons.transform(e, Hook.class);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Hook update(String uid, HookUpdate hookUpdate) {
		Optional<HookEntity> o = hookRepo.findById(uid);// check
		if (o.isPresent()) {
			HookEntity e = o.get();

			if (hookUpdate.getDescription() != null) {
				e.setDescription(hookUpdate.getDescription());
			}

			e = hookRepo.save(e);

			logger.debug("update hook {}", uid);

			return Jsons.transform(e, Hook.class);
		} else {
			throw new BadArgumentException("hook {} not found", uid);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Hook trigger(String uid) {
		return trigger(uid, null, null);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Hook trigger(String uid, @Nullable Map<String, Object> args, @Nullable HookMatch match) {
		Optional<HookEntity> o = hookRepo.findById(uid);// check
		if (!o.isPresent()) {
			throw new NotFoundException("hook {} not found", uid);
		}
		HookEntity e = o.get();

		if (match != null) {
			boolean m = true;
			if (match.getZone() != null && !Objects.equal(match.getZone(), e.getZone())) {
				m = false;
			}
			if (!m) {
				throw new NotFoundException("hook {} not match", uid);
			}
		}

		Integer trigger = e.getTrigger();
		if (trigger == null || trigger.intValue() < 0) {
			trigger = 0;
		}

		Map<String, Object> wrapArgs = args == null ? new LinkedHashMap<>() : new LinkedHashMap<>(args);

		Hook hook = Jsons.transform(e, Hook.class);
		eventPublisher.publishEvent(new TriggerHookEvent(hook, wrapArgs));

		hook.setTrigger(trigger + 1);
		Runnable asyncUpdpateTrigger = () -> {
			eventPublisher.publishEvent(new TriggeredHookEvent(hook, wrapArgs));
		};
		// no matter commit or rollback
		asyncExService.asyncRunAfterTxCommit(asyncUpdpateTrigger);
		asyncExService.asyncRunAfterTxRollback(asyncUpdpateTrigger);

		logger.debug("triggered hook {}, trigger {}", uid, hook.getTrigger());

		return hook;
	}

	static class TriggeredHookEvent extends ApplicationEvent<Hook> {
		private static final long serialVersionUID = 1L;

		Map<String, Object> args;

		public TriggeredHookEvent(Hook hook, Map<String, Object> args) {
			super(hook);
			this.args = args;
		}

		public Map<String, Object> getArgs() {
			return args;
		}
	}

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleTriggeredHook(TriggeredHookEvent event) {
		Hook hook = event.getData();

		Integer trigger = hook.getTrigger();
		Integer life = hook.getTriggerLife();
		if (trigger != null) {
			if (life != null) {
				if (life.intValue() <= trigger.intValue()) {
					hookRepo.deleteById(hook.getUid());
					logger.debug("deleted hook {}, life {} <= {}", hook.getUid(), life, trigger);
					return;
				}
			}
			Optional<HookEntity> o = hookRepo.findById(hook.getUid());
			if (o.isPresent()) {
				HookEntity e = o.get();
				if (e.getTrigger() == null || e.getTrigger().intValue() < trigger) {
					e.setTrigger(trigger);
				}
			}
		}

	}

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleDeletedUser(DeletedUserEvent event) {
		User user = event.getData();
		ListPage<Hook> hooks = list(new HookFilter().withOwnerType(OWNER_USER).withOwnerUid(user.getUid()));
		for (Hook hook : hooks.getItems()) {
			hookRepo.deleteById(hook.getUid());
		}
	}

}