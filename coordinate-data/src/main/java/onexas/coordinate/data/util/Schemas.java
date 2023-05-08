package onexas.coordinate.data.util;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Schemas {

	private static final Logger logger = LoggerFactory.getLogger(Schemas.class);

	public static void checkAndInit(DataSource dataSource, String checkTableSchema, String initSchema) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		try {
			// check
			logger.debug("check table by {}", checkTableSchema);
			template.execute(checkTableSchema);
		} catch (BadSqlGrammarException x) {
			logger.debug("init schema with {}", checkTableSchema);
			// init if table not found
			template.execute(initSchema);
		}
	}
}
