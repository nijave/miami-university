package venengnj.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class Logger {
	public static void log(ServletContext ctx, HttpServletRequest req, String message, boolean isError) {
		//String type = "INFO";
		//if(isError) type = "ERROR";
		ctx.log(getUser(req) + "," + getEmail(req) + "," + getIP(req) + " - " + message + "\n");
	}
	
	public static void log(String message) {
		System.err.println(message);
	}
	
	private static String getIP(HttpServletRequest req) {
		return req.getRemoteAddr();
	}
	
	private static String getUser(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		if(username == null)
			return "";
		else
			return username;
	}
	
	private static String getEmail(HttpServletRequest req) {
		String email = (String) req.getSession().getAttribute("email");
		if(email == null)
			return "";
		else
			return email;
	}
}