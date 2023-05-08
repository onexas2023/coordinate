package onexas.axes.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import onexas.coordinate.common.app.Env;


/**
 * 
 * @author Dennis Chen
 *
 */
@Controller(Env.NS_BEAN + "AxesController")
public class AxesController {

	public static final String ROOT_VIEW = "axes";
	
	@Value("${axes.contextName}")
	String contextName;

	@Autowired
	HttpServletRequest request;

	@GetMapping("/")
	public String root() {
		return "redirect:/" + contextName;
	}
	
	@GetMapping("/${axes.contextName}")
	public String axes() {
		return axes(null);
	}

	@GetMapping("/${axes.contextName}/{page}/**")
	public String axes(@PathVariable("page") String page) {
		request.setAttribute(Constants.REQ_PATH_PAGE, page);
		String uri = request.getRequestURI();
		String[] args = uri.split("/");
		args = args.length > 2 ? Arrays.stream(args, 3, args.length).toArray(String[]::new) : args;
		request.setAttribute(Constants.REQ_PATH_PAGE_ARGS, args);
		return ROOT_VIEW;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@GetMapping("/logout")
	public String logout() {
		HttpSession sess = request.getSession(false);
		if (sess != null) {
			sess.invalidate();
		}
		return "redirect:/login";
	}
}
