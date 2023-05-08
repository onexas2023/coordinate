package onexas.coordinate.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@Controller(Env.NS_BEAN + "GeneralErrorController")
public class GeneralErrorController implements ErrorController {
//	private static final Logger logger = LoggerFactory.getLogger(GeneralErrorController.class);
	
	@RequestMapping("/error")
	public Object handleError(HttpServletRequest request, HttpServletResponse response) {
		Integer statusCode = (Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String message = (String)request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Exception ex = (Exception)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		HttpStatus status = null;
		if (statusCode != null) {
			status = HttpStatus.valueOf(statusCode);
		}else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		if(Strings.isBlank(message) && ex!=null) {
			message = ex.getMessage();
		}
		
		if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
			//don't show server side error detail to client
			message = "Please try again later, the detail of error is under server log";
		}
		if(Strings.isBlank(message)) {
			message = "No error message";
		}

		Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		if (uri != null && uri.toString().startsWith("/api/")) {
			
			Response err = new Response(true, message);
			
			ResponseEntity<Response> e = new ResponseEntity<>(err,status);
			if (ex!=null) {
				ApiExceptionHandler.applyErrorInfo(err, ex);
			}else {
				err.setMsg(status.getReasonPhrase());				
			}
			return e;
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("thymeleaf/error");
		if (status != null) {
			if (status == HttpStatus.NOT_FOUND) {
				mv.setViewName("thymeleaf/error-404");
			} else if (status == HttpStatus.FORBIDDEN) {
				mv.setViewName("thymeleaf/error-403");
			} else if (status == HttpStatus.UNAUTHORIZED) {
				mv.setViewName("thymeleaf/error-401");
			} else if (status == HttpStatus.BAD_REQUEST) {
				mv.setViewName("thymeleaf/error-400");
			} else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
				mv.setViewName("thymeleaf/error-500");
			}
		}
		
		mv.addObject("statusCode", statusCode);
		mv.addObject("errorMessage", message);
		mv.addObject("requestUri", uri);
		return mv;
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}