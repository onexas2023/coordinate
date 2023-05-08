package onexas.coordinate.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.model.Permission;
import onexas.coordinate.service.test.CoordinateImplTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PermissionServiceTest extends CoordinateImplTestBase {

	String role1 = "role1";
	String role2 = "role2";
	String role3 = "role3";
	String role4 = "role4";

	String fn1 = "fn1";
	String fn2 = "fn2";
	String fn3 = "fn3";

	String view = "view";
	String edit = "edit";

	@Autowired
	PermissionService service;

	@Test
	public void testSimple() {

		List<Permission> list = service.listByPrincipal(role1);
		Assert.assertEquals(0, list.size());
		list = service.listByTarget(fn1);
		Assert.assertEquals(0, list.size());

		Permission p1 = service.create(role1, fn1, view, "R");
		Assert.assertEquals(role1, p1.getPrincipal());
		Assert.assertEquals(fn1, p1.getTarget());
		Assert.assertEquals(view, p1.getAction());
		Assert.assertEquals("R", p1.getRemark());

		Permission p2 = service.create(role1, fn2, edit, "R");
		Assert.assertEquals(role1, p2.getPrincipal());
		Assert.assertEquals(fn2, p2.getTarget());
		Assert.assertEquals(edit, p2.getAction());
		Assert.assertEquals("R", p2.getRemark());

		Permission p3 = service.create(role2, fn1, view, null);
		Assert.assertEquals(role2, p3.getPrincipal());
		Assert.assertEquals(fn1, p3.getTarget());
		Assert.assertEquals(view, p3.getAction());
		Assert.assertEquals(null, p3.getRemark());

		Permission p4 = service.create(role2, fn2, view, null);
		Assert.assertEquals(role2, p4.getPrincipal());
		Assert.assertEquals(fn2, p4.getTarget());
		Assert.assertEquals(view, p4.getAction());
		Assert.assertEquals(null, p4.getRemark());

		Permission p = service.get(p1.getUid());
		Assert.assertEquals(p1.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p1.getTarget(), p.getTarget());
		Assert.assertEquals(p1.getAction(), p.getAction());
		Assert.assertEquals(p1.getRemark(), p.getRemark());

		p = service.find(p1.getUid());
		Assert.assertEquals(p1.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p1.getTarget(), p.getTarget());
		Assert.assertEquals(p1.getAction(), p.getAction());
		Assert.assertEquals(p1.getRemark(), p.getRemark());

		list = service.listByPrincipal(role1);
		Assert.assertEquals(2, list.size());
		p = list.get(0);
		Assert.assertEquals(p1.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p1.getTarget(), p.getTarget());
		Assert.assertEquals(p1.getAction(), p.getAction());
		Assert.assertEquals(p1.getRemark(), p.getRemark());
		p = list.get(1);
		Assert.assertEquals(p2.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p2.getTarget(), p.getTarget());
		Assert.assertEquals(p2.getAction(), p.getAction());
		Assert.assertEquals(p2.getRemark(), p.getRemark());

		list = service.listByPrincipal(role2);
		Assert.assertEquals(2, list.size());
		p = list.get(0);
		Assert.assertEquals(p3.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p3.getTarget(), p.getTarget());
		Assert.assertEquals(p3.getAction(), p.getAction());
		Assert.assertEquals(p3.getRemark(), p.getRemark());
		p = list.get(1);
		Assert.assertEquals(p4.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p4.getTarget(), p.getTarget());
		Assert.assertEquals(p4.getAction(), p.getAction());
		Assert.assertEquals(p4.getRemark(), p.getRemark());

		list = service.listByTarget(fn1);
		Assert.assertEquals(2, list.size());
		p = list.get(0);
		Assert.assertEquals(p1.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p1.getTarget(), p.getTarget());
		Assert.assertEquals(p1.getAction(), p.getAction());
		Assert.assertEquals(p1.getRemark(), p.getRemark());
		p = list.get(1);
		Assert.assertEquals(p3.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p3.getTarget(), p.getTarget());
		Assert.assertEquals(p3.getAction(), p.getAction());
		Assert.assertEquals(p3.getRemark(), p.getRemark());

		list = service.listByTarget(fn2);
		p = list.get(0);
		Assert.assertEquals(2, list.size());
		Assert.assertEquals(p2.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p2.getTarget(), p.getTarget());
		Assert.assertEquals(p2.getAction(), p.getAction());
		Assert.assertEquals(p2.getRemark(), p.getRemark());
		p = list.get(1);
		Assert.assertEquals(p4.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p4.getTarget(), p.getTarget());
		Assert.assertEquals(p4.getAction(), p.getAction());
		Assert.assertEquals(p4.getRemark(), p.getRemark());

		service.delete(p1.getUid(), true);
		list = service.listByPrincipal(role1);
		Assert.assertEquals(1, list.size());
		p = list.get(0);
		Assert.assertEquals(p2.getPrincipal(), p.getPrincipal());
		Assert.assertEquals(p2.getTarget(), p.getTarget());
		Assert.assertEquals(p2.getAction(), p.getAction());
		Assert.assertEquals(p2.getRemark(), p.getRemark());

		try {
			service.get(p1.getUid());
			Assert.fail("not here");
		} catch (NotFoundException x) {
		}

		Assert.assertNull(service.find(p1.getUid()));

		service.deleteByTarget(fn2);
		list = service.listByTarget(fn2);
		Assert.assertEquals(0, list.size());

		service.deleteByTarget(fn1);
		list = service.listByTarget(fn1);
		Assert.assertEquals(0, list.size());

	}

	@Test
	public void testPermitted() {
		List<Permission> list = service.listByPrincipal(role1);
		Assert.assertEquals(0, list.size());
		list = service.listByTarget(fn1);
		Assert.assertEquals(0, list.size());

		Permission p1 = service.create(role1, fn1, view, null);
		Permission p2 = service.create(role2, fn2, Permission.ANY_ACTION, null);
		Permission p3 = service.create(role3, Permission.ANY_RPINCIPAL, view, null);
		Permission p4 = service.create(role4, Permission.ANY_RPINCIPAL, Permission.ANY_ACTION, null);

		// role1
		Assert.assertTrue(service.isPermitted(role1, fn1, view));
		Assert.assertFalse(service.isPermitted(role1, fn1, edit));
		Assert.assertFalse(service.isPermitted(role1, fn2, view));
		Assert.assertFalse(service.isPermitted(role1, fn2, edit));
		Assert.assertFalse(service.isPermitted(role1, fn3, view));
		Assert.assertFalse(service.isPermitted(role1, fn3, edit));

		// role2
		Assert.assertFalse(service.isPermitted(role2, fn1, view));
		Assert.assertFalse(service.isPermitted(role2, fn1, edit));
		Assert.assertTrue(service.isPermitted(role2, fn2, view));
		Assert.assertTrue(service.isPermitted(role2, fn2, edit));
		Assert.assertFalse(service.isPermitted(role2, fn3, view));
		Assert.assertFalse(service.isPermitted(role2, fn3, edit));

		// role3
		Assert.assertTrue(service.isPermitted(role3, fn1, view));
		Assert.assertFalse(service.isPermitted(role3, fn1, edit));
		Assert.assertTrue(service.isPermitted(role3, fn2, view));
		Assert.assertFalse(service.isPermitted(role3, fn2, edit));
		Assert.assertTrue(service.isPermitted(role3, fn3, view));
		Assert.assertFalse(service.isPermitted(role3, fn3, edit));

		// role4
		Assert.assertTrue(service.isPermitted(role4, fn1, view));
		Assert.assertTrue(service.isPermitted(role4, fn1, edit));
		Assert.assertTrue(service.isPermitted(role4, fn2, view));
		Assert.assertTrue(service.isPermitted(role4, fn2, edit));
		Assert.assertTrue(service.isPermitted(role4, fn3, view));
		Assert.assertTrue(service.isPermitted(role4, fn3, edit));

		service.delete(p1.getUid(), true);
		service.delete(p2.getUid(), true);
		service.delete(p3.getUid(), true);
		service.delete(p4.getUid(), true);
		list = service.listByTarget(fn2);
		Assert.assertEquals(0, list.size());

		service.deleteByTarget(fn1);
		list = service.listByTarget(fn1);
		Assert.assertEquals(0, list.size());

		// role1
		Assert.assertFalse(service.isPermitted(role1, fn1, view));
		Assert.assertFalse(service.isPermitted(role1, fn1, edit));
		Assert.assertFalse(service.isPermitted(role1, fn2, view));
		Assert.assertFalse(service.isPermitted(role1, fn2, edit));
		Assert.assertFalse(service.isPermitted(role1, fn3, view));
		Assert.assertFalse(service.isPermitted(role1, fn3, edit));

		// role2
		Assert.assertFalse(service.isPermitted(role2, fn1, view));
		Assert.assertFalse(service.isPermitted(role2, fn1, edit));
		Assert.assertFalse(service.isPermitted(role2, fn2, view));
		Assert.assertFalse(service.isPermitted(role2, fn2, edit));
		Assert.assertFalse(service.isPermitted(role2, fn3, view));
		Assert.assertFalse(service.isPermitted(role2, fn3, edit));

		// role3
		Assert.assertFalse(service.isPermitted(role3, fn1, view));
		Assert.assertFalse(service.isPermitted(role3, fn1, edit));
		Assert.assertFalse(service.isPermitted(role3, fn2, view));
		Assert.assertFalse(service.isPermitted(role3, fn2, edit));
		Assert.assertFalse(service.isPermitted(role3, fn3, view));
		Assert.assertFalse(service.isPermitted(role3, fn3, edit));

		// role4
		Assert.assertFalse(service.isPermitted(role4, fn1, view));
		Assert.assertFalse(service.isPermitted(role4, fn1, edit));
		Assert.assertFalse(service.isPermitted(role4, fn2, view));
		Assert.assertFalse(service.isPermitted(role4, fn2, edit));
		Assert.assertFalse(service.isPermitted(role4, fn3, view));
		Assert.assertFalse(service.isPermitted(role4, fn3, edit));
	}

}
