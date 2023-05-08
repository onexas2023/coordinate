package onexas.axes.web.zk.ctrl;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.zk.component.Xterm;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;

/**
 * 
 * @author Dennis Chen
 *
 */
public class TestCtrl extends CtrlBase {

	@Wire
	Textbox vuri;

	@Wire
	Xterm vxterm;

	protected void afterCompose() throws Exception {
		vuri.addEventListener(Events.ON_CHANGE, (evt)->{
			vxterm.setWebsocketUri(Zks.trimValue(vuri));
		});
		mainComp.addEventListener("onRemove", (evt)->{
			vxterm.detach();
		});
	}

	
}
