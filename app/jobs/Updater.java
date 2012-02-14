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
	
	@Override
	public void doJob() throws Exception {
		try {
			// load feed
			url = new URL(play.Play.configuration.get("feedURL").toString());
			Logger.info("Parser started on " + url.toString());
			Cache.set("Job_" + this.uuid + "_done", false);
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
		        	//System.out.println("This article has already been saved. Skipped to next article.");
		        	ignoredItems++;
		        	appendMessage("Item " + current + "/" + totalItems + " skipped. Article already parsed: <i>" + entry.getTitle() + "</i>");
		        	continue;
		        }
		        // Save new article
		        Article a = new Article(entry).save();
		        Logger.info("New article parsed : ", entry.getTitle().toString());
		        appendMessage("Item " + current + "/" + totalItems + " parsed : <b>" + a.title + "</b>");
		        parsedItems++;
		    }
			reader.close();
			Logger.info("Parsing finished.");
			appendMessage("Finished parsing " + totalItems + " items -- " + parsedItems + " parsed, " + ignoredItems + " skipped.");
			Cache.set("Job_" + this.uuid + "_done", true);
		} catch (Exception e) {
			System.out.println("Could not open feed.");
    	} finally {
    		feed = null;
    		reader = null;
    	}
	}
	
	private void appendMessage(String message) {
		try {
			Cache.set("Job_" + this.uuid + "_message", message + "<br />" + Cache.get("Job_" + this.uuid + "_message").toString());
		} catch(Exception e) {
			Cache.set("Job_" + this.uuid + "_message", message);
		}
	}
	
	public String getMessages() {
		return Cache.get("Job_" + this.uuid + "_message").toString();
	}

}
