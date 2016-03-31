package com.ibm.shdwebportlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import com.shd.websso.M2KSSO;
import com.shd.websso.M2KSSOPortletSessionBean;
import com.shd.websso.SHAHashing;
import com.shd.websso.UserBean;

/**
 * A sample portlet based on GenericPortlet
 */
public class ShdWebPortlet extends GenericPortlet {

	public static final String JSP_FOLDER    = "/_shdWebPortlet/jsp/";    // JSP folder name

	public static final String VIEW_JSP      = "ShdWebPortletView";         // JSP file name to be rendered on the view mode
	
	public static final String SESSION_BEAN = "M2KSSOPortletSessionBean";

	// Parameter name for the remote SSO APP TYPE
	public static final String EDIT_DEFAULTS_SSOAPTYPE = "M2KSSOPortletEditDefaultsSSOType";
	// Parameter name for the remote SSO IFrame width
	public static final String EDIT_DEFAULTS_SSOWIDTH = "M2KSSOPortletEditDefaultsSSOWidth";
	// Parameter name for the remote SSO IFrame hheight
	public static final String EDIT_DEFAULTS_SSOHEIGHT = "M2KSSOPortletEditDefaultsSSOHeight";
	// Action name for submit form
	public static final String EDIT_DEFAULTS_SUBMIT = "M2KSSOPortletEditDefaultsSubmit";
	
	// Parameter name for the validation failed keys
	public static final String CONTEXT_SESSIONS_LIST = "M2KSSOActiveUsers";
	
	private M2KSSO m2ksso = M2KSSO.getInstance();
	private SHAHashing ss = SHAHashing.getInstance();
	/**
	 * @see javax.portlet.Portlet#init()
	 */
	public void init() throws PortletException{
		super.init();
	}

	/**
	 * Serve up the <code>view</code> mode.
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// Set the MIME type for the render response
		response.setContentType(request.getResponseContentType());
		
		System.out.println("======shdWeb doView=====");
		
		// Check if portlet session exists
		M2KSSOPortletSessionBean sessionBean = getSessionBean(request);
		if (sessionBean == null) {
			response.getWriter().println("<b>NO PORTLET SESSION YET, PLEASE LOGIN AT FIRST!</b>");
			return;
		}
		
		// get Login user name
		PortletSession portletSession = request.getPortletSession();
		String portletSessionId = portletSession.getId();

		System.out.println("shdWeb portletSessionId: " + portletSessionId);

		String uid = request.getUserPrincipal().getName();
		portletSession.setAttribute("uid", uid);
		System.out.println("shdWeb uid: " + uid);

	  	m2ksso.setId("userid");
    	m2ksso.setKey("key");
	  		  	
		// get HTTP Servlet Request Object
		HttpServletRequest httpreq = (HttpServletRequest) (com.ibm.ws.portletcontainer.portlet.PortletUtils
				.getHttpServletRequest(request));
		if (httpreq != null) {
			String reqSessionId = httpreq.getRequestedSessionId();
			System.out.println("shdWeb reqSessionId: " + reqSessionId);
			String httpSessionId = httpreq.getSession().getId();
			System.out.println("shdWeb httpSessionId: " + httpSessionId);
			UserBean ub = new UserBean(uid, httpSessionId, portletSessionId);
			System.out.println("shdWeb ub=" + ub);
			String key = ss.hashing(uid + httpSessionId + portletSessionId);
			System.out.println("shdWeb key=" + key);
			ub.setHash(key);
			m2ksso.registerUser(uid, ub);
		}else{
			System.out.println("httpreq is null");
		}
		
		// Invoke the JSP to render
		PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(getJspFilePath(request, VIEW_JSP));
		rd.include(request,response);
	}


	
	/**
	 * Process an action request.
	 * 
	 * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, java.io.IOException {
	
	}

	/**
	 * Returns JSP file path.
	 * 
	 * @param request Render request
	 * @param jspFile JSP file name
	 * @return JSP file path
	 */
	private static String getJspFilePath(RenderRequest request, String jspFile) {
		String markup = request.getProperty("wps.markup");
		if( markup == null )
			markup = getMarkup(request.getResponseContentType());
		return JSP_FOLDER + markup + "/" + jspFile + "." + getJspExtension(markup);
	}

	/**
	 * Convert MIME type to markup name.
	 * 
	 * @param contentType MIME type
	 * @return Markup name
	 */
	private static String getMarkup(String contentType) {
		if( "text/vnd.wap.wml".equals(contentType) )
			return "wml";
        else
            return "html";
	}

	/**
	 * Returns the file extension for the JSP file
	 * 
	 * @param markupName Markup name
	 * @return JSP extension
	 */
	private static String getJspExtension(String markupName) {
		return "jsp";
	}
	
	
	/**
	 * Get SessionBean.
	 * 
	 * @param request
	 *            PortletRequest
	 * @return M2KSSOPortletSessionBean
	 */
	private static M2KSSOPortletSessionBean getSessionBean(PortletRequest request) {
		PortletSession session = request.getPortletSession();
		if (session == null)
			return null;
		M2KSSOPortletSessionBean sessionBean = (M2KSSOPortletSessionBean) session.getAttribute(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new M2KSSOPortletSessionBean();
			session.setAttribute(SESSION_BEAN, sessionBean);
		}
		return sessionBean;
	}
	
}
