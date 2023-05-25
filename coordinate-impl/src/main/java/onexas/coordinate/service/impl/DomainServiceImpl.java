package onexas.coordinate.service.impl;

import static onexas.coordinate.service.GlobalCacheEvictService.UNLESS_RESULT_NULL;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_DOMAIN;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_DOMAIN_CONFIG;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_DOMAIN_CONFIGYAML;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.common.util.Yamls;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainCreate;
import onexas.coordinate.model.DomainUpdate;
import onexas.coordinate.model.MapDomainConfig;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.GlobalCacheEvictService;
import onexas.coordinate.service.LogService;
import onexas.coordinate.service.event.BeforeDeleteDomainEvent;
import onexas.coordinate.service.event.DeletedDomainEvent;
import onexas.coordinate.service.event.DisabledDomainEvent;
import onexas.coordinate.service.impl.dao.DomainEntityRepo;
import onexas.coordinate.service.impl.entity.DomainEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "DomainServiceImpl")
public class DomainServiceImpl implements DomainService {
	
	private static final Logger logger = LoggerFactory.getLogger(DomainServiceImpl.class);

	@Autowired
	DomainEntityRepo domainRepo;

	@Autowired
	ApplicationEventPublisher eventPublisher;
	
	@Autowired
	GlobalCacheEvictService cacheEvictService;

	@Autowired
	LogService logService;
	
	@Autowired
	AsyncExService asyncExService;

