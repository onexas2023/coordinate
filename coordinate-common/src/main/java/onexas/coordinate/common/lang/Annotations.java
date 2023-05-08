package onexas.coordinate.common.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.util.LRUMap;

import onexas.coordinate.common.util.ValueII;
import onexas.coordinate.common.util.ValueIII;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Annotations {

	// clz,annoclz,searchIterface
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Map<ValueIII<Class, Class, Boolean>, Object> clzAnnoCache = Collections
			.synchronizedMap((Map) new LRUMap(100, 500));

	// clz,annoclz
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Map<ValueII<Class, Class>, List<ValueII<Field, Object>>> fieldAnnoCache = Collections
			.synchronizedMap((Map) new LRUMap(100, 500));

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Map<ValueII<Class, Class>, List<ValueII<Method, Object>>> methodAnnoCache = Collections
			.synchronizedMap((Map) new LRUMap(100, 500));

	private static final Object NULL = new Object();

	/**
	 * Search annotation for it's class, super class return the first hit one.
	 * 
	 * @param clz
	 * @param annoclz
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T extends Annotation> T searchAnnotation(final Class clz, final Class<T> annoclz) {
		return searchAnnotation0(clz, annoclz, false);
	}

	/**
	 * Search annotation for it's class, super class, interface, super class's
	 * interface, interface's interface return the first hit one.
	 * 
	 * @param clz
	 * @param annoclz
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T extends Annotation> T searchAnnotationWihtInterface(final Class clz, final Class<T> annoclz) {
		return searchAnnotation0(clz, annoclz, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T extends Annotation> T searchAnnotation0(final Class clz, final Class<T> annoclz,
			boolean searchIterface) {
		T anno = (T) clz.getAnnotation(annoclz);
		if (anno != null) {
			return anno;
		}
		ValueIII<Class, Class, Boolean> key = new ValueIII<Class, Class, Boolean>(clz, annoclz, searchIterface);
		Object val = clzAnnoCache.get(key);
		if (val != null && annoclz.isInstance(val)) {
			return (T) val;
		} else if (val == NULL) {
			return null;
		}

		List<Class> ifas = searchIterface ? new LinkedList<Class>() : null;
		Set<Class> compared = new HashSet<Class>();
		Class parent = clz;
		while (parent != null && !parent.equals(Object.class)) {
			anno = (T) clz.getAnnotation(annoclz);
			if (anno != null) {
				clzAnnoCache.put(key, anno);
				return anno;
			}
			if (searchIterface) {
				for (Class c : parent.getInterfaces()) {
					ifas.add(c);
				}
			}
			parent = parent.getSuperclass();
		}
		if (searchIterface) {
			while (ifas.size() > 0) {
				Class ifa = ifas.remove(0);
				if (compared.contains(ifa)) {
					continue;
				}
				compared.add(ifa);
				for (Class c : ifa.getInterfaces()) {
					ifas.add(c);
				}
				anno = (T) ifa.getAnnotation(annoclz);
				if (anno != null) {
					clzAnnoCache.put(key, anno);
					return anno;
				}
			}
		}
		clzAnnoCache.put(key, NULL);
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Annotation> List<ValueII<Field, T>> searchFieldAnnotation(final Class clz,
			final Class<T> annoclz) {
		ValueII<Class, Class> key = new ValueII<Class, Class>(clz, annoclz);

		List cahce = fieldAnnoCache.get(key);
		if (cahce != null) {
			return cahce;
		}

		List<ValueII<Field, Object>> list = new LinkedList<ValueII<Field, Object>>();

		Class parent = clz;
		while (parent != null && !parent.equals(Object.class)) {

			Field[] fields = parent.getDeclaredFields();

			if (fields != null) {
				for (Field field : fields) {
					T anno = field.getAnnotation(annoclz);
					if (anno != null) {
						list.add(new ValueII<Field, Object>(field, anno));
					}
				}
			}
			parent = parent.getSuperclass();
		}

		fieldAnnoCache.put(key, list);
		return (List) list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends Annotation> List<ValueII<Method, T>> searchMethodAnnotation(final Class clz,
			final Class<T> annoclz) {
		ValueII<Class, Class> key = new ValueII<Class, Class>(clz, annoclz);

		List cahce = methodAnnoCache.get(key);
		if (cahce != null) {
			return cahce;
		}

		List<ValueII<Method, Object>> list = new LinkedList<ValueII<Method, Object>>();

		Class parent = clz;
		while (parent != null && !parent.equals(Object.class)) {

			Method[] methods = parent.getDeclaredMethods();

			if (methods != null) {
				for (Method method : methods) {
					T anno = method.getAnnotation(annoclz);
					if (anno != null) {
						list.add(new ValueII<Method, Object>(method, anno));
					}
				}
			}
			parent = parent.getSuperclass();
		}

		methodAnnoCache.put(key, list);
		return (List) list;
	}

}