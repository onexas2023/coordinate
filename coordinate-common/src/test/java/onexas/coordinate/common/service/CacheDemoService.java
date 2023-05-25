package onexas.coordinate.common.service;

import java.util.List;


public interface CacheDemoService {

	public Item add(String code, String name);

	public Item remove(String code);

	public Item getByArg0(String code, String uselesskey);

	public Item get(String code);
	
	public Item getBy2Key(String co, String de);

	public int getCallGet();

	public Item update(String code, String name);

	public void clearAll(String unlesskey);
	
	public Item clearByResult(String uselesskey, String code);

	public void resetCallGet();

	public List<Item> list();

	static public class Item {
		String code;
		String name;

		public Item(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
