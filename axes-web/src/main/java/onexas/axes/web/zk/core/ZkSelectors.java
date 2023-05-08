package onexas.axes.web.zk.core;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Page;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ZkSelectors extends org.zkoss.zk.ui.select.Selectors {

	public static Component findFirst(Component root, String selector) {
		Component comp = findFirstIfAny(root, selector);
		if (comp == null) {
			throw new ComponentNotFoundException("can't find " + selector + " under " + root);
		}
		return comp;
	}

	public static Component findFirstIfAny(Component root, String selector) {
		Iterator<Component> iter = iterable(root, selector).iterator();
		return iter.hasNext() ? iter.next() : null;
	}

	public static Component findFirst(Page root, String selector) {
		Component comp = findFirstIfAny(root, selector);
		if (comp == null) {
			throw new ComponentNotFoundException("can find " + selector + " under " + root);
		}
		return comp;
	}

	public static Component findFirstIfAny(Page root, String selector) {
		Iterator<Component> iter = iterable(root, selector).iterator();
		return iter.hasNext() ? iter.next() : null;
	}
}