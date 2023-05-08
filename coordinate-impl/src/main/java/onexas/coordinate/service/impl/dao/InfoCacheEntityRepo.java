package onexas.coordinate.service.impl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.entity.InfoCacheEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "InfoCacheEntityRepo")
public interface InfoCacheEntityRepo extends JpaRepository<InfoCacheEntity, String> {
	
	@Modifying
	@Query(value = "delete from cooInfoCache as e where e.timeout <= ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByTimeout(Long timeBefore);
	
}