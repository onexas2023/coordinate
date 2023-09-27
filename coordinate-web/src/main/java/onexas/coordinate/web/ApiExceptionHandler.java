package onexas.coordinate.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.BadStatusException;
import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.err.LicenseForbiddenException;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.err.StateConflictException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@ControllerAdvice
@Component(Env.NS_BEAN + "ApiExceptionHandler")
public class ApiExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

//	private static final TimeZone utctz = TimeZone.getTimeZone("UTC");

	private BodyBuilder applyCommon(BodyBuilder builder) {
		builder.contentType(MediaType.APPLICATION_JSON);
		return builder;
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Response> handleException(Exception ex, HttpServletRequest request) {
		Response err = new Response(true, simplifyMessage(ex));
		logger.debug(ex.getMessage(), ex);

		if (ex instanceof ClientAbortException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(null);
		}

		// bad request
		if (ex instanceof HttpMessageNotReadableException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof BadArgumentException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof BadStatusException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof StateConflictException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}

		if (ex instanceof ConstraintViolationException) {
			StringBuilder msg = new StringBuilder();
			for (ConstraintViolation<?> cv : ((ConstraintViolationException) ex).getConstraintViolations()) {
				if (msg.length() > 0) {
					msg.append(", ");
				}
				String m = Strings.format("{}.{}:{}", cv.getRootBeanClass().getSimpleName(), cv.getPropertyPath(),
						cv.getMessage());
				msg.append(m);
			}
			err.setMsg(msg.toString());
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof HttpRequestMethodNotSupportedException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof HttpMediaTypeNotSupportedException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof HttpMediaTypeNotAcceptableException) {
			err.setMsg(err.getMsg() + " : " + request.getHeader("accept"));
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof ValidationException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof IntegrityViolationException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof MethodArgumentTypeMismatchException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof MethodArgumentNotValidException) {
			BindingResult r = ((MethodArgumentNotValidException) ex).getBindingResult();
			if (r != null && r.hasErrors()) {
				StringBuilder msg = new StringBuilder();
				for (ObjectError objErr : r.getAllErrors()) {
					if (msg.length() > 0) {
						msg.append(", ");
					}
					String m = Strings.format("{}:{}",
							objErr.getCodes() != null && objErr.getCodes().length > 0 ? objErr.getCodes()[0]
									: objErr.getObjectName(),
							objErr.getDefaultMessage());
					msg.append(m);

				}
				err.setMsg(msg.toString());
			}
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof MissingServletRequestParameterException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}
		if (ex instanceof MethodArgumentConversionNotSupportedException) {
			return applyCommon(ResponseEntity.status(HttpStatus.BAD_REQUEST)).body(err);
		}

		// not found
		if (ex instanceof NotFoundException) {
			return applyCommon(ResponseEntity.status(HttpStatus.NOT_FOUND)).body(err);
		}

		// unauthenticated
		if (ex instanceof UnauthenticatedException) {
			return applyCommon(ResponseEntity.status(HttpStatus.UNAUTHORIZED)).body(err);
		}

		// unauthorized
		if (ex instanceof NoPermissionException) {
			return applyCommon(ResponseEntity.status(HttpStatus.FORBIDDEN)).body(err);
		}

		// license
		if (ex instanceof LicenseForbiddenException) {
			return applyCommon(ResponseEntity.status(HttpStatus.FORBIDDEN)).body(err);
		}

		applyErrorInfo(err, ex);

		return applyCommon(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)).body(err);
	}

	static void applyErrorInfo(Response err, Exception ex) {
		// ISO 8601
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);// use en locale
		df.setTimeZone(TimeZone.getDefault());// use server time zone

		String ts = df.format(System.currentTimeMillis());
		String errorid = Strings.randomUid().replace("-", "");

		if (ex == null) {
			logger.error(Strings.format("errorid: {}, ts: {}, msg: {}", errorid, ts, "unknown exception"));
		} else {
			logger.error(Strings.format("errorid: {}, ts: {}, msg: {}", errorid, ts, ex.getMessage()), ex);
		}
		err.setMsg("Encountered server error, please contact admin to read server error logs");
		err.setTimestamp(ts);
		err.setErrid(errorid);
	}

	static String simplifyMessage(Exception ex) {
		String msg = ex.getMessage();
		if (ex instanceof NestedRuntimeException) {
			int i = msg.indexOf("nested exception is");
			if (i >= 0) {
				return msg.substring(0, i).trim();
			}
		}
		return msg;
	}
}