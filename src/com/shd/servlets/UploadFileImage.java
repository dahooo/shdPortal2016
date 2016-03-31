package com.shd.servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imgscalr.Scalr;

import com.ibm.portal.um.PumaProfile;
import com.shd.utils.JSONUtils;
import com.shd.utils.UUIDHexGenerator;

public class UploadFileImage extends BaseServlet  {
	private static final long serialVersionUID = 1L;
	
	private final long MAX_TRANS_SIZE = 5242880;//5M
	private final int MAX_WIDTH = 800;
	private final String [] allowedImageExt = new String [] {"gif", "jpg" ,"png","GIF", "JPG" ,"PNG"};
	
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		JSONUtils jsonUtils = new JSONUtils();
		String filesRoot = (String) imConfiguration.get("filesRoot");
		String UPLOAD_FILE_DIRECTORY = filesRoot+"\\temp";
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
			
			
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {		
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				List<FileItem> multiparts = upload.parseRequest(request);
				for (FileItem item : multiparts) {
					if (!item.isFormField()) {
						String uuid = new UUIDHexGenerator().generate();
						
						String fileName = item.getName();
						BufferedImage im = ImageIO.read(item.getInputStream());
						String t_ext = fileName.substring (fileName.lastIndexOf (".") + 1);//附檔名
						
						String extResult = "";
						for(int i=0; i < allowedImageExt.length ;i++){
							if(allowedImageExt[i].equals(t_ext)){
								extResult = t_ext;
								break;
							}
						}
						
						if(extResult.isEmpty()){//非圖片
							if(item.getSize() > 0){
								if(item.getSize() > MAX_TRANS_SIZE){
									jsonUtils.processFail(response, new Exception("檔案太大請重新壓縮上傳!"));
								}else{//儲存檔案
									item.write(new File(UPLOAD_FILE_DIRECTORY + File.separator + uuid + "." + t_ext));
								}
							}else{
								jsonUtils.processFail(response, new Exception("請上傳檔案!"));
							}
						}else if(im.getWidth() > MAX_WIDTH || item.getSize() > MAX_TRANS_SIZE){
							//縮圖
							BufferedImage thumb = Scalr.resize(im, MAX_WIDTH);
							ImageIO.write(thumb, extResult, new File(UPLOAD_FILE_DIRECTORY + File.separator + uuid + "." + t_ext ));
						}else{
							item.write(new File(UPLOAD_FILE_DIRECTORY + File.separator + uuid + "." + t_ext));
						}
						jsonUtils.put("fileName",uuid);
						jsonUtils.put("ext",t_ext);	
						jsonUtils.put("msg","檔案上傳成功!");	

						jsonUtils.processSuccess(response);
					}
				}
			}else{
				jsonUtils.processFail(response, new Exception("檔案上傳失敗!"));
			}
		}catch (Exception e){
			e.printStackTrace();
			jsonUtils.processFail(response, new Exception("檔案上傳失敗!"));
		}
		
	}
	

}