package onexas.coordinate.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SshClient {

	private static final Logger logger = LoggerFactory.getLogger(SshClient.class);

	static private Properties commonConfig = new java.util.Properties();
	static {
		commonConfig.put("StrictHostKeyChecking", "no");
	}

	private String ip;
	private String user;
	private String password;
	private String privateKeyPassphrase;

	private String sudoUser;

	private File privateKeyFile;
	
	private String privateKey;

	private String encoding;
	private int port;
	private Session session = null;

	int connectTimeout = -1;

	public SshClient(String ip) {
		this(ip, 22, null, null);
	}

	public SshClient(String ip, int port) {
		this(ip, port, null, null);
	}

	public SshClient(String ip, String account) {
		this(ip, 22, account, null);
	}

	public SshClient(String ip, int port, String account) {
		this(ip, port, account, null);
	}

	public SshClient(String ip, int port, String user, String encoding) {
		this.ip = ip;
		this.user = user;
		this.port = port;
		this.encoding = encoding == null ? "UTF-8" : encoding;
	}

	public String getIp() {
		return ip;
	}

	public String getUser() {
		return user;
	}

	public int getPort() {
		return port;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSudoUser() {
		return sudoUser;
	}

	public void setSudoUser(String sudoUser) {
		this.sudoUser = sudoUser;
	}

	/**
	 * Sets the private key file
	 * 
	 * @param privateKeyFile       private key file, pem (classic openssh) format
	 * @param privateKeyPassphrase the key passphrase, supported when using pem,
	 *                             null if key file doesn't has phrase.
	 */
	public void setPrivateKeyFile(File privateKeyFile, String privateKeyPassphrase) {
		this.privateKeyFile = privateKeyFile;
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	/**
	 * Sets the private key
	 * 
	 * @param privateKeyFile       private key string, pem (classic openssh) format
	 * @param privateKeyPassphrase the key passphrase, supported when using pem,
	 *                             null if key file doesn't has phrase.
	 */	
	public void setPrivateKey(String privateKey, String privateKeyPassphrase) {
		this.privateKey = privateKey;
		this.privateKeyPassphrase = privateKeyPassphrase;
	}

	public OutputStream upload(String remoteFilePath) throws JSchException, SftpException, IOException {
		String uplodPath;
		String fileName;
		int idx = remoteFilePath.lastIndexOf("/");
		if (idx > 0) {
			uplodPath = remoteFilePath.substring(0, idx);
			fileName = remoteFilePath.substring(idx + 1);
		} else {
			uplodPath = "/";
			fileName = remoteFilePath;
		}
		return upload(uplodPath, fileName);
	}

	private OutputStream upload(String uplodPath, String fileName) throws JSchException, SftpException, IOException {

		exec("mkdir -p " + uplodPath);

		Session session = getSession();
		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();

		try {
			channelSftp.setFilenameEncoding(encoding);
			channelSftp.cd(uplodPath);
		} catch (SftpException x) {
			channelSftp.disconnect();
			throw x;
		}

		OutputStream outputStream = channelSftp.put(fileName);

		return new WrappedOutputStream(outputStream) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					try {
						channelSftp.disconnect();
					} catch (Exception x) {
					}
				}
			}
		};

	}

	public ExecResult execSudo(String cmd) throws JSchException, IOException {
		return execSudo(cmd, sudoUser);
	}

	private static String escapeBashC(String cmd) {
		return cmd.replace("\\", "\\\\").replace("'", "\\'");
	}

	public ExecResult execSudo(String cmd, String sudoUser) throws JSchException, IOException {
		// first sudo for catch user and password
		String sudoCmd = (sudoUser != null)
				? Strings.format("echo '{}' | sudo -i -u {} --stdin -p '' bash -c $'{}'", password, sudoUser,
						escapeBashC(cmd))
				: Strings.format("sudo {}", cmd);
		return exec(sudoCmd);
	}

	public ExecResult execBash(String cmd) throws JSchException, IOException {
		// first sudo for catch user and password
		String bashCmd = Strings.format("bash -l -c $'{}'", escapeBashC(cmd));
		return exec(bashCmd);
	}

	public ExecResult exec(String cmd) throws JSchException, IOException {
		ChannelExec channelExec = null;
		try {
			Session session = getSession();
			channelExec = (ChannelExec) session.openChannel("exec");
			InputStream inputStream = channelExec.getInputStream();

			ByteArrayOutputStream err = new ByteArrayOutputStream();
			channelExec.setErrStream(err, true);

			channelExec.setCommand(cmd);

			if (connectTimeout > 0) {
				channelExec.connect(connectTimeout);
			} else {
				channelExec.connect();
			}

			String stdout = readAsString(inputStream);

			// disconnect for exit status
			channelExec.disconnect();
			int exitCode = channelExec.getExitStatus();
			// damn, exitCode is still possible be -1. a bug?
			channelExec = null;

			ExecResult result = new ExecResult();
			result.setExitCode(exitCode);
			result.setStdOut(stdout);
			result.setStdErr(new String(err.toByteArray(), encoding));

			return result;

		} finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
		}
	}

	static public class ExecResult {
		int exitCode;
		String stdOut;
		String stdErr;

		public int getExitCode() {
			return exitCode;
		}

		public void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}

		public String getStdOut() {
			return hasStdOut() ? stdOut : "";
		}

		public void setStdOut(String stdOut) {
			this.stdOut = stdOut;
		}

		public boolean hasStdOut() {
			return this.stdOut != null && this.stdOut.length() > 0;
		}

		public String getStdErr() {
			return hasStdErr() ? stdErr : "";
		}

		void setStdErr(String stdErr) {
			this.stdErr = stdErr;
		}

		public boolean hasStdErr() {
			return this.stdErr != null && this.stdErr.length() > 0;
		}

		public String getStdAny() {
			if (hasStdOut()) {
				return this.stdOut;
			}
			if (hasStdErr()) {
				return this.stdErr;
			}
			return "";
		}

		public boolean hasStdAny() {
			return (this.stdOut != null && this.stdOut.length() > 0)
					|| (this.stdErr != null && this.stdErr.length() > 0);
		}
	}

	public InputStream download(String fullFileName) throws JSchException, SftpException, IOException {
		Session session = getSession();

		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();

		InputStream inputStream = channelSftp.get(fullFileName);
		return new WrappedInputStream(inputStream) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					try {
						channelSftp.disconnect();
					} catch (Exception x) {
					}
				}
			}
		};

	}

	@SuppressWarnings("unchecked")
	public List<LsEntry> ls(String fullPath) throws JSchException, SftpException, IOException {
		Session session = getSession();

		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");

		try {
			channelSftp.connect();
			return new LinkedList<>(channelSftp.ls(fullPath));
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
		}

	}

	public SftpATTRS lstat(String fullPath) throws JSchException, SftpException, IOException {
		Session session = getSession();

		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		try {
			channelSftp.connect();

			SftpATTRS attrs = channelSftp.lstat(fullPath);
			return attrs;

		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}

		}

	}

	public void connect() throws JSchException {
		getSession(true);
	}

	private Session getSession() throws JSchException {
		return getSession(false);
	}

	private Session getSession(boolean quiet) throws JSchException {
		if (session == null || !session.isConnected()) {
			JSch jsch = new JSch();

			if (privateKey != null) {
				jsch.addIdentity("sshclient", privateKey.getBytes(Strings.UTF8), null,
						Strings.isBlank(privateKeyPassphrase) ? null : privateKeyPassphrase.getBytes(Strings.UTF8));
				session = jsch.getSession(user, ip, port);
			}else if (privateKeyFile != null) {
				jsch.addIdentity(privateKeyFile.getAbsolutePath(), Strings.isBlank(privateKeyPassphrase) ? null : privateKeyPassphrase);
				session = jsch.getSession(user, ip, port);
			}else {
				session = jsch.getSession(user, ip, port);
				session.setPassword(password);
			}

			session.setConfig(commonConfig);
			try {
				if (connectTimeout > 0) {
					session.connect(connectTimeout);
				} else {
					session.connect();
				}
			} catch (JSchException x) {
				if (!quiet) {
					logger.error("connect to {} fail, user:{}", ip, user);
				}
				throw x;
			}
		}

		return session;
	}

	public void disconnect() {
		if (null != session && session.isConnected()) {
			session.disconnect();
			session = null;
		}
	}

	private String readAsString(InputStream in) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int r;
		while ((r = in.read(buffer, 0, buffer.length)) != -1) {
			os.write(buffer, 0, r);
		}
		return new String(os.toByteArray(), encoding);
	}

	/**
	 * test path is a file(1), directory(2) or not exist(0)
	 */
	public int testPath(String path) throws IOException, JSchException {
		String command = Strings.format("[ -f '{}' ] && echo 1 || ([ -e '{}' ] && echo 2 || echo 0)", path, path);
		ExecResult result = exec(command);
		if (result.hasStdErr()) {
			throw new IOException(result.getStdErr());
		}
		return Integer.parseInt(result.getStdOut().trim());
	}
}
