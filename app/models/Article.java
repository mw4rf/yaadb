package models;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import mobilizers.GoogleMobilizer;
import mobilizers.InstapaperMobilizer;

import com.sun.syndication.feed.synd.SyndEntry;

import controllers.Articles;

import play.db.jpa.Model;

@Entity
public class Article extends Model {
	
	/**
	 * FIELDS mapped to DB table
	 */
	// From Feed
	public String title = "";					// article title
	public String author = "";					// article author
	public String link = "";					// article URL
	public Date publishedAt;					// article publication date
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String excerpt = "";					// article description
	
	// From mobilizer
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String fulltext = "";				// article contents
	public Boolean mobilized = false;			// is article properly mobilized ?
	
	// Computed values
	public String source = "";					// website (just domain name)
	
	// Internal logic
	public Date addedAt;						// record creation date
	public Date updatedAt;						// record update date
	
	// User properties
	public Boolean starred = false;
	public Boolean cited = false;
	
	
	/** 
	 * Raw Constructor
	 */
	public Article() {
		addedAt = new Date(); // now
		updatedAt = new Date(); // now
	}
	
	/**
	 * Constructor with feed entry from ROME RSS parser library.
	 * @param entry
	 */
	public Article(SyndEntry entry) {
		addedAt = new Date(); // now
		updatedAt = new Date(); // now
		
		// Services management (and feed formats)
		// 1. Tumblr links (link and title are in the "description" field, no other fields are available)
		if(play.Play.configuration.get("service").toString().equalsIgnoreCase("tumblr")) {
			title = entry.getTitle();
			// get link out of description
			Pattern p = Pattern.compile("href=\"(.*?)\"");
			Matcher m = p.matcher(entry.getDescription().getValue());
			if (m.find()) {
			    link = m.group(1); // this variable should contain the link URL
			}
			System.out.println("Found from tumblr: " + title + " @ " + link);
		}
		// 2. Generic (every field is what it's supposed to be)
		else {
			title = entry.getTitle();
			author = entry.getAuthor();
			link = entry.getLink();
			excerpt = entry.getDescription().getValue();
		}
		
		// Get publication date or, if it's null, updateDate ;
		// or, if both are null, now date.
		if(entry.getPublishedDate() != null)
			publishedAt = entry.getPublishedDate();
		else if(entry.getUpdatedDate() != null)
			publishedAt = entry.getUpdatedDate();
		else
			publishedAt = new Date();
		
		// Logic fields
		try {
			// ----- Source field -----
			source = new URL(link).getHost().replaceFirst("www.", "");
			// ----- MOBILIZATION -----
			// Switch mobilizer type
			if(play.Play.configuration.get("mobilizer").toString().equalsIgnoreCase(GoogleMobilizer.MOBILIZER_NAME))
				fulltext = GoogleMobilizer.mobilize(link);
			else
				fulltext = InstapaperMobilizer.mobilize(link);
			mobilized = true;
		} catch(Exception e) {
			System.out.println("Mobilization error. Article not mobilized.");
			fulltext = "";
			mobilized = false;
		}
	}
	
	/**
	 * Returns a {@link List} of cited or un-cited {@link Article} objects.
	 * @param cited
	 * @return
	 */
	public static List<Article> getCited(boolean cited) {
		return Article.find("cited = ? order by updatedAt desc", cited).fetch();
	}
	
	/**
	 * Count {@link Article} objects <b>starred</b>.
	 * @return
	 */
	public static Long countCited() {
		return Article.count("cited = ?", true);
	}
	
	/**
	 * Returns a {@link List} of starred or un-starred {@link Article} objects.
	 * @param starred
	 * @return
	 */
	public static List<Article> getStarred(boolean starred) {
		return Article.find("starred = ? order by updatedAt desc", starred).fetch();
	}
	
	/**
	 * Count {@link Article} objects <b>starred</b>.
	 * @return
	 */
	public static Long countStarred() {
		return Article.count("starred = ?", true);
	}
	
	/**
	 * Returns a {@link List} of {@link Article} objects <i>published</i> this week (starting on monday).
	 * @return
	 */
	public static List<Article> getThisWeek() {
		DateTime now = new DateTime();
    	DateTime dt = now.minusDays(now.getDayOfWeek() - 1).minusHours(now.getHourOfDay()).minusMinutes(now.getMinuteOfHour());
    	Date ldate = dt.toDate();
    	return Article.find("publishedAt > Date(?) order by publishedAt desc", ldate).fetch();
	}
	
	/**
	 * Count {@link Article} objects <b>published</b> this week (starting on monday).
	 * @return
	 */
	public static Long countThisWeek() {
		DateTime now = new DateTime();
		DateTime dt = now.minusDays(now.getDayOfWeek() - 1).minusHours(now.getHourOfDay()).minusMinutes(now.getMinuteOfHour());
		Date ldate = dt.toDate();
		return Article.count("publishedAt > Date(?)", ldate);
	}
	
}
