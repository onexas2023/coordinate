package onexas.coordinate.api.generator;

import io.swagger.v3.oas.models.Operation;

/**
 * 
 * @author Dennis Chen
 *
 */
public class TypeScriptFetchClientCodegen extends org.openapitools.codegen.languages.TypeScriptFetchClientCodegen {

	public TypeScriptFetchClientCodegen() {
		super();
		outputFolder = "generated-code/coordinate-typescript-fetch";
		embeddedTemplateDir = templateDir = "coordinate-typescript-fetch";
		
		//fix the issue when JSON.stringify(set); is just a '{}', which can't be deserialized back to a Set in server
		typeMapping.put("Set", "Array");
        typeMapping.put("set", "Array");
        
        //default to true if not set to avoid parameter name (i.e. method name) conflict in index.ts export
        if (!additionalProperties.containsKey(PREFIX_PARAMETER_INTERFACES)) {
        	additionalProperties.put(PREFIX_PARAMETER_INTERFACES, Boolean.TRUE);
        }
        
	}

	@Override
	public String getName() {
		return "coordinate-typescript-fetch";
	}
	
	@Override
	protected String getOrGenerateOperationId(Operation operation, String path, String httpMethod) {
		String operationId = operation.getOperationId();
		if (operationId != null) {
			//we use # for an operationId prefix separator in api definition generator
			int idx = operationId.indexOf("#");
			if (idx >= 0) {
				return operationId.substring(idx + 1);
			}
		}
		return super.getOrGenerateOperationId(operation, path, httpMethod);
	}
}
