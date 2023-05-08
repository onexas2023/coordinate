package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the given configuration is not correct
 * @author Dennis Chen
 *
 */
public class BadConfigurationException extends Exception {
	private static final long serialVersionUID = 1L;

	public BadConfigurationException() {
		super("Bad Configuration");
	}

	public BadConfigurationException(String message) {
		super(message);
	}
	
	public BadConfigurationException(String message, Object... args) {
		super(Strings.format(message, args));
	}

	public BadConfigurationException(String message, Throwable x) {
		super(message, x);
	}
}
