package onexas.coordinate.service.impl.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.RoleEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "RoleEntityRepo")
public interface RoleEntityRepo extends JpaRepository<RoleEntity, String> {

	@Query(value = "select e from cooRole as e where e.code = ?1")
	Optional<RoleEntity> findByCode(String code);
	
	@Query(value = "select count(e)>0 from cooRole as e where e.code = ?1")
	boolean existsByUniqueConstraintCode(String code);
}