package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;
import play.libs.Crypto;
import play.libs.Crypto.HashType;

@Entity
@Table(name="users")
public class User extends Model {
	
	@Column(nullable=false, length=64)
	public String name = "";
	
	@Column(nullable=false)
	public String pwdhash = "";
	
	public Date createdAt = new Date();
	public Date updatedAt = new Date();
	
	/**
	 * Constructor to create a new user.
	 * @param username
	 * @param password
	 */
	public User(String username, String password) {
		this.name = username;
		this.pwdhash = getPasswordHash(password);
	}
	
	/**
	 * Finds the {@link User} with the given name, and loads it from the database.
	 * Username is sanitized before use, in order to prevent SQL injections.
	 * @param username
	 * @return
	 */
	public static User findByName(String username) {
		username = validateUserName(username);
		return User.find("byName", username).first();
	}
	
	/**
	 * Check if the giver username exists.
	 * @param username
	 * @return <b>true</b> if the user exists, <b>false</b> otherwise.
	 */
	public static boolean exists(String username) {
		if(findByName(username) == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Get a SHA-512 secured hash of the given password.
	 * <br />The hash uses the {@link String} given as argument to the function, 
	 * <b>and</b> the application secret key, defined in conf/application.conf
	 * <br />As a result, changing the app secret key will invalidate all password hashes.
	 * @param password
	 * @return {@link String} secured hash of the password
	 */
	public static String getPasswordHash(String password) {
		String hash = password + play.Play.configuration.getProperty("application.secret");
		String hashed = Crypto.passwordHash(hash, HashType.SHA512);
		return hashed;
	}
	
	/**
	 * Check the given password, return <b>true</b> if there's a match, <b>false</b> otherwise.
	 * @param password
	 * @return
	 */
	public boolean checkPassword(String password) {
		return getPasswordHash(password).equals(this.pwdhash);
	}
	
	/**
	 * Strips some current special characters from the given {@link String}.
	 * This prevents funny user names, as well as SQL injections.
	 * @param userName
	 * @return {@link String} userName without special chars
	 */
    public static String validateUserName(String userName) {
    	if(userName == null || userName.isEmpty())
    		return null;
        userName = userName.replace("'", "");
        userName = userName.replace("`", "");
        userName = userName.replace("\"", "");
        userName = userName.replace(" ", "_");
        userName = userName.replace("$", "");
        userName = userName.replace("\\", "");
        userName = userName.replace("/", "");
        userName = userName.replace("[", "");
        userName = userName.replace("]", "");
        userName = userName.replace("(", "");
        userName = userName.replace(")", "");
        userName = userName.replace("{", "");
        userName = userName.replace("}", "");
        userName = userName.replace("%", "");
        userName = userName.replace("=", "");
        userName = userName.replace("+", "");
        userName = userName.replace("@", "");
        userName = userName.replace("#", "");
        userName = userName.replace("&", "");
        return userName;
    }
	
}