	@Override
	public List<Domain> list() {
		List<Domain> list = new LinkedList<>();
		int localIdx = -1;
		int idx = 0;
		for (DomainEntity e : domainRepo.findAll(Sort.by(Order.asc("code")))) {
			list.add(Jsons.transform(e, Domain.class));
			if (e.getCode().equals(Domain.LOCAL)) {
				localIdx = idx;
			}
			idx++;
		}
		if (localIdx == -1) {
			Domain local = newDefaultLocalDomain();
			list.add(0, local);
		} else {
			// move to first
			Domain local = list.remove(localIdx);
			list.add(0, local);
		}
		return list;
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_DOMAIN, unless = UNLESS_RESULT_NULL)
	public Domain get(String code) {
		Optional<DomainEntity> o = domainRepo.findById(code);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Domain.class);
		}
		if (Domain.LOCAL.equals(code)) {
			return newDefaultLocalDomain();
		}
		throw new NotFoundException("domain {} not found", code);
	}

	private Domain newDefaultLocalDomain() {
		Domain l = new Domain();
		l.setCode(Domain.LOCAL);
		l.setProvider(Domain.LOCAL);
		l.setName("Local");
		l.setDisabled(false);
		return l;
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_DOMAIN, unless = UNLESS_RESULT_NULL)
	public Domain find(String code) {
		Optional<DomainEntity> o = domainRepo.findById(code);
		if (o.isPresent()) {
			return Jsons.transform(o.get(), Domain.class);
		}
		if (Domain.LOCAL.equals(code)) {
			return newDefaultLocalDomain();
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Domain create(DomainCreate domainCreate) {
		if (Domain.LOCAL.equals(domainCreate.getCode())) {
			throw new BadArgumentException("local is already existed");
		}
		if (domainRepo.existsById(domainCreate.getCode())) {
			throw new BadArgumentException("{} is already existed", domainCreate.getCode()) ;
		}
		String configYaml = null;
		if (!Strings.isBlank(domainCreate.getConfigYaml())) {
			configYaml = domainCreate.getConfigYaml().trim();
			//check
			Yamls.objectify(configYaml, Map.class);
		}

		DomainEntity e = Jsons.transform(domainCreate, DomainEntity.class);
		if (e.getName() == null) {
			e.setName(e.getCode());
		}
		if (e.getDisabled() == null) {
			e.setDisabled(false);
		}

		e.setConfigYaml(configYaml);
		e = domainRepo.save(e);

		logService.info(getClass(), e.getCode(), Domain.class, null, null, "Created domain {}/{}", e.getCode(),
				e.getName());

		return Jsons.transform(e, Domain.class);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Domain update(String domainCode, DomainUpdate domainUpdate) {
		Optional<DomainEntity> o = domainRepo.findById(domainCode);
		DomainEntity e = null;
		if (!o.isPresent()) {
			if (Domain.LOCAL.equals(domainCode)) {
				e = Jsons.transform(newDefaultLocalDomain(), DomainEntity.class);
				e = domainRepo.save(e);
			} else {
				throw new BadArgumentException("domain {} not found", domainCode);
			}
		} else {
			e = o.get();
		}

		Boolean disabled = e.getDisabled();
		boolean fireDisabled = false;

		if (domainUpdate.getName() != null) {
			e.setName(domainUpdate.getName());
		}
		if (domainUpdate.getDescription() != null) {
			e.setDescription(domainUpdate.getDescription());
		}
		if (domainUpdate.getDisabled() != null) {
			e.setDisabled(domainUpdate.getDisabled());
		}
		if (domainUpdate.getConfigYaml() != null) {
			e.setConfigYaml(domainUpdate.getConfigYaml().trim());
			//check
			Yamls.objectify(e.getConfigYaml(), Map.class);
		}

		if (Boolean.FALSE.equals(disabled) && Boolean.TRUE.equals(e.getDisabled())) {
			fireDisabled = true;
		}

		e = domainRepo.save(e);

		logService.info(getClass(), e.getCode(), Domain.class, null, null, "Update domain {}/{}", e.getCode(),
				e.getName());
		
		
		cacheEvictService.evict(domainCode, CACHE_NAME_DOMAIN, CACHE_NAME_DOMAIN_CONFIG, CACHE_NAME_DOMAIN_CONFIGYAML);
		
		Domain domain = Jsons.transform(e, Domain.class);

		if (fireDisabled) {
			eventPublisher.publishEvent(new DisabledDomainEvent(domain));
		}

		return domain;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void delete(String domainCode, boolean quiet) {
		if (Domain.LOCAL.equals(domainCode)) {
			if (!quiet) {
				throw new IntegrityViolationException("can not delete domain {}", Domain.LOCAL);
			}
			return;
		}
		Domain domain;
		if ((domain = find(domainCode)) == null) {
			if (!quiet) {
				throw new NotFoundException("domain {} not found", domainCode);
			}
		} else {

			eventPublisher.publishEvent(new BeforeDeleteDomainEvent(domain));

			domainRepo.deleteById(domainCode);

			logService.info(getClass(), domain.getCode(), Domain.class, null, null, "Deleted domain {}/{}",
					domain.getCode(), domain.getName());
			
			cacheEvictService.evict(domainCode, CACHE_NAME_DOMAIN, CACHE_NAME_DOMAIN_CONFIG, CACHE_NAME_DOMAIN_CONFIGYAML);

			asyncExService.asyncRunAfterTxCommit(() -> {
				eventPublisher.publishEvent(new DeletedDomainEvent(domain));
			});
		}
	}

	@Override
	@Cacheable(cacheNames= CACHE_NAME_DOMAIN_CONFIG, unless = UNLESS_RESULT_NULL)
	public DomainConfig getConfig(String domainCode) {
		Optional<DomainEntity> o = domainRepo.findById(domainCode);
		if (!o.isPresent()) {
			if (Domain.LOCAL.equals(domainCode)) {
				return new MapDomainConfig();
			}
			throw new NotFoundException("domain {} not found", domainCode);
		}
		String configYaml = o.get().getConfigYaml();
		try {
			return Strings.isBlank(configYaml) ? new MapDomainConfig() : Yamls.objectify(configYaml, MapDomainConfig.class);
		} catch (Exception x) {
			logger.warn(x.getMessage(), x);
			return new MapDomainConfig();
		}
	}

	@Override
	@Cacheable(cacheNames= CACHE_NAME_DOMAIN_CONFIGYAML, unless = UNLESS_RESULT_NULL)
	public String getConfigYaml(String domainCode) {
		Optional<DomainEntity> o = domainRepo.findById(domainCode);
		if (!o.isPresent()) {
			if (Domain.LOCAL.equals(domainCode)) {
				return "";
			}
			throw new NotFoundException("domain {} not found", domainCode);
		}
		String configYaml = o.get().getConfigYaml();
		return Strings.isBlank(configYaml) ? "" : configYaml.trim();
	}
}