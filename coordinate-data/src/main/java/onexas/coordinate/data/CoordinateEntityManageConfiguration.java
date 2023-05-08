package onexas.coordinate.data;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateEntityManageConfiguration")
@EnableTransactionManagement
@PropertySource("classpath:coordinate-data.properties")
@DependsOn(AppContext.BEAN_NAME)
public class CoordinateEntityManageConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(CoordinateEntityManageConfiguration.class);

	public static final String PERSISTENT_UNIT = "coordinate";
	public static final String ENTITY_MANAGER_FACTORY = "coordinateEntityManagerFactory";
	public static final String TX_MANAGER = "coordinateTransactionManager";

	@Autowired
	Environment env;

	@Profile("!" + Constants.PROFILE_DISABLE_ENTITY)
	@Bean(name = { ENTITY_MANAGER_FACTORY })
	public LocalContainerEntityManagerFactoryBean coordinateEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier(CoordinateDataConfiguration.DATA_SOURCE) DataSource dataSource,
			@Qualifier(CoordinateSchemaConfiguration.SCHEMA_MIGRATION) Object schemaMigration) {
		String[] packages = null;
		List<String> pkgList = AppContext.config().getStringList("coordinateData.entityPackage");

		if (pkgList == null || pkgList.size() == 0) {
			packages = new String[] { "empty-package" };
			logger.info("No entity package found to scan, you can register entity scan by set coordinateData.entityPackage");
		} else {
			packages = Arrays.stream(pkgList.toArray(new String[pkgList.size()])).map(val -> {
				logger.info("Register entity-package {}", val);
				return val.trim();
			}).toArray(String[]::new);
		}

		return builder.dataSource(dataSource).packages(packages).persistenceUnit(PERSISTENT_UNIT).build();
	}

	@Profile("!" + Constants.PROFILE_DISABLE_ENTITY)
	@Bean(name = { TX_MANAGER })
	public PlatformTransactionManager coordinateTransactionManager(
			@Qualifier(ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager(entityManagerFactory);
		return txManager;
	}
	
}