package onexas.coordinate.data;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Classes;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateSchemaConfiguration")
public class CoordinateSchemaConfiguration {

	private static Logger logger = LoggerFactory.getLogger(CoordinateSchemaConfiguration.class);

	@Value("${coordinate-data.schema.migration.enabled:true}")
	boolean enabled;

	public static final String SCHEMA_MIGRATION = "coordinateSchemaMigration";

	@Profile("!"+Constants.PROFILE_DISABLE_ENTITY)
	@Bean(SCHEMA_MIGRATION)
	public Object migrate(@Qualifier(CoordinateDataConfiguration.DATA_SOURCE) DataSource coordinateDataSource) {
		if (!enabled) {
			logger.info("Schema migration disabled");
			return "disabled";
		}
		Config config = AppContext.config();

		List<String> migratorClzs = config.getStringList("coordinateData.schemaMigratorClass");
		List<SchemaMigrator> migrators = new LinkedList<>();
		for (String m : migratorClzs) {
			try {
				Class<?> clz = Classes.forNameByThread(m);
				Object mc = clz.newInstance();
				if (!(mc instanceof SchemaMigrator)) {
					throw new IllegalStateException("not a SchemaMigrator, is " + mc);
				}
				migrators.add((SchemaMigrator) mc);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		
		
		Long timeout = config.getMillisecond("coordinateData.schemaMigratorTimeout", "3m");
		
		Locker locker = AppContext.bean(Locker.class);
		
		return locker.runOrWait("coordinateData.schemaMigrator", new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				logger.info("Schema migration locked");
				for (SchemaMigrator m : migrators) {
					logger.info("Start to migrate {}", m.getInfo());
					m.migrate(coordinateDataSource);
				}
				logger.info("Schema migration done");
				return "succeed";
			}
			
		}, timeout);
	}

}