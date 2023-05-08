package onexas.axes.web.zk.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.zk.fn.JspFns;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.WebManager;

import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Streams;


/**
 * the extendlet to load resource from class and set cachableable 
 * @author Dennis Chen
 * 
 */
public class StaticClassResourceExtendlet implements Extendlet {

	ExtendletContext _webctx;
	
	String _path;
	
	File _uiResourceFolder;
	
	public StaticClassResourceExtendlet(String path, String uiResourceFolder){
		path = path.startsWith("/")?path:"/"+path;
		if(path.endsWith("/")){
			path = path.substring(path.length()-1); 
		}
		_path = path;
		_uiResourceFolder = Strings.isBlank(uiResourceFolder)?null:new File(uiResourceFolder);
	}

	WebApp getWebApp() {
		return _webctx != null ? WebManager.getWebManager(_webctx.getServletContext()).getWebApp() : null;
	}

	ServletContext getServletContext() {
		return _webctx != null ? _webctx.getServletContext() : null;
	}

	@Override
	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
	}

	@Override
	public boolean getFeature(int feature) {
		return false;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		
		if(JspFns.setCacheControl(getServletContext(),
			request, response, "org.zkoss.web.classWebResource.cache", 8760)){
			return;
		}
		
		final String ext = Servlets.getExtension(path, false); // complete
																// extension
		String ctype = ContentTypes.getContentType(ext);
		if (ctype == null) {
			ctype = ";charset=UTF-8";
		} else {
			final int k = ctype.indexOf(';');
			if (k >= 0)
				ctype = ctype.substring(0, k);
			if (!ContentTypes.isBinary(ctype))
				ctype += ";charset=UTF-8";
		}
		path = normalizePath(path);
		
		InputStream is = null;
		try{// NOSONAR
			if(_uiResourceFolder!=null){
				File f = new File(_uiResourceFolder, path);
				if(f.exists()){
					is = new FileInputStream(f);
				}
			}
			
			if(is==null){
				is = Classes.getResourceAsStreamByThread(getClass().getClassLoader(), path);
			}
			
			byte[] data = Streams.toByteArray(is);
			is.close();
			is = null;
			
			int len = data.length;
			response.setContentType(ctype);
			response.setContentLength(len);
			
			OutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			
		}catch(Exception x){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}finally{
			if(is!=null){
				try{
					is.close();
				}catch(Exception x){}
			}
		}
		
	}
	private String normalizePath(String path) {
		int j = path.lastIndexOf('?');
		if (j >= 0) path = path.substring(0, j);
		j = path.lastIndexOf(";jsession");
		if (j >= 0) path = path.substring(0, j);
		if(!path.startsWith("/")){
			path = new StringBuilder(_path).append("/").append(path).toString();
		}else{
			path = new StringBuilder(_path).append(path).toString(); 
		}
		return path;
	}
	
}