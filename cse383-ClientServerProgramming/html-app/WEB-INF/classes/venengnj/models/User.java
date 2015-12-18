package venengnj.models;

import org.mindrot.jbcrypt.BCrypt;
import venengnj.handlers.*;

/**
 * A class to represent a user account
 * @author Nick Venenga
 */
public class User {
	private String username;
	private boolean isAuthenticated;
	private boolean isAdmin;
	
	/**
	 * Create an account object given username
	 * @param username
	 */
	public User(String username) {
		this.username = username;
		isAuthenticated = false;
		isAdmin = false;
	}
	
	/**
	 * Create an account given username and password
	 * @param username
	 * @param password
	 */
	public User(String username, String password) {
		this.username = username;
		this.isAuthenticated = authenticate(password);
	}
	
	/**
	 * Try to authenticate a user
	 * @param password
	 * @return success or fail
	 */
	public boolean authenticate(String password) {
		String hash;
		try {
			hash = (new MySQL_Controller()).getPasswordHash(this.username);
			isAdmin = (new MySQL_Controller()).userIsAdmin(this.username);
		} catch (Exception e) {
			this.isAuthenticated = false;
			this.isAdmin = false;
			return false;
		}
		boolean result = BCrypt.checkpw(password, hash);
		this.isAuthenticated = result;
		return result;
	}
	
	/**
	 * Return whether an account is authenticated
	 * @return status
	 */
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	/**
	 * Getter
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	
	public static String emailFromUsername(String username) {
		try {
			return (new MySQL_Controller()).getUserEmail(username);
		} catch (Exception e) {
			Logger.log("ERR: Exception encountered getting email for " + username);
			return "";
		}
	}
}
