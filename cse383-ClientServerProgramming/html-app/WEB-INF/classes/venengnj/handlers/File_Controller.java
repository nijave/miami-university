package venengnj.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
This class handles interacting with the
file system and is used by the servlets toString
check/retrieve information
*/	

public class File_Controller implements StorageHandler {
	public ArrayList<Integer> getStories() {
		ArrayList<String> stories = new ArrayList<String>();
		
		Path dir = Paths.get("/opt/jetty/webapps/Story/stories");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path file: stream) {
				String name = file.getFileName().toString();
				int dotI = name.lastIndexOf('.');
				name = name.substring(0, dotI);
				stories.add(name);
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.err.println(x);
		}
		
		//return stories;
		return new ArrayList<Integer>(); //fix this later if file storage is needed again
	}
	
	public HashMap<String,Object> getStory(String storyName, int pageNumber) {
		HashMap<String,Object> data = new HashMap<String,Object>();
		File input;
		Document doc;
		Elements pages;
		
		try {
			input = new File("/opt/jetty/webapps/Story/stories/" + storyName + ".sty");
			doc = Jsoup.parse(input, "UTF-8");
			pages = doc.select("page");
		}
		catch (Exception e) {
			//print stack trace to log
			data.put("text", "Error fetching story contents");
			return data;
		}
		
		int pageCnt = pages.size();
		data.put("pages", pageCnt);
		if(pageNumber > pageCnt) pageNumber = 1;
		
		if(pageNumber <= pageCnt) {
			data.put("text", pages.get(pageNumber-1).html());
		}
		else {
			data.put("text", doc.html());
		}
		
		return data;
	}
}