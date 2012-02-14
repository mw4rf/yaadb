package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import models.Article;
import play.modules.paginate.ModelPaginator;
import play.mvc.Controller;

public class Articles extends Controller {

//###################################################################################################
//############## DISPLAY HTML PAGES
//###################################################################################################
	
	/**
	 * Index of the /articles/ path. Show a summary of what we've got in the database and some useful links
	 * in a "homepage" view.
	 * URL: /articles/ OR /articles/index
	 * <b><u>Gives to template:</u></b>
	 * 	-	{@link Long} <b>count_all</b>: number of articles in the database
	 * 	-	{@link Long} <b>count_week</b>: number of new articles added this week
	 * <b><u>Rendering:</u></b> HTML
	 */
	public static void index() {
		// Count all articlintes
		Long count_all = Article.count();
		// Count articles of the week
		DateTime now = new DateTime();
		DateTime dt = now.minusDays(now.getDayOfWeek() - 1).minusHours(now.getHourOfDay()).minusMinutes(now.getMinuteOfHour());
		Date ldate = dt.toDate();
		Long count_week = Article.count("publishedAt > Date(?)", ldate);
		// Render
		render(count_all, count_week);
	}
	
	/**
	 * Show a paginated list of all the articles in the database, as a table sorted by date desc (newer to older).
	 * URL: /articles/list
	 * <b><u>Gives to template:</u></b>
	 * 	-	{@link ModelPaginator} articles: a paginated list of {@link Article} objects.
	 * <b><u>Rendering:</u></b> HTML
	 */
    public static void list() {    	
    	// Pagination
    	ModelPaginator articles = new ModelPaginator<Article>(Article.class).orderBy("addedAt desc");
    	articles.setPageSize(Integer.parseInt(play.Play.configuration.get("pagination").toString()));
    	// render
        render(articles);
    }
    
    /**
     * Show the mobilized version of the given article.
     * URL: /articles/mobilized?id={param}
     * @param id The id of the article to load.
     * <b><u>Gives to template:</u></b>
     * 	-	{@link Article} <i>fulltext</i> field, a {@link String} containing raw HTML. 
     * <b><u>Rendering:</u></b> HTML
     */
    public static void mobilized(Long id) {
    	Article a = Article.findById(id);
    	renderHtml(a.fulltext);
    }
    
    /**
     * Show articles of the week (starting on monday).
     * URL: /articles/week
     * <b><u>Gives to template:</u></b>
     * 	-	{@link List} of {@link Article} objects, sorted by <i>publishedAt</i> desc.
     * <b><u>Rendering:</u></b> HTML
     */
    public static void week() {
    	DateTime now = new DateTime();
    	DateTime dt = now.minusDays(now.getDayOfWeek() - 1).minusHours(now.getHourOfDay()).minusMinutes(now.getMinuteOfHour());
    	Date ldate = dt.toDate();
    	//ModelPaginator articles = new ModelPaginator<Article>(Article.class, "publishedAt > Date(?)", ldate).orderBy("addedAt asc");
    	List<Article> articles = Article.find("publishedAt > Date(?) order by publishedAt desc", ldate).fetch();
    	render(articles);
    }
    
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
    public static void rss() {
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
    		render("articles.rss", articles);
    	} else {
    		// not enabled: redirect to home
    		index();
    	}
    }
    
 //###################################################################################################
 //############## ACTIONS
 //###################################################################################################   
    
    /**
     * Toggle the <i>cited</i> state of the given article.
     * @param id: the id of the {@link Article} to load.
     * <b><u>Gives to template:</u></b>
     * 	- 	{@link Article} object.
     * <b><u>Rendering:</u></b> AJAX HTML
     */
    public static void cite(Long id) {
    	Article article = Article.findById(id);
    	if(article.cited)
    		article.cited = false;
    	else
    		article.cited = true;
    	article.updatedAt = new Date();
    	article.save();
    	render("Articles/_articleLine.html", article);
    }
    
}
