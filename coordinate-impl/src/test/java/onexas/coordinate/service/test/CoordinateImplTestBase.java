package onexas.coordinate.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.test.CoordinateDataTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@TestPropertySource(locations = "classpath:coordinate-impl-test.properties")
@ActiveProfiles({ Env.PROFILE_QUEUE })
public class CoordinateImplTestBase extends CoordinateDataTestBase {

	@Rule
	public TestName name = new TestName();

	public void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void sleep1() {
		sleep(500);
	}

	public void sleep2() {
		sleep(1000);
	}

	public void sleep3() {
		sleep(2000);
	}

	public void sleep4() {
		sleep(4000);
	}

	@Before
	public void before() {
		System.out.println(Strings.format("====Before====\n{} # {} @ {}, JVM {}", getClass().getSimpleName(), name.getMethodName(), Integer.toHexString(hashCode()), Objects.JVM_ID));
	}

	@After
	public void after() {
		System.out.println(Strings.format("====After====\n{} # {} @ {}, JVM {}", getClass().getSimpleName(), name.getMethodName(), Integer.toHexString(hashCode()), Objects.JVM_ID));
		sleep(1000);
	}

}
