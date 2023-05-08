package onexas.coordinate.web;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.web.api.model.Response;

/**
 * the custom response message converter to handle any media type for returning
 * error response in api exception handler.
 * 
 * @author Dennis Chen
 *
 */
public class ResponseMessageConverter extends AbstractHttpMessageConverter<Response> {

	@Override
	protected boolean canWrite(MediaType mediaType) {
		return true;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return Response.class.isAssignableFrom(clazz);
	}

	protected Long getContentLength(Response t, @Nullable MediaType contentType) throws IOException {
		return null;
	}

	@Override
	protected Response readInternal(Class<? extends Response> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new HttpMessageNotReadableException("for write only", inputMessage);
	}

	@Override
	protected void writeInternal(Response t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		if (t != null) {
			outputMessage.getBody().write(Jsons.jsonify(t).getBytes(Strings.UTF8));
		}
	}

}
