package onexas.coordinate.data;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.data.test.CoordinateDataTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LockerTest extends CoordinateDataTestBase {

	static AtomicInteger count = new AtomicInteger(0);

	@Test
	public void testLock() {
		int worker = 10;
		ExecutorService executor = Executors.newFixedThreadPool(worker);
		List<Future<Void>> result = new LinkedList<>();
		for (int i = 0; i < worker; i++) {
			result.add(executor.submit(new TestTask(i)));
		}

		while (result.size() > 0) {
			Future<Void> f = result.remove(0);
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				Assert.fail(e.getMessage());
			}
		}

	}

	public static class TestTask implements Callable<Void> {
		int taskId;

		public TestTask(int taskId) {
			this.taskId = taskId;
		}

		@Override
		public Void call() throws Exception {
			System.out.println("Task " + taskId + " waiting");
			AppContext.getBean(Locker.class).runOrWait("test", new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					int i = count.incrementAndGet();
					System.out.println("Task " + taskId + " is running " + i);
					Assert.assertEquals(1, i);

					Thread.sleep(Randoms.random.nextInt(1000));
					Assert.assertEquals(1, count.get());

					i = count.decrementAndGet();
					System.out.println("Task " + taskId + " is stopped " + i);
					return null;
				}
			});
			return null;
		}

	}
}
