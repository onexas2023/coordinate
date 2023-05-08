package onexas.coordinate.model;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Result {

	protected boolean err;

	protected String msg;

	public Result() {
		this(false, "Success");
	}

	public Result(String msg) {
		this(false, msg);
	}

	public Result(boolean err, String msg) {
		this.err = err;
		if (err && Strings.isBlank(msg)) {
			this.msg = "Error";
		}
		this.msg = msg;
	}

	public boolean isErr() {
		return err;
	}

	public void setErr(boolean err) {
		this.err = err;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
