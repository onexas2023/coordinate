package onexas.coordinate.service.domain;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface DomainProvider {

	public DomainAuthenticator getAuthenticator();
	
	public DomainUserFinder getUserFinder();
}
