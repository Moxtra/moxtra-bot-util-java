package com.moxtra.webapp;

import com.moxtra.bot.OAuth2;
import com.moxtra.examples.MyBot;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This servlet is to handle request to do Moxtra Bot provided features
 */
public class BotServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(BotServlet.class);
	private MyBot bot = null;
	private OAuth2 oauth2 = null;

    public void init(ServletConfig config) throws ServletException {
	    super.init(config);

	    try {
			Properties conf = new Properties();
			conf.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));

			// debug
			Enumeration<?> e = conf.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = conf.getProperty(key);
				logger.info("Key : " + key + ", Value : " + value);
			}

			bot = new MyBot(conf);
			bot.setGenericHandling(false);
			bot.setVerifyPostSignature(false);

	    	oauth2 = new OAuth2(conf);

	    } catch (Exception e) {
	    	logger.error("Unable to init Servlet!");
	    	throw new ServletException(e);
	    }
	}

	/**
	 * Handle Get request
	 * 1. Account Link
	 * 2. OAuth2 auth
	 * 3. OAuth2 callback
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {

		String requestURI = request.getRequestURI().substring(request.getContextPath().length());

		if ("/webhooks".equals(requestURI)) {
			try {
				bot.handleGetRequest(request, response);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServletException(e.getMessage());
			}
		} else if ("/auth".equals(requestURI)) {
			oauth2.auth(request, response);
		} else if ("/callback".equals(requestURI)) {
			oauth2.callback(request, response, bot);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
 	}

	/**
	 * The doPost method handles
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI().substring(request.getContextPath().length());

		if ("/webhooks".equals(requestURI)) {
			try {
				bot.handlePostRequest(request, response);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServletException(e.getMessage());
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}