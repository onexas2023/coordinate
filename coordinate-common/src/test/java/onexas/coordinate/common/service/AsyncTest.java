package onexas.coordinate.common.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.lang.Randoms;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class AsyncTest extends CoordinateCommonTestBase {

	@Autowired
	AsyncService asyncService;

	@Test
	public void testSimple() {

		List<Future<Integer>> result = new LinkedList<>();
		System.out.println("Following number shouln't not list by the order");
		for (int i = 0; i < 10; i++) {
			final int j = i;
			result.add(asyncService.asyncCall(() -> {
				System.out.println("Enter " + j);
				Thread.sleep(Randoms.random.nextInt(1000));
				System.out.println("Running " + j);
				return Integer.valueOf(j);
			}));
		}
		for (int i = 0; i < 10; i++) {
			Future<Integer> f = result.get(i);
			try {
				Assert.assertEquals(Integer.valueOf(i), f.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				Assert.fail("not here");
			}
		}
	}

}
