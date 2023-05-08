package onexas.coordinate.service.impl.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.entity.JobEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "JobEntityRepo")
public interface JobEntityRepo extends JpaRepository<JobEntity, Long> {
	
	@Modifying
	@Query(value = "delete from cooJob as e where e.finishedDateTime <= ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByFinishedDateTime(Long createdDateTimeBefore);

	@Query(value = "select e from cooJob as e where e.queryUid = ?1")
	Optional<JobEntity> findByQueryUid(String queryUid);
	
}