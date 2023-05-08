package onexas.coordinate.service.event;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import onexas.coordinate.common.app.ApplicationEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public class InitDemoEvent extends ApplicationEvent<String> {
	private static final long serialVersionUID = 1L;

	String demo;

	Set<String> availableDemos = new LinkedHashSet<>();

	int reportCount = 0;

	public InitDemoEvent(String demo) {
		super(demo);
		this.demo = demo;
	}

	public String getDemo() {
		return demo;
	}

	public void report() {
		reportCount++;
	}

	public void reportDemo(String... demos) {
		for (String demo : demos) {
			availableDemos.add(demo);
		}
	}

	public int getReportCount() {
		return reportCount;
	}

	public Set<String> getAvailableDemos() {
		return Collections.unmodifiableSet(availableDemos);
	}

}
