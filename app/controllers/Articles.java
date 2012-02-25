package controllers;

import java.util.Date;
import java.util.List;

import models.Article;
import play.modules.paginate.ModelPaginator;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
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
		// Count articles
		Long count_all = Article.count();
		Long count_week = Article.countThisWeek();
		Long count_cited = Article.countCited();
		Long count_starred = Article.countStarred();
		// Render
		render(count_all, count_week, count_cited, count_starred);
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
    	boolean paginated = true;
    	// render
        render(articles, paginated);
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
    	List<Article> articles = Article.getThisWeek();
    	render("Articles/list.html", articles);
    }
    
    /**
     * Show all cited articles.
     * URL: /articles/cited
     * <b><u>Gives to template:</u></b>
     * 	-	{@link List} of {@link Article} objects, sorted by <i>updatedAt</i> desc.
     * <b><u>Rendering:</u></b> HTML
     */
    public static void cited() {
    	List<Article> articles = Article.getCited(true);
    	render("Articles/list.html", articles);
    }
    
    /**
     * Show all starred articles.
     * URL: /articles/starred
     * <b><u>Gives to template:</u></b>
     * 	-	{@link List} of {@link Article} objects, sorted by <i>updatedAt</i> desc.
     * <b><u>Rendering:</u></b> HTML
     */
    public static void starred() {
    	List<Article> articles = Article.getStarred(true);
    	render("Articles/list.html", articles);
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
    
    /**
     * Toggle the <i>starred</i> state of the given article.
     * @param id: the id of the {@link Article} to load.
     * <b><u>Gives to template:</u></b>
     * 	- 	{@link Article} object.
     * <b><u>Rendering:</u></b> AJAX HTML
     */
    public static void star(Long id) {
    	Article article = Article.findById(id);
    	if(article.starred)
    		article.starred = false;
    	else
    		article.starred = true;
    	article.updatedAt = new Date();
    	article.save();
    	render("Articles/_articleLine.html", article);
    }
    
}
