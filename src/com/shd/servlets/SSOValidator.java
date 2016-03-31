package com.shd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.shdwebportlet.ShdWebPortlet;
import com.shd.websso.M2KSSO;
import com.shd.websso.SHAHashing;
import com.shd.websso.UserBean;

/**
 * Servlet implementation class SSOValidator
 */
public class SSOValidator extends BaseServlet {
	private static final long serialVersionUID = 1L;
      
	private M2KSSO m2ksso = M2KSSO.getInstance();
	private SHAHashing ss = SHAHashing.getInstance();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SSOValidator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		String ssoId = request.getParameter("userid");
		String ssoKey = request.getParameter("key");
		
		String mail2000_url_176 = (String) imConfiguration.get("mail2000_url_176");
		String mail2000_url_177 = (String) imConfiguration.get("mail2000_url_177");
		
		if("check".equals(action)){
			validator(request, response,ssoId,ssoKey);
		}else{
			PrintWriter out = response.getWriter();
			
			if(requestCheck(mail2000_url_176,ssoId,ssoKey).contains("OK")){
				out.println("OK");
			}else if(requestCheck(mail2000_url_177,ssoId,ssoKey).contains("OK")){
				out.println("OK");
			}else{
				out.println("Unauthenticated");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	private String requestCheck(String ip,String ssoId, String ssoKey){
		String respond = "";
        String gateway_url = "http://"+ip+":10039/wps/PA_shdWeb/SSOValidator?action=check&userid="+ssoId+"&key="+ssoKey;
        System.out.println("gateway_url===" + gateway_url);
        
        HttpURLConnection conn = null;

        try
        {
            URL url = new URL(gateway_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-agent","Mozilla/5.0 (Linux; Android 4.2.1; Nexus 7 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
            conn.connect();

            BufferedReader reader = new BufferedReader(new  InputStreamReader(conn.getInputStream()));
            String lines = "";

            while  ((lines  =  reader.readLine())  !=  null ){
                respond += lines;
            }

            reader.close();
            conn.disconnect();
        } catch (Exception e) {
            //e.printStackTrace();
            respond = e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            conn =null;
        }
		return respond;
	}
	
	
	protected void validator(HttpServletRequest request, HttpServletResponse response, String ssoId, String ssoKey) throws ServletException,
	IOException {
		response.setContentType(request.getContentType());
		
		System.out.println("===validatorServlet===start====");
		
		UserBean ub = m2ksso.getUserBean(ssoId);
		System.out.println("ub=" + ub);
		String key = ss.hashing(ssoId + ub.getHttpSessionId() + ub.getportletSessionId());
		System.out.println("hash=" + key);
		System.out.println("ssoKey=" + ssoKey);

		ServletContext context = request.getSession().getServletContext();
		HashMap activeUsers = null;
		if (context.getAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST) != null){
			activeUsers = (HashMap) context.getAttribute(ShdWebPortlet.CONTEXT_SESSIONS_LIST);
		}
		System.out.println(this.getClass().getName() + ":activeUsers:" +activeUsers);

		PrintWriter out = response.getWriter();
		if (activeUsers.get(ub.getHttpSessionId()) != null && (key.equals(ssoKey))){
			out.println("OK");
		}else{
			out.println("Unauthenticated");
		}
	}
	
}
