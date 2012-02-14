package mobilizers;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import core.URLReader;
import models.Article;

public class InstapaperMobilizer extends Mobilizer {
	
	public static String MOBILIZER_NAME = "Instapaper";
	public static String MOBILIZER_URL = "http://www.instapaper.com/m?u=";
	public static String MOBILIZER_ERROR = "<h1>Exceeded rate limit</h1>";
	
	public static String mobilize(String link)
			throws MobilizationException {
		return Mobilizer.mobilize(link, InstapaperMobilizer.MOBILIZER_URL, InstapaperMobilizer.MOBILIZER_ERROR);
	}

}
