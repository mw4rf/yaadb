package controllers;

import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	/** TODO: add some authentication checks here **/
    	Articles.index();
    }

}