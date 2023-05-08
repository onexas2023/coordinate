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
import onexas.coordinate.service.impl.entity.PropertyEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "PropertyEntityRepo")
public interface PropertyEntityRepo extends JpaRepository<PropertyEntity, PropertyEntity.PK> {

	@Query(value = "select e from cooProperty as e where e.objUid = ?1")
	List<PropertyEntity> findAllByObjUid(String uid);

	@Query(value = "select e from cooProperty as e where e.objUid = ?1 and e.category = ?2")
	List<PropertyEntity> findAllByObjUid(String uid, String category);

	@Modifying
	@Query(value = "delete from cooProperty as e where e.objUid = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByObjUid(String uid);
}