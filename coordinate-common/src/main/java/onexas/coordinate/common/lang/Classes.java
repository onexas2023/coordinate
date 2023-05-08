package onexas.coordinate.common.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class Classes {

	@SuppressWarnings("rawtypes")
	public static Class forName(String name) throws ClassNotFoundException {
		return forName(Classes.class.getClassLoader(), name);
	}

	@SuppressWarnings("rawtypes")
	public static Class forName(ClassLoader loader, String clsName) throws ClassNotFoundException {
		return Class.forName(clsName, true, loader);
	}

	@SuppressWarnings("rawtypes")
	public static Class forNameByThread(String name) throws ClassNotFoundException {
		return forNameByThread(Classes.class.getClassLoader(), name);
	}

	@SuppressWarnings("rawtypes")
	public static Class forNameByThread(ClassLoader loader, String clsName) throws ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null)
			try {
				return Class.forName(clsName, true, cl);
			} catch (ClassNotFoundException ex) { // ignore and try the other
			}
		return forName(loader, clsName);
	}

	public static Enumeration<URL> getResources(ClassLoader loader, String resourceName) throws IOException {
		while (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		return loader.getResources(resourceName);
	}

	public static Enumeration<URL> getResourcesByThread(ClassLoader loader, String resourceName) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null)
			try {
				return getResources(cl, resourceName);
			} catch (IOException ex) { // ignore and try the other
			}
		return getResources(loader, resourceName);
	}

	public static URL getResource(ClassLoader loader, String resourceName) throws IOException {
		while (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		return loader.getResource(resourceName);
	}

	public static URL getResourceByThread(ClassLoader loader, String resourceName) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			return getResource(cl, resourceName);
		}
		return getResource(loader, resourceName);
	}

	public static InputStream getResourceAsStream(ClassLoader loader, String resourceName) throws IOException {
		while (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		return loader.getResourceAsStream(resourceName);
	}

	public static InputStream getResourceAsStreamByThread(ClassLoader loader, String resourceName) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			return getResourceAsStream(cl, resourceName);
		}
		return getResourceAsStream(loader, resourceName);
	}

	public static String getResourceAsString(ClassLoader loader, String resourceName, Charset charset)
			throws IOException {
		try (InputStream is = getResourceAsStream(loader, resourceName)) {
			return new String(Streams.toByteArray(is), charset);
		}
	}

	public static String getResourceAsStringByThread(ClassLoader loader, String resourceName, Charset charset)
			throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			try (InputStream is = getResourceAsStream(cl, resourceName)) {
				return new String(Streams.toByteArray(is), charset);
			}
		}
		return getResourceAsString(loader, resourceName, charset);
	}

	private static HashMap<Class<?>, Class<?>> primitiveToObject = new HashMap<Class<?>, Class<?>>();
	static {
		primitiveToObject.put(java.lang.Boolean.TYPE, java.lang.Boolean.class);
		primitiveToObject.put(java.lang.Character.TYPE, java.lang.Character.class);
		primitiveToObject.put(java.lang.Byte.TYPE, java.lang.Byte.class);
		primitiveToObject.put(java.lang.Short.TYPE, java.lang.Short.class);
		primitiveToObject.put(java.lang.Integer.TYPE, java.lang.Integer.class);
		primitiveToObject.put(java.lang.Long.TYPE, java.lang.Long.class);
		primitiveToObject.put(java.lang.Float.TYPE, java.lang.Float.class);
		primitiveToObject.put(java.lang.Double.TYPE, java.lang.Double.class);
		primitiveToObject.put(java.lang.Void.TYPE, java.lang.Void.class);
	}

	public static Class<?> toObjectClass(Class<?> clz) {
		Class<?> o = primitiveToObject.get(clz);
		return o == null ? clz : o;
	}

	@SuppressWarnings("rawtypes")
	public static String getResourceAsString(Class clz, String name) throws IOException {
		return getResourceAsString(clz, name, Strings.UTF8);
	}

	@SuppressWarnings("rawtypes")
	public static String getResourceAsString(Class clz, String name, Charset charset) throws IOException {
		try (InputStream is = clz.getResourceAsStream(name)) {
			if (is == null) {
				throw new IOException(Strings.format("resource {} not found", name));
			}
			return new String(Streams.toByteArray(is), charset);
		}
	}
}