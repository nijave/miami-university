package venengnj.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import venengnj.handlers.*;

public class Story {
	private int id;
	private String title;
	
	public Story(int id) {
		this.id = id;
		try {
			title = (new MySQL_Controller()).storyIdToTitle(id);
		} catch (IOException e) {/*already logged by mysql controller*/}
		if(title == null)
			throw new IllegalArgumentException("Story not found");
	}

	/**
	 * Create a new story given the title
	 * @param title of the story
	 * @return a story
	 * @throws IllegalArgumentException if the title doesn't exist
	 */
	public static Story storyFromTitle(String title) throws IllegalArgumentException {
		try {
			int id = (new MySQL_Controller()).storyTitleToId(title);
			return new Story(id);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Invalid story title: " + title);
		}
	}
	
	public static ArrayList<Story> getStoryList() {
		try {
			ArrayList<Story> stories = new ArrayList<Story>();
			ArrayList<Integer> story_ids = (new MySQL_Controller()).getStories();
			for(Integer i : story_ids) {
				stories.add(new Story(i.intValue()));
			}
			return stories;
		} catch (IOException e) {
			Logger.log("ERR: Unable to get list of stories");
			return new ArrayList<Story>();
		}
	}
	
	public static ArrayList<String> getStoryTitles() {
		try {
			return (new MySQL_Controller()).getStoryTitles();
		} catch (IOException e) {
			Logger.log("ERR: Fetching list of story titles");
			return new ArrayList<String>();
		}
	}
	
	public String getPageAsString(int num) {
		try {
			return (new MySQL_Controller()).getPage(this.getTitle(), num);
		} catch (Exception e) {
			return "";
		}
	}
	
	public HashMap<String,Object> getPage(int num) {
		HashMap<String,Object> data = new HashMap<String,Object>();
		
		data.put("pages", this.getNumberOfPages());
		try {
			if(num <= this.getNumberOfPages()) {
				data.put("text", getPageAsString(num));
			}
			else {
				data.put("text", getPageAsString(1));
			}
		}
		catch (Exception e) {
			Logger.log("ERR: Couldn't get page " + num + " of " + this.getTitle());
			data.put("text", "Error fetching story contents");
			return data;
		}
		
		return data;
	}
	
	public int getNumberOfPages() {
		try {
			return (new MySQL_Controller()).getPageCount(this.getId());
		} catch (IOException e) {
			Logger.log("ERR: Fetching page count of story " + this.getId());
			return 1;
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean delete() throws Exception {
		return (new MySQL_Controller()).deleteStory(this.getId());
	}
	
	public boolean insertPage(int number, String content) {
		return false;
	}
	
	public boolean removePage(int number, String content) {
		return false;
	}
	
	public boolean updatePage(int number, String content) {
		return false;
	}
}
