package onexas.axes.web.zk.core;

import java.io.File;
import java.net.URL;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.LabelLocator;

import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class FileResourceLabeLocator implements LabelLocator {

	private static Logger logger = LoggerFactory.getLogger(FileResourceLabeLocator.class);
	
	private boolean universal = false;
	private String path;
	private static String postFix = ".labels";
	
	public FileResourceLabeLocator(String path){
		this(new File(path),false);
	}
	public FileResourceLabeLocator(String path,boolean universal){
		this(new File(path),universal);
	}
	public FileResourceLabeLocator(File file){
		this(file,false);
	}
	public FileResourceLabeLocator(File file,boolean universal){
		this.path = file.getAbsolutePath();
		this.universal = universal;
		try {
			if(locate(null)==null){
				logger.warn("can't find 18n label at {}",path);
			}
		} catch (Exception e) {
			logger.warn(Strings.format("can't find 18n label at {}",path),e);
		}
	}
	
	
	public URL locate(Locale locale) throws Exception {
		File file = null;
		if(universal || locale==null){
			file = new File(Strings.cat(path,postFix));
		}else{
			String lan = locale.getLanguage();
			String ctry = locale.getCountry();
			//zk load lan_ctry then lan
			if(!Strings.isEmpty(lan) && !Strings.isEmpty(ctry)){
				file = new File(Strings.cat(path,"_",lan,"_",ctry,postFix));
			}else if(!Strings.isEmpty(lan)){
				file = new File(Strings.cat(path,"_",lan,postFix));
			}
		}
		if(file!=null && file.exists() && file.isFile()){
			return file.toURI().toURL();
		}
		
		return null;
	}

}