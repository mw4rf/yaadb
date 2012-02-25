package jobs;

import java.net.URL;
import java.util.Iterator;

import models.Article;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import core.URLReader;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart(async=true)
@Every("5mn")
public class Updater extends Job {
		
	URL url = null;
    XmlReader reader = null;
    SyndFeed feed = null;
    
    public int totalItems = 0;
    public int parsedItems = 0;
    public int ignoredItems = 0;
    
    String uuid = "";
    
    public Updater(String uuid) {
    	this.uuid = uuid;
    }
	
    /**
     * Job main routine :
     * 	-	get the feed URL from configuration ;
     * 	-	loads the URL content and builds the XML feed ;
     * 	-	for each &lt;item&gt; in this feed, creates a new {@link SyndEntry} object ;
     * 	-	extracts data from the {@link SyndEntry} object and check if there is a matching {@link Article} in the database ;
     * 	-	if there's already a matching (URL or title for Tumblr) {@link Article} in the database, skip to next feed entry ;
     * 	-	if there's no matching {@link Article}, create a new one, mobilize it, and save it in the database. 
     */
	@Override
	public void doJob() throws Exception {
		try {
			// load feed
			url = new URL(play.Play.configuration.get("feedURL").toString());
			Logger.info("Parser started on " + url.toString());
			Cache.set("Job_" + this.uuid + "_done", false);
			Cache.set("Job_" + this.uuid + "_percent", 0);
			appendMessage("Parser started on " + url.toString());
			reader = new XmlReader(url);
			feed = new SyndFeedInput().build(reader);
			totalItems = feed.getEntries().size();
			appendMessage(totalItems + " items loaded from feed.");
			// parse feed items
			int current = 0;
			for (Iterator<SyndEntry> i = feed.getEntries().iterator(); i.hasNext();) {
				current++;
		        SyndEntry entry = i.next();
		        // Check if the article has already been saved
		        Article test = null;
		        if(play.Play.configuration.get("service").toString().equalsIgnoreCase("tumblr")) {
		        	test = Article.find("byTitle", entry.getTitle()).first();
		        } else {
		        	test = Article.find("byLink", entry.getLink()).first();
		        }
		        if(test != null) {
		        	ignoredItems++;
		        	appendMessage("<span class=\"label label-default\">" + current + "/" + totalItems + "</span> <span class=\"label label-warning\">Skipped</span> Article already parsed: <i>" + entry.getTitle() + "</i>");
		        	continue;
		        }
		        // Save new article
		        Article a = new Article(entry).save();
		        Logger.info("New article parsed : ", entry.getTitle().toString());
		        appendMessage("<span class=\"label label-default\">" + current + "/" + totalItems + "</span> <span class=\"label label-success\">Parsed</span> <b>" + a.title + "</b>");
		        Cache.set("Job_" + this.uuid + "_percent", (int) current * 100 / totalItems);
		        parsedItems++;
		    }
			reader.close();
			Logger.info("Parsing finished.");
			appendMessage("Finished parsing " + totalItems + " items -- " + parsedItems + " parsed, " + ignoredItems + " skipped.");
			Cache.set("Job_" + this.uuid + "_done", true);
			Cache.set("Job_" + this.uuid + "_percent", 100);
		} catch (Exception e) {
			System.out.println("Could not open feed.");
    	} finally {
    		feed = null;
    		reader = null;
    	}
	}
	
	/**
	 * Appends the given message to the {@link Play} {@link Cache}
	 * @param message
	 */
	private void appendMessage(String message) {
		try {
			Cache.set("Job_" + this.uuid + "_message", message + "<br />" + Cache.get("Job_" + this.uuid + "_message").toString());
		} catch(Exception e) {
			Cache.set("Job_" + this.uuid + "_message", message);
		}
	}
	
	/**
	 * Get the current message from the {@link Play} {@link Cache}
	 * @return
	 */
	public String getMessages() {
		return Cache.get("Job_" + this.uuid + "_message").toString();
	}

}
