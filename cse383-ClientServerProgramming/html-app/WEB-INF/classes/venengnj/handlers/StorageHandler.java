/*
	This interface is used by the application
	to store and retrieve information. Classes
	can implement this to use different storage
	methods while maintaining compatibility
*/
package venengnj.handlers;

import java.util.ArrayList;
	
public interface StorageHandler {
	abstract ArrayList<Integer> getStories();
	//abstract HashMap<String,Object> getStory(String storyName, int pageNumber);
}