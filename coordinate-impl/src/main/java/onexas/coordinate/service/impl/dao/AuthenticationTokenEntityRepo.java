package onexas.coordinate.service.impl.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.AuthenticationTokenEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "AuthenticationTokenEntityRepo")
public interface AuthenticationTokenEntityRepo extends JpaRepository<AuthenticationTokenEntity, Long> {

	@Query(value = "select e from cooAuthenticationToken as e where e.token = ?1")
	Optional<AuthenticationTokenEntity> findByToken(String token);
	
	@Query(value = "select e from cooAuthenticationToken as e where e.timeoutAt <= ?1")
	public Page<AuthenticationTokenEntity> findAllBeforeTimeout(Long timeout, Pageable pageable);
	
	@Query(value = "select e from cooAuthenticationToken as e where e.account = ?1 and e.domain = ?2")
	List<AuthenticationTokenEntity> findAllByAccountDomain(String account, String domain);
	
	@Query(value = "select e from cooAuthenticationToken as e where e.domain = ?1")
	List<AuthenticationTokenEntity> findAllByDomain(String domain);
}