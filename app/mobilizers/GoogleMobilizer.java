package mobilizers;

public class GoogleMobilizer extends Mobilizer {
	
	public static String MOBILIZER_NAME = "GoogleMobilizer";
	public static String MOBILIZER_URL = "http://www.google.com/gwt/x?u=";
	public static String MOBILIZER_ERROR = "";
	
	/**
	 * Mobilize the given URL, as {@link String}, and returns the content of the web page
	 * as {@link String}.
	 * @param link
	 * @return {@link String} mobilized version of the web page
	 * @throws MobilizationException
	 */
	public static String mobilize(String link)
			throws MobilizationException {
		String res = Mobilizer.mobilize(link, GoogleMobilizer.MOBILIZER_URL, GoogleMobilizer.MOBILIZER_ERROR);
		// Correct broken image links
		res.replaceAll("/gwt/x/i?u=", "http://www.google.com/gwt/x/i?u=");
		return res;
	}
}
