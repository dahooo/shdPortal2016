package com.shd.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.shd.core.dao.ExTbNewsDeptMappingDao;
import com.shd.core.dao.ExTbNewsFilesModelDao;
import com.shd.core.dao.ExTbNewsModelDao;
import com.shd.core.entity.ExTbNewsModelMEntity;
import com.shd.core.enums.DeptCodeEnum;
import com.shd.core.pojo.ExTbNewsDeptMappingM;
import com.shd.core.pojo.ExTbNewsFilesM;
import com.shd.core.pojo.ExTbNewsModelM;
import com.shd.core.utils.DeptNode;
import com.shd.core.utils.Pagination;
import com.shd.utils.ImDateStringUtils;
import com.shd.utils.ImDateUtils;
import com.shd.utils.ImFileUtil;
import com.shd.utils.ImStringUtils;
import com.shd.utils.UUIDHexGenerator;

/**
 * 
 * 電子公布欄/新聞
 * @author Roy
 *
 */
@Service
public class ShdNewsService extends BaseService{
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ExTbNewsModelDao exTbNewsModelDao;
	
	@Autowired
	private ExTbNewsFilesModelDao exTbNewsFilesModelDao;
	
	@Autowired
	private ExTbNewsDeptMappingDao exTbNewsDeptMappingDao;
	
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateNewsByBatch(){
		String newsTempRoot = (String) imConfiguration.get("newsRoot")+"/temp";
		
		System.out.println(newsTempRoot+"===newsTempRoot");
		
		File dir = new File(newsTempRoot);
	    File[] firstLevelFiles = dir.listFiles();
	    if (firstLevelFiles != null && firstLevelFiles.length > 0) {
	        for (File aFile : firstLevelFiles) {
	        	if (aFile.isFile()) {
	        		ExTbNewsModelMEntity em = new ExTbNewsModelMEntity();
	            	String fileName = getFileName(aFile);
	            	String fileType = getExtension(aFile);
	            	
	            	System.out.println(fileName+"===fileName");
	            	em.setTOPIC(fileName);
	            	em.setFILE_TYPE(fileType);
	            	try {
						autoSaveExTbNewsModelM(em,aFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        	
	        }
	    }
	}
	
	/**
	 * 自動儲存新聞與附檔
	 * @param param
	 * @param aFile 
	 * @throws Exception
	 */
	private void autoSaveExTbNewsModelM(ExTbNewsModelMEntity param, File aFile) throws Exception {
		
		if(ImStringUtils.isNotBlank(param.getEND_DATE_STR())){
			param.setEND_DATE(ImDateStringUtils.transDateStrToTimestamp(param.getEND_DATE_STR(), "yyyyMMdd"));
		}
		
		ExTbNewsModelM etm = this.transferEntity2Pojo(param, ExTbNewsModelM.class);
		etm.setCREATED_DATE(ExTbNewsModelMEntity.systime());
		etm.setCREATED_ID("222222");
		etm.setUPDATED_DATE(ExTbNewsModelMEntity.systime());
		etm.setUPDATED_ID("222222");
		etm.setDELETE_FLAG("0");
		etm.setNEWS_TYPE(new Long(2));//產業新聞
		
		Long newsId = exTbNewsModelDao.save(etm);
		

		//儲存附檔至資料庫
		ExTbNewsFilesM etf = new ExTbNewsFilesM();
		String uuid = new UUIDHexGenerator().generate();
		etf.setFILE_ID(uuid);
		etf.setFILE_NAME(param.getTOPIC());
		etf.setFILE_TYPE(param.getFILE_TYPE());
		etf.setNEWS_ID(newsId);
		exTbNewsFilesModelDao.save(etf);
		
		 
		//移動資料從原來至web資料夾
		String filesRoot = (String) imConfiguration.get("filesRoot");
		ImFileUtil.copyFile(filesRoot+"/"+uuid+"."+param.getFILE_TYPE(), aFile);
		
		//移動資料按照月份至網路磁碟備份並刪除
		String year = ImDateStringUtils.fetchCurrentDateString(new Date(), "yyyy");
		String month = ImDateStringUtils.fetchCurrentDateString(new Date(), "MM");
		String newsBackUpRoot = (String) imConfiguration.get("newsRoot")+"/"+year+"每日簡報摘要/日報/"+month+"月";
		File backUpDir = new File(newsBackUpRoot);
		System.out.println(newsBackUpRoot+"===newsBackUpRoot");
		if(!backUpDir.exists()){
			System.out.println("建立資料夾:"+newsBackUpRoot);
			if (backUpDir.mkdirs()) {
				ImFileUtil.moveFile(newsBackUpRoot, aFile);
			}else{
				System.out.println("dir create fail");
			}
		}else{
			ImFileUtil.moveFile(newsBackUpRoot, aFile);
		}
		
	}
	

	
	private String getFileName(File file){
        int endIndex = file.getName().lastIndexOf(46);
        return  file.getName().substring(0, endIndex);
    }
	
	private String getExtension(File file){
        int startIndex = file.getName().lastIndexOf(46) + 1;
        int endIndex = file.getName().length();
        return  file.getName().substring(startIndex, endIndex);
    }
	
	
	
	
	/**
	 * 根據ID取得新聞附檔
	 * @param NEWS_ID
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ExTbNewsModelMEntity> getFileData(Long NEWS_ID) throws Exception {
		List<ExTbNewsFilesM> filePojoList = exTbNewsFilesModelDao.findByProperty("NEWS_ID", NEWS_ID);		
		return this.listPojo2ViewBean(filePojoList, ExTbNewsModelMEntity.class);
	}
	
	
	/**
	 * 根據ID取得新聞
	 * @param NEWS_M_ID
	 * @return
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public ExTbNewsModelMEntity getNewsById(Long NEWS_M_ID) throws Exception {
		ExTbNewsModelMEntity etm = exTbNewsModelDao.getNewsById(NEWS_M_ID);
		return etm;
	}

	/**
	 * 刪除新聞
	 * @param NEWS_M_ID
	 * @param empNo 
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteNews(Long NEWS_M_ID, String empNo) throws Exception{
		ExTbNewsModelM etm = exTbNewsModelDao.find(NEWS_M_ID);
		etm.setDELETE_FLAG("1");
		etm.setUPDATED_DATE(ImDateUtils.getSysDateTime());
		etm.setUPDATED_ID(empNo);
	}
	
	
	/**
	 * 刪除附檔
	 * @param fileId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteFileById(Long fileId) throws Exception {
		ExTbNewsFilesM fileData = exTbNewsFilesModelDao.find(fileId);
		String filesRoot = (String) imConfiguration.get("filesRoot");
		ImFileUtil.deleteFile(filesRoot+File.separator+fileData.getFILE_ID()+"."+fileData.getFILE_TYPE());
		exTbNewsFilesModelDao.delete(fileData);
	}
	
	/**
	 * 更新新聞/電子佈告欄與附檔
	 * @param param
	 * @throws Exception 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateExTbNewsModelM(ExTbNewsModelMEntity param) throws Exception {
		
		ExTbNewsModelM etm = exTbNewsModelDao.find(param.getNEWS_M_ID());
		etm.setTOPIC(param.getTOPIC());
		etm.setSOURCE_URL(param.getSOURCE_URL());
		etm.setNEWS_TYPE(param.getNEWS_TYPE());
		etm.setUPDATED_DATE(ExTbNewsModelMEntity.systime());
		etm.setUPDATED_ID(param.getUPDATED_ID());
		etm.setCONTENT(param.getCONTENT().replace("isTemp=true", "isTemp=false"));
		
		if(ImStringUtils.isNotBlank(param.getEND_DATE_STR())){
			etm.setEND_DATE(ImDateStringUtils.transDateStrToTimestamp(param.getEND_DATE_STR(), "yyyy-MM-dd"));
		}
		
		

		//刪除原聯結
		exTbNewsDeptMappingDao.deleteByProperty("NEWS_M_ID", param.getNEWS_M_ID());
		//新增新聞與部門聯結
		saveNewsDeptMapping(param.getRelativeDeptNode(), param.getNEWS_M_ID(),"0");		
		saveNewsDeptMapping(param.getSelectedDeptNode(), param.getNEWS_M_ID(),"1");
		
		//儲存附檔
		JSONArray arr = JSONArray.fromObject(param.getUploadFiles());
		for (int i=0; i<arr.size(); i++) {
			JSONObject obj = JSONObject.fromObject(arr.get(i));
			String fileId = obj.get("NEW_UPLOAD_FILE_ID").toString();
			String fileName = obj.get("NEW_UPLOAD_FILE_NAME").toString();
			String fileExt = obj.get("NEW_UPLOAD_FILE_EXT").toString();
			ExTbNewsFilesM etf = new ExTbNewsFilesM();
			etf.setFILE_ID(fileId);
			etf.setFILE_NAME(fileName);
			etf.setFILE_TYPE(fileExt);
			etf.setNEWS_ID(etm.getNEWS_M_ID());
			exTbNewsFilesModelDao.save(etf);
			//移動圖片從temp至正式資料夾
			String filesRoot = (String) imConfiguration.get("filesRoot");
			ImFileUtil.moveFile(filesRoot+"/", new File(filesRoot+"/temp/"+fileId+"."+fileExt));
		}
	}
	
	
	/**
	 * 儲存新聞/電子佈告欄與附檔
	 * @param param
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveExTbNewsModelM(ExTbNewsModelMEntity param) throws Exception {
		
		if(ImStringUtils.isNotBlank(param.getEND_DATE_STR())){
			param.setEND_DATE(ImDateStringUtils.transDateStrToTimestamp(param.getEND_DATE_STR(), "yyyy-MM-dd"));
		}
		
		ExTbNewsModelM etm = this.transferEntity2Pojo(param, ExTbNewsModelM.class);
		etm.setCREATED_DATE(ExTbNewsModelMEntity.systime());
		etm.setUPDATED_DATE(ExTbNewsModelMEntity.systime());
		etm.setDELETE_FLAG("0");
		etm.setCONTENT(param.getCONTENT().replace("isTemp=true", "isTemp=false"));
		Long newsId = exTbNewsModelDao.save(etm);
		
		//新增新聞與部門聯結
		saveNewsDeptMapping(param.getRelativeDeptNode(), newsId,"0");		
		saveNewsDeptMapping(param.getSelectedDeptNode(), newsId,"1");
		
		//儲存附檔
		JSONArray arr = JSONArray.fromObject(param.getUploadFiles());
		for (int i=0; i<arr.size(); i++) {
			JSONObject obj = JSONObject.fromObject(arr.get(i));
			String fileId = obj.get("NEW_UPLOAD_FILE_ID").toString();
			String fileName = obj.get("NEW_UPLOAD_FILE_NAME").toString();
			String fileExt = obj.get("NEW_UPLOAD_FILE_EXT").toString();
			ExTbNewsFilesM etf = new ExTbNewsFilesM();
			etf.setFILE_ID(fileId);
			etf.setFILE_NAME(fileName);
			etf.setFILE_TYPE(fileExt);
			etf.setNEWS_ID(newsId);
			exTbNewsFilesModelDao.save(etf);
			//移動圖片從temp至正式資料夾
			String filesRoot = (String) imConfiguration.get("filesRoot");
			ImFileUtil.moveFile(filesRoot+"/", new File(filesRoot+"/temp/"+fileId+"."+fileExt));
		}
	}


	private void saveNewsDeptMapping(String val, Long newsId,String selected) {
		if(val != null){
			String[] seList = val.split(",");
			for(String deptId :seList){
				if(ImStringUtils.isNotBlank(deptId)){
					ExTbNewsDeptMappingM mapping = new ExTbNewsDeptMappingM();
					mapping.setDEPT_CODE(deptId);
					mapping.setNEWS_M_ID(newsId);
					mapping.setSELECTED(selected);
					exTbNewsDeptMappingDao.save(mapping);
				}
			}
		}
	}

	
	/**
	 * 新聞類別(1:企業新聞/2:電子佈告欄)取得列表
	 * @param newsType_parent_id
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ExTbNewsModelMEntity> getNewsTypeList(Long newsType_parent_id) {
		logger.debug("*** into getNewsTypeList () ***");
		return exTbNewsModelDao.getNewsTypeList(newsType_parent_id);
	}

	
	
	/**
	 * 根據新聞形態(1:企業新聞/2:電子佈告欄)取得新聞列表
	 * @param authorName2 
	 * @param endDate 
	 * @param startDate 
	 * @param authorId 
	 * @param newsType
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbNewsModelMEntity> getNewsPagination(String isFilterByEndDate, String authorName, Date startDate, Date endDate, Long newsType, int currentPage, int pageSize) {
		logger.debug("*** into getNewsPagination () ***");
		
		Pagination<ExTbNewsModelMEntity> pg = exTbNewsModelDao.getNewsPagination(isFilterByEndDate,authorName,startDate,endDate,newsType,currentPage, pageSize);
		List<ExTbNewsModelMEntity> list = pg.getCurrentList();
		
		//只拿第一筆file當作代表
		for(ExTbNewsModelMEntity etnmm : list ){
			List<ExTbNewsFilesM> filePojoList = exTbNewsFilesModelDao.findByProperty("NEWS_ID", etnmm.getNEWS_M_ID());
			if(filePojoList.size() > 0){
				ExTbNewsFilesM file = filePojoList.get(0);
				etnmm.setFILE_ID(file.getFILE_ID());
				etnmm.setFILE_NAME(file.getFILE_NAME());
				etnmm.setFILE_TYPE(file.getFILE_TYPE());
			}
		}
		Pagination<ExTbNewsModelMEntity> collPagin = new Pagination<ExTbNewsModelMEntity>(list, currentPage, pageSize, pg.getTotalRow());
		
		return collPagin;
	}

	/**
	 * 新增新聞/電子公布欄
	 * @param etb
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addExTbNewsModelM(ExTbNewsModelMEntity etb) throws Exception{
		ExTbNewsModelM data = transferEntity2Pojo(etb, ExTbNewsModelM.class);
		exTbNewsModelDao.save(data);
	}
	
	
	/**
	 * 根據id取得新聞/電子公布欄
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public ExTbNewsModelMEntity getExTbNewsModelMById(Long id) throws Exception{
		ExTbNewsModelM et = exTbNewsModelDao.find(id);
		return this.transferPojo2ViewBean(et, ExTbNewsModelMEntity.class);
	}
	
	

	/**
	 * 根據deptLevel 取得部門列表 (50(本部)/60(協理)/70(部)/80(品牌)/90(G(課)(營業區)))
	 * @param deptLevel
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ExTbNewsModelMEntity> getDeptListByDeptLevel(String deptLevel){
		logger.debug("*** into getDeptListByDeptLevel () ***");
		return exTbNewsModelDao.getDeptListByDeptLevel(deptLevel);
	}

	
	private void getAllDeptList(String parentDeptCode, List<ExTbNewsModelMEntity> initList, List<String> selectedDeptCodeList, List<DeptNode> topNodeList) {
		
		for(ExTbNewsModelMEntity ent: initList){
			if(parentDeptCode.equals(parentDeptCode)){
				List<DeptNode> nodeList = new ArrayList<DeptNode>();
				DeptNode node = new DeptNode();
				node.setKey(ent.getDeptCode());
				node.setTitle(ent.getDeptCname());
				if(selectedDeptCodeList.contains(ent.getDeptCode())){node.setSelect(true);}
				
				List<ExTbNewsModelMEntity> list = exTbNewsModelDao.getDeptListByParentDeptCode(ent.getDeptCode());
				getAllDeptList(ent.getDeptCode(),list,selectedDeptCodeList,nodeList);
				
				node.setChildren(nodeList);
				topNodeList.add(node);
			}
		}

	}
	

	private DeptNode getBossNode(String deptCode, String Name, List<String> selectedDeptCodeList) {
		DeptNode bossNode = new DeptNode();
		bossNode.setKey(deptCode);
		bossNode.setTitle(Name);
		if(selectedDeptCodeList.contains(deptCode)){bossNode.setSelect(true);}
		return bossNode;
	}
	
	/**
	 * 取得所有部門列表
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<DeptNode> getALLDeptList(String NEWS_M_ID) {
			
		List<String> selectedDeptCodeList = new ArrayList<String>();
		
		if(NEWS_M_ID != null){//如果有選取 則前端顯示打勾
			List<ExTbNewsDeptMappingM> mappingList = exTbNewsDeptMappingDao.findByProperty("NEWS_M_ID", new Long(NEWS_M_ID));
			for(ExTbNewsDeptMappingM mapp: mappingList){
				if("1".equals(mapp.getSELECTED())){
					selectedDeptCodeList.add(mapp.getDEPT_CODE());
				}
			}
		}
		//selectedDeptCodeList.add("2641");
		//selectedDeptCodeList.add("2660");
		//selectedDeptCodeList.add("2680");
		//selectedDeptCodeList.add("2400");
		//selectedDeptCodeList.add("2460");
		//selectedDeptCodeList.add("2470");
		String initDeptCode = "A004";
		
		
		
		
		List<ExTbNewsModelMEntity> initList = exTbNewsModelDao.getDeptListByParentDeptCode(initDeptCode);
		List<DeptNode> topNodeList = new ArrayList<DeptNode>();
		
		DeptNode bossTopNode = new DeptNode();
		bossTopNode.setKey("911");
		bossTopNode.setTitle("集團高層");
		
		List<DeptNode> bossNodeList = new ArrayList<DeptNode>();
		bossNodeList.add(getBossNode("111336","李國興",selectedDeptCodeList));
		bossNodeList.add(getBossNode("110057","李國祥",selectedDeptCodeList));
		bossNodeList.add(getBossNode("119356","稻垣幸朗",selectedDeptCodeList));
		bossTopNode.setChildren(bossNodeList);
		
		topNodeList.add(bossTopNode);
		
		getAllDeptList(initDeptCode,initList,selectedDeptCodeList,topNodeList);
		
		
		//=========================================
		/*
		List<ExTbNewsModelMEntity> list50 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel50.getIndex());
		List<ExTbNewsModelMEntity> list60 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel60.getIndex());
		List<ExTbNewsModelMEntity> list70 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel70.getIndex());
		List<ExTbNewsModelMEntity> list80 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel80.getIndex());
		List<ExTbNewsModelMEntity> list90 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel90.getIndex());
		
		List<ExTbNewsModelMEntity> list100 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel100.getIndex());
		List<ExTbNewsModelMEntity> list110 = exTbNewsModelDao.getDeptListByDeptLevel(DeptCodeEnum.DeptLevel110.getIndex());
		
		List<DeptNode> n50List = new ArrayList<DeptNode>();
		for(ExTbNewsModelMEntity d50: list50){
			DeptNode n50 = new DeptNode();
			n50.setKey(d50.getDeptCode());
			n50.setTitle(d50.getDeptCname());
			if(selectedDeptCodeList.contains(d50.getDeptCode())){n50.setSelect(true);}
			
			List<DeptNode> n60List = new ArrayList<DeptNode>();
			for(ExTbNewsModelMEntity d60: list60){
				if(d60.getParentDeptNo().equals(d50.getDeptCode())){
					DeptNode n60 = new DeptNode();
					n60.setKey(d60.getDeptCode());
					n60.setTitle(d60.getDeptCname());
					if(selectedDeptCodeList.contains(d60.getDeptCode())){n60.setSelect(true);}
					List<DeptNode> n70List = new ArrayList<DeptNode>();
					for(ExTbNewsModelMEntity d70: list70){
						if(d70.getParentDeptNo().equals(d60.getDeptCode())){
							DeptNode n70 = new DeptNode();
							n70.setKey(d70.getDeptCode());
							n70.setTitle(d70.getDeptCname());
							if(selectedDeptCodeList.contains(d70.getDeptCode())){n70.setSelect(true);n70.setExpand(true);}
							List<DeptNode> n80List = new ArrayList<DeptNode>();
							for(ExTbNewsModelMEntity d80: list80){
								if(d80.getParentDeptNo().equals(d70.getDeptCode())){
									DeptNode n80 = new DeptNode();
									n80.setKey(d80.getDeptCode());
									n80.setTitle(d80.getDeptCname());
									if(selectedDeptCodeList.contains(d80.getDeptCode())){n80.setSelect(true);}
									n80List.add(n80);
								}
							}
							n70.setChildren(n80List);
							List<DeptNode> n90List = new ArrayList<DeptNode>();
							for(ExTbNewsModelMEntity d90: list90){
								if(d90.getParentDeptNo().equals(d70.getDeptCode())){
									DeptNode n90 = new DeptNode();
									n90.setKey(d90.getDeptCode());
									n90.setTitle(d90.getDeptCname());
									if(selectedDeptCodeList.contains(d90.getDeptCode())){n90.setSelect(true);}
									List<DeptNode> n100List = new ArrayList<DeptNode>();
									for(ExTbNewsModelMEntity d100: list100){
										if(d100.getParentDeptNo().equals(d90.getDeptCode())){
											DeptNode n100 = new DeptNode();
											n100.setKey(d100.getDeptCode());
											n100.setTitle(d100.getDeptCname());
											if(selectedDeptCodeList.contains(d100.getDeptCode())){n100.setSelect(true);n100.setExpand(true);}
											List<DeptNode> n110List = new ArrayList<DeptNode>();
											for(ExTbNewsModelMEntity d110: list110){
												if(d110.getParentDeptNo().equals(d100.getDeptCode())){
													DeptNode n110 = new DeptNode();
													n110.setKey(d110.getDeptCode());
													n110.setTitle(d110.getDeptCname());
													if(selectedDeptCodeList.contains(d110.getDeptCode())){n110.setSelect(true);}
													n110List.add(n110);
												}
											}
											n100.setChildren(n110List);
											n100List.add(n100);
										}
									}
									n90.setChildren(n100List);
									n90List.add(n90);
								}
							}
							n70.setChildren(n90List);
							n70List.add(n70);
						}
					}
					n60.setChildren(n70List);
					n60List.add(n60);
				}
			}
			n50.setChildren(n60List);
			n50List.add(n50);
		}
		*/
		return topNodeList;
		
	}

	
	//======
	
	
	
	


	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbNewsModelMEntity> getBulletinBoardPagination(String isFilterByEndDate,String empNo ,String authorName, Date startDate,
			Date endDate, Long newsType, int currentPage, int pageSize) {
		logger.debug("*** into getBulletinBoardPagination () ***");
		return exTbNewsModelDao.getBulletinBoardPagination(isFilterByEndDate,empNo,authorName,startDate,endDate,newsType,currentPage, pageSize);
	}
	

	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbNewsModelMEntity> getBulletinBoardAdminPagination(String authorName, Date startDate,
			Date endDate, Long newsType, int currentPage, int pageSize) {
		logger.debug("*** into getBulletinBoardAdminPagination () ***");
		return exTbNewsModelDao.getBulletinBoardAdminPagination(authorName,startDate,endDate,newsType,currentPage, pageSize);
	}

	

	
	
}
