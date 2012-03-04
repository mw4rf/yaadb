package controllers;

import java.util.List;

import models.Article;
import play.mvc.Controller;

public class Feeds extends Controller {

	 //###################################################################################################
	 //############## RSS Output
	 //###################################################################################################
	    
	    /**
	     * Publish a RSS 2.0 feed of the last articles.
	     * This method uses 2 global variables, defined in the app configuration file :
	     * 	- <i>social.rss.enable</i>: wether to enable or not the rss feed (if not, calling this page will redirect to index) ;
	     * 	- <i>social.rss.count</i>: the number of items to include in the feed.
	     * <b><u>Gives to template:</u></b>
	     * 	-	{@link List} of {@link Article} objects.
	     * <b><u>Rendering:</u></b> RSS 2.0 feed (XML)
	     */
	    public static void articles() {
	    	// Get properties
	    	boolean enable; // is the output rss feed enabled ?
	    	int count; // how many items should we show ?
	    	try {
	    		enable = Boolean.parseBoolean(play.Play.configuration.get("social.rss.enable").toString());
	    		count = Integer.parseInt(play.Play.configuration.get("social.rss.count").toString());
	    	} catch(Exception e) {
	    		enable = false;
	    		count = 0;
	    	}
	    	// If enable, proceed
	    	if(enable) {
	    		// Get <count> last articles
	    		List<Article> articles = Article.find("order by addedAt desc").fetch(count);
	    		render("Feeds/articles.rss", articles);
	    	} else {
	    		// not enabled: 404
	    		Controller.notFound();
	    	}
	    }
	
}
