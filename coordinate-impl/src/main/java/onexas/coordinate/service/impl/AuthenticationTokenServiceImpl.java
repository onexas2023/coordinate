package onexas.coordinate.service.impl;

import static onexas.coordinate.service.GlobalCacheEvictService.UNLESS_RESULT_NULL;
import static onexas.coordinate.service.impl.Constants.CACHE_NAME_AUTHTOKEN;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.AuthenticationTokenCreate;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.User;
import onexas.coordinate.service.AsyncExService;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.GlobalCacheEvictService;
import onexas.coordinate.service.event.DeletedUserEvent;
import onexas.coordinate.service.event.DisabledDomainEvent;
import onexas.coordinate.service.event.DisabledUserEvent;
import onexas.coordinate.service.impl.dao.AuthenticationTokenEntityRepo;
import onexas.coordinate.service.impl.dao.PropertyEntityRepo;
import onexas.coordinate.service.impl.entity.AuthenticationTokenEntity;
import onexas.coordinate.service.impl.entity.PropertyEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@DependsOn(AppContext.BEAN_NAME)
@Service(Env.NS_BEAN + "AuthenticationTokenServiceImpl")
public class AuthenticationTokenServiceImpl implements AuthenticationTokenService {

	private static final int TOKEN_LOOP = 6;
	private static final String PROP_PREFIX = "authtoken-";

	@Autowired
	AuthenticationTokenEntityRepo atRepo;

	@Autowired
	PropertyEntityRepo propertyRepo;

	@Autowired
	AsyncExService asyncExService;
	
	@Autowired
	GlobalCacheEvictService cacheEvictService;
	
	@Value("${coordinate.authentication.token-timeout:60m}")
	String tokenTimeout;
	
	@Value("${coordinate.authentication.token-extend-after:1m}")
	String tokenExtendAfter;
	

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AuthenticationToken create(AuthenticationTokenCreate authCreate) {
		AuthenticationTokenEntity t = Jsons.transform(authCreate, AuthenticationTokenEntity.class);
		t.setToken(Strings.randomUid(TOKEN_LOOP));
		t.setTimeoutAt(System.currentTimeMillis() + getTokenTimeout());
		t = atRepo.save(t);
		return Jsons.transform(t, AuthenticationToken.class);
	}
	
	private long getTokenTimeout() {
		return Strings.parseMillisecond(tokenTimeout);
	}
	
	public boolean shouldExtend(long timeoutAt) {
		long extendAfter = Strings.parseMillisecond(tokenExtendAfter);
		long now = System.currentTimeMillis();
		long tokenTimeout = getTokenTimeout();
		return now > timeoutAt - tokenTimeout + extendAfter;
	}

	@Override
	@Cacheable(cacheNames = CACHE_NAME_AUTHTOKEN, unless = UNLESS_RESULT_NULL)
	public AuthenticationToken find(String token) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		return o.isPresent() ? o.get() : null;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AuthenticationToken extend(String token) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		if (!o.isPresent()) {
			throw new NotFoundException("token {} not found", Strings.ellipsis(token, 10));
		}
		AuthenticationTokenEntity e = o.get();
		e.setTimeoutAt(System.currentTimeMillis() + getTokenTimeout());
		
		cacheEvictService.evict(token, CACHE_NAME_AUTHTOKEN);
		
		return Jsons.transform(e, AuthenticationToken.class);
	}

	@Override
	public void delete(String token, boolean quiet) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		if (!o.isPresent()) {
			if (!quiet) {
				throw new NotFoundException("token {} not found", Strings.ellipsis(token, 10));
			}
			return;
		}
		deleteEntity(o.get());
		
		cacheEvictService.evict(token, CACHE_NAME_AUTHTOKEN);
	}

	@Override
	public Map<String, String> getProperties(String token) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		if (!o.isPresent()) {
			throw new NotFoundException("token {} not found", Strings.ellipsis(token, 10));
		}

		Map<String, String> m = new LinkedHashMap<>();

		for (PropertyEntity e : propertyRepo.findAllByObjUid(getPropUid(o.get().getId()))) {
			m.put(e.getName(), e.getValue());
		}

		return m;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void setProperties(String token, Map<String, String> properties) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		if (!o.isPresent()) {
			throw new NotFoundException("token {} not found", Strings.ellipsis(token, 10));
		}

		properties = new LinkedHashMap<>(properties);
		for (PropertyEntity e : propertyRepo.findAllByObjUid(getPropUid(o.get().getId()))) {
			String n = e.getName();
			if (properties.containsKey(n)) {
				String v = properties.get(n);
				if (v == null) {
					propertyRepo.delete(e);
				} else {
					e.setValue(v);
				}
				properties.remove(n);
			}
		}
		for (String n : properties.keySet()) {
			String v = properties.get(n);
			if (v != null) {
				PropertyEntity e = new PropertyEntity();
				e.setObjUid(getPropUid(o.get().getId()));
				e.setName(n);
				e.setValue(v);
				propertyRepo.save(e);
			}
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void setProperty(String token, String name, String value) {
		Map<String, String> m = new HashMap<>();
		m.put(name, value);
		setProperties(token, m);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void deleteProperties(String token, Set<String> name) {
		Optional<AuthenticationTokenEntity> o = atRepo.findByToken(token);
		if (!o.isPresent()) {
			return;
		}

		for (String n : name) {
			propertyRepo.deleteById(new PropertyEntity.PK(getPropUid(o.get().getId()), n));
		}
		propertyRepo.flush();
	}

	private String getPropUid(Long tokenId) {
		return PROP_PREFIX + tokenId;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void prune(final Long time) {
		PageRequest pr = PageRequest.of(0, AppContext.config().getInteger("coordinate.authentication.tokenPruneMax", 500));
		Page<AuthenticationTokenEntity> page = atRepo.findAllBeforeTimeout(time, pr);
		for (AuthenticationTokenEntity e : page.getContent()) {
			deleteEntity(e);
		}
		atRepo.flush();
		propertyRepo.flush();
		if (page.getTotalPages() > 1) {
			// trigger async again
			asyncExService.asyncRunAfterTxCommit(() -> {
				AppContext.getBean(AuthenticationTokenService.class).prune(time);
			});
		}
		if(page.getSize()>0) {
			cacheEvictService.clear(CACHE_NAME_AUTHTOKEN);
		}
	}

	@Override
	public long count() {
		return atRepo.count();
	}

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleDisabledUser(DisabledUserEvent event) {
		asyncExService.asyncRunAfterTxCommit(() -> {
			User user = event.getData();
			pruneByUser(user, event.getCreatedDateTime());
		});
	}

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleDisabledDomain(DisabledDomainEvent event) {
		asyncExService.asyncRunAfterTxCommit(() -> {
			Domain domain = event.getData();
			pruneByDomain(domain, event.getCreatedDateTime());
		});
	}

	@EventListener
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void handleDeletedUser(DeletedUserEvent event) {
		asyncExService.asyncRunAfterTxCommit(() -> {
			User user = event.getData();
			pruneByUser(user, event.getCreatedDateTime());
		});
	}

	private void pruneByUser(User user, long createdDateTimeBefore) {
		for (AuthenticationTokenEntity e : atRepo.findAllByAccountDomain(user.getAccount(), user.getDomain())) {
			if (createdDateTimeBefore >= e.getCreatedDateTime()) {
				deleteEntity(e);
			}
		}
		cacheEvictService.clear(CACHE_NAME_AUTHTOKEN);
	}

	private void pruneByDomain(Domain domain, long createdDateTimeBefore) {
		for (AuthenticationTokenEntity e : atRepo.findAllByDomain(domain.getCode())) {
			if (createdDateTimeBefore >= e.getCreatedDateTime()) {
				deleteEntity(e);
			}
		}
		cacheEvictService.clear(CACHE_NAME_AUTHTOKEN);
	}

	private void deleteEntity(AuthenticationTokenEntity e) {
		atRepo.delete(e);
		propertyRepo.deleteByObjUid(getPropUid(e.getId()));
	}

}