package onexas.axes.web.zk.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Calendar;
import java.net.URL;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Exceptions;
import org.zkoss.io.Files;
import org.zkoss.io.WriterOutputStream;
import org.zkoss.util.FastReadArray;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.Locators;
import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.xml.XMLs;

import onexas.coordinate.common.lang.Classes;

@SuppressWarnings("unused")
public class ClassResourceExtendletContext implements ExtendletContext {
	private static final Logger logger = LoggerFactory.getLogger(ClassResourceExtendletContext.class);
	private final String _path;
	private final ServletContext _ctx;
	private final List<File> resourceFolders = new LinkedList<File>();

	public ClassResourceExtendletContext(String path, ServletContext ctx, List<String> resourceFolders) {
		path = path.startsWith("/") ? path : "/" + path;
		if (path.endsWith("/")) {
			path = path.substring(path.length() - 1);
		}
		_path = path;
		_ctx = ctx;
		if (resourceFolders != null && resourceFolders.size() > 0) {
			for (String f : resourceFolders) {
				File folder = new File(f);
				if (folder.isDirectory()) {
					logger.info("Register {} resources folder {} ", _path, folder.getAbsoluteFile());
					this.resourceFolders.add(folder);
				}
			}
		}
	}

	private final Locator _locator = new Locator() {
		public String getDirectory() {
			return null;
		}

		public URL getResource(String name) {
			return ClassResourceExtendletContext.this.getResource(name);
		}

		public InputStream getResourceAsStream(String name) {
			return ClassResourceExtendletContext.this.getResourceAsStream(name);
			// Note: it doesn't handle _debugJS
		}
	};

	// -- ExtendletContext --//
	public ServletContext getServletContext() {
		return _ctx;
	}

	public Locator getLocator() {
		return _locator;
	}

	public boolean shallCompress(ServletRequest request, String ext) {
//			return ClassWebResource.this.shallCompress(request, ext);
		return false;
	}

	public String encodeURL(ServletRequest request, ServletResponse response, String uri)
			throws ServletException, UnsupportedEncodingException {
		throw new IllegalStateException("not implement to encodeRedirectURL: " + uri);
//			uri = Servlets.locate(_ctx, request, uri, getLocator()); //resolves "*"
//			uri = (_encURLPrefix != null ? _mappingURI + _encURLPrefix: _mappingURI)
//				+ uri; //prefix with mapping
//
//			//prefix context path
//			if (request instanceof HttpServletRequest) {
//				String ctxpath = ((HttpServletRequest)request).getContextPath();
//				if (ctxpath == null)
//					throw new NullPointerException("HttpServletRequest#getContentPath() returns a null value from [ " + request + " ]");
//				final int ctxlen = ctxpath.length();
//				if (ctxlen > 0) {
//					final char cc = ctxpath.charAt(0);
//					if (cc != '/') ctxpath = '/' + ctxpath;
//						//Work around a bug for Pluto's RenderRequest (1.0.1)
//					else if (ctxlen == 1) ctxpath = ""; // "/" =>  ""
//						//Work around liferay's issue: Upload 1627928 (not verified)
//				}
//				uri = ctxpath + uri;
//			}
//
//			int j = uri.indexOf('?');
//			if (j < 0) {
//				uri = Encodes.encodeURI(uri);
//			} else {
//				uri = Encodes.encodeURI(uri.substring(0, j))
//					+ uri.substring(j);
//			}
//			//encode
//			if (response instanceof HttpServletResponse)
//				uri = ((HttpServletResponse)response).encodeURL(uri);
//			return uri;
	}

	public String encodeRedirectURL(HttpServletRequest request, HttpServletResponse response, String uri,
			@SuppressWarnings("rawtypes") Map params, int mode) {
		throw new IllegalStateException("not implement to encodeRedirectURL: " + uri);
//			return Https.encodeRedirectURL(_ctx, request, response,
//				_mappingURI + uri, params, mode);
	}

	public RequestDispatcher getRequestDispatcher(String uri) {
		throw new IllegalStateException("not implement to load: " + uri);
//			if (log.isDebugEnabled()) log.debug("getRequestDispatcher: "+uri);
//			return _ctx.getRequestDispatcher(_path + uri);
	}

	public void include(HttpServletRequest request, HttpServletResponse response, String uri,
			@SuppressWarnings("rawtypes") Map params) throws ServletException, IOException {
		throw new IllegalStateException("not implement to include: " + uri);
//			//Note: it is caller's job to convert related path to ~./
//			if (uri.startsWith("~./") && uri.indexOf('?') < 0
//			&& isDirectInclude(uri)) {
//				Object old = request.getAttribute(Attributes.ARG);
//				if (params != null)
//					request.setAttribute(Attributes.ARG, params);
//					//If params=null, use the 'inherited' one (same as Servlets.include)
//
//				final String attrnm = "org.zkoss.web.servlet.include";
//				request.setAttribute(attrnm, Boolean.TRUE);
//					//so Servlets.isIncluded returns correctly
//				try {
//					service(request, response,
//						Servlets.locate(_ctx, request, uri.substring(2), _cwc.getLocator()));
//				} finally {
//					request.removeAttribute(attrnm);
//					request.setAttribute(Attributes.ARG, old);
//				}
//			} else {
//				Servlets.include(_ctx, request, response,
//					uri, params, Servlets.PASS_THRU_ATTR);
//			}
	}

	/**
	 * Returns whether the page can be directly included.
	 */
	private boolean isDirectInclude(String path) {
		throw new IllegalStateException("not implement to isDirectInclude: " + path);
//			final String ext = Servlets.getExtension(path, false);
//			final Extendlet extlet = ext != null ? getExtendlet(ext): null;
//			if (extlet != null) {
//				try {
//					return extlet.getFeature(Extendlet.ALLOW_DIRECT_INCLUDE);
//				} catch (Throwable ex) { //backward compatibility
//				}
//			}
//			return true;
	}

	public URL getResource(String uri) {
		uri = normalizeURI(uri);
		try {
			if (resourceFolders.size() > 0) {
				for (File folder : resourceFolders) {
					File f = new File(folder, uri);
					if (f.isFile()) {
						return f.toURI().toURL();
					}
				}
			}
			
			return Classes.getResourceByThread(getClass().getClassLoader(), uri);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
//			if (_debugJS && "js".equals(Servlets.getExtension(uri))) {
//				String orgpi = uri.substring(0, uri.length() - 3) + ".src.js";
//				URL url = ClassWebResource.this.getResource(orgpi);
//				if (url != null) return url;
//			}
//			return ClassWebResource.this.getResource(uri);
	}

	public InputStream getResourceAsStream(String uri) {
		try {
			URL url = getResource(uri);
			if (url != null) {
				return url.openStream();
			}
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
//			if (_debugJS && "js".equals(Servlets.getExtension(uri))) {
//				String orgpi = uri.substring(0, uri.length() - 3) + ".src.js";
//				InputStream is = ClassWebResource.this.getResourceAsStream(orgpi);
//				if (is != null) return is;
//			}
//			return ClassWebResource.this.getResourceAsStream(uri);
	}

	private String normalizeURI(String uri) {
		int j = uri.lastIndexOf('?');
		if (j >= 0)
			uri = uri.substring(0, j);
		j = uri.lastIndexOf(";jsession");
		if (j >= 0)
			uri = uri.substring(0, j);
		if (!uri.startsWith("/")) {
			uri = new StringBuilder(_path).append("/").append(uri).toString();
		} else {
			uri = new StringBuilder(_path).append(uri).toString();
		}
		return uri;
	}
}