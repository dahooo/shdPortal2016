package com.shd.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.portal.um.PumaProfile;
import com.shd.utils.ImRequestUtils;

/**
 * Servlet implementation class ImageServlet
 */
public class FileServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
			ImRequestUtils imRequestUtils = new ImRequestUtils(request);
			String fileName = imRequestUtils.getString("fileName");
			String isTemp = request.getParameter("isTemp");
			String isDownload = request.getParameter("isDownload");
	
			String filesRoot = (String) imConfiguration.get("filesRoot");
			
			if("true".equals(isDownload)){
				String filePath = filesRoot+File.separator+fileName;
				this.getFile(response, filePath, fileName);
			}else if("true".equals(isTemp)){
				String filePath = filesRoot+File.separator+"temp"+File.separator+fileName;
				getIMG(response, filePath);
			}else{
				String filePath = filesRoot+File.separator+fileName;
				getIMG(response, filePath);
			}
		
		}catch(Exception ex){
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	
	
	private void getFile(HttpServletResponse response, String filePath,String fileName)
			throws IOException {		
		
		if("pdf".equals(getExtension(fileName))|| "PDF".equals(getExtension(fileName))){
			response.setContentType("application/pdf");
		}else{
			response.setContentType("application/force-download");
		}
		
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");   
        File my_file = new File(filePath);
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(my_file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
           out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
        out.close();
	}
	
	
	private String getExtension(String fileName){
        int startIndex = fileName.lastIndexOf(46) + 1;
        int endIndex = fileName.length();
        return  fileName.substring(startIndex, endIndex);
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
