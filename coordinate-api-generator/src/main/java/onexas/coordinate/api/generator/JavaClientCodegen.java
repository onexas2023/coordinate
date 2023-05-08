package onexas.coordinate.api.generator;

import org.openapitools.codegen.CodegenProperty;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */
@SuppressWarnings("rawtypes")
public class JavaClientCodegen extends org.openapitools.codegen.languages.JavaClientCodegen {

	public JavaClientCodegen() {
		super();
		
		templateDir = "coordinate-java-okhttp-gson";
		
		importMapping.put("BigInteger", "java.math.BigInteger");
	}

	public CodegenProperty fromProperty(String name, Schema p) {
		CodegenProperty cp = super.fromProperty(name, p);
		// a number without format should be a big integer in java
		if (cp.isInteger && p.getFormat() == null && !"BigInteger".equals(cp.dataType)) {
			// force it to big integer
			cp.dataType = "BigInteger";
			cp.datatypeWithEnum = "BigInteger";
			cp.baseType = "BigInteger";
			if (p.getDefault() != null) {
				cp.defaultValue = "new BigInteger(\"" + p.getDefault().toString() + "\")";
			}
		}
		return cp;
	}

	@Override
	protected String getOrGenerateOperationId(Operation operation, String path, String httpMethod) {
		String operationId = operation.getOperationId();
		if (operationId != null) {
			// we use # for an operationId prefix separator in api definition generator
			int idx = operationId.indexOf("#");
			if (idx >= 0) {
				return operationId.substring(idx + 1);
			}
		}
		return super.getOrGenerateOperationId(operation, path, httpMethod);
	}
}
