package onexas.coordinate.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import onexas.coordinate.common.lang.Files;

/**
 * 
 * @author Dennis Chen
 *
 */
public class FileDownloadMessageConverter extends AbstractHttpMessageConverter<FileDownload> {

	// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like
	// Gecko) Chrome/96.0.4664.55 Safari/537.36 Edg/96.0.1054.43
	private Pattern modernUserAgentRegex;

	public FileDownloadMessageConverter() {
	}

	public String getModernUserAgentRegex() {
		return modernUserAgentRegex == null ? null : modernUserAgentRegex.pattern();
	}

	public void setModernUserAgentRegex(String modernUserAgentRegex) {
		this.modernUserAgentRegex = Pattern.compile(modernUserAgentRegex, Pattern.CASE_INSENSITIVE);
	}

	@Override
	protected boolean canWrite(MediaType mediaType) {
		return true;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return FileDownload.class.isAssignableFrom(clazz);
	}

	protected Long getContentLength(FileDownload t, @Nullable MediaType contentType) throws IOException {
		return t.getContentLength();
	}

	@Override
	protected FileDownload readInternal(Class<? extends FileDownload> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		throw new HttpMessageNotReadableException("for download only", inputMessage);
	}

	@Override
	protected void writeInternal(FileDownload t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		InputStream is = null;

		try {
			//get/open stream first to prevent open stream error (e.g. no user permission) after set http header
			//in such case, the error message will not been shown to client
			is = t.getInputStream();
			
			String name = t.getName();
			MediaType mediaType = t.getMediaType();
			Long lastModified = t.getLastModified();
			boolean inline = t.isInline();
	
			HttpHeaders headers = outputMessage.getHeaders();
			if (mediaType == null) {
	
				if (name != null) {
					String type = URLConnection.guessContentTypeFromName(name);
					if (type != null) {
						try {
							mediaType = MediaType.parseMediaType(type);
						} catch (Exception x) {
						}
					}
				}
			}
	
			if (mediaType == null) {
				mediaType = inline ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_OCTET_STREAM;
			}
	
			headers.setContentType(mediaType);
	
			// modernUserAgent guess
			String filename = null;
			if (modernUserAgentRegex != null) {
				try {
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
							.getRequest();
					if (request != null) {
						String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
						if (userAgent != null && modernUserAgentRegex.matcher(userAgent).matches()) {
							filename = URLEncoder.encode(name, "UTF8");
						}
					}
				} catch (Exception x) {// eat
				}
			}
	
			if (filename == null) {
				filename = Files.safeFileName(name);
			}
	
			headers.setContentDisposition(ContentDisposition.builder(Boolean.TRUE.equals(inline) ? "inline" : "attachment")
					.filename(filename).build());
	
			if (lastModified != null) {
				headers.setLastModified(lastModified.longValue());
			}	

			// flush body (for header out)first, it is useful when the mediaType is known by
			// client
			OutputStream os = outputMessage.getBody();
			os.flush();

			byte[] chunk = new byte[1024];
			int readLen = -1;
			while ((readLen = is.read(chunk)) != -1) {
				try {
					os.write(chunk, 0, readLen);
				} catch (IOException x) {
					// client possibly close the download, so just show a single line warn
					logger.warn("Clinet possibly close the download, message:" + x.getMessage());
					break;
				}
			}
		} finally {
			try {
				is.close();
			} catch (Exception x) {
			}
		}
	}

}
