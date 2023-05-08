package onexas.coordinate.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateConfiguration")
@PropertySource("classpath:coordinate.properties")
public class CoordinateConfiguration {

}