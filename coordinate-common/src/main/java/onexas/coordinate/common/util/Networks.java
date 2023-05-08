package onexas.coordinate.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Streams;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Networks {

	private final static Logger logger = LoggerFactory.getLogger(Networks.class);

	private static final X509TrustManager trustAllManager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)// NOSONAR
				throws CertificateException {
			//trust all
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)// NOSONAR
				throws CertificateException {
			//trust all
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	};

	private static OkHttpClient shcaredHttpClient;

	public static synchronized OkHttpClient getSharedHttpClient() {
		if (shcaredHttpClient == null) {
			shcaredHttpClient = buildSharedHttpClient(null);
		}
		return shcaredHttpClient;
	}

	public static synchronized OkHttpClient buildSharedHttpClient(HttpClientBuilderCallback callback) {
		return shcaredHttpClient = buildHttpClient(callback);
	}

	public static OkHttpClient buildHttpClient(HttpClientBuilderCallback callback) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		try {
			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, new TrustManager[] { trustAllManager }, new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			builder.sslSocketFactory(sslSocketFactory, trustAllManager);
			builder.hostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;// NOSONAR
				}
			});
		} catch (Exception e) {
			logger.error("Error:" + e.getMessage(), e);
		}

		builder.connectTimeout(30, TimeUnit.SECONDS);
		builder.readTimeout(5, TimeUnit.MINUTES);
		builder.writeTimeout(5, TimeUnit.MINUTES);

		if (callback != null) {
			callback.beforeBuild(builder);
		}
		return builder.build();
	}

	public interface HttpClientBuilderCallback {
		void beforeBuild(OkHttpClient.Builder builder);
	}

	public static boolean isReachable(String hostOrIp, int timeout) {
		InetAddress addr;
		boolean r;
		try {
			addr = InetAddress.getByName(hostOrIp);
			r = addr.isReachable(timeout);
		} catch (Exception x) {
			return false;
		}
		return r;
	}

	/**
	 * @param timeout millisecond
	 */
	public static boolean ping(String hostOrIp, long timeout) {
		return ping(hostOrIp, 1, timeout);
	}

	/**
	 * @param timeout millisecond
	 */
	public static boolean ping(String hostOrIp, int count, long timeout) {
		String cmd;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			// For Windows
			cmd = Strings.format("ping -n {} -w {} {}", count, timeout, hostOrIp);
		} else {
			// For Linux and OSX
			cmd = Strings.format("ping -c {} -W {} {}", count, timeout / 1000, hostOrIp);
		}
		Process myProcess;
		try {
			myProcess = Runtime.getRuntime().exec(cmd);
			myProcess.waitFor();
			if (myProcess.exitValue() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}

	}

	public static String getLocalHostMacAddress() throws UnknownHostException, SocketException {
		return getLocalHostMacAddress(":");
	}

	public static String getLocalHostMacAddress(String splitChar) throws UnknownHostException, SocketException {

		InetAddress ip = InetAddress.getLocalHost();
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		// null when running in dev01 on 2015/10/21
		if (network == null) {
			throw new UnknownHostException(
					"Can't get NetworkInterface with " + ip.getHostName() + "/" + ip.getHostAddress());
		}
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? splitChar : ""));
		}

		String macStr = sb.toString().toLowerCase();
		return macStr;
	}

	public static Mac getMac(String nifName) throws SocketException {
		for (Mac mac : getAllMacAddress()) {
			if (mac.getName().equals(nifName)) {
				return mac;
			}
		}
		return null;
	}

	public static List<Mac> getAllMacAddress() throws SocketException {
		Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
		List<Mac> macs = new LinkedList<>();
		while (networks.hasMoreElements()) {
			NetworkInterface network = networks.nextElement();
			byte[] mac = network.getHardwareAddress();

			if (mac != null && mac.length > 0) {
				macs.add(new Mac(network.getName() == null ? "" : network.getName(), mac));
			}
		}
		return macs;
	}

	public static class Mac {
		String name;
		byte[] value;

		public Mac(String name, byte[] value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public byte[] getValue() {
			return value;
		}

		public String getValueDisplay() {
			return getValueDisplay(":");
		}

		public String getValueDisplay(String splitChar) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < value.length; i++) {
				sb.append(String.format("%02X%s", value[i], (i < value.length - 1) ? splitChar : ""));
			}

			String macStr = sb.toString().toLowerCase();
			return macStr;
		}

		@Override
		public String toString() {
			return "Mac [" + (name != null ? "name=" + name + ", " : "")
					+ (value != null ? "value=" + getValueDisplay() : "") + "]";
		}
	}

	public static List<InetAddress> getAvailableInetAddresses() throws SocketException {
		LinkedList<InetAddress> address = new LinkedList<InetAddress>();
		Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
		while (enu.hasMoreElements()) {
			NetworkInterface ifc = enu.nextElement();
			if (ifc.isUp()) {
				Enumeration<InetAddress> addrEnu = ifc.getInetAddresses();
				while (addrEnu.hasMoreElements()) {
					InetAddress addr = addrEnu.nextElement();
					address.add(addr);
				}
			}
		}
		return address;
	}

	public static List<String> getAvailableHostAddresses() throws SocketException {
		LinkedList<String> hostAddresses = new LinkedList<String>();
		Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
		while (enu.hasMoreElements()) {
			NetworkInterface ifc = enu.nextElement();
			if (ifc.isUp()) {
				Enumeration<InetAddress> addrEnu = ifc.getInetAddresses();
				while (addrEnu.hasMoreElements()) {
					InetAddress addr = addrEnu.nextElement();
					hostAddresses.add(addr.getHostAddress());
				}
			}
		}
		return hostAddresses;
	}

	/**
	 * check ipv4 ip address match
	 * 
	 * @param address
	 * @param pattern the pattern is a ip or a ip with mask , e.x 192.168.1.1/24
	 */
	public static boolean isIpV4Match(String address, String pattern) {
		if (Objects.equals(address, pattern)) {
			return true;
		}
		try {
			int mask = 32;
			int m = pattern.indexOf("/");
			if (m != -1) {
				mask = Integer.parseInt(pattern.substring(m + 1, pattern.length()));
				pattern = pattern.substring(0, m);
			}

			int[] addrBytes = toIpv4Array(address);
			int[] patternBytes = toIpv4Array(pattern);

			for (int i = 0; i < mask; i++) {
				int byteIdx = i / 8;// index of byte
				int bitIdx = 8 - i % 8 - 1;// index of bit
				int addrByte = addrBytes[byteIdx];
				int patternByte = patternBytes[byteIdx];

				// compare bitIdx of byte
				int addrBit = ((addrByte >>> bitIdx) & 0x01);
				int patternBit = ((patternByte >>> bitIdx) & 0x01);
				if (addrBit != patternBit) {
					return false;
				}
			}
			return true;
		} catch (Exception x) {
			throw new IllegalStateException(x.getMessage(), x);
		} // eat
	}

	private static int[] toIpv4Array(String ip) {
		int[] ipArray = new int[4];
		String[] e = ip.split("\\.");
		if (e.length != 4) {
			throw new IllegalArgumentException("not a legal ipv4 address: " + ip);
		}
		for (int i = 0; i < 4; i++) {
			try {
				ipArray[i] = Integer.parseInt(e[i]);
			} catch (Exception x) {
				throw new IllegalArgumentException("not a legal ipv4 address: " + ip);
			}
		}
		return ipArray;

	}

	public static String loadString(URL url) throws IOException {
		return loadString(url, "UTF-8");
	}

	public static String loadString(URL url, String encoding) throws IOException {
		InputStream is = null;
		ResponseBody body = null;
		try {
			String p = url.getProtocol();
			if ("http".equalsIgnoreCase(p) || "https".equalsIgnoreCase(p)) {
				// use http client to avoid some site reject url.openconnection (ex, highchart)
				Request request = new Request.Builder().url(url).build();

				Response response = getSharedHttpClient().newCall(request).execute();

				int code = response.code();
				if (code >= 300) {
					throw new IOException(" Server returned HTTP response code: " + code + " for URL: " + url);
				}
				body = response.body();
				if (body != null) {
					MediaType contentType = body.contentType();
					if (contentType != null) {
						Charset charSet = contentType.charset();
						if (charSet != null) {
							encoding = charSet.name();
						}
					}
					is = body.byteStream();
				}
			}
			if (is == null) {
				is = url.openStream();
			}
			return Streams.loadString(is, encoding);
		} finally {
			if (body != null) {
				try {
					body.close();
				} catch (Exception e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static class WwwAuthResult {
		String schema;
		String token;
		int code;
		String msg;

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

	}

	public static WwwAuthResult handleWwwAuth(OkHttpClient httpClient, String wwwAuthHeader, String baseAuth)
			throws IOException {
		// Bearer
		// realm="https://gitlab.com/jwt/auth",service="container_registry",scope="repository:onexas/docker-image/coordinate:pull",error="invalid_token"
		WwwAuthResult r = new WwwAuthResult();
		String[] wah = wwwAuthHeader.split(" ", 2);
		if (wah.length != 2) {
			r.code = 400;
			r.msg = "Wrong Www-Authenticate header";
			return r;
		}
		r.schema = wah[0];
		switch (r.schema.toLowerCase()) {
		case "bearer": {
			Map<String, String> m = new HashMap<>();
			for (String tt : wah[1].split(",")) {
				int idx = tt.indexOf('=');
				if (idx == -1) {
					continue;
				}
				String key = tt.substring(0, idx);
				String val = tt.substring(idx + 1, tt.length());
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length() - 1);
				}
				m.put(key, val);
			}
			String realm = m.get("realm");
			if (Strings.isBlank(realm)) {
				r.code = 400;
				r.msg = "Wrong Www-Authenticate header, realm not found";
				return r;
			}
			Request.Builder req = new Request.Builder();
			req.header("Accept", "application/json");
			HttpUrl.Builder url;
			try {
				url = HttpUrl.get(realm).newBuilder();
			} catch (IllegalArgumentException x) {
				r.code = 400;
				r.msg = "Wrong Www-Authenticate header, realm unsupported " + realm;
				return r;
			}

			if (!Strings.isBlank(baseAuth)) {
				req.header("Authorization", Strings.format("Basic {}", baseAuth));
			}
			for (String k : m.keySet()) {
				if ("realm".equals(k)) {
					continue;
				}
				url.addQueryParameter(k, m.get(k));
			}
			req.url(url.build());

			Response response = httpClient.newCall(req.build()).execute();
			r.code = response.code();

			if (r.code == 200) {
				@SuppressWarnings("unchecked")
				Map<String, Object> d = Jsons.objectify(response.body().string(), Map.class);
				Object token = d.get("token");
				if (token instanceof String) {
					r.msg = "OK";
					r.token = (String) token;
					return r;
				}
				r.code = 400;
				r.msg = "Wrong Www-Authenticate response token";
				return r;
			}
			r.msg = "Fail to authenticate";

			@SuppressWarnings("unchecked")
			Map<String, Object> d = Jsons.objectify(response.body().string(), Map.class);
			Object errors = d.get("errors");
			if (errors != null) {
				r.msg = Jsons.jsonify(errors);
			}

			return r;
		}
		case "basic": {
			r.code = 200;
			r.msg = "Bypass to Basic";
			r.token = (String) baseAuth;
			return r;
		}
		default:
			r.code = 400;
			r.msg = "Unsupported Www-Authenticate schema " + r.schema;
			return r;
		}
	}
}