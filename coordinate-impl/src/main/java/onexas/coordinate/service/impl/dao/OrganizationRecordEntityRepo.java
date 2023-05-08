package onexas.coordinate.service.impl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.service.impl.entity.OrganizationRecordEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "OrganizationRecordEntityRepo")
public interface OrganizationRecordEntityRepo extends JpaRepository<OrganizationRecordEntity, String> {

}