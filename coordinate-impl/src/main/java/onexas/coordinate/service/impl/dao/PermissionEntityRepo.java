package onexas.coordinate.service.impl.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.entity.PermissionEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "PermissionEntityRepo")
public interface PermissionEntityRepo extends JpaRepository<PermissionEntity, String> {

	@Query(value = "select e from cooPermission as e where e.principal = ?1 order by e.target, e.action asc")
	List<PermissionEntity> findAllByPrincipal(String principal);
	
	@Query(value = "select e from cooPermission as e where e.target = ?1  order by e.principal, e.action asc")
	List<PermissionEntity> findAllByTarget(String target);
	
	@Query(value = "select e from cooPermission as e where (e.principal = '*' or e.principal = ?1) and (e.target = '*' or e.target = ?2) order by e.target, e.action asc")
	List<PermissionEntity> findAllByPrincipalTargetWithAny(String princiapl, String target);

	@Modifying
	@Query(value = "delete from cooPermission as e where e.principal = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByPrincipal(String principal);
	
	@Modifying
	@Query(value = "delete from cooPermission as e where e.target = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByTarget(String target);
	
	@Query(value = "select count(e)>0 from cooPermission as e where e.principal = ?1 and e.target = ?2 and e.action = ?3")
	boolean existsByUniqueConstraintCode(String princial, String target, String action);
}