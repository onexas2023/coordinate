package onexas.coordinate.common.app;
import java.io.Serializable;
import java.util.Set;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface Credential extends Serializable{

	/**
	 * the display name of credential user
	 */
	String getDisplayName();
	
	/**
	 * the account of credential user
	 */
	String getAccount();
	
	/**
	 * the uid of credential user
	 */
	String getUserUid();
	
	/**
	 * the token of this credential
	 */
	String getToken();
	
	/**
	 * the roles of credential user
	 */
	Set<String> getRoles();
	
	boolean isGuest();
	
	
	long getIssuedAt();
}