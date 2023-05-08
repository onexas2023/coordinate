package onexas.axes.web.zk.component;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SingleTabpanel extends Tabpanel implements DynamicPropertied {
	private static final long serialVersionUID = 1L;
	private String src;
	private Include include;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void clearInclude() {
		if (include != null) {
			include.setSrc("");
		}
	}

	public Include getOrCreateInclude() {
		if (include == null) {
			include = new Include();
			include.setHflex("1");
			include.setVflex("1");

			// detect unique tabbpanel include id for
			// implement #YZ-58 Support to remember last selected tab of a region
			String id = this.getId();
			if (!Strings.isBlank(id)) {
				// id form tabpanel
				include.setId(id + "Inc");
			} else {
				int idx = this.getIndex();
				if (idx >= 0) {
					id = getTabN(idx).getId();
				}

				if (!Strings.isBlank(id)) {
					// id form tab
					include.setId(id + "TpInc");
				} else {
					id = getTabbox().getId();

					if (!Strings.isBlank(id)) {
						// id form tabbox
						include.setId(Strings.format("{}Tp{}Inc", id, idx));
					} else {
						// create random unique one
						while (true) {
							id = "stpInc" + Strings.randomName(5);
							if (getFellowIfAny(id) == null) {
								include.setId(id);
								break;
							}
						}
					}
				}
			}

			appendChild(include);
		}
		return include;
	}

	private Tab getTabN(int idx) {
//		Tab tab = null;
		Tabs tabs = getTabbox().getTabs();
		if (tabs != null) {
			int i = idx;
			for (Component comp : tabs.getChildren()) {
				if (comp instanceof Tab) {
					if (i == 0) {
						return (Tab) comp;
					}
					i--;
				}
			}
		}
		throw new UiException("can't find " + idx + " Tab");
	}

	@Override
	public boolean hasDynamicProperty(String name) {
		return include == null ? false : include.hasDynamicProperty(name);
	}

	@Override
	public Object getDynamicProperty(String name) {
		return include == null ? null : include.getDynamicProperty(name);
	}

	@Override
	public void setDynamicProperty(String name, Object value) throws WrongValueException {
		getOrCreateInclude().setDynamicProperty(name, value);
	}
}
