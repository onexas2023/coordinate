package onexas.coordinate.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;

import onexas.coordinate.web.test.CoordinateWebTestBase;


public class ErrorHandlingTest extends CoordinateWebTestBase{
	
	@LocalServerPort
	private int port;
	
	@Value("${springdoc.api-docs.path}")
	private String apiDocPath;


	@Test
	public void testGeneralErrorHandling() {
		URL url;
		try {
			url = new URL("http", "localhost", port, apiDocPath);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Assert.assertEquals(200,conn.getResponseCode());
			//after upgrade to new spring version
			//https://www.ietf.org/rfc/rfc4627.txt , 
			//JSON text SHALL be encoded in Unicode.  The default encoding is UTF-8.
			Assert.assertEquals("application/json",conn.getContentType());
		} catch (IOException e1) {
			Assert.fail();
			return;
		}
		
		try {
			url = new URL("http", "localhost", port, "/api/nosuchurl");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Assert.assertEquals(404,conn.getResponseCode());
			Assert.assertEquals("application/json",conn.getContentType());
		} catch (IOException e1) {
			Assert.fail();
			return;
		}
		
		try {
			url = new URL("http", "localhost", port, "/nosuchhtml");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Assert.assertEquals(404,conn.getResponseCode());
			Assert.assertEquals("text/html;charset=UTF-8",conn.getContentType());
		} catch (IOException e1) {
			Assert.fail();
			return;
		}
		
		try {
			url = new URL("http", "localhost", port, "/api/nosuchurl");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Assert.assertEquals(404,conn.getResponseCode());
		} catch (IOException e1) {
			Assert.fail();
			return;
		}
	}
}
