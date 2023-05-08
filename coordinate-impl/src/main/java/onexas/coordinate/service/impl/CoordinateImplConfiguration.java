package onexas.coordinate.service.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.dao.DaoScan;
/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateImplConfiguration")
@EnableJpaRepositories(basePackageClasses = {
		DaoScan.class }, entityManagerFactoryRef = CoordinateEntityManageConfiguration.ENTITY_MANAGER_FACTORY, 
				transactionManagerRef = CoordinateEntityManageConfiguration.TX_MANAGER)
public class CoordinateImplConfiguration {

}
