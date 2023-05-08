package onexas.axes.web.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.RefreshableConfigLoader;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Menus {

	private static final Logger logger = LoggerFactory.getLogger(Menus.class);

	public static final String MENU_MAIN = "main";
	public static final String MENU_MISC = "misc";

	private static RefreshableConfigLoader<Map<String, Menu>> menuLoader = new RefreshableConfigLoader<Map<String, Menu>>() {
		protected Map<String, Menu> load(Config cfg) {
			Map<String, Menu> menus = new LinkedHashMap<>();

			for (Config sub : cfg.getSubConfigList("axes.menus.menu", Config.PATH_NAME_ATTR, false)) {
				buildMenu(sub, null, menus);
			}

			// build children structure
			for (Menu menu : menus.values()) {
				Menu pm = menus.get(menu.parent);
				if (pm != null) {
					if (pm.children == null) {
						pm.children = new LinkedList<>();
					}
					pm.children.add(menu);
				}
			}

			// sort children
			for (Menu menu : menus.values()) {
				if (menu.children != null) {
					Collections.sort(menu.children, (Menu o1, Menu o2) -> {
						return Integer.compare(o1.priority, o2.priority);
					});
				}
			}
			for (Menu menu : menus.values()) {
				logger.info("Register menu {}", menu);
			}
			return menus;
		}

		private void buildMenu(Config cfg, String parent, Map<String, Menu> menus) {
			String name = cfg.getString(Config.PATH_NAME_ATTR);
			if (Strings.isBlank(name)) {
				name = Strings.randomUid();
			}
			Menu menu = new Menu();
			menu.name = name;
			menu.parent = cfg.getString("[@parent]", parent);
			menu.label = cfg.getString("[@label]", name);
			menu.priority = cfg.getInteger("[@priority]", 100);
			menu.group = cfg.getString("[@group]", "default");
			menu.permission = cfg.getString("[@permission]");
			menu.action = cfg.getString("[@action]");
			menu.enabled = cfg.getBoolean("[@enabled]", Boolean.TRUE);
			if (menus.containsKey(name)) {
				// ignore nested single if exist
			} else {
				menus.put(name, menu);
			}
			for (Config sub : cfg.getSubConfigList("menu", Config.PATH_NAME_ATTR, false)) {
				buildMenu(sub, name, menus);
			}
		}
	};

	public static Menu getMenu(String name) {
		return menuLoader.load().get(name);
	}

	public static class Menu {
		String name;
		String label;
		String parent;
		int priority;
		String group;
		List<Menu> children;
		String action;
		String permission;
		boolean enabled;

		public String getName() {
			return name;
		}

		public String getLabel() {
			return label;
		}

		public String getParent() {
			return parent;
		}

		public int getPriority() {
			return priority;
		}

		public String getGroup() {
			return group;
		}

		public List<Menu> getChildren() {
			return children == null ? null : Collections.unmodifiableList(children);
		}

		public boolean isDisabled() {
			return enabled;
		}

		public String getAction() {
			return action;
		}

		public String getPermission() {
			return permission;
		}

		@Override
		public String toString() {
			return "Menu [" + (name != null ? "name=" + name + ", " : "")
					+ (label != null ? "label=" + label + ", " : "") + (parent != null ? "parent=" + parent + ", " : "")
					+ "priority=" + priority + ", " + (group != null ? "group=" + group + ", " : "")
					+ (children != null ? "children=" + children + ", " : "")
					+ (action != null ? "action=" + action + ", " : "")
					+ (permission != null ? "permission=" + permission + ", " : "") + "enabled=" + enabled + "]";
		}

	}
}
