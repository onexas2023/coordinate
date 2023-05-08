package onexas.axes.app;

import org.springframework.boot.builder.SpringApplicationBuilder;

import onexas.coordinate.common.app.CoordinateBootApplication;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AxesApplication{

	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(CoordinateBootApplication.class).properties(CoordinateBootApplication.getDefaultProperties()).build()
				.run(args);
	}

}
