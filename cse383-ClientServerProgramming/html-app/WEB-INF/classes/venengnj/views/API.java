package venengnj.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import venengnj.models.*;

@SuppressWarnings("serial")
public class API extends HttpServlet {
	private String[] getParts (HttpServletRequest req) {
		return req.getPathInfo().split("/");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String output = "{}";
		// split URL into parts
		String objects[] = getParts(request);
		// /story/rest/storylist ->
		if (objects.length <= 1) {
			// no object specified
			sendJSONError(response, "invalid request - no API specified");
			return;
		}

		// see if they are asking for a key
		if ("getkey".equals(objects[1])) {
			try {
				String key = (new RestKey()).getKey();	
				output = (new JSONObject()).put("key", key).toString();
			} catch (Exception e) {
				sendJSONError(response, "Application error. Couldn't create key.");
				return;
			}
		}
		
		if(output.length() == 2) {
			//System.err.println(objects);
			// minimum is /story/rest/APIKEY/command which is 5
			if (objects.length <= 2) {
				sendJSONError(response, "No key specified or invalid format");
				return;
			}
	
			// validate key
			try {
				RestKey restKey = new RestKey(objects[1]);
				if (!restKey.validate())
					throw new Exception("invalid key");
			} catch (Exception err) {
				sendJSONError(response, "INVALID KEY");
				return;
			}
	
			if ("storylist".equals(objects[2])) {
				JSONArray jArr = new JSONArray();
				ArrayList<Story> storyList = Story.getStoryList();
				System.err.println("list size: " + storyList.size());
				for(Story s: storyList) {
					JSONObject st = new JSONObject();
					st.put("pk", s.getId());
					st.put("title", s.getTitle());
					System.err.println(st.toString());
					jArr.put(st);
				}
				JSONObject jOut = new JSONObject();
				jOut.put("StoryList", jArr);
				output = jOut.toString();
			} else if ("story".equals(objects[2])) {
				try {
					Story s = new Story(Integer.parseInt(objects[3]));
					JSONObject story = new JSONObject();
					if(objects.length == 5) {
						int pageNum = -1;
						try {
							pageNum = Integer.parseInt(objects[4]);
							if(pageNum > s.getNumberOfPages() || pageNum < 1)
								throw new IllegalArgumentException();
						} catch (IllegalArgumentException e) {
							sendJSONError(response, "Invalid page specified");
							return;
						}
						story.put("contents", s.getPageAsString(pageNum));
					}
					else {
						JSONArray pages = new JSONArray();
						for(int i = 1; i <= s.getNumberOfPages(); i++) {
							pages.put(s.getPageAsString(i));
						}
						story.put("pages", pages);
						story.put("pk", s.getId());
						story.put("title", s.getTitle());
						story.put("numpages", s.getNumberOfPages());
					}
					output = story.toString();
				}
				catch (IllegalArgumentException e) {
					sendJSONError(response, "Invalid story specified");
					return;
				}
			} else if ("delete".equals(objects[2])) {
				JSONObject results = new JSONObject();
				try {
					Story s = new Story(Integer.parseInt(objects[3]));
					s.delete();
					results.put("Status", "SUCCESS");
				}
				catch (Exception e) {
					results.put("Error", e.getMessage());
				}
				output = results.toString();
			} else if ("edit".equals(objects[2]) && objects.length == 5) {
				int storyNum = Integer.parseInt(objects[3]);
				int pageNum = Integer.parseInt(objects[4]);
				Story s = new Story(storyNum);
				StringBuffer strbuf = new StringBuffer();
				String line = null;
				try {
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null)
						strbuf.append(line);
				} catch (Exception e) {};
				output = strbuf.toString();
			} else {
				sendJSONError(response, "Command not recogonized");
				return;
			}
		}
		response.setStatus(200);
		response.setContentType("application/json");
		out.write(output);
	}

	/**
	 * Just take care of everything in doGet for organization and cleanliness purposes
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void sendJSONError(HttpServletResponse response, String msg) throws IOException {
		PrintWriter out = response.getWriter();
		response.setStatus(400);
		response.setContentType("application/json");

		JSONObject json = new JSONObject();
		json.put("error", msg);
		out.print(json.toString());
	}
}
