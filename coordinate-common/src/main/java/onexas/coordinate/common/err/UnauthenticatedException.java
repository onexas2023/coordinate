package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the current requester is not authenticated
 * @author Dennis Chen
 *
 */
public class UnauthenticatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnauthenticatedException() {
		super("Unauthenticated");
	}

	public UnauthenticatedException(String message) {
		super(message);
	}

	public UnauthenticatedException(String message, Object... args) {
		super(Strings.format(message, args));
	}
	
	public UnauthenticatedException(String message, Throwable x) {
		super(message, x);
	}
}
