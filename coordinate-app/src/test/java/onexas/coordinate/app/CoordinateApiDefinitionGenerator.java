package onexas.coordinate.app;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;

import onexas.coordinate.app.test.CoordinateAppTestBase;
import onexas.coordinate.common.lang.Files;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.common.util.Networks;

/**
 * This is running in test env to generate definition.json in api-sdk
 * 
 * @author Dennis Chen
 *
 */
public class CoordinateApiDefinitionGenerator extends CoordinateAppTestBase {

	@LocalServerPort
	private int port;

	@Value("${springdoc.api-docs.path}")
	private String apiDocPath;

	@Test
	public void generateDefinitionJson() {
		try {
			// http://localhost:8080/api/swagger.json
			URL swaggerUrl = new URL("http", "localhost", port, apiDocPath);
			String json = Networks.loadString(swaggerUrl);
			json = json.replace("http://localhost:"+port, "http://localhost:8088");
			json = Jsons.pretty(json);
			File projectFolder = new File(".");
			File definitionFolder = new File(projectFolder.getCanonicalFile(), "api");
			if (!definitionFolder.exists()) {
				definitionFolder.mkdirs();
			}
			File definitionFile = new File(definitionFolder.getCanonicalFile(), "definition.json");
			System.out.println(">>>definition api json save to >>>>>>>>>>>>" + definitionFile.getAbsolutePath());
			Files.saveString(definitionFile, json);
			
			
		} catch (Exception x) {
			x.printStackTrace();
			Assert.fail(x.getMessage());
		}
	}

}
