package onexas.coordinate.lic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;

import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.lic.Feature;
import onexas.coordinate.lic.License;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LoaderLicense implements License {

	protected String serialNumber;

	protected String dateFrom, dateTo;

	protected String issuer, subject, owner;

	protected List<String> ipsList;
	protected List<String> macsList;

	protected Map<String, LoaderFeature> featuresMap;

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public String getIssuer() {
		return issuer;
	}

	public String getOwner() {
		return owner;
	}

	public String getSubject() {
		return subject;
	}

	@Override
	public boolean hasFeature(String featureName) {
		return featuresMap == null ? false : featuresMap.containsKey(featureName);
	}

	@Override
	public Feature getFeature(String featureName) {
		return featuresMap == null ? null : featuresMap.get(featureName);
	}

	@SuppressWarnings("unchecked")
	public List<Feature> listFeature() {
		if (featuresMap == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(new ArrayList<Feature>(featuresMap.values()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllowedIps() {
		return ipsList == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(ipsList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllowedMacs() {
		return macsList == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(macsList);
	}

	private void mesh(StringBuilder sb, Object obj) {
		if (sb.length() > 0) {
			sb.append(",");
		}
		sb.append(obj == null ? "" : obj.toString());
	}

	public String getMesh() {
		StringBuilder sb = new StringBuilder();
		sb.append(getSerialNumber());
		mesh(sb, getSubject());
		mesh(sb, getIssuer());
		mesh(sb, getOwner());
		mesh(sb, getDateFrom());
		mesh(sb, getDateTo());

		List<String> allowIps = getAllowedIps();
		List<String> allowMacs = getAllowedMacs();

		if (allowIps.size() > 0) {
			mesh(sb, toListString(allowIps));
		}
		if (allowMacs.size() > 0) {
			mesh(sb, toListString(allowMacs));
		}

		for (Feature f : listFeature()) {
			String fn = f.getName();
			mesh(sb, fn);
			Map<String, String> valueMap = ((LoaderFeature) f).getValueMap();
			// sort to guarantee the sequence for hash
			List<String> keys = new ArrayList<String>(valueMap.keySet());
			Collections.sort(keys);
			for (String key : keys) {
				mesh(sb, key);
				String value = f.getStringValue(key);
				mesh(sb, value);
			}
		}
		return sb.toString();
	}

	private String toListString(List<String> listStr) {
		StringBuilder sb = new StringBuilder();
		for (String s : listStr) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public void load(PropertiesConfiguration pp) {
		serialNumber = pp.getString("SN");
		subject = pp.getString("SUBJECT");
		issuer = pp.getString("ISSUER");
		owner = pp.getString("OWNER");
		dateFrom = pp.getString("DATE-FROM");
		dateTo = pp.getString("DATE-TO");

		String ips = pp.getString("ALLOWED-IPs");
		String macs = pp.getString("ALLOWED-MACs");
		ipsList = null;
		if (ips != null) {
			ipsList = new ArrayList<String>();
			for (String ip : ips.split(",")) {
				ipsList.add(ip.trim());
			}
		}
		macsList = null;
		if (macs != null) {
			macsList = new ArrayList<String>();
			for (String mac : macs.split(",")) {
				macsList.add(mac.trim());
			}
		}

		Iterator<String> featureIter = pp.getKeys();
		featuresMap = featureIter.hasNext() ? new TreeMap<String, LoaderFeature>() : null;
		while (featureIter.hasNext()) {
			String fullkey = featureIter.next();
			if (!fullkey.startsWith("FEATURE-")) {
				continue;
			}
			int featureIdx = fullkey.indexOf('-');
			if (featureIdx < 0) {
				continue;
			}
			int propIdx = fullkey.indexOf('-', featureIdx + 1);

			if (propIdx < 0) {
				String k = fullkey.substring(featureIdx + 1, fullkey.length());
				LoaderFeature f = (LoaderFeature) featuresMap.get(k);
				if (f == null) {
					featuresMap.put(k, f = new LoaderFeature(k));
					f.values = new TreeMap<String, String>();
				}

			} else {
				String k = fullkey.substring(featureIdx + 1, propIdx);
				String vk = fullkey.substring(propIdx + 1, fullkey.length());

				LoaderFeature f = (LoaderFeature) featuresMap.get(k);
				if (f == null) {
					featuresMap.put(k, f = new LoaderFeature(k));
					f.values = new TreeMap<String, String>();
				}
				f.values.put(vk, pp.getString(fullkey));
			}
		}
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Serial Number : ").append(getSerialNumber());
		sb.append("\nSubject : ").append(getSubject());
		sb.append("\nOwner : ").append(getOwner());
		sb.append("\nIssuer : ").append(getIssuer());
		sb.append("\nDate from : ").append(getDateFrom());
		sb.append("\nDate to : ").append(getDateTo());
		sb.append("\nAllowed Ips : ").append(getAllowedIps());
		sb.append("\nAllowed Macs :").append(getAllowedMacs());
		for (Feature f : listFeature()) {
			for (String key : ((LoaderFeature) f).getValueMap().keySet()) {
				sb.append("\nFeature-").append(f.getName()).append("-").append(key).append(" : ")
						.append(f.getStringValue(key));
			}
		}
		return sb.toString();
	}

	@Override
	public String getShortDescription() {
		if (isUnlimitedDateFrom() && isUnlimitedDateTo()) {
			return Strings.format("{}, unlimited duration", subject, dateFrom, dateTo);
		} else if (isUnlimitedDateFrom()) {
			return Strings.format("{}, until ", subject, dateTo);
		} else if (isUnlimitedDateTo()) {
			return Strings.format("{}, since {}", subject, dateFrom);
		} else {
			return Strings.format("{}, {} to {}", subject, dateFrom, dateTo);
		}

	}

	@Override
	public boolean isUnlimitedDateFrom() {
		return Feature.UNLIMITED.equals(dateFrom);
	}

	@Override
	public boolean isUnlimitedDateTo() {
		return Feature.UNLIMITED.equals(dateTo);
	}

}
