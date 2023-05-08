package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate something went wrong because of a unknown reason, it is usually a uncontrolled/unexpected status
 * @author Dennis Chen
 *
 */
public class BadStatusException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadStatusException() {
		super("Bad Status");
	}

	public BadStatusException(String message) {
		super(message);
	}
	
	public BadStatusException(String message, Object... args) {
		super(Strings.format(message, args));
	}

	public BadStatusException(String message, Throwable x) {
		super(message, x);
	}
}
