package onexas.axes.web.zk.util;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Events {

	@SuppressWarnings("unchecked")
	public static <T extends Event> T unwrap(Event event, Class<T> type) {
		if (event == null)
			return null;

		while (event instanceof ForwardEvent) {
			event = ((ForwardEvent) event).getOrigin();
		}
		if (type.isAssignableFrom(event.getClass())) {
			return (T) event;
		}
		throw new ClassCastException(Strings.format("{} is not the type {}", event.getClass(), type));

	}

}
