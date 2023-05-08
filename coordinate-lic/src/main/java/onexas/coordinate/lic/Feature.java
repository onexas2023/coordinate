package onexas.coordinate.lic;

import java.util.List;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface Feature {
	
	
	String UNLIMITED = "*";
	
	String getName();

	public boolean hasValue(String prop);

	public boolean isUnlimited(String prop);

	public Number getNumberValue(String prop);

	public String getStringValue(String prop);

	public Boolean getBooleanValue(String prop);
	
	public List<String> listProp();
}