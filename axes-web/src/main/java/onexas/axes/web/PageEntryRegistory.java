package onexas.axes.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "axes.PageEntryRegistory")
public class PageEntryRegistory {

	private static final Logger logger = LoggerFactory.getLogger(PageEntryRegistory.class);

	Map<String, PageEntry> entries = Collections.newConcurrentMap();

	public PageEntry get(String page) {
		PageEntry f = entries.get(page);
		if (f == null) {
			throw new NotFoundException("page entry {} not found", page);
		}
		return f;
	}

	public List<PageEntry> list() {
		List<PageEntry> fs = new LinkedList<>();
		fs.addAll(entries.values());
		java.util.Collections.sort(fs, (o1, o2) -> {
			return o1.getPage().compareTo(o2.getPage());
		});
		return fs;
	}

	public void register(PageEntry entry) {
		entries.put(entry.getPage(), entry);
		logger.info("Register page entry {}, view {}", entry.getPage(), entry.getViewUri());
	}
}
