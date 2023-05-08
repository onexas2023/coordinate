package onexas.coordinate.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;

import onexas.coordinate.data.test.CoordinateDataTestBase;



/**
 * 
 * @author Dennis Chen
 *
 */
public class EntityManagerTest extends CoordinateDataTestBase{

	@PersistenceContext
	EntityManager em;
	
	@PersistenceContext
	@Qualifier(CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	EntityManager xsem;
	
	@Test
	public void run() {
		Assert.assertNotNull(em);
		Assert.assertNotNull(xsem);
	}
}
