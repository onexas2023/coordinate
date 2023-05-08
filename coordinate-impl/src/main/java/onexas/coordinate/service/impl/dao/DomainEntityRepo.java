package onexas.coordinate.service.impl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.DomainEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "DomainEntityRepo")
public interface DomainEntityRepo extends JpaRepository<DomainEntity, String> {

}