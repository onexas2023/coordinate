package onexas.coordinate.app.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import onexas.coordinate.common.app.CoordinateBootApplication;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CoordinateBootApplication.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:coordinate-app-test.properties")
public class CoordinateAppTestBase extends CoordinateImplTestBase{
}
