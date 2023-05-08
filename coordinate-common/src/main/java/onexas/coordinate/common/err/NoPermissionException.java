package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the current action user doesn't has permission to do something
 * @author Dennis Chen
 *
 */
public class NoPermissionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoPermissionException() {
		super("No permission");
	}

	public NoPermissionException(String message) {
		super(message);
	}

	public NoPermissionException(String message, Object... args) {
		super(Strings.format(message, args));
	}
	
	public NoPermissionException(String message, Throwable x) {
		super(message, x);
	}
}
