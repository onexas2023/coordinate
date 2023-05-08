package onexas.axes.web.zk.core;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;

import onexas.coordinate.common.lang.Strings;

/**
 * https://www.zkoss.org/wiki/Small_Talks/2012/January/
 * Execute_a_Loading_or_Performance_Test_on_ZK_using_JMeter
 * http://blog.zkoss.org/2013/08/06/zk-jmeter-plugin/
 * 
 * @author Dennis Chen
 * 
 */
public class SequenceIdGenerator implements IdGenerator {

	@Override
	public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo compInfo) {
		String desktopKey = "_seq-id-default";

		// always count the default to tolerance seq-id new added in fresh code
		Integer counter;
		if ((counter = (Integer) desktop.getAttribute(desktopKey)) == null) {
			counter = 0;
		}
		desktop.setAttribute(desktopKey, counter + 1);

		String group = "seq";
		Annotation anno = null;
		String idGroup = null;
		//get id-group form component annotation
		if (comp instanceof AbstractComponent) {
			anno = ((AbstractComponent) comp).getAnnotation(null, "seq-id");
			if (anno != null) {
				idGroup = anno.getAttribute("value");
			}
		}
		//get id-group from component id property
		if (compInfo!=null && Strings.isBlank(idGroup)) {
			for (Property p : compInfo.getProperties()) {
				if ("id".equals(p.getName())) {
					idGroup = p.getRawValue();
					//YZ-221 Investigate error when click on container's open e2e
					//(Illegal character, }, not allowed in uid, grid${arg.id}_15:)
					//zk only alow a-zA-Z0-9_
					idGroup = idGroup.replaceAll("[^a-zA-Z0-9]", "_");
					break;
				}
			}
		}
		
		//use the uid from idGroup if any
		if (!Strings.isBlank(idGroup)) {
			desktopKey = Strings.cat("_seq-id-", idGroup);
			group = idGroup;

			// specific for new seq-id key
			if ((counter = (Integer) desktop.getAttribute(desktopKey)) == null) {
				counter = 0;
			}
			desktop.setAttribute(desktopKey, counter + 1);
		}

		return Strings.cat(group, "_", counter);
	}

	@Override
	public String nextDesktopId(Desktop desktop) {
		return null;
	}

	@Override
	public String nextPageUuid(Page page) {
		return null;
	}
	
	public static void applySeqIdAnno(AbstractComponent comp, String id){
		Map<String, String[]> annoAttr = new HashMap<>();
		annoAttr.put("value", new String[] { id });
		comp.addAnnotation(null, "seq-id", annoAttr);
	}

}