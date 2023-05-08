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
import onexas.coordinate.service.impl.entity.RoleEntity;
import onexas.coordinate.service.impl.entity.RoleUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "RoleUserRelationRepo")
public interface RoleUserRelationEntityRepo extends JpaRepository<RoleUserRelationEntity, RoleUserRelationEntity.PK> {

	@Query(value = "select distinct jo from cooRoleUserRelation e join e.role jo where e.userUid = ?1 order by jo.code asc")
	List<RoleEntity> findAllRolesByUserUid(String userUid);
	
	@Query(value = "select distinct jo from cooRoleUserRelation e join e.user jo where e.roleUid = ?1 order by jo.account asc")
	List<UserEntity> findAllUsersByRoleUid(String roleUid);

	@Modifying
	@Query(value = "delete from cooRoleUserRelation e where e.userUid = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByUserUid(String userUid);

	@Modifying
	@Query(value = "delete from cooRoleUserRelation e where e.roleUid = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByRoleUid(String roleUid);
}