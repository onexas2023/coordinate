package onexas.coordinate.service.impl;

import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.service.CacheEvictService;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.model.CacheEvict;
import onexas.coordinate.service.GlobalCacheEvictService;
import onexas.coordinate.service.event.GlobalEvent;
import onexas.coordinate.service.jms.GlobalEventSender;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "GlobalCacheEvictServiceImpl")
public class GlobalCacheEvictServiceImpl implements GlobalCacheEvictService {

	private static Logger logger = LoggerFactory.getLogger(GlobalCacheEvictServiceImpl.class);

	@Autowired
	CacheEvictService cacheEvictService;

	@Autowired
	GlobalEventSender globalEventSender;

	String instanceUid;

	public GlobalCacheEvictServiceImpl() {
		instanceUid = Strings.randomUid(2);
	}

	private void localClear() {
		cacheEvictService.clear();
	}

	private void localClear(String... names) {
		cacheEvictService.clear(names);
	}

	private void localEvict(Object key, String... names) {
		cacheEvictService.evict(key, names);
	}

	@Override
	public void clear() {
		localClear();
		globalEventSender.send(new GlobalEvent(GLOBAL_EVENT_CACHE_EVICT, instanceUid, new CacheEvict()));
	}

	@Override
	public void clear(String... names) {
		localClear(names);
		globalEventSender
				.send(new GlobalEvent(GLOBAL_EVENT_CACHE_EVICT, instanceUid, new CacheEvict(Arrays.asList(names))));
	}

	@Override
	public void evict(Serializable key, String... names) {
		localEvict(key, names);
		globalEventSender.send(
				new GlobalEvent(GLOBAL_EVENT_CACHE_EVICT, instanceUid, new CacheEvict(key, Arrays.asList(names))));
	}

	@EventListener
	public synchronized void handleGlobalEvent(GlobalEvent event) {
		if (GLOBAL_EVENT_CACHE_EVICT.equals(event.getName()) && !instanceUid.equals(event.getPosterUid())) {
			Serializable d = event.getData();
			if (d != null && d instanceof CacheEvict) {
				CacheEvict cacheEvict = (CacheEvict) d;
				if (logger.isDebugEnabled()) {
					logger.debug("global cache evict : {}", Jsons.jsonify(cacheEvict));
				}
				if (cacheEvict.getKey() != null && cacheEvict.getNames() != null) {
					localEvict(cacheEvict.getKey(),
							cacheEvict.getNames().toArray(new String[cacheEvict.getNames().size()]));
				} else if (cacheEvict.getNames() != null) {
					localClear(cacheEvict.getNames().toArray(new String[cacheEvict.getNames().size()]));
				} else {
					localClear();
				}
			}
		}
	}

}