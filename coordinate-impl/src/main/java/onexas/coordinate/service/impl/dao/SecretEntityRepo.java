package onexas.coordinate.service.impl.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.SecretEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "SecretEntityRepo")
public interface SecretEntityRepo extends JpaRepository<SecretEntity, String> {

	@Query(value = "select e from cooSecret as e where e.code = ?1")
	Optional<SecretEntity> findByCode(String code);

	@Query(value = "select count(e)>0 from cooSecret as e where e.code = ?1")
	boolean existsByUniqueConstraintCode(String code);

}