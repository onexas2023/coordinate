package onexas.coordinate.data;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.data.test.CoordinateDataTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CoordinateEntityManagerTest extends CoordinateDataTestBase {

	@Test
	public void testTx() {
		Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

		try {
			AppContext.bean(CoordinateEntityManager.class).runInTx(() -> {
				Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
				try {
					TransactionStatus statusa = TransactionAspectSupport.currentTransactionStatus();
					System.out.println(">>>>>>A " + statusa.isNewTransaction() + "," + statusa.isCompleted() + ","
							+ statusa.isRollbackOnly() + ",");
					Assert.assertTrue(statusa.isNewTransaction());
					Assert.assertFalse(statusa.isCompleted());
					Assert.assertFalse(statusa.isRollbackOnly());

					AppContext.bean(CoordinateEntityManager.class).runInTx(() -> {
						Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

						TransactionStatus statusb = TransactionAspectSupport.currentTransactionStatus();
						System.out.println(">>>>>>B " + statusb.isNewTransaction() + "," + statusb.isCompleted() + ","
								+ statusb.isRollbackOnly() + ",");
						// nested
						Assert.assertFalse(statusb.isNewTransaction());
						Assert.assertFalse(statusb.isCompleted());
						Assert.assertFalse(statusb.isRollbackOnly());
						throw new RuntimeException("xxx");
					});

				} catch (Exception x) {

					Assert.assertEquals("xxx", x.getMessage());

					Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

					TransactionStatus statusc = TransactionAspectSupport.currentTransactionStatus();
					System.out.println(">>>>>>C " + statusc.isNewTransaction() + "," + statusc.isCompleted() + ","
							+ statusc.isRollbackOnly() + ",");
					Assert.assertTrue(statusc.isNewTransaction());
					Assert.assertFalse(statusc.isCompleted());
					Assert.assertTrue(statusc.isRollbackOnly());
				}
			});
			//rooback exception
			Assert.fail("not here");
		} catch (Exception x) {
			Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
		}
	}

	@Test
	public void testNestTx() {
		Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());

		try {
			AppContext.bean(CoordinateEntityManager.class).runInTx(() -> {
				Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
				try {
					TransactionStatus statusa = TransactionAspectSupport.currentTransactionStatus();
					System.out.println(">>>>>>A " + statusa.isNewTransaction() + "," + statusa.isCompleted() + ","
							+ statusa.isRollbackOnly() + ",");
					Assert.assertTrue(statusa.isNewTransaction());
					Assert.assertFalse(statusa.isCompleted());
					Assert.assertFalse(statusa.isRollbackOnly());

					AppContext.bean(CoordinateEntityManager.class).runInTx(() -> {
						Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

						TransactionStatus statusb = TransactionAspectSupport.currentTransactionStatus();
						System.out.println(">>>>>>B " + statusb.isNewTransaction() + "," + statusb.isCompleted() + ","
								+ statusb.isRollbackOnly() + ",");
						// nested
						Assert.assertFalse(statusb.isNewTransaction());
						Assert.assertFalse(statusb.isCompleted());
						Assert.assertFalse(statusb.isRollbackOnly());
						throw new RuntimeException("xxx");
					});

				} catch (Exception x) {

					Assert.assertEquals("xxx", x.getMessage());

					Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

					TransactionStatus statusc = TransactionAspectSupport.currentTransactionStatus();
					System.out.println(">>>>>>C " + statusc.isNewTransaction() + "," + statusc.isCompleted() + ","
							+ statusc.isRollbackOnly() + ",");
					Assert.assertTrue(statusc.isNewTransaction());
					Assert.assertFalse(statusc.isCompleted());
					Assert.assertTrue(statusc.isRollbackOnly());
					
					AppContext.bean(CoordinateEntityManager.class).runInNewTx(() -> {
						Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

						TransactionStatus statusd = TransactionAspectSupport.currentTransactionStatus();
						System.out.println(">>>>>>D " + statusd.isNewTransaction() + "," + statusd.isCompleted() + ","
								+ statusd.isRollbackOnly() + ",");
						// new
						Assert.assertTrue(statusd.isNewTransaction());
						Assert.assertFalse(statusd.isCompleted());
						Assert.assertFalse(statusd.isRollbackOnly());
					});
					
					TransactionStatus statuse = TransactionAspectSupport.currentTransactionStatus();
					System.out.println(">>>>>>E " + statuse.isNewTransaction() + "," + statuse.isCompleted() + ","
							+ statuse.isRollbackOnly() + ",");
					Assert.assertTrue(statuse.isNewTransaction());
					Assert.assertFalse(statuse.isCompleted());
					Assert.assertTrue(statuse.isRollbackOnly());
				}
			});
			//rooback exception
			Assert.fail("not here");
		} catch (Exception x) {
			Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
		}
	}
}
