package mobilizers;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import core.URLReader;

import models.Article;

public class Mobilizer {
	
	/**
	 * Get the mobilized output from the mobilizer engine.
	 * @param link
	 * @return
	 */
	public static String mobilize(String link, String mobilizerURL, String mobilizerError) throws MobilizationException {
		// Get safe URL
		String encURL;
		try {
			encURL = Mobilizer.getEncodedURL(new URL(link));
		} catch(UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncoding. This URL will not be mobilized.");
			throw new MobilizationException();
		} catch(MalformedURLException e) {
			System.out.println("Malformed URL. This URL will not be mobilized.");
			throw new MobilizationException();
		}
		// Get the URL of the mobilized page
		String mobURL = mobilizerURL + encURL;
		// Call the mobilizer and get the result
		String result = URLReader.readURL(mobURL);
		// Check result
		if(result.equalsIgnoreCase(mobilizerError)) {
			throw new MobilizationException();
		}
		return result;
	}
	
	/**
	 * Returns a safe encoded URL from the article link
	 * for use by mobilizers : http://service.com/process?=http%3A%2F%2FescapedURI
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncodedURL(URL link) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(link.toString(), "UTF-8");
	}
}
