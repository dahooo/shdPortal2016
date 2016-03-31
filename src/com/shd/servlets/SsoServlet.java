package com.shd.servlets;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.util.Base64;

import com.shd.utils.ImRequestUtils;

/**
 * Servlet implementation class SsoServlet
 */
public class SsoServlet  extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SsoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ImRequestUtils imRequestUtils = new ImRequestUtils(request);
		
		String action = request.getParameter("action");
		
		if("logout".equals(action)){
			String contextPath= "http://ibmportalweb.shiseido.com.tw/wps/myportal/!ut/p/z1/04_Sj9CPykssy0xPLMnMz0vMAfIjo8zi_d1NnD2MDAx9LMydTA0cQ02dXEPNQgwMTMz0wwkpiAJKG-AAjgZA_VFgJbhMsDCDKsBjRkFuhEGmo6IiAKNCTYE!/dz/d5/L3dDb1ZJQSEhL3dPb0JKTnNBLzRFS2lqaFVNV0hFIS9MeDQ6UXVtWnFwUS8xMTA4L2xv/";
		    response.sendRedirect(response.encodeRedirectURL(contextPath));
		}else{
			String userid = imRequestUtils.getString("userid");
			String pwd = imRequestUtils.getString("pwd");
			Base64 base64 = new Base64();
			String pass = new String(base64.decode(pwd), "UTF-8");
			String contextPath= "http://ibmportalweb.shiseido.com.tw/wps/portal/cxml/04_SD9ePMtCP1I800I_KydQvyHFUBADPmuQy?userid="+URLEncoder.encode(userid, "UTF-8")+"&password="+URLEncoder.encode(pass, "UTF-8");
		    response.sendRedirect(response.encodeRedirectURL(contextPath));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
