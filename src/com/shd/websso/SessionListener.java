package com.shd.websso;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ibm.shdwebportlet.ShdWebPortlet;

/**
 * Application Lifecycle Listener implementation class SessionListener
 *
 */
public class SessionListener implements HttpSessionListener {

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext context = session.getServletContext();
		HashMap activeUsers = null;
		if (context.getAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST) != null){
			activeUsers = (HashMap) context.getAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST);
		}else{
			activeUsers = new HashMap();
		}
		System.out.println(this.getClass().getName()+":sessionId:" + session.getId());
		activeUsers.put(session.getId(), session);
		System.out.println(this.getClass().getName()+":activeUsers:" + activeUsers);
		context.setAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST, activeUsers);
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext context = session.getServletContext();
		HashMap activeUsers = (HashMap) context.getAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST);
		activeUsers.remove(session.getId());
		context.setAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST, activeUsers);
	}

	
}
