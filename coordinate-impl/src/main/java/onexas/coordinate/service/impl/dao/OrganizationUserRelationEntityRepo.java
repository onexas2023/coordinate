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
import onexas.coordinate.service.impl.entity.OrganizationEntity;
import onexas.coordinate.service.impl.entity.OrganizationUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "OrganizationUserRelationRepo")
public interface OrganizationUserRelationEntityRepo extends JpaRepository<OrganizationUserRelationEntity, OrganizationUserRelationEntity.PK> {

	@Query(value = "select distinct jo from cooOrganizationUserRelation e join e.organization jo where e.userUid = ?1 order by jo.code asc")
	List<OrganizationEntity> findAllOrganizationsByUserUid(String userUid);
	
	@Query(value = "select distinct jo from cooOrganizationUserRelation e join e.user jo where e.organizationUid = ?1 order by jo.account asc")
	List<UserEntity> findAllUsersByOrganizationUid(String organizationUid);
	
	@Query(value = "select r from cooOrganizationUserRelation r where r.organizationUid = ?1")
	List<OrganizationUserRelationEntity> findAllByOrganizationUid(String organizationUid);

	@Query(value = "select r from cooOrganizationUserRelation r where r.userUid = ?1")
	List<OrganizationUserRelationEntity> findAllByUserUid(String organizationUid);
	
	@Modifying
	@Query(value = "delete from cooOrganizationUserRelation e where e.userUid = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByUserUid(String userUid);

	@Modifying
	@Query(value = "delete from cooOrganizationUserRelation e where e.organizationUid = ?1")
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	void deleteByOrganizationUid(String organizationUid);
}