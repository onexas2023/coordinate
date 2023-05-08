package onexas.coordinate.common.err;

import onexas.coordinate.common.lang.Strings;

/**
 * Indicate the current action is forbidded because it doesn't have a correct license or limited 
 * @author Dennis Chen
 *
 */
public class LicenseForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LicenseForbiddenException() {
		super("No License");
	}

	public LicenseForbiddenException(String message) {
		super(message);
	}

	public LicenseForbiddenException(String message, Object... args) {
		super(Strings.format(message, args));
	}
	
	public LicenseForbiddenException(String message, Throwable x) {
		super(message, x);
	}
}
