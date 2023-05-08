package onexas.coordinate.service.impl.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "UserEntityRepo")
public interface UserEntityRepo extends JpaRepository<UserEntity, String> {

	@Query(value = "select e from cooUser as e where e.account = ?1 and e.domain = ?2")
	Optional<UserEntity> findByAccountDomain(String account, String domainCode);
	
	@Query(value = "select e from cooUser as e where e.aliasUid = ?1")
	Optional<UserEntity> findByAliasUid(String aliasUid);
	
	@Query(value = "select e from cooUser as e where "
			+ "(lower(e.account) like lower(?1) or lower(e.displayName) like lower(?1) or lower(e.email) like lower(?1)) "
			+ "and e.domain = ?2")
	Page<UserEntity> findAllByCriteriaDomain(String criteria, String domainCode, Pageable pageable);
	
	@Query(value = "select count(e)>0 from cooUser as e where e.account = ?1 and e.domain = ?2")
	boolean existsByUniqueConstraintAccountDomain(String account, String domainCode);
	
}