package onexas.coordinate.lic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onexas.coordinate.common.util.ObfuscatedString;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface License {

	// ObfuscatedString: coordinate.LICENSE
	public static final Logger logger = LoggerFactory.getLogger(
			new ObfuscatedString(4632039037817003020L,1696468376059332080L,5664601772552797422L,-6111430817106064909L).toString());
	public static final String dateFormat = "yyyy-MM-dd";

	public String getSerialNumber();

	/**
	 * The date-from that is formated by yyyy-MM-dd pattern
	 */
	public String getDateFrom();

	public boolean isUnlimitedDateFrom();

	/**
	 * The date-to that is formated by yyyy-MM-dd pattern
	 */
	public String getDateTo();

	public boolean isUnlimitedDateTo();

	public String getIssuer();

	public String getOwner();

	public String getSubject();

	public List<String> getAllowedIps();

	/**
	 * format by ':', e.x 08:00:27:d6:e6:16
	 */
	public List<String> getAllowedMacs();

	public boolean hasFeature(String featureName);

	public Feature getFeature(String featureName);

	public String getDescription();

	public String getShortDescription();
}
