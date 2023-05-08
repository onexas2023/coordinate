package onexas.coordinate.service.impl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.entity.LogEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "LogEntityRepo")
public interface LogEntityRepo extends JpaRepository<LogEntity, Long> {
	
	@Modifying
	@Query(value = "delete from cooLog as e where e.level <= ?1 and e.createdDateTime <= ?2")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByLevelCreatedDateTime(Integer levelLe, Long createdDateTimeBefore);
	
}