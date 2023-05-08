package onexas.coordinate.service.impl.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.OrganizationEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "OrganizationEntityRepo")
public interface OrganizationEntityRepo extends JpaRepository<OrganizationEntity, String> {

	@Query(value = "select e from cooOrganization as e where e.code = ?1")
	Optional<OrganizationEntity> findByCode(String code);
	
	@Query(value = "select count(e)>0 from cooOrganization as e where e.code = ?1")
	boolean existsByUniqueConstraintCode(String code);
}