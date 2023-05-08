package onexas.coordinate.data;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.util.Schemas;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateDataConfiguration")
@PropertySource("classpath:coordinate-data.properties")
public class CoordinateDataConfiguration {

	public static final String DATA_SOURCE = "coordinateDataSource";

	private static final String LOCK_TABLE_NAME = "COD_DB_LOCK";

	/**
	 * Override the default data and set it as primary for easy to use, this also
	 * avoid some no-qualifier inject issue such as session-jdbc
	 * 
	 * @return
	 */
	@Primary
	@Profile("!"+Constants.PROFILE_DISABLE_DATA_SOURCE)
	@Bean(name = { "dataSource" })
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Profile("!"+Constants.PROFILE_DISABLE_DATA_SOURCE)
	@Bean
	public LockProvider lockProvider(@Qualifier("dataSource") DataSource dataSource) {
		try {
			Schemas.checkAndInit(dataSource, Classes.getResourceAsString(getClass().getClassLoader(), "schema/coordinate-data-shedlock-check-mariadb.sql", Strings.UTF8),
					Classes.getResourceAsString(getClass().getClassLoader(), "schema/coordinate-data-shedlock-init-mariadb.sql", Strings.UTF8));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return new JdbcTemplateLockProvider(dataSource, LOCK_TABLE_NAME);
	}
	

	@Profile("!"+Constants.PROFILE_DISABLE_ENTITY)
	@Bean(name = { DATA_SOURCE })
	@ConfigurationProperties(prefix = "coordinate.datasource")
	public DataSource coordinateDataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}
}