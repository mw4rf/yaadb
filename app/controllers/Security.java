package controllers;

import models.User;

public class Security extends controllers.Secure.Security {
	
	/**
	 * Returns <b>true</b> if the given password matches the password hash saved
	 * in the database for the given player name.
	 * @param username
	 * @param password
	 * @return {@link Boolean} <b>true</b> for legit users, <b>false</b> for the others.
	 */
	public static boolean authentify(String username, String password) {
		User user = User.findByName(username);
        return user != null && user.checkPassword(password);
	}
	
	
	
}
