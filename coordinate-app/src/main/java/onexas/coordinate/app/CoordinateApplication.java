package onexas.coordinate.app;

import org.springframework.boot.builder.SpringApplicationBuilder;

import onexas.coordinate.common.app.CoordinateBootApplication;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CoordinateApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(CoordinateBootApplication.class).properties(CoordinateBootApplication.getDefaultProperties()).build()
				.run(args);
	}

}
