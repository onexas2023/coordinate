package onexas.coordinate.lic;

import org.junit.Test;

import onexas.coordinate.common.util.ObfuscatedString;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LicenseStringDump {

	
	@Test
	public void dumpLicenseHolder(){
		printCode("logger", "coordinate.LICENSE");
		printCode("NA",LicenseHolder.NA);
		printCode("NO_LICENSE",LicenseHolder.NO_LICENSE);
	}

	protected String printCode(String name,String value) {
		System.out.println("	//"+value);
//		System.out.println("	static final String "+name+" = \""+value+"\";");
		String obfuscated = ObfuscatedString.obfuscate(value);
		StringBuilder sb = new StringBuilder();
		for(String val:obfuscated.split(" ")){
			if(sb.length()>0){
				sb.append(",");
			}
			sb.append(val).append("L");
		}
		
		obfuscated = sb.toString();
		
		System.out.println("	static final String "+name+" = new ObfuscatedString("+obfuscated+").toString();");
		return null;
	}
	
	@Test
	public void dumpLicenseInit(){
		printCode("MSG_CANT_FIND_CFG_FOLDER",LicenseInit.MSG_CANT_FIND_CFG_FOLDER);
		printCode("MSG_LIC_FILE_NOT_FOUND",LicenseInit.MSG_LIC_FILE_NOT_FOUND);
		printCode("MSG_LIC_FILE_NOT_VALID",LicenseInit.MSG_LIC_FILE_NOT_VALID);
		printCode("MSG_LIC_FILE_LOAD_OK",LicenseInit.MSG_LIC_FILE_LOAD_OK);
		printCode("MSG_LIC_FILE_LOAD_ERROR",LicenseInit.MSG_LIC_FILE_LOAD_ERROR);
	}

}
