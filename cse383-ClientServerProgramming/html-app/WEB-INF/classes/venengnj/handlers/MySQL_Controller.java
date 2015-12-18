/**
 * This class stores and retrieves information
 * using MySQL as the storage mechanism
 */
package venengnj.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;

public class MySQL_Controller implements StorageHandler {
	String user = "383-story";
	String password = "";
	String dbUrl = "jdbc:mysql://localhost/383_story";
	Connection conn = null;
	
	public MySQL_Controller() throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl,user,password);

		} catch (Exception err) {
			throw new IOException(err);
		}
	}
	
	/**
	 * Returns a list of all stories by title
	 * @return ArrayList of story titles
	 */
	public ArrayList<String> getStoryTitles() {
		ArrayList<String> stories = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			String sql = "select title from story_hdr";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String title = rs.getString("title");
				stories.add(title);
			}
			rs.close();
			stmt.close();
		} catch (Exception err) {
			Logger.log("ERROR: Error retrieving list of stories from the database");
		}
		return stories;
	}
	
	public ArrayList<Integer> getStories() {
		ArrayList<Integer> stories = new ArrayList<Integer>();
		try {
			Statement stmt = conn.createStatement();
			String sql = "select sid from story_hdr";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				int id = rs.getInt("sid");
				stories.add(id);
			}
			rs.close();
			stmt.close();
		} catch (Exception err) {
			Logger.log("ERROR: Error retrieving list of story ids from the database");
		}
		return stories;
	}
	
	public int storyTitleToId(String title) throws SQLException, IllegalArgumentException {
		int id = -1;
		String sql = "select sid from story_dtl where title = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, title);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			id = rs.getInt("sid");
		}
		rs.close();
		stmt.close();
		if(id == -1)
			throw new IllegalArgumentException();
		return id;
	}
	
	public String storyIdToTitle(int id) {
		String title = null;
		String sql = "select title from story_dtl where sid = ?";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				title = rs.getString("title");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			Logger.log("ERR: Couldn't get title from story id " + id);
		}
		if(id == -1)
			throw new IllegalArgumentException();
		return title;
	}
	
	/**
	 * Returns the number of pages a story has
	 * @param id of story
	 * @return int number of pages
	 */
	public int getPageCount(int id) {
		int pageCnt = 1;
		try {
			String sql = "select pages from story_dtl where sid = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				pageCnt = rs.getInt("pages");
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {}
		return pageCnt;
	}
	
	/**
	 * Fetches a particular page of a story
	 * @param storyName
	 * @param pageNum
	 * @return String containing page text
	 * @throws Exception
	 */
	public String getPage(String storyName, int pageNum) throws Exception {
		String text = null;
		//Prepare a sql statement
		String sql = "select text from story_page_title where page = ? and title = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, pageNum);
		stmt.setString(2, storyName);
		ResultSet rs = stmt.executeQuery();
		//Parse results
		while(rs.next()) {
			text = rs.getString("text");
		}
		rs.close();
		stmt.close();
		if(text == null)
			throw new Exception("Couldn't fetch page "+pageNum+" for "+storyName);
		return text;
	}
	
	/**
	 * Gets the password hash of a user given username
	 * @param username
	 * @return hash
	 */
	public String getPasswordHash(String username) throws Exception {
		String hash = null;
		//Prepare a sql statement
		String sql = "select password from users where username = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		//Parse results
		while(rs.next()) {
			hash = rs.getString("password");
		}
		rs.close();
		stmt.close();
		if(password == null)
			throw new Exception("Couldn't find user " + username);
		return hash;
	}
	
	/**
	 * Finds a user's email given their username
	 * @param username to search for
	 * @return the email address
	 * @throws Exception when anything goes wrong
	 */
	public String getUserEmail(String username) throws Exception {
		String email = "";
		//Prepare a sql statement
		String sql = "select email from users where username = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		//Parse results
		while(rs.next()) {
			email = rs.getString("email");
		}
		rs.close();
		stmt.close();
		if(email == null)
			throw new Exception("Couldn't find user " + username);
		return email;
	}
	
	public boolean userIsAdmin(String username) throws Exception {
		int isAdmin = -1;
		//Prepare a sql statement
		String sql = "select isAdmin from users where username = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			//Parse results
			while(rs.next()) {
				isAdmin = rs.getInt("isAdmin");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			throw new Exception("Error checking admin status of " + username);
		}
		return isAdmin == 1;
	}
	
	/**
	 * Adds a rest key to the database
	 * @param key to add
	 * @return whether the key was successfully added
	 */
	public boolean addRestKey(String key) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "insert into rest_keys (id,time) values('"+key+"', time)";
			System.err.println(sql);
			int rs = stmt.executeUpdate(sql);
			return rs == 1;
		} catch (Exception err) {
			System.out.println(err.getMessage());
			Logger.log("ERROR: Error adding rest key to database: " + key);
		}
		return true;
	}
	
	/**
	 * Checks to make sure the key exists and was issued in the last hour
	 * @param key to check
	 * @return whether the key is valid
	 */
	public boolean validateRestKey(String key) {
		try {
			String sql = "select count(*) as cnt from rest_keys where id = ?" 
					+ " and time <= DATE_SUB(NOW(),INTERVAL 1 HOUR)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, key);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int results = rs.getInt("cnt");
			return results == 1;
		} catch (Exception err) {
			Logger.log("ERROR: Error getting rest key from database: " + key);
		}
		return false;
	}
	
	public boolean deleteStory(int id) {
		try {
			String sql = "delete from story_dtl where sid=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int rs = stmt.executeUpdate();
			return rs == 1;
		} catch (Exception err) {
			Logger.log("ERROR: Error deleting story from database: " + id);
		}
		return false;
	}
}
