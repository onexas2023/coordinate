package onexas.coordinate.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.InfoCacheService;
import onexas.coordinate.service.impl.dao.InfoCacheEntityRepo;
import onexas.coordinate.service.impl.entity.InfoCacheEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "InfoCacheServiceImpl")
public class InfoCacheServiceImpl implements InfoCacheService {

	private static final int TOKEN_LOOP = 5;

	@Autowired
	InfoCacheEntityRepo infoCacheRepo;

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public String put(String info, long timeoutAt) {
		String token = Strings.randomUid(TOKEN_LOOP);
		InfoCacheEntity e = new InfoCacheEntity();
		e.setToken(token);
		e.setTimeout(timeoutAt);
		e.setInfo(info);
		e = infoCacheRepo.save(e);
		return token;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public String acquire(String token, boolean remove) {
		Optional<InfoCacheEntity> o = infoCacheRepo.findById(token);
		if (!o.isPresent()) {
			return null;
		}
		InfoCacheEntity e = o.get();
		if (e.getTimeout() < System.currentTimeMillis()) {
			return null;
		}
		String info = e.getInfo();
		if (remove) {
			infoCacheRepo.delete(o.get());
		}
		return info;
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public void prune(long timeBefore) {
		infoCacheRepo.deleteByTimeout(timeBefore);
	}

	@Override
	public long count() {
		return infoCacheRepo.count();
	}

}