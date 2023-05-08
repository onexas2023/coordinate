package onexas.axes.web.zk.component;

import org.zkoss.lang.Objects;
import org.zkoss.zul.Div;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Xterm extends Div {

	private static final long serialVersionUID = 1L;

	private String websocketUri;

	public String getZclass() {
		return (this._zclass != null ? this._zclass : "z-xterm");
	}

	public String getWebsocketUri() {
		return websocketUri;
	}

	public void setWebsocketUri(String websocketUri) {
		if (websocketUri != null && websocketUri.length() == 0)
			websocketUri = null;

		if (!Objects.equals(this.websocketUri, websocketUri)) {
			this.websocketUri = websocketUri;
			smartUpdate("websocketUri", websocketUri);
		}
	}

	// super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "websocketUri", websocketUri);
	}
}
