package onexas.coordinate.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Streams;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class WebUtility {

	static private final Logger logger = LoggerFactory.getLogger(WebUtility.class);

	// resourceFolders , use in non-production only
	static private final List<File> resourceFolders = Collections.synchronizedList(new LinkedList<File>());

	public static void addResourceFolders(List<String> resourceFolders) {
		if (resourceFolders != null && resourceFolders.size() > 0) {
			for (String f : resourceFolders) {
				File folder = new File(f);
				if (folder.isDirectory() && !WebUtility.resourceFolders.contains(folder)) {
					logger.info("add WebUtil resources folder {} ", folder.getAbsoluteFile());
					WebUtility.resourceFolders.add(folder);
				}
			}
		}
	}

	public static String compressJs(String jsContent, final String info) {
		StringReader in = new StringReader(jsContent);
		StringWriter out = new StringWriter();
		ErrorReporter reporter = new ErrorReporter() {
			public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
				if (line < 0) {
					logger.warn("in {} {}", info, message);
				} else {
					logger.warn("in {} {}:{} {}", info, line, lineOffset, message);
				}
			}

			public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
				if (line < 0) {
					logger.error("in {} {}", info, message);
				} else {
					logger.error("in {} {}:{} {}", info, line, lineOffset, message);
				}
			}

			public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
					int lineOffset) {
				error(message, sourceName, line, lineSource, lineOffset);
				return new EvaluatorException(message);
			}
		};
		try {
			JavaScriptCompressor compressor = new JavaScriptCompressor(in, reporter);
			compressor.compress(out, 120, true, false, true, false);

			String result = out.toString();
			return result;
		} catch (EvaluatorException e) {
			return "";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String compressCss(String cssContent, final String info) {
		StringReader in = new StringReader(cssContent);
		StringWriter out = new StringWriter();
		try {

			CssCompressor compressor = new CssCompressor(in);
			compressor.compress(out, 120);
			String result = out.toString();
			return result;
		} catch (EvaluatorException e) {
			return "";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String loadStringResource(final String resource) {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			URL url = null;
			for (File folder : resourceFolders) {
				File file = new File(folder, resource);
				if (file.isFile()) {
					url = file.toURI().toURL();
					break;
				}
			}
			if (url == null) {
				url = Classes.getResourceByThread(WebUtility.class.getClassLoader(), resource);
			}
			if (url == null) {
				logger.warn("resource not found {}", resource);
				return "";
			}
			in = url.openStream();
			out = new ByteArrayOutputStream();
			Streams.flush(in, out);
			in.close();
			out.close();

			String result = new String(out.toByteArray(), "UTF-8");

			return result;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static Map<String, String> jsCache = onexas.coordinate.common.lang.Collections.newConcurrentMap();

	public static String loadJsResource(String resource, boolean cache, boolean compress) {
		if (cache) {
			String result = jsCache.get(resource);
			if (result != null) {
				return result;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("load js {}, compress {}, cache {}", resource, compress, cache);
		}
		String js = loadStringResource(resource);
		if (compress && !Strings.isEmpty(js)) {
			js = WebUtility.compressJs(js, resource);
		}
		if (cache) {
			jsCache.put(resource, js);
		}
		return js;
	}

	static Map<String, String> cssCache = onexas.coordinate.common.lang.Collections.newConcurrentMap();

	public static String loadCssResource(String resource, boolean cache, boolean compress) {
		if (cache) {
			String result = cssCache.get(resource);
			if (result != null) {
				return result;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("load css {}, compress {}, cache {}", resource, compress, cache);
		}
		String css = loadStringResource(resource);
		if (compress && !Strings.isEmpty(css)) {
			css = WebUtility.compressCss(css, resource);
		}
		if (cache) {
			cssCache.put(resource, css);
		}
		return css;
	}
}