package onexas.axes.web.zk.component;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.util.Zks;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class SingleTabbox extends Tabbox implements EventListener<Event>, AfterCompose {

	private static final Logger logger = LoggerFactory.getLogger(SingleTabbox.class);

	private static final long serialVersionUID = 1L;

	private boolean keepSelection;

	public SingleTabbox() {
		this.addEventListener(Events.ON_SELECT, this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() != this.getSelectedTab()) {
			return;
		}
		if (Events.ON_SELECT.equals(event.getName())) {
			if (keepSelection) {
				try {
					String path = Path.getPath(this);
					Tab tab = getSelectedTab();
					String tabId = tab.getId();
					Map<String, String> map = getStoredMap();
					if (Strings.isBlank(tabId)) {
						map.remove(path);
					} else {
						map.put(path, tabId);
					}
				} catch (Exception x) {
					// to catch UiException when not inside a id space
					logger.warn("can't get component path for keepSelection #1, {}", x.getMessage());
				}
			}
			handleSelectTab();
		}
	}

	public boolean isKeepSelection() {
		return keepSelection;
	}

	public void setKeepSelection(boolean keepSelection) {
		this.keepSelection = keepSelection;
	}

	private void handleSelectTab() {
		if (getTabpanels() == null) {
			return;
		}
		Tabpanel sel = getSelectedPanel();
		for (Component panel : getTabpanels().getChildren()) {
			if (!(panel instanceof SingleTabpanel))
				continue;

			if (panel != sel) {
				((SingleTabpanel) panel).clearInclude();
			} else {
				// append include
				Include inc = ((SingleTabpanel) panel).getOrCreateInclude();
				inc.setSrc(((SingleTabpanel) panel).getSrc());
			}
		}

	}

	@Override
	public void setSelectedTab(Tab tab) {
		super.setSelectedTab(tab);
		if (keepSelection) {
			try {
				String path = Path.getPath(this);
				String tabId = tab.getId();
				Map<String, String> map = getStoredMap();
				if (!Strings.isBlank(tabId)) {
					map.put(path, tabId);
				}
			} catch (Exception x) {
				// to catch UiException when not inside a id space
				logger.warn("can't get component path for keepSelection #2, {}", x.getMessage());
			}
		}
		handleSelectTab();
	}

	@Override
	public void afterCompose() {
		if (keepSelection) {
			try {
				String path = Path.getPath(this);
				Map<String, String> map = getStoredMap();
				String selectedId = map.get(path);
				if (selectedId != null) {
					for (Component tab : getTabs().getChildren()) {
						if (tab instanceof Tab && selectedId.equals(tab.getId())) {
							super.setSelectedTab((Tab) tab);
							break;
						}
					}
				}
			} catch (Exception x) {
				// to catch UiException when not inside a id space
				logger.warn("can't get component path for keepSelection #3, {}", x.getMessage());
			}
		}

		handleSelectTab();
	}

	private static final String STORED_KEY = "_singleTabboxStoredMap";

	@SuppressWarnings("unchecked")
	private Map<String, String> getStoredMap() {
		Map<String, String> storedMap;

		Component pageContainer = (Component) Zks.getScopeArg(this, Constants.ATTR_PAGE_CONTAINER);
		if (pageContainer != null) {
			storedMap = (Map<String, String>) pageContainer.getAttribute(STORED_KEY);
			if (storedMap == null) {
				pageContainer.setAttribute(STORED_KEY, storedMap = new HashMap<>());
			}
		} else {
			storedMap = (Map<String, String>) getDesktop().getAttribute(STORED_KEY);
			if (storedMap == null) {
				getDesktop().setAttribute(STORED_KEY, storedMap = new HashMap<>());
			}
		}
		return storedMap;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		handleSelectTab();
	}

	public void clearInclude() {
		if (getTabpanels() == null) {
			return;
		}
		for (Component panel : getTabpanels().getChildren()) {
			if ((panel instanceof SingleTabpanel)) {
				((SingleTabpanel) panel).clearInclude();
			}
		}
	}

}
