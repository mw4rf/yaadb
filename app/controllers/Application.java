package controllers;

import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

public class Application extends Controller {

	/**
	 * Application home
	 */
    public static void index() {
    	boolean enableRegistration = Boolean.parseBoolean(play.Play.configuration.get("enable.registration").toString());
    	render(enableRegistration);
    }
    
    /**
     * Check if the visitor is a logged-in user.
     * @return
     */
    public static boolean isConnected() {
    	if(controllers.Secure.Security.isConnected() && getCurrentUsername() != null)
    		return true;
    	else
    		return false;
    }
    
    /**
     * Return the username of the visitor, if he's a logged-in user.
     * @return
     */
    public static String getCurrentUsername() {
    	return Security.connected();
    }
    
    /**
     * Return the {@link User} object belonging to the visitor, if he's a logged-in user.
     * @return
     */
    public static User getCurrentUser() {
    	return User.findByName(getCurrentUsername());
    }
    
    /**
     * Login the user with the given name and password.
     * @param username
     * @param password
     * @param remember
     */
    public static void login(@Required String username, @Required String password, boolean remember) {
    	try {
			Secure.authenticate(username, password, remember);
    	} catch(Throwable e) {
    		e.printStackTrace();
    	} finally {
    		Controller.redirect("Application.index");
    	}
    }
    
    /**
     * Logout the user and clear the session.
     */
    public static void logout() {
    	try {
			Secure.logout();
		} catch (Throwable e) {
			;
		} finally {
			Controller.redirect("Application.index");
		}
    }
    
    /**
     * Create a new user account.
     * @param username
     * @param password
     */
    public static void register(@Required String username, @Required String password) {
    	boolean enableRegistration = Boolean.parseBoolean(play.Play.configuration.get("enable.registration").toString());
    	// Validate mandatory fields and check if the user doesn't already exist
    	if(!enableRegistration || validation.hasErrors() || User.exists(username)) {
            flash.error("flash.error.register");
            params.flash();
    		index();
    	}
    	// Sanitize username
    	username = User.validateUserName(username);
    	// Create user :)
    	User user = new User(username, password).save();
    	// Log in for the user, so she doesn't have to fill the form again
    	login(username, password, true);
    	// flash
    	flash.success("flash.success.register", username);
    }

}