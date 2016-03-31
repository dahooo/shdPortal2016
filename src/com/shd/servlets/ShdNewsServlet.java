package com.shd.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.portal.um.PumaProfile;
import com.shd.constants.PaginationConstants;
import com.shd.core.entity.ExTbNewsModelMEntity;
import com.shd.core.service.ShdNewsService;
import com.shd.core.utils.DeptNode;
import com.shd.utils.AjaxConverter;
import com.shd.utils.ImRequestUtils;
import com.shd.utils.JSONUtils;

/**
 * Servlet implementation class ShdNewsServlet
 */
public class ShdNewsServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
      
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ShdNewsService shdNewsService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShdNewsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONUtils jsonUtils = new JSONUtils();
		
		String uid = "";
		try {

			
			PumaProfile pProfile = pumaHome.getProfile();
			
	        if(pProfile!=null){
		        
	        	List<String> attribNames = new ArrayList<String>();
	        	attribNames.add("cn");
	        	attribNames.add("uid");
				Map<String, Object> map = pProfile.getAttributes(pProfile.getCurrentUser(),attribNames);
				String userCnFromPuma = (String)map.get("cn");
				uid = (String)map.get("uid");
				if(userCnFromPuma != null) {
					System.out.println("userCnFromPuma: " + userCnFromPuma);
					System.out.println("uid: " + uid);
				} else {
					System.out.println("userCnFromPuma from PUMA is null!");
					throw new Exception("請登入網站!");
				}
			}else{
				throw new Exception("請登入網站!");
			}
			
			
	        String empNo = uid;
	        ImRequestUtils imRequestUtils = new ImRequestUtils(request);
			String action = imRequestUtils.getString("action");
		
			
			//=====
			
			if("getBulletinBoardById".equals(action)){
				logger.debug("run getBulletinBoardById");
				String NEWS_M_ID = imRequestUtils.getString("NEWS_M_ID");
				try {
					ExTbNewsModelMEntity etb = shdNewsService.getNewsById(new Long(NEWS_M_ID));
					List<ExTbNewsModelMEntity> etfile = shdNewsService.getFileData(new Long(NEWS_M_ID));
					
					jsonUtils.put("data",etb);
					jsonUtils.put("fileData",etfile);
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("saveBulletinBoardData".equals(action)){
				logger.debug("run saveBulletinBoardData");
				ExTbNewsModelMEntity param = (ExTbNewsModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbNewsModelMEntity.class);
				param.setCREATED_ID(empNo);
				param.setUPDATED_ID(empNo);
				
				try {
					if(param.getNEWS_M_ID() != null ){
						shdNewsService.updateExTbNewsModelM(param);
					}else{
						shdNewsService.saveExTbNewsModelM(param);
					}
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, new Exception("儲存失敗!"));
				}
			}
			
			
			if("getBulletinBoardTypeList".equals(action)){
				logger.debug("run getBulletinBoardTypeList");
				try {
					jsonUtils.put("data",shdNewsService.getNewsTypeList(new Long(2)));
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			if("getBulletinBoardPagination".equals(action)){
				
				logger.debug("run getBulletinBoardPagination");

				try {
					int currentPage = imRequestUtils.getCurrentPage();
					int pageSize = imRequestUtils.getPageSize();
					String authorName = imRequestUtils.getString("authorName");
					Date startDate = imRequestUtils.getStartDate("CREATED_DATE_STAR");
					Date endDate = imRequestUtils.getStartDate("CREATED_DATE_END");
					Long newsType = imRequestUtils.getLong("newsType");
					String isFilterByEndDate = imRequestUtils.getString("isFilterByEndDate");
					
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdNewsService.getBulletinBoardPagination(isFilterByEndDate,empNo,authorName,startDate,endDate,newsType,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){
					ex.printStackTrace();
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			if("getBulletinBoardAdminPagination".equals(action)){
				
				logger.debug("run getBulletinBoardAdminPagination");

				try {
					int currentPage = imRequestUtils.getCurrentPage();
					int pageSize = imRequestUtils.getPageSize();
					String authorName = imRequestUtils.getString("authorName");
					Date startDate = imRequestUtils.getStartDate("CREATED_DATE_STAR");
					Date endDate = imRequestUtils.getStartDate("CREATED_DATE_END");
					Long newsType = imRequestUtils.getLong("newsType");
					
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdNewsService.getBulletinBoardAdminPagination(authorName,startDate,endDate,newsType,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){
					ex.printStackTrace();
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			
			
			//=====
			
			
			if("deleteNews".equals(action)){
				logger.debug("run deleteNews");
				String NEWS_M_ID = imRequestUtils.getString("NEWS_M_ID");
				try {
					shdNewsService.deleteNews(new Long(NEWS_M_ID),empNo);
					
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("deleteFileById".equals(action)){
				logger.debug("run deleteFileById");
				String FILE_M_ID = imRequestUtils.getString("FILE_M_ID");
				try {
					shdNewsService.deleteFileById(new Long(FILE_M_ID));
					
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("getNewsById".equals(action)){
				logger.debug("run getNewsById");
				String NEWS_M_ID = imRequestUtils.getString("NEWS_M_ID");
				try {
					ExTbNewsModelMEntity etb = shdNewsService.getNewsById(new Long(NEWS_M_ID));
					List<ExTbNewsModelMEntity> etfile = shdNewsService.getFileData(new Long(NEWS_M_ID));
					
					jsonUtils.put("data",etb);
					jsonUtils.put("fileData",etfile);
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			
			
			if("saveNewsData".equals(action)){
				logger.debug("run saveNewsData");
				ExTbNewsModelMEntity param = (ExTbNewsModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbNewsModelMEntity.class);
				param.setCREATED_ID(empNo);
				param.setUPDATED_ID(empNo);
				
				try {
					if(param.getNEWS_M_ID() != null ){
						shdNewsService.updateExTbNewsModelM(param);
					}else{
						shdNewsService.saveExTbNewsModelM(param);
					}
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, new Exception("儲存失敗!"));
				}
			}
			
			
			
			if("getNewsTypeList".equals(action)){
				logger.debug("run getNewsTypeList");
				try {
					jsonUtils.put("data",shdNewsService.getNewsTypeList(new Long(1)));
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			if("getNewsPagination".equals(action)){
				logger.debug("run getNewsPagination");

				try {
					int currentPage = imRequestUtils.getCurrentPage();
					int pageSize = imRequestUtils.getPageSize();
					String authorName = imRequestUtils.getString("authorName");
					Date startDate = imRequestUtils.getStartDate("CREATED_DATE_STAR");
					Date endDate = imRequestUtils.getStartDate("CREATED_DATE_END");
					Long newsType = imRequestUtils.getLong("newsType");
					String isFilterByEndDate = imRequestUtils.getString("isFilterByEndDate");

					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdNewsService.getNewsPagination(isFilterByEndDate,authorName,startDate,endDate,newsType,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			if("getALLDeptList".equals(action)){
				logger.debug("run getALLDeptList");
				String NEWS_M_ID = imRequestUtils.getString("NEWS_M_ID");
				List<DeptNode> n50List = shdNewsService.getALLDeptList(NEWS_M_ID);
				
				jsonUtils.put("data",n50List);
				jsonUtils.processSuccess(response);
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

}
