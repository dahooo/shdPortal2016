package com.shd.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.portal.um.PumaProfile;
import com.shd.constants.PaginationConstants;
import com.shd.core.entity.ExTbBcModelMEntity;
import com.shd.core.enums.BcActionEnum;
import com.shd.core.service.ShdBcService;
import com.shd.utils.AjaxConverter;
import com.shd.utils.ImRequestUtils;
import com.shd.utils.ImStringUtils;
import com.shd.utils.JSONUtils;

/**
 * Servlet implementation class ShdBcServlet
 */
public class ShdBcServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ShdBcService shdBcService;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShdBcServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
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
	
			
			if("checkHasMail".equals(action)){
				logger.debug("run checkHasMail");
				
				try {
					boolean result = shdBcService.checkHasMailByUid(uid);
					
					
					jsonUtils.put("data",result);
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("getHistoryLog".equals(action)){
				logger.debug("run getHistoryLog");
				String BC_M_ID = imRequestUtils.getString("BC_M_ID");
				try {
					List<ExTbBcModelMEntity> list = shdBcService.getHistoryLog(BC_M_ID);
					for(ExTbBcModelMEntity etb:list){
						ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(etb.getUSER_ID());
						etb.setEmpCname(ett.getEmpCname());
					}
					jsonUtils.put("data",list);
					jsonUtils.processSuccess(response);
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("getExTbBcModelMById".equals(action)){
				logger.debug("run getExTbBcModelMById");
				String BC_M_ID = imRequestUtils.getString("BC_M_ID");
				ExTbBcModelMEntity etb = shdBcService.getExTbBcModelMById(BC_M_ID);
				ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(empNo);
				
				jsonUtils.put("data",etb);
				jsonUtils.put("levelData",ett);
				jsonUtils.processSuccess(response);
			}
			
			if("updateExTbBcModelMStatus".equals(action)){
				logger.debug("run updateExTbBcModelMStatus");
				ExTbBcModelMEntity param = (ExTbBcModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbBcModelMEntity.class);
				try {
					param.setEmpNo(empNo);
					if(shdBcService.updateExTbBcModelMStatus(param)){
						jsonUtils.processSuccess(response);
					}else{
						jsonUtils.processFail(response, new Exception("變更失敗!"));
					}
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			if("updateExTbBcModelM".equals(action)){
				logger.debug("run updateExTbBcModelM");
				ExTbBcModelMEntity param = (ExTbBcModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbBcModelMEntity.class);
				
				try {
					param.setEmpNo(empNo);
					param.setEDIT_STATUS(BcActionEnum.action_7.getValue());
					if(shdBcService.updateExTbBcModelM(param)){
						jsonUtils.processSuccess(response);
					}else{
						jsonUtils.processFail(response, new Exception("儲存失敗!"));
					}
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			
			if("saveExTbBcModelM".equals(action)){
				logger.debug("run saveExTbBcModelM");
				ExTbBcModelMEntity param = (ExTbBcModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbBcModelMEntity.class);
				try {
					param.setEmpNo(empNo);
					if(ImStringUtils.isNotBlank(param.getBC_M_ID())){
						param.setEDIT_STATUS(BcActionEnum.action_7.getValue());
						if(shdBcService.updateExTbBcModelM(param)){
							jsonUtils.processSuccess(response);
						}else{
							jsonUtils.processFail(response, new Exception("儲存失敗!"));
						}
					}else{
						param.setEDIT_STATUS(BcActionEnum.action_1.getValue());
						if(shdBcService.saveExTbBcModelM(param)){
							jsonUtils.processSuccess(response);
						}else{
							jsonUtils.processFail(response, new Exception("儲存失敗!"));
						}
					}
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			if("tempExTbBcModelM".equals(action)){
				logger.debug("run tempExTbBcModelM");
				ExTbBcModelMEntity param = (ExTbBcModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbBcModelMEntity.class);

				try {
					param.setEmpNo(empNo);
					param.setEDIT_STATUS(BcActionEnum.action_0.getValue());
					if(ImStringUtils.isNotBlank(param.getBC_M_ID())){
						if(shdBcService.updateExTbBcModelM(param)){
							jsonUtils.processSuccess(response);
						}else{
							jsonUtils.processFail(response, new Exception("儲存失敗!"));
						}
					}else{
						if(shdBcService.saveExTbBcModelM(param)){
							jsonUtils.processSuccess(response);
						}else{
							jsonUtils.processFail(response, new Exception("儲存失敗!"));
						}
					}
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			if("deleteTempExTbBcModelM".equals(action)){
				logger.debug("run deleteTempExTbBcModelM");
				ExTbBcModelMEntity param = (ExTbBcModelMEntity) AjaxConverter.json2Bean(
						request.getParameter("data"), ExTbBcModelMEntity.class);

				try {
					if(ImStringUtils.isNotBlank(param.getBC_M_ID())){
						shdBcService.deleteExTbBcModelM(param);
						jsonUtils.processSuccess(response);
					}else{
						jsonUtils.processFail(response, new Exception("刪除失敗!"));
					}
				} catch (Exception e) {
					jsonUtils.processFail(response, e);
				}
			}
			
			
			
			if("getUnReleaseExTbBcModelMPagination".equals(action)){
				logger.debug("run getUnReleaseExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(empNo);
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getUnReleaseExTbBcModelMPagination(ett,currentPage, pageSize));			
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			if("getBcLevel".equals(action)){
				logger.debug("run getBcLevel");
				try {
					ExTbBcModelMEntity bvLevel = shdBcService.getBcLevelByEmpNo(empNo);
					jsonUtils.put("bvLevel",bvLevel);	
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			
			if("getReleaseExTbBcModelMPagination".equals(action)){
				logger.debug("run getReleaseExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				String EDIT_STATUS = "6";
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getExTbBcModelMByStatusPagination(EDIT_STATUS,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			if("getExTbBcModelMByStatusPagination".equals(action)){
				logger.debug("run getReleaseExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				String EDIT_STATUS = imRequestUtils.getString("EDIT_STATUS");
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getExTbBcModelMByStatusPagination(EDIT_STATUS,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			
			if("getStatus4ExTbBcModelMPagination".equals(action)){
				logger.debug("run getStatus4ExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(empNo);
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getStatus4ExTbBcModelMPagination(ett,currentPage, pageSize, empNo));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			
			if("getStatus5ExTbBcModelMPagination".equals(action)){
				logger.debug("run getStatus5ExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(empNo);
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getStatus5ExTbBcModelMPagination(ett,currentPage, pageSize));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
			if("getStatus0ExTbBcModelMPagination".equals(action)){
				logger.debug("run getStatus0ExTbBcModelMPagination");
				int currentPage = imRequestUtils.getCurrentPage();
				int pageSize = imRequestUtils.getPageSize();
				ExTbBcModelMEntity ett = shdBcService.getBcLevelByEmpNo(empNo);
				try {
					jsonUtils.put(PaginationConstants.PAGINATION_KEY, shdBcService.getStatus0ExTbBcModelMPagination(ett,currentPage, pageSize,empNo));		
					jsonUtils.processSuccess(response);
				}catch(Exception ex){	
					jsonUtils.processFail(response, ex);
				}
			}
			
		
		
		}catch(Exception ex){	
			jsonUtils.processFail(response, ex);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		this.doGet(request, response);
	}

}
