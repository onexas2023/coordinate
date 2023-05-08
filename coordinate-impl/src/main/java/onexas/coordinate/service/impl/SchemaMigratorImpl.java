package onexas.coordinate.service.impl;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.core.env.Environment;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.data.SchemaMigrator;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SchemaMigratorImpl implements SchemaMigrator {

	
	private String getBaseLine() {
		Environment env = AppContext.bean(Environment.class);
		String baselineVersion = env.getProperty("coordinate-data.schema.migration.baseline.coordinate", "0.0.0");
		return baselineVersion;
	}
	
	@Override
	public void migrate(DataSource dataSource) {
		String baselineVersion = getBaseLine();
		
		//Note: the baseline row record is not inserted if there is no any table in this database yet
		FluentConfiguration configuration = new FluentConfiguration();
		configuration.dataSource(dataSource);
		configuration.table("flyway_history_coordinate");
		configuration.baselineDescription("baseline from "+baselineVersion);
		configuration.baselineOnMigrate(true);
		configuration.baselineVersion(MigrationVersion.fromVersion(baselineVersion));
		configuration.locations("classpath:schema/coordinate");
		configuration.cleanDisabled(true);
		Flyway flyway = configuration.load();
		flyway.migrate();
	}
	
	@Override
	public String getInfo() {
		return "coordinate, baseline "+getBaseLine();
	}

}
