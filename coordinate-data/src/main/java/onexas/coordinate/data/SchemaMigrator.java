package onexas.coordinate.data;

import javax.sql.DataSource;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface SchemaMigrator {
	
	String getInfo();

	void migrate(DataSource dataSource);

}
