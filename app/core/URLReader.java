package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Reads raw HTML from an URL, and returns it as String.
 * 
 * This class can either be instanciated or used with a static method :
 * 
 * 1) Instanciation
 * URLReader r = new URLReader("http://myaddress.com");
 * String result = r.getHTML();
 * 
 * 2) Static
 * String result = URLReader.readURL("http://myaddress.com");
 * 
 * @author mw4rf
 *
 */
public class URLReader {
	
	URL url = null;
	String html = "";
	
	/**
	 * Constructor with address as String.
	 * @param URLAddress
	 */
	public URLReader(String URLAddress) {
		// Form the URL
		try {
			url = new URL(URLAddress);
		} catch(MalformedURLException e) {
			System.out.println("Malformed URL: " + URLAddress);
		}
		// do the job
		try {
			readURL();
		} catch(IOException e) {
			System.out.println("IO Error on " + url);
		}
	}
	
	/**
	 * Constructor with address as URL.
	 * @param url
	 */
	public URLReader(URL url) {
		this.url = url;
		// do the job
		try {
			readURL();
		} catch(IOException e) {
			System.out.println("IO Error on " + url);
		}
	}
	
	/**
	 * Read raw HTML from the URL, line by line, and saves the result in a field.
	 * @throws IOException
	 */
	private void readURL() throws IOException {
		URLConnection urlc = url.openConnection();
		urlc.setDefaultUseCaches(false);
		BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
		String inputLine;
		// Read all the lines
		while ((inputLine = in.readLine()) != null)
			html += inputLine + "\n";
		// Close stream
		in.close();
	}
	
	/**
	 * Get raw HTML from the read URL.
	 * @return
	 */
	public String getHTML() {
		return html;
	}
	
	/**
	 * Static constructor, with String. Use it like this : URLReader.readURL("http://myaddress.com");
	 * @param u
	 * @return
	 */
	public static String readURL(String u) {
		return new URLReader(u).getHTML();
	}
	
	/**
	 * Static constructor, with URL.
	 * @param u
	 * @return
	 */
	public static String readURL(URL u) {
		return new URLReader(u).getHTML();
	}
	
}
