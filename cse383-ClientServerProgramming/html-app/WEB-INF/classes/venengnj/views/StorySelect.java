package venengnj.views;

import freemarker.template.*;
import venengnj.handlers.Logger;
import venengnj.models.Story;
import venengnj.models.User;

import java.util.*;
import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

import java.net.*;

@SuppressWarnings("serial")
public class StorySelect extends HttpServlet {

	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			this.generatePage(request,out);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		try {
			this.generateLogin(req, out);
		}
		catch (Exception e) {
			e.printStackTrace(out);
		}
	}
	
	private Configuration getFMConfig() throws Exception {
		Configuration f = new Configuration(Configuration.VERSION_2_3_22);
		f.setDirectoryForTemplateLoading(new File("/opt/jetty/webapps/Story/templates"));
		f.setDefaultEncoding("UTF-8");
		return f;
	}
	
	private void generatePage(HttpServletRequest req, PrintWriter out) throws Exception {
		/* Create and adjust the configuration singleton */
		Configuration cfg = this.getFMConfig();
		HttpSession s = req.getSession();
		/* ------------------------------------------------------------------------ */    
		/* You usually do these for MULTIPLE TIMES in the application life-cycle:   */    

		/* Create a data-model */
		Map<String,Object> root = new HashMap<String,Object>();

		if(s.getAttribute("username") != null)
			root.put("username", s.getAttribute("username"));
		if(s.getAttribute("email") != null)
			root.put("email", s.getAttribute("email"));
		if(s.getAttribute("isAdmin") != null && (boolean)s.getAttribute("isAdmin"))
			root.put("isAdmin", true);
		
		//String pathInfo = req.getPathInfo();
		root.put("stories", Story.getStoryTitles());

		/* Get the template (uses cache internally) */
		Template temp = cfg.getTemplate("home.ftl");

		/* Merge data-model with template */
		temp.process(root, out);
		// Note: Depending on what `out` is, you may need to call `out.close()`.
		// This is usually the case for file output, but not for servlet output.
	}
	
	private void generateLogin(HttpServletRequest req, PrintWriter out) throws Exception {
		HttpSession s = req.getSession();
		String username = null, password = null;
		
		StringBuffer strbuf = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				strbuf.append(line);
		} catch (Exception e) {};
		
		try {
			Map<String, String> params = splitQuery(strbuf.toString());
			if(params.get("username") != null)
				username = params.get("username");
			if(params.get("password") != null)
				password = params.get("password");
			if(username != null && password != null) {
				User u = new User(username, password);
				if(u.isAuthenticated()) {
					s.setAttribute("isAdmin", u.isAdmin());
					s.setAttribute("username", username);
					s.setAttribute("email", User.emailFromUsername(username));
				}
			}
		}
		catch (Exception e) {
			Logger.log(getServletContext(), req, "Error logging in user " + username, true);
		}
		
		this.generatePage(req, out);
	}
	
	//copied/modified from http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}
}

