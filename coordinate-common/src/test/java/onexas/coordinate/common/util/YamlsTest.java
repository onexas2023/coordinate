package onexas.coordinate.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class YamlsTest extends CoordinateCommonTestBase {
	long parseBirthTime(String str) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		try {
			return f.parse(str).getTime();
		} catch (ParseException e) {
		}
		return -1;
	}

	@Test
	public void testSimple() {
		Person p = new Person();
		p.setName("Dennis");
		p.setBirthTime(parseBirthTime("19750214"));
		Address addr = new Address();
		addr.setStreet("abcde");
		p.setAddress(addr);

		Map<String, Object> attr = new LinkedHashMap<>();
		attr.put("abc.def", "123");
		attr.put("abc.xyz", 99);
		p.setAttr(attr);

		String yaml = Yamls.yamlify(p);
		System.out.println("1>>" + yaml);

		Person p1 = Yamls.objectify(yaml, Person.class);

		Assert.assertEquals(p.getName(), p1.getName());
		Assert.assertEquals(p.getBirthTime(), p1.getBirthTime());
		Assert.assertEquals(p.getAddress().getStreet(), p1.getAddress().getStreet());
		Assert.assertEquals(p.getAttr().get("abc.def"), p1.getAttr().get("abc.def"));
		Assert.assertEquals(p.getAttr().get("abc.xyz"), p1.getAttr().get("abc.xyz"));
	}

	@Test
	public void testCollection() {
		Person p1 = new Person();
		p1.setName("Dennis");

		Person p2 = new Person();
		p2.setName("Alice");

		List<Person> list = new LinkedList<>();
		list.add(p1);
		list.add(p2);

		String yaml = Yamls.yamlify(list);
		System.out.println("1>>" + yaml);

		PersonList list2 = Yamls.objectify(yaml, PersonList.class);

		Assert.assertEquals(2, list2.size());
		Person p = list2.get(0);
		Assert.assertEquals(p1.getName(), p.getName());
		p = list2.get(1);
		Assert.assertEquals(p2.getName(), p.getName());
	}

	@Test
	public void testArray() {
		Person p1 = new Person();
		p1.setName("Dennis");

		Person p2 = new Person();
		p2.setName("Alice");

		Person[] list = new Person[] { p1, p2 };

		String yaml = Yamls.yamlify(list);
		System.out.println("1>>" + yaml);

		Person[] list2 = Yamls.objectify(yaml, Person[].class);

		Assert.assertEquals(2, list2.length);
		Person p = list2[0];
		Assert.assertEquals(p1.getName(), p.getName());
		p = list2[1];
		Assert.assertEquals(p2.getName(), p.getName());
	}

	@Test
	public void testConfig() {
		Person p = new Person();
		p.setName("Dennis");
		p.setBirthTime(parseBirthTime("19750214"));
		Address addr = new Address();
		addr.setStreet("abcde");
		p.setAddress(addr);

		Map<String, Object> attr = new LinkedHashMap<>();
		attr.put("abc.def", "123");
		attr.put("abc.xyz", 99);
		p.setAttr(attr);

		String yaml = Yamls.yamlify(p);
		System.out.println("1>>" + yaml);

		Configuration config = null;
		try {
			config = Yamls.toConfiguration(yaml);
		} catch (BadConfigurationException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertEquals(p.getName(), config.getString("name"));
		Assert.assertEquals(p.getBirthTime(), config.getLong("birthTime"));
		Assert.assertEquals(p.getAddress().getStreet(), config.getString("address.street"));
		Iterator<String> keys = config.getKeys();
		while (keys.hasNext()) {
			String k = keys.next();
			System.out.println(">>>>" + k);
		}
		Assert.assertEquals(p.getAttr().get("abc.def"), config.getString("attr.abc..def"));
		Assert.assertEquals(p.getAttr().get("abc.xyz"), config.getInteger("attr.abc..xyz", null));
	}

	@Test
	public void testTransform() {
		Person p1 = new Person();
		p1.setName("Dennis");
		p1.setBirthTime(parseBirthTime("19750214"));

		Person2 p2 = new Person2();
		p2.setName("Alice");
		p2.setBirthTime(parseBirthTime("19800303"));
		p2.setAddress("dingx st.");

		Person2 p1Top2 = Yamls.transform(p1, Person2.class);
		Person p2Top1 = Yamls.transform(p2, Person.class);

		Assert.assertEquals(p1.getName(), p1Top2.getName());
		Assert.assertEquals(p1.getBirthTime(), p1Top2.getBirthTime());
		Assert.assertNull(p1Top2.getAddress2());

		Assert.assertEquals(p2.getName(), p2Top1.getName());
		Assert.assertEquals(p2.getBirthTime(), p2Top1.getBirthTime());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMap() {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("a", "a1");
		m.put("b", 123);
		String yaml = Yamls.yamlify(m);
		System.out.println(">>>>>" + yaml);
		Map<String, Object> m2 = Yamls.objectify(yaml, Map.class);
		Assert.assertEquals(m.get("a"), m2.get("a"));
		Assert.assertEquals(m.get("b"), m2.get("b"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyMap() {
		Map<String, Object> m = new LinkedHashMap<>();
		String yaml = Yamls.yamlify(m);
		System.out.println(">>>>>" + yaml);
		Map<String, Object> m2 = Yamls.objectify(yaml, Map.class);
		Assert.assertEquals(m.size(), m2.size());

		m2 = Yamls.objectify("", Map.class);
		Assert.assertEquals(m.size(), m2.size());
	}

	@Test
	public void testCleanOnErrory() {

		@SuppressWarnings("rawtypes")
		Map map = Yamls.objectify("abc", Map.class, true);
		Assert.assertEquals(0, map.size());

		Person person = Yamls.objectify("abc", Person.class, true);
		Assert.assertNull(person.getName());
	}
	
	@Test
	public void testBlankOrCommentOnly() {

		@SuppressWarnings("rawtypes")
		Map map = Yamls.objectify("#abcd", Map.class);
		Assert.assertEquals(0, map.size());

		Person person = Yamls.objectify(" #abcd", Person.class);
		Assert.assertNull(person.getName());
		
		map = Yamls.objectify(" ", Map.class);
		Assert.assertEquals(0, map.size());

		person = Yamls.objectify(" ", Person.class);
		Assert.assertNull(person.getName());
	}

	@Test
	public void testPropertiesPropertySource() {
		String yaml = "" + "config1: true\n" //
				+ "config1.name: alice\n" //
				+ "config1.age: 30\n" //
				+ "config2.name: john\n" //
				+ "config2.age: 34\n" //
				+ "config3:\n" //
				+ "  name: dennis\n" //
				+ "  age: 41\n" //
				+ "config3.age: 42\n" // overwrite
				+ "";

		try {

			PropertiesPropertySource conf = Yamls.toPropertiesPropertySource(yaml);

			System.out.println(">>>>>" + conf.getProperty("config1"));
			System.out.println(">>>>>" + conf.getProperty("config1.name"));
			System.out.println(">>>>>" + conf.getProperty("config1.age"));
			System.out.println(">>>>>" + conf.getProperty("config2"));
			System.out.println(">>>>>" + conf.getProperty("config2.name"));
			System.out.println(">>>>>" + conf.getProperty("config2.age"));
			System.out.println(">>>>>" + conf.getProperty("config3"));
			System.out.println(">>>>>" + conf.getProperty("config3.name"));
			System.out.println(">>>>>" + conf.getProperty("config3.age"));

			Assert.assertEquals(Boolean.TRUE, conf.getProperty("config1"));
			Assert.assertEquals("alice", conf.getProperty("config1.name"));
			Assert.assertEquals(30, conf.getProperty("config1.age"));
			Assert.assertEquals(null, conf.getProperty("config2"));
			Assert.assertEquals("john", conf.getProperty("config2.name"));
			Assert.assertEquals(34, conf.getProperty("config2.age"));
			Assert.assertEquals(null, conf.getProperty("config3"));
			Assert.assertEquals("dennis", conf.getProperty("config3.name"));
			Assert.assertEquals(42, conf.getProperty("config3.age"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testYamlMapPropertySource() {
		String yaml = "" + "config1: true\n" //
				+ "config1.name: alice\n" //
				+ "config1.age: 30\n" //
				+ "config2.name: john\n" //
				+ "config2.age: 34\n" //
				+ "config3:\n" //
				+ "  name: dennis\n" //
				+ "  age: 41\n" //
				+ "config3.age: 42\n" // overwrite
				+ "";

		try {

			MapPropertySource conf = Yamls.toMapPropertySource(yaml);

			System.out.println(">>>>>list " + Collections.asList(conf.getPropertyNames()));
			System.out.println(">>>>>source " + conf.getSource());

			System.out.println(">>>>>" + conf.getProperty("config1"));
			System.out.println(">>>>>" + conf.getProperty("config1.name"));
			System.out.println(">>>>>" + conf.getProperty("config1.age"));
			System.out.println(">>>>>" + conf.getProperty("config2"));
			System.out.println(">>>>>" + conf.getProperty("config2.name"));
			System.out.println(">>>>>" + conf.getProperty("config2.age"));
			System.out.println(">>>>>" + conf.getProperty("config3"));
			System.out.println(">>>>>" + conf.getProperty("config3.name"));
			System.out.println(">>>>>" + conf.getProperty("config3.age"));

			Assert.assertEquals(Boolean.TRUE, conf.getProperty("config1"));
			Assert.assertEquals("alice", conf.getProperty("config1.name"));
			Assert.assertEquals(30, conf.getProperty("config1.age"));
			Assert.assertEquals(null, conf.getProperty("config2"));
			Assert.assertEquals("john", conf.getProperty("config2.name"));
			Assert.assertEquals(34, conf.getProperty("config2.age"));

			@SuppressWarnings("unchecked")
			Map<String, Object> obj = (Map<String, Object>) conf.getProperty("config3");

			Assert.assertEquals("dennis", obj.get("name"));
			Assert.assertEquals(41, obj.get("age"));
			Assert.assertEquals(42, conf.getProperty("config3.age"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSimplePropertySource() {
		String yaml = ""//
				+ "string-array:\n" //
				+ " - value1\n" //
				+ " - value2\n" //
				+ " - value3\n" //
				+ "number-array:\n" //
				+ "  - 1\n" //
				+ "  - 2.2\n" //
				+ "  - 3\n" //
				+ "object-array:\n" //
				+ "  - name: value1\n" //
				+ "    ttl: 1\n" //
				+ "  - name: value2\n" //
				+ "    ttl: 3\n" //
				+ "string-array-line: a, b, c\n" //
				+ "a: 42\n" // overwrite
				+ "a: aabbbcc\n" // overwrite
				+ "a: ddeeff\n" // overwrite
				+ "a.b: aabbbcc\n" // overwrite
				+ "a.b: 33\n" // overwrite
				+ "c: iii\n" // type overwrite
				+ "c:\n" // type overwrite
				+ "  d: jjj\n" // type overwrite
				+ "c.e: fff\n" // type overwrite
				+ "";
		System.out.println(">>>\n" + yaml);
		try {

			BetterPropertySource conf = Yamls.toPropertySource(yaml);

			System.out.println(">>>>>props " + Collections.asList(conf.getPropertyNames()));
			System.out.println(">>>>>source " + conf.getSource());
			System.out.println(">>>>>" + conf.getProperty("string-array"));
			System.out.println(">>>>>getLength " + conf.getLength("string-array"));
			System.out.println(">>>>>len " + conf.length("string-array"));

			System.out.println(">>>>>len null " + conf.getLength("string-array[0]"));
			System.out.println(">>>>>len 0 " + conf.length("string-array[0]"));

			System.out.println(">>>>>" + conf.getProperty("string-array[0]"));
			System.out.println(">>>>>" + conf.getProperty("string-array[1]"));
			System.out.println(">>>>>" + conf.getProperty("string-array[2]"));
			System.out.println(">>>>>" + conf.getProperty("string-array[3]"));
			System.out.println(">>>>>" + conf.getProperty("number-array"));
			System.out.println(">>>>>getLength " + conf.getLength("number-array"));
			System.out.println(">>>>>len " + conf.length("number-array"));
			System.out.println(">>>>>" + conf.getProperty("number-array[0]"));
			System.out.println(">>>>>" + conf.getProperty("number-array[1]"));
			System.out.println(">>>>>" + conf.getProperty("number-array[2]"));
			System.out.println(">>>>>" + conf.getProperty("number-array[3]"));
			System.out.println(">>>>>" + conf.getProperty("object-array"));
			System.out.println(">>>>>getLength " + conf.getLength("object-array"));
			System.out.println(">>>>>len " + conf.length("object-array"));
			System.out.println(">>>>>" + conf.getProperty("object-array[0]"));
			System.out.println(">>>>>" + conf.getProperty("object-array[0].name"));
			System.out.println(">>>>>" + conf.getProperty("object-array[0].ttl"));
			System.out.println(">>>>>" + conf.getProperty("object-array[1]"));
			System.out.println(">>>>>" + conf.getProperty("object-array[1].name"));
			System.out.println(">>>>>" + conf.getProperty("object-array[1].ttl"));

			System.out.println(">>>>a>" + conf.getProperty("a"));
			System.out.println(">>>>a.b>" + conf.getProperty("a.b"));
			System.out.println(">>>>c>" + conf.getProperty("c"));
			System.out.println(">>>>c.d>" + conf.getProperty("c.d"));
			System.out.println(">>>>c.e>" + conf.getProperty("c.e"));

			Assert.assertEquals(null, conf.getProperty("string-array"));
			Assert.assertEquals(3, conf.getLength("string-array").intValue());
			Assert.assertEquals(3, conf.length("string-array"));
			Assert.assertEquals(null, conf.getLength("string-array[0]"));
			Assert.assertEquals(0, conf.length("string-array[0]"));
			Assert.assertEquals("value1", conf.getProperty("string-array[0]"));
			Assert.assertEquals("value2", conf.getProperty("string-array[1]"));
			Assert.assertEquals("value3", conf.getProperty("string-array[2]"));
			Assert.assertEquals(null, conf.getProperty("string-array[3]"));

			Assert.assertEquals("value1", conf.getString("string-array[0]"));
			Assert.assertEquals("value2", conf.getString("string-array[1]"));
			Assert.assertEquals("value3", conf.getString("string-array[2]"));
			Assert.assertEquals(null, conf.getString("string-array[3]"));
			Assert.assertEquals("novalue", conf.getString("string-array[3]", "novalue"));

			Assert.assertEquals(null, conf.getInteger("string-array[0]"));
			Assert.assertEquals(null, conf.getInteger("string-array[1]"));
			Assert.assertEquals(null, conf.getInteger("string-array[2]"));
			Assert.assertEquals(null, conf.getInteger("string-array[3]"));
			Assert.assertEquals(10, conf.getInteger("string-array[3]", 10).intValue());

			List<String> list = conf.getStringList("string-array");
			Assert.assertNotNull(list);
			Assert.assertEquals(3, list.size());
			Assert.assertEquals("value1", list.get(0));
			Assert.assertEquals("value2", list.get(1));
			Assert.assertEquals("value3", list.get(2));
			
			list = conf.getStringList("string-array-line");
			Assert.assertNotNull(list);
			Assert.assertEquals(3, list.size());
			Assert.assertEquals("a", list.get(0));
			Assert.assertEquals("b", list.get(1));
			Assert.assertEquals("c", list.get(2));

			Assert.assertEquals(Collections.asList("value1"), conf.getStringList("string-array[0]"));

			Assert.assertEquals(3, conf.getLength("number-array").intValue());
			Assert.assertEquals(3, conf.length("number-array"));
			Assert.assertEquals(null, conf.getLength("number-array[0]"));
			Assert.assertEquals(0, conf.length("number-array[0]"));
			Assert.assertEquals(1, conf.getProperty("number-array[0]"));
			Assert.assertEquals(2.2, conf.getProperty("number-array[1]"));
			Assert.assertEquals(3, conf.getProperty("number-array[2]"));
			Assert.assertEquals(null, conf.getProperty("number-array[3]"));

			Assert.assertEquals("1", conf.getString("number-array[0]"));
			Assert.assertEquals("2.2", conf.getString("number-array[1]"));
			Assert.assertEquals("3", conf.getString("number-array[2]"));
			Assert.assertEquals(null, conf.getString("number-array[3]"));
			Assert.assertEquals("novalue", conf.getString("number-array[3]", "novalue"));

			Assert.assertEquals(1, conf.getInteger("number-array[0]").intValue());
			Assert.assertEquals(2, conf.getInteger("number-array[1]").intValue());
			Assert.assertEquals(3, conf.getInteger("number-array[2]").intValue());
			Assert.assertEquals(null, conf.getInteger("number-array[3]"));

			Assert.assertEquals(2.2, conf.getFloat("number-array[1]").floatValue(), 0.1);

			list = conf.getStringList("number-array");
			Assert.assertNotNull(list);
			Assert.assertEquals(3, list.size());
			Assert.assertEquals("1", list.get(0));
			Assert.assertEquals("2.2", list.get(1));
			Assert.assertEquals("3", list.get(2));

			Assert.assertEquals(Collections.asList("1"), conf.getStringList("number-array[0]"));

			Assert.assertEquals("ddeeff", conf.getProperty("a"));
			Assert.assertEquals(33, conf.getProperty("a.b"));

			Assert.assertEquals(null, conf.getProperty("c"));
			Assert.assertEquals("jjj", conf.getProperty("c.d"));
			Assert.assertEquals("fff", conf.getProperty("c.e"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testSimplePropertySourceSpecialArray() {
		String yaml = ""//
				+ "a.b:\n" //
				+ " - v1\n" //
				+ " - v2\n" //
				+ " - v3\n" //
				+ "a:\n" //
				+ " c:\n" //
				+ "  - v4\n" //
				+ "  - v5\n" //
				+ " d.e:\n" //
				+ "  - v6\n" //
				+ "  - v7\n" //
				+ "  - v8\n" //
				+ "";
		System.out.println(">>>\n" + yaml);
		try {

			BetterPropertySource conf = Yamls.toPropertySource(yaml);

			System.out.println(">>>>>props " + Collections.asList(conf.getPropertyNames()));
			System.out.println(">>>>>source " + conf.getSource());
			System.out.println(">>>>>map props " + Collections.asList(conf.getMapPropertySource().getPropertyNames()));
			System.out.println(">>>>>map source " + conf.getMapPropertySource().getSource());
			System.out.println(">>>>>" + conf.getProperty("a.b"));
			System.out.println(">>>>>getLength " + conf.getLength("a.b"));
			System.out.println(">>>>>len " + conf.length("a.b"));
			System.out.println(">>>>>getLength " + conf.getLength("a.c"));
			System.out.println(">>>>>getLength " + conf.getLength("a.d.e"));

			Assert.assertEquals(3, conf.getLength("a.b").intValue());
			Assert.assertEquals(3, conf.length("a.b"));
			Assert.assertEquals("v1", conf.getString("a.b[0]"));
			Assert.assertEquals("v2", conf.getString("a.b[1]"));
			Assert.assertEquals("v3", conf.getString("a.b[2]"));
			
			Assert.assertEquals(2, conf.getLength("a.c").intValue());
			Assert.assertEquals(2, conf.length("a.c"));
			Assert.assertEquals("v4", conf.getString("a.c[0]"));
			Assert.assertEquals("v5", conf.getString("a.c[1]"));
			
			Assert.assertEquals(3, conf.getLength("a.d.e").intValue());
			Assert.assertEquals(3, conf.length("a.d.e"));
			Assert.assertEquals("v6", conf.getString("a.d.e[0]"));
			Assert.assertEquals("v7", conf.getString("a.d.e[1]"));
			Assert.assertEquals("v8", conf.getString("a.d.e[2]"));

			

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSimplePropertySourceException() {
		String yaml;
		try {
			yaml = ""//
					+ "a:\n" //
					+ "	b=tab:\n" //
					+ "";
			System.out.println(">>>\n" + yaml);
			Yamls.toPropertySource(yaml);
			Assert.fail("no here");
		} catch (BadConfigurationException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			yaml = ""//
					+ "a:\n" //
					+ "b\n" //
					+ "";
			System.out.println(">>>\n" + yaml);
			Yamls.toPropertySource(yaml);
			Assert.fail("no here");
		} catch (BadConfigurationException e) {
			System.out.println(e.getMessage());
		}
	}

	public static class Person {
		String name;
		long birthTime;
		Address address;
		Map<String, Object> attr;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getBirthTime() {
			return birthTime;
		}

		public void setBirthTime(long birthTime) {
			this.birthTime = birthTime;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public Map<String, Object> getAttr() {
			return attr;
		}

		public void setAttr(Map<String, Object> attr) {
			this.attr = attr;
		}

	}

	public static class Address {
		String street;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

	}

	public static class Person2 {
		String name;
		long birthTime;
		String address2;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getBirthTime() {
			return birthTime;
		}

		public void setBirthTime(long birthTime) {
			this.birthTime = birthTime;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress(String address2) {
			this.address2 = address2;
		}

	}

	public static class PersonList extends LinkedList<Person> {
		private static final long serialVersionUID = 1L;

	}
}
