package onexas.axes.web.zk.core;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import onexas.axes.web.RequestContext;
import onexas.axes.web.SessionContext;
import onexas.axes.web.Workspace;
import onexas.coordinate.common.app.AppContext;
/**
 * 
 * @author Dennis Chen
 *
 */
public class ZulVariableResolver implements VariableResolver {

	public Object resolveVariable(String name) throws XelException {
		switch(name){
		case "ws":
			return AppContext.bean(Workspace.class);
		case "cfg":
			return AppContext.config();
//		case "wsUiCfg":
//			return WsUiConfigs.getInstance();
//		case "wsDesktop":
//			return WsDesktops.getCurrent();
		case "appCtx":
			return AppContext.instance();
		case "sessCtx":
			return AppContext.bean(SessionContext.class);
		case "reqCtx":
			return AppContext.bean(RequestContext.class);	
//		case "credential":
//			return Workspaces.getCurrent().getCredential();
		case "appName":
			return AppContext.config().getString("app.name","unknown");
		case "appVer":
			return AppContext.config().getString("app.version","unknown");
//		case "appLicense":
//			return LicenseHolder.getCurrent();
		default :
			return null;
		}
	}

}
