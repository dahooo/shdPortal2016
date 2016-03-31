package com.shd.servlets;

import java.awt.Graphics2D;
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
import org.imgscalr.Scalr.Rotation;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.ibm.portal.um.PumaProfile;
import com.shd.utils.JSONUtils;
import com.shd.utils.UUIDHexGenerator;

public class UploadFile extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final long MAX_TRANS_SIZE = 5242880;
	private final int MAX_WIDTH = 800;
	private final String [] allowedExt = new String [] {"gif", "jpg","jpeg" ,"png","GIF", "JPG" ,"PNG","JPEG"};

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		JSONUtils jsonUtils = new JSONUtils();
		String imageRoot = (String) imConfiguration.get("imageRoot");
		String UPLOAD_DIRECTORY = imageRoot+"\\Image\\temp";
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
						String t_ext = fileName.substring (fileName.lastIndexOf (".") + 1);
						String extResult = "";
						for(int i=0; i < allowedExt.length ;i++){
							if(allowedExt[i].equals(t_ext)){
								extResult = t_ext;
								break;
							}
						}
						if(extResult.isEmpty()){
							jsonUtils.processFail(response, new Exception("請上傳圖檔!(JPG/GIF/PNG/JPEG)"));
						}else if(im.getWidth() > MAX_WIDTH || item.getSize() > MAX_TRANS_SIZE){
							//縮圖
							BufferedImage thumb = Scalr.resize(im, MAX_WIDTH);
							Metadata metadata = ImageMetadataReader.readMetadata(item.getInputStream());
							BufferedImage destinationImage = checkExif(metadata, thumb); 
							ImageIO.write(destinationImage, extResult, new File(UPLOAD_DIRECTORY + File.separator + uuid + "." + t_ext ));
						}else{
							Metadata metadata = ImageMetadataReader.readMetadata(item.getInputStream());
							BufferedImage destinationImage = checkExif(metadata, im); 
							ImageIO.write(destinationImage, t_ext, new File(UPLOAD_DIRECTORY + File.separator + uuid + "." + t_ext ));
						}
						jsonUtils.put("fileName",uuid + "." + t_ext);	
						jsonUtils.put("msg","圖片上傳成功!");	
						jsonUtils.processSuccess(response);
					}
				}
			}else{
				jsonUtils.processFail(response, new Exception("圖片上傳失敗!"));
			}
		}catch (Exception e){
			e.printStackTrace();
			jsonUtils.processFail(response, new Exception("圖片上傳失敗!"));
		}
		
	}
	
	
	
	private BufferedImage checkExif(Metadata metadata, BufferedImage im) throws ImageProcessingException, IOException {
		ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
		int orientation = 1;
		try {
			orientation = exifIFD0Directory
					.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		System.out.println(orientation+":=====orientation");
		BufferedImage destinationImage = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());

		if(orientation == 6){
			destinationImage = Scalr.rotate(im, Rotation.CW_90);
			return destinationImage;
		}else{
			Graphics2D graphics = (Graphics2D) destinationImage.getGraphics();
			graphics.drawImage(im, 0, 0, im.getWidth(), im.getHeight(), null);
			return destinationImage;
		}
		
	}
	

}