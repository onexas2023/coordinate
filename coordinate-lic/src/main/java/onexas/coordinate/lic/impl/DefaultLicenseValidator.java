package onexas.coordinate.lic.impl;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import onexas.coordinate.common.util.CalendarHelper;
import onexas.coordinate.common.util.Networks;
import onexas.coordinate.common.util.ObfuscatedString;
import onexas.coordinate.common.util.Networks.Mac;
import onexas.coordinate.lic.Feature;
import onexas.coordinate.lic.License;
import onexas.coordinate.lic.LicenseValidator;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DefaultLicenseValidator extends LicenseValidator {

	// Duration not match {} to {}
	static final String MSG_DURATION_NOT_MATCH = new ObfuscatedString(-483606084265629324L, -4086286823344362678L,
			-5431350522577643339L, 8931078034390752947L, -5651337121583588032L).toString();
	// IP not match {}, available is {}
	static final String MSG_IP_NOT_MATCH = new ObfuscatedString(6872207712160620076L, 7644279085209478844L,
			2234302543044570589L, -8414977073672616260L, 4148621627302924367L).toString();
	// No host mac address found
	static final String MSG_NO_HOST_MAC = new ObfuscatedString(-4060044090387533793L, 7452113246793597599L,
			-8181295519788094383L, 5346192001565074267L, -3924877362948537002L).toString();
	// MAC not match {}, available is {}
	static final String MSG_MAC_NOT_MATCH = new ObfuscatedString(-1164538867777827369L, -4467972687328329169L,
			3352332916803666405L, -105108313467444920L, 4337900939981588364L, -6389239505944393543L).toString();

	@Override
	public boolean isValid(License lic) {
		if (lic == null) {
			return false;
		}
		// check time
		try {
			if (!checkDuration(lic)) {
				return false;
			}
			if (!checkIp(lic)) {
				return false;
			}
			if (!checkMac(lic)) {
				return false;
			}
			return true;
		} catch (Exception x) {
			License.logger.debug(x.getMessage());
			return false;
		}
	}

	private boolean checkDuration(License lic) throws Exception {
		long now = System.currentTimeMillis();
		// use system's timezone
		CalendarHelper cal = new CalendarHelper(TimeZone.getDefault());
		SimpleDateFormat dateFormater = new SimpleDateFormat(License.dateFormat);
		long dateFrom = 0;
		if (!lic.isUnlimitedDateFrom()) {
			dateFrom = dateFormater.parse(lic.getDateFrom()).getTime();
			dateFrom = cal.toDayStart(new Date(dateFrom)).getTime();
		}
		long dateTo = Long.MAX_VALUE;
		if (!lic.isUnlimitedDateTo()) {
			dateTo = dateFormater.parse(lic.getDateTo()).getTime();
			dateTo = cal.toDayEnd(new Date(dateTo)).getTime();
		}

		if (now < dateFrom || now > dateTo) {
			License.logger.warn(MSG_DURATION_NOT_MATCH, lic.getDateFrom(), lic.getDateTo());
			return false;
		}
		return true;
	}

	private boolean checkIp(License lic) throws Exception {
		List<String> ips = lic.getAllowedIps();
		if (ips == null || ips.size() == 0) {
			return false;
		}
		for (String ip : ips) {
			if (ip.equals(Feature.UNLIMITED)) {
				return true;
			}
		}

		Set<String> hostAddrSet = new LinkedHashSet<String>();
		// check if any ip overlapped
		for (InetAddress addr : Networks.getAvailableInetAddresses()) {
			if (addr instanceof Inet4Address) {
				String hostAddr = addr.getHostAddress();
				for (String ip : ips) {
					if (Networks.isIpV4Match(hostAddr, ip)) {
						return true;
					}
				}
				hostAddrSet.add(hostAddr);
			}
		}
		License.logger.warn(MSG_IP_NOT_MATCH, ips, hostAddrSet);
		return false;
	}

	private boolean checkMac(License lic) throws Exception {

		List<String> macs = lic.getAllowedMacs();
		if (macs == null || macs.size() == 0) {
			return false;
		}
		Set<String> macSet = new LinkedHashSet<String>(macs);
		for (String mac : macs) {
			if (mac.equals(Feature.UNLIMITED)) {
				return true;
			}
			mac = mac.toLowerCase();
			macSet.add(mac);
		}

		List<Mac> macList = null;
		try {
			macList = Networks.getAllMacAddress();
		} catch (Exception x) {
		}
		if (macList == null) {
			License.logger.warn(MSG_NO_HOST_MAC);
			return false;
		}

		// check any mac matches
		for (Mac m : macList) {
			if (macSet.contains(m.getValueDisplay())) {
				return true;
			}
		}

		StringBuilder sb = new StringBuilder();
		for (Mac m : macList) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(m.getValueDisplay());
		}

		License.logger.warn(MSG_MAC_NOT_MATCH, macSet, sb.toString());
		return false;
	}

}
