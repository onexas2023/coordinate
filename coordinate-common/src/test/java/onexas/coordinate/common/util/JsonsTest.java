package onexas.coordinate.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.test.CoordinateCommonTestBase;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JsonsTest extends CoordinateCommonTestBase{
	long parseBirthTime(String str) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		try {
			return f.parse(str).getTime();
		} catch (ParseException e) {}
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
		
		Map<String,Object> attr = new LinkedHashMap<>();
		attr.put("abc.def", "123");
		attr.put("abc.xyz", 99);
		p.setAttr(attr);
		
		String json = Jsons.jsonify(p);
		String plain = json;
		String pretty = Jsons.pretty(json);
		String plain2 = Jsons.plain(pretty);
		System.out.println("1>>"+json);
		System.out.println("2>>"+pretty);
		System.out.println("2>>"+plain2);
		
		Assert.assertEquals(plain, plain2);
		Assert.assertNotEquals(plain, pretty);
		
		Person p1 = Jsons.objectify(json, Person.class);
		
		Assert.assertEquals(p.getName(), p1.getName());
		Assert.assertEquals(p.getBirthTime(), p1.getBirthTime());
		Assert.assertEquals(p.getAddress().getStreet(), p1.getAddress().getStreet());
		Assert.assertEquals(p.getAttr().get("abc.def"), p1.getAttr().get("abc.def"));
		Assert.assertEquals(p.getAttr().get("abc.xyz"), p1.getAttr().get("abc.xyz"));
	}
	
	@Test
	public void testConfig() {
		Person p = new Person();
		p.setName("Dennis");
		p.setBirthTime(parseBirthTime("19750214"));
		Address addr = new Address();
		addr.setStreet("abcde");
		p.setAddress(addr);
		
		Map<String,Object> attr = new LinkedHashMap<>();
		attr.put("abc.def", "123");
		attr.put("abc.xyz", 99);
		p.setAttr(attr);
		
		String ymal = Jsons.jsonify(p);
		System.out.println("1>>"+ymal);
		
		Configuration config = null;
		try {
			config = Jsons.toConfiguration(ymal);
		} catch (ConfigurationException e) {
			Assert.fail();
		}
		
		Assert.assertEquals(p.getName(), config.getString("name"));
		Assert.assertEquals(p.getBirthTime(), config.getLong("birthTime"));
		Assert.assertEquals(p.getAddress().getStreet(), config.getString("address.street"));
		Iterator<String> keys = config.getKeys();
		while(keys.hasNext()) {
			String k = keys.next();
			System.out.println(">>>>"+k);
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
		
		Person2 p1Top2 = Jsons.transform(p1, Person2.class);
		Person p2Top1 = Jsons.transform(p2, Person.class);
		
		Assert.assertEquals(p1.getName(), p1Top2.getName());
		Assert.assertEquals(p1.getBirthTime(), p1Top2.getBirthTime());
		Assert.assertNull(p1Top2.getAddress2());
		
		Assert.assertEquals(p2.getName(), p2Top1.getName());
		Assert.assertEquals(p2.getBirthTime(), p2Top1.getBirthTime());
		
	}
	
	@Test
	public void testGenericMemberField() {
		
		Foo foo = new Foo("foo",Collections.asList(new Bar("bar1"), new Bar("bar2")));
		
		String json = Jsons.jsonify(foo);
		System.out.println(">>>>"+json);
		
		foo = Jsons.objectify(json, Foo.class);
		
		Assert.assertEquals("foo", foo.getName());
		Bar bar = foo.getBars().get(0);
		Assert.assertEquals("bar1", bar.getName());
		bar = foo.getBars().get(1);
		Assert.assertEquals("bar2", bar.getName());
	}
	
	@Test
	public void testCleanOnErrory() {
		
		@SuppressWarnings("rawtypes")
		Map map = Jsons.objectify("abc", Map.class, true);
		Assert.assertEquals(0, map.size());
		
		Person person = Jsons.objectify("abc", Person.class, true);
		Assert.assertNull(person.getName());
	}
	
	@Test
	public void testTimeZone() {
		
		TimeZone timeZone = TimeZone.getDefault();
		
		String str = Jsons.jsonify(timeZone);
		System.out.println(">>>>"+str);
		
		TimeZone tz = Jsons.objectify(str, TimeZone.class);
		
		Assert.assertEquals(timeZone, tz);
	}
	
	@Test
	public void testLocale() {
		
		Locale locale = Locale.getDefault();
		
		String str = Jsons.jsonify(locale);
		System.out.println(">>>>"+str);
		
		Locale l = Jsons.objectify(str, Locale.class);
		
		Assert.assertEquals(locale, l);
	}
	
	public static class Person {
		String name;
		long birthTime;
		Address address;
		Map<String,Object> attr;
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
		public Map<String,Object> getAttr() {
			return attr;
		}
		public void setAttr(Map<String,Object> attr) {
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
	
	public static class Foo {
		String name;
		
		List<Bar> bars;
		
		public Foo() {}

		public Foo(String name, List<Bar> bars) {
			super();
			this.name = name;
			this.bars = bars;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Bar> getBars() {
			return bars;
		}

		public void setBars(List<Bar> bars) {
			this.bars = bars;
		}
		
		
	}
	
	public static class Bar {
		String name;

		public Bar() {}
		
		public Bar(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public static class Data {
		byte data1;
		byte[] data2;
		public byte getData1() {
			return data1;
		}
		public void setData1(byte data1) {
			this.data1 = data1;
		}
		public byte[] getData2() {
			return data2;
		}
		public void setData2(byte[] data2) {
			this.data2 = data2;
		}
	}
	
	@Test
	public void testByte() {
		Data data = new Data();
		System.out.println(">>>>>>>>"+Jsons.jsonify(data));
		Data d1 = Jsons.transform(data, Data.class);
		Assert.assertEquals(0x00, d1.data1);
		Assert.assertNull(d1.data2);
		
		
		data.data1 = 0x33;
		data.data2 = new byte[] {0x12,0x11};
		
		System.out.println(">>>>>>>>"+Jsons.jsonify(data));
		Data d2 = Jsons.transform(data, Data.class);
		Assert.assertEquals(0x33, d2.data1);
		Assert.assertEquals(2,d2.data2.length);
		Assert.assertEquals(0x12, d2.data2[0]);
		Assert.assertEquals(0x11, d2.data2[1]);
	}
}
