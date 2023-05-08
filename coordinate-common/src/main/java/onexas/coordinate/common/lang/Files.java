package onexas.coordinate.common.lang;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/**
 * @author Dennis.Chen
 * 
 */
public class Files {

	static public long copyFileTo(File src, File dest) throws IOException {
		if (!src.exists() || src.isDirectory()) {
			throw new IllegalArgumentException("not a file : " + src); //$NON-NLS-1$
		}

		if (dest.exists() && dest.isDirectory()) {
			throw new IllegalArgumentException("not a file : " + dest); //$NON-NLS-1$
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		long size = 0;
		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);

			byte[] buff = new byte[1024];
			int r;
			while ((r = fis.read(buff)) != -1) {
				fos.write(buff, 0, r);
				size += r;
			}
			return size;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
	}

	static public long flushTo(InputStream is, File dest) throws IOException {
		if (dest.exists() && dest.isDirectory()) {
			throw new IllegalArgumentException("not a file : " + dest); //$NON-NLS-1$
		}

		FileOutputStream fos = null;
		long size = 0;
		try {
			fos = new FileOutputStream(dest);

			byte[] buff = new byte[1024];
			int r;
			while ((r = is.read(buff)) != -1) {
				fos.write(buff, 0, r);
				size += r;
			}
			return size;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
	}

	public static File getJavaTempFolder() {
		return new File(getJavaTempFolderPath());
	}

	public static String getJavaTempFolderPath() {
		return System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
	}

	public static boolean deepClean(File folder) {
		boolean r = true;
		File[] folders = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File f : folders) {
			r &= deepClean(f);
			r &= f.delete();
		}
		r &= clean(folder);
		return r;
	}

	public static boolean clean(File folder) {
		boolean r = true;
		File[] files = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});

		for (File file : files) {
			r &= file.delete();
		}
		return r;
	}

	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1 && index < fileName.length()) {
			return fileName.substring(index + 1, fileName.length());
		} else {
			return null;
		}
	}

	public static String getMain(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(0, index);
		} else {
			return fileName;
		}
	}

	public static Properties loadProperties(File file) {
		java.util.Properties p = new java.util.Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			p.load(is);
			return p;
		} catch (Exception x) {
			throw new RuntimeException(x.getMessage(), x);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveString(String str, File file, String encoding) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			Streams.flush(new ByteArrayInputStream(str.getBytes(encoding)), os);
		} catch (Exception x) {
			throw new RuntimeException(x.getMessage(), x);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveProperties(Properties p, File file) throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			p.store(os, "");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String loadString(File file) throws IOException {
		return loadString(file, "UTF8");
	}

	public static String loadString(File file, String encoding) throws IOException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return Streams.loadString(is, encoding);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	//////////////////////////////
	public static void saveString(String file, String str) throws IOException {
		saveString(file, str, "UTF8");
	}

	public static void saveString(String file, String str, String encoding) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			saveString(os, str, encoding);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveString(File file, String str) throws IOException {
		saveString(file, str, "UTF8");
	}

	public static void saveString(File file, String str, String encoding) throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			saveString(os, str, encoding);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveString(OutputStream os, String str) throws IOException {
		saveString(os, "UTF8");
	}

	public static void saveString(OutputStream os, String str, String encoding) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes(encoding));
		Streams.flush(is, os);
	}

	public static File createTempFile(String ext) throws IOException {
		return File.createTempFile("coordinate-tmp-", "."+ext);
	}
	public static File createTempFile() throws IOException {
		return File.createTempFile("coordinate-tmp-", "");
	}

	public static InputStream openDeleteOnCloseFile(File file) throws IOException {
		return java.nio.file.Files.newInputStream(Paths.get(file.getAbsolutePath()),
				StandardOpenOption.DELETE_ON_CLOSE);
	}
	
	public static String safeFileName(String name) {
		if (name != null) {
			// a linux name could cause error when downloading file to windows client which
			// use content-disposition without care. (i.e. api client by open api code generator)
			name = name.replaceAll("[:\\\\/*?|<>]", "_");
		}
		return name;
	}

}