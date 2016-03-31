package com.shd.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.portal.um.PumaProfile;
import com.shd.utils.ImRequestUtils;
import com.shd.utils.JSONUtils;

/**
 * Servlet implementation class ImageServlet
 */
public class ImageServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONUtils jsonUtils = new JSONUtils();
		try {
			
			PumaProfile pProfile = pumaHome.getProfile();
	        if(pProfile!=null){
		        
	        	List<String> attribNames = new ArrayList<String>();
	        	attribNames.add("cn");
	        	attribNames.add("uid");
				Map<String, Object> map = pProfile.getAttributes(pProfile.getCurrentUser(),attribNames);
				String userCnFromPuma = (String)map.get("cn");
				if(userCnFromPuma != null) {
					System.out.println("userCnFromPuma: " + userCnFromPuma);
				} else {
					System.out.println("userCnFromPuma from PUMA is null!");
					throw new Exception("請登入網站!");
				}
			}else{
				throw new Exception("請登入網站!");
			}

			String imageName = request.getParameter("imageName");
			String isTemp = request.getParameter("isTemp");
			
			String isUrl = request.getParameter("isUrl");
			String urlName = request.getParameter("urlName");
			
			String imageRoot = (String) imConfiguration.get("imageRoot");
			if("true".equals(isUrl)){
				String url = (String) imConfiguration.get(urlName);
				jsonUtils.put("data",url);
				jsonUtils.processSuccess(response);
			}else if("true".equals(isTemp)){
				String filePath = imageRoot+"\\Image\\temp\\"+imageName;
				getIMG(response, filePath);
			}else{
				String filePath = imageRoot+"\\Image\\"+imageName;
				getIMG(response, filePath);
			}
		}catch(Exception ex){
			jsonUtils.processFail(response, ex);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	
	
	private void getIMG(HttpServletResponse response, String filePath)
		throws IOException {
		InputStream fis = new FileInputStream(new File(filePath));
		
		BufferedInputStream bis = new BufferedInputStream(fis);
		response.setContentType("image/jpeg");
		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buffer = new byte[1024];
		int length = bis.read(buffer);
		while (length != -1) {
			bos.write(buffer, 0, length);
			length = bis.read(buffer);
		}
		fis.close();
		bos.flush();
		bos.close();
	}

}
