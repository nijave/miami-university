package venengnj.views;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.template.Configuration;
import freemarker.template.Template;
import venengnj.models.RestKey;

@SuppressWarnings("serial")
public class Admin extends HttpServlet {

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
		Template temp =  cfg.getTemplate("admin.ftl");
		HttpSession s = req.getSession();
		
		HashMap<String,Object> data = new HashMap<String,Object>();
		
		//The template won't display anything if key isn't set
		if(s.getAttribute("isAdmin")!= null && (boolean)s.getAttribute("isAdmin") == true) {
			data.put("key", (new RestKey()).getKey());
		}	
		
		/* Merge data-model with template */
		temp.process(data, out);
		// Note: Depending on what `out` is, you may need to call `out.close()`.
		// This is usually the case for file output, but not for servlet output.
	}
}

