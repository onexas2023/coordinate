package onexas.coordinate.web.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Response {

	public static final Response OK = new Response();

	@Schema(description = "error status of response")
	Boolean err;

	@Schema(description = "the message of the error response, it will not present if there is no error")
	String msg;

	@Schema(description = "the server timestamp to identify error, e.g. status 500 only")
	String timestamp;

	@Schema(description = "the server error id to identify error, e.g. status 500 only")
	String errid;

	public Response() {
		this(null, "OK");
	}

	public Response(String msg) {
		this(null, msg);
	}

	public Response(Boolean err, String msg) {
		this.err = err;
		if (Boolean.TRUE.equals(err) && Strings.isBlank(msg)) {
			this.msg = "Error without message";
		}
		this.msg = msg;
	}

	public Boolean getErr() {
		return err;
	}

	public void setErr(Boolean err) {
		this.err = err;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getErrid() {
		return errid;
	}

	public void setErrid(String errid) {
		this.errid = errid;
	}

}
