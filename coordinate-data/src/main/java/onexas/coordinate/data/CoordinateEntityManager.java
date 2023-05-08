package onexas.coordinate.data;

import java.util.concurrent.Callable;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateEntityManager")
@DependsOn(AppContext.BEAN_NAME)
public class CoordinateEntityManager {

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public <T> T callInNewTx(Callable<T> call) {
		try {
			return call.call();
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new IllegalStateException(x.getMessage(), x);
		}
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public void runInNewTx(Runnable run) {
		run.run();
	}
	
	
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public <T> T callInTx(Callable<T> call) {
		try {
			return call.call();
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new IllegalStateException(x.getMessage(), x);
		}
	}

	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void runInTx(Runnable run) {
		run.run();
	}
}