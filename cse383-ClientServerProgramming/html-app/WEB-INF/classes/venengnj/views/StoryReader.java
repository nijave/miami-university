package venengnj.views;

import freemarker.template.*;
import venengnj.handlers.Logger;
import venengnj.models.*;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

@SuppressWarnings("serial")
public class StoryReader extends HttpServlet {

	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			this.generatePage(request,out);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

	private Configuration getFMConfig() throws Exception {
		Configuration f = new Configuration(Configuration.VERSION_2_3_22);
		f.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/Story/templates"));
		f.setDefaultEncoding("UTF-8");
		return f;
	}
	
	/**
	 * 
	 * @param req
	 * @param out
	 * @throws Exception
	 */
	private void generatePage(HttpServletRequest req, PrintWriter out) throws Exception {
		/* Create and adjust the configuration singleton */
		Configuration cfg = this.getFMConfig();

		HashMap<String,Object> data;
		Template temp;
		int pageNumber = 1;
		
		String story = null;
		try {
			story = req.getPathInfo().substring(1).split("/")[0];
			pageNumber = Integer.parseInt(req.getPathInfo().substring(1).split("/")[1]);
		}
		catch (Exception e){};
		
		if(story == null || !Story.getStoryTitles().contains(story)) {
			//data = displayError(req);
			data = new HashMap<String,Object>();
			data.put("data", story);
			Logger.log(getServletContext(), req, "fetching '" + story + "'", true);
			temp = cfg.getTemplate("error.ftl");
		}
		else {
			data = displayStory(story, pageNumber);
			temp = cfg.getTemplate("story.ftl");
			this.paginate(data, story, pageNumber, ((Integer)data.get("pages")).intValue());
			Logger.log(getServletContext(), req, "fetching '" + story + "'", false);
		}
		
		/* Merge data-model with template */
		temp.process(data, out);
		// Note: Depending on what `out` is, you may need to call `out.close()`.
		// This is usually the case for file output, but not for servlet output.
	}
	
	private HashMap<String,Object> displayStory(String storyName, int pageNumber) throws IOException {
		if(pageNumber < 1) pageNumber = 1; // Handle negative page number inputs
		Story s = Story.storyFromTitle(storyName);
		return s.getPage(pageNumber);
	}
	
	/**
	 * handles story pagination
	 * @param data hashmap used by freemarker - links are added to this if applicable
	 * @param storyName name of the story needed for the url
	 * @param currentPage of the story
	 * @param maxPage how many pages long the story is
	 */
	private void paginate(HashMap<String,Object> data, String storyName, int currentPage, int maxPage) {
		if(currentPage > maxPage) currentPage = 1; // Handle page numbers that are larger than the story
		String baseLink = "/Story/read/" + storyName + "/"; // create base link
		if(maxPage > 1) { //if the story has multiple pages, create necessary links
			if(currentPage < maxPage) {
				//there is a next page
				data.put("nextLink", baseLink + (currentPage + 1));
			}
			if(currentPage > 1) {
				//there is a previous page
				data.put("prevLink", baseLink + (currentPage - 1));
			}
		}
	}
}

