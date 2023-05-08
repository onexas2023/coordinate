package onexas.coordinate.common.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

public class Validations {
	
	public static boolean isValidEmail(String email){
		return EmailValidator.getInstance().isValid(email);
	}
	
	public static boolean isValidIp(String inetAddress){
		return InetAddressValidator.getInstance().isValid(inetAddress);
	}
}