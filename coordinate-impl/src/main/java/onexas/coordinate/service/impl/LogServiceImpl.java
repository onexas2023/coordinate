package onexas.coordinate.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import onexas.coordinate.model.Log;
import onexas.coordinate.model.LogCreate;
import onexas.coordinate.model.LogFilter;
import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.LogService;
import onexas.coordinate.service.UserActivityContext;
import onexas.coordinate.service.impl.dao.LogEntityDao;
import onexas.coordinate.service.impl.dao.LogEntityRepo;
import onexas.coordinate.service.impl.entity.LogEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "LogServiceImpl")
public class LogServiceImpl implements LogService {

	private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

	@Autowired
	LogEntityRepo logRepo;

	@Autowired
	LogEntityDao logDao;

	@Autowired
	UserActivityContext userActivityContext;

	@Override
	public ListPage<Log> list(LogFilter filter) {
		List<LogEntity> list = new LinkedList<>();
		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;
		Sort sort = Sort.by(direction, "createdDateTime");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();
		Integer pageTotal = 1;
		Long itemTotal;

		if (filter == null) {
			list = logRepo.findAll(sort);
			itemTotal = Long.valueOf(list.size());
		} else {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField }, new String[] { "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}
			Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

			Page<LogEntity> page = logDao.findAll(filter, pageable);
			list = page.getContent();
			pageTotal = page.getTotalPages();
			itemTotal = page.getTotalElements();
		}
		return new ListPage<>(Jsons.transform(list, new TypeReference<List<Log>>() {
		}), pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public Log get(Long id) {
		Optional<LogEntity> o = logRepo.findById(id);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Log.class);
		}
		throw new NotFoundException("log {} not found", id);
	}

	@Override
	public Log find(Long id) {
		Optional<LogEntity> o = logRepo.findById(id);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Log.class);
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void prune(int levelLe, long dateTimeBefore) {
		logRepo.deleteByLevelCreatedDateTime(levelLe, dateTimeBefore);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(Long id, boolean quiet) {
		if ((find(id)) == null) {
			if (!quiet) {
				throw new NotFoundException("log {} not found", id);
			}
		} else {
			logRepo.deleteById(id);
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log create(LogCreate logCreate) {

		Integer level = logCreate.getLevel();
		if (level == null) {
			throw new BadArgumentException("unknown log level");
		}
		String reporter = logCreate.getReporter();

		Logger logger;
		if (Strings.isBlank(reporter)) {
			logger = LogServiceImpl.logger;
		} else {
			logger = LoggerFactory.getLogger(reporter);
		}

		if (level <= Log.DEBUG) {
			logger.debug(logCreate.getContent());
		} else if (level == Log.INFO) {
			logger.info(logCreate.getContent());
		} else if (level == Log.WARN) {
			logger.warn(logCreate.getContent());
		} else if (level >= Log.ERROR) {
			logger.error(logCreate.getContent());
		}

		LogEntity log = Jsons.transform(logCreate, LogEntity.class);

		UserActivityToken userActivityToken = userActivityContext.getTokenIfAny();
		if (userActivityToken != null) {
			log.setRequestUid(userActivityToken.getRequestUid());
			log.setUserAccount(userActivityToken.getUserAccount());
			log.setUserDomain(userActivityToken.getUserDomain());
		}

		log = logRepo.save(log);
		return Jsons.transform(log, Log.class);
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log debug(String reporter, String objUid, String objType, String subjectUid, String subjectType,
			String content, Object... contentArgs) {
		LogCreate logCreate = new LogCreate().withLevel(Log.DEBUG).withReporter(reporter).withObjUid(objUid)
				.withObjType(objType).withSubjectUid(subjectUid).withSubjectType(subjectType)
				.withContent(Strings.format(content, contentArgs));
		return create(logCreate);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log debug(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		return debug(reporter == null ? null : reporter.getName(), objUid, objType == null ? null : objType.getName(),
				subjectUid, subjectType == null ? null : subjectType.getName(), content, contentArgs);
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log info(String reporter, String objUid, String objType, String subjectUid, String subjectType,
			String content, Object... contentArgs) {
		LogCreate logCreate = new LogCreate().withLevel(Log.INFO).withReporter(reporter).withObjUid(objUid)
				.withObjType(objType).withSubjectUid(subjectUid).withSubjectType(subjectType)
				.withContent(Strings.format(content, contentArgs));
		return create(logCreate);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log info(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		return info(reporter == null ? null : reporter.getName(), objUid, objType == null ? null : objType.getName(),
				subjectUid, subjectType == null ? null : subjectType.getName(), content, contentArgs);
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log warn(String reporter, String objUid, String objType, String subjectUid, String subjectType,
			String content, Object... contentArgs) {
		LogCreate logCreate = new LogCreate().withLevel(Log.WARN).withReporter(reporter).withObjUid(objUid)
				.withObjType(objType).withSubjectUid(subjectUid).withSubjectType(subjectType)
				.withContent(Strings.format(content, contentArgs));
		return create(logCreate);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log warn(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		return warn(reporter == null ? null : reporter.getName(), objUid, objType == null ? null : objType.getName(),
				subjectUid, subjectType == null ? null : subjectType.getName(), content, contentArgs);
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log error(String reporter, String objUid, String objType, String subjectUid, String subjectType,
			String content, Object... contentArgs) {
		LogCreate logCreate = new LogCreate().withLevel(Log.ERROR).withReporter(reporter).withObjUid(objUid)
				.withObjType(objType).withSubjectUid(subjectUid).withSubjectType(subjectType)
				.withContent(Strings.format(content, contentArgs));
		return create(logCreate);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Log error(Class<?> reporter, String objUid, Class<?> objType, String subjectUid, Class<?> subjectType,
			String content, Object... contentArgs) {
		return error(reporter == null ? null : reporter.getName(), objUid, objType == null ? null : objType.getName(),
				subjectUid, subjectType == null ? null : subjectType.getName(), content, contentArgs);
	}

}