package onexas.coordinate.common.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import onexas.coordinate.common.app.CoordinateBootApplication;

/**
 * 
 * @author Dennis Chen
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CoordinateBootApplication.class})
@TestPropertySource(locations = "classpath:coordinate-common-test.properties")
public class CoordinateCommonTestBase {
}
