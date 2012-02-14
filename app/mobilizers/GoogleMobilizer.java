package mobilizers;

public class GoogleMobilizer extends Mobilizer {
	
	public static String MOBILIZER_NAME = "GoogleMobilizer";
	public static String MOBILIZER_URL = "http://www.google.com/gwt/x?u=";
	public static String MOBILIZER_ERROR = "";
	
	public static String mobilize(String link)
			throws MobilizationException {
		String res = Mobilizer.mobilize(link, GoogleMobilizer.MOBILIZER_URL, GoogleMobilizer.MOBILIZER_ERROR);
		// Correct broken image links
		res.replaceAll("/gwt/x/i?u=", "http://www.google.com/gwt/x/i?u=");
		return res;
	}
}
