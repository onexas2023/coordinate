package onexas.coordinate.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.MediaType;

/**
 * 
 * @author Dennis Chen
 *
 */
public class FileDownload {

	protected MediaType mediaType;

	protected String name;

	protected boolean inline;

	protected Long contentLength;

	protected File file;

	protected InputStream inputStream;

	protected Long lastModified;

	protected FileDownload() {
	}

	public FileDownload(File file) {
		this.file = file;
	}

	public FileDownload(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getName() {
		return name != null ? name : file != null ? file.getName() : null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInline() {
		return inline;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public Long getLastModified() {
		return lastModified != null ? lastModified : file != null ? file.lastModified() : null;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public Long getContentLength() {
		return contentLength != null ? contentLength : file != null ? file.length() : null;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public InputStream getInputStream() throws IOException {
		return inputStream != null ? inputStream
				: file != null ? inputStream = new FileInputStream(file) : openInputStream();
	}

	/**
	 * Open the InputStream dynamically, the sub class could override this to
	 * provide dynamic/deferred InputStream.
	 * 
	 * @return null by default
	 */
	protected InputStream openInputStream() {
		return null;
	}

	public FileDownload withName(String name) {
		this.name = name;
		return this;
	}

	public FileDownload withInline(boolean inline) {
		this.inline = inline;
		return this;
	}

	public FileDownload withMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	public FileDownload withContentLength(Long contentLength) {
		this.contentLength = contentLength;
		return this;
	}
}
