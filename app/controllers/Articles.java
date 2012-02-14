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
	
    public static void index() {    	
    	//List<Article> articles = Article.find("order by addedAt desc").fetch();
    	// Pagination
    	ModelPaginator articles = new ModelPaginator<Article>(Article.class).orderBy("addedAt desc");
    	articles.setPageSize(Integer.parseInt(play.Play.configuration.get("pagination").toString()));
    	// render
        render(articles);
    }
    
    public static void mobilized(Long id) {
    	Article a = Article.findById(id);
    	renderHtml(a.fulltext);
    }
    
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
