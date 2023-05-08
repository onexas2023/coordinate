package onexas.coordinate.common.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Dennis Chen
 *
 */
public class WrappedOutputStream extends OutputStream {

	protected OutputStream os;

	public WrappedOutputStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

}
