package com.shd.core.service;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.shd.core.dao.ExTbBcLogMDao;
import com.shd.core.dao.ExTbBcModelMDao;
import com.shd.core.entity.ExTbBcModelMEntity;
import com.shd.core.pojo.ExTbBcLogM;
import com.shd.core.utils.Pagination;
import com.shd.utils.ImFileUtil;

/**
 * 
 * 好事曆 
 * @author Roy
 *
 */
@Service
public class ShdBcService extends BaseService{
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ExTbBcModelMDao exTbBcModelMDao;

	@Autowired
	private ExTbBcLogMDao exTbBcLogMDao;
	

	
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ExTbBcModelMEntity> getHistoryLog(String bC_M_ID) throws Exception {
		List<ExTbBcLogM> list = exTbBcLogMDao.findByProperty("BC_M_ID", bC_M_ID);
		return this.listPojo2ViewBean(list, ExTbBcModelMEntity.class);
	}
	
	/**
	 * 根據狀態取得好事曆列表
	 * @param edit_status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbBcModelMEntity> getExTbBcModelMByStatusPagination(String edit_status, int currentPage, int pageSize){
		logger.debug("*** into getReleaseExTbBcModelMPagination () ***");
		return exTbBcModelMDao.getExTbBcModelMByStatusPagination(edit_status,currentPage, pageSize);
	}
	
	
	/**
	 * D:BC: 821, 822
	 * B:美容主任: 702,特戰主任: 703,美容擔當: 802
	 * C:美容課長: 512
	 * A:塗老師 /顧客G陳老師
	 * 根據階層取得未發佈好事曆分頁
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbBcModelMEntity> getUnReleaseExTbBcModelMPagination(ExTbBcModelMEntity etb ,int currentPage, int pageSize){
		logger.debug("*** into getExTbBcModelMPagination () ***");
		return exTbBcModelMDao.getUnReleaseExTbBcModelMPagination(etb, currentPage, pageSize);
	}
	
	
	/**
	 * 取得作廢好事曆
	 * @param ett
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbBcModelMEntity> getStatus4ExTbBcModelMPagination(ExTbBcModelMEntity ett,
			int currentPage, int pageSize,String empNo) {
		return exTbBcModelMDao.getStatus4ExTbBcModelMPagination(ett, currentPage, pageSize,empNo);
	}
	
	
	
	/**
	 * 取得同部門未入選好事曆
	 * @param ett
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbBcModelMEntity> getStatus5ExTbBcModelMPagination(ExTbBcModelMEntity ett,
			int currentPage, int pageSize) {
		return exTbBcModelMDao.getStatus5ExTbBcModelMPagination(ett, currentPage, pageSize);
	}
	
	
	/**
	 * 取得好事曆草稿
	 * @param ett
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public Pagination<ExTbBcModelMEntity> getStatus0ExTbBcModelMPagination(ExTbBcModelMEntity ett,
			int currentPage, int pageSize,String empNo) {
		return exTbBcModelMDao.getStatus0ExTbBcModelMPagination(ett, currentPage, pageSize,empNo);
	}
	
	
	/**
	 * 根據empNo 取得 empNo/positionCode/deptCode/empCname
	 * @param empNo
	 * @return empNo/positionCode/deptCode/empCname
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public ExTbBcModelMEntity getBcLevelByEmpNo(String empNo){
		logger.debug("*** into getBcLevelByEmpNo () ***");
		ExTbBcModelMEntity ett = exTbBcModelMDao.getBcLevelByEmpNo(empNo);
		return ett;
	}
	
	
	
	/**
	 * 新增好事曆
	 * @param exTbBcModelM
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveExTbBcModelM(ExTbBcModelMEntity exTbBcModelM){
		logger.debug("*** into saveExTbBcModelM () ***");
		exTbBcModelMDao.saveExTbBcModelM(exTbBcModelM);
		
		//移轉圖片
		String imageRoot = (String) imConfiguration.get("imageRoot");
		try {
			System.out.println(imageRoot+"/Image/temp/"+exTbBcModelM.getNEW_UPLOAD_PIC_NAME()+"---from");
			System.out.println(imageRoot+"/Image/"+exTbBcModelM.getNEW_UPLOAD_PIC_NAME()+"---to");
			ImFileUtil.moveFile(imageRoot+"/Image/", new File(imageRoot+"/Image/temp/"+exTbBcModelM.getNEW_UPLOAD_PIC_NAME()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	private void saveBcLog(ExTbBcModelMEntity exTbBcModelM,String action) {
		ExTbBcLogM etm = new ExTbBcLogM();
		etm.setBC_M_ID(exTbBcModelM.getBC_M_ID());
		etm.setACTION(action);
		etm.setCONTENT(exTbBcModelM.getHISTORY_COMMENT());
		etm.setCREATED_TIME(exTbBcModelM.systime());
		etm.setUSER_ID(exTbBcModelM.getEmpNo());
		exTbBcLogMDao.save(etm);
	}
	
	
	/**
	 * 變更好事曆狀態
	 * @param exTbBcModelM
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean updateExTbBcModelMStatus(ExTbBcModelMEntity exTbBcModelM){
		logger.debug("*** into updateExTbBcModelM () ***");
		exTbBcModelMDao.updateExTbBcModelMStatus(exTbBcModelM);
		
		//LOG
		saveBcLog(exTbBcModelM,exTbBcModelM.getEDIT_STATUS());
		return true;
	}
	
	
	/**
	 * 編輯好事曆
	 * @param exTbBcModelM
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean updateExTbBcModelM(ExTbBcModelMEntity exTbBcModelM){
		logger.debug("*** into updateExTbBcModelM () ***");
		ExTbBcModelMEntity ro = exTbBcModelMDao.getExTbBcModelMById(exTbBcModelM.getBC_M_ID());
		exTbBcModelMDao.updateExTbBcModelM(exTbBcModelM);
		
		saveBcLog(exTbBcModelM,exTbBcModelM.getEDIT_STATUS());
		
		if(!ro.getPIC_NAME().equals(exTbBcModelM.getNEW_UPLOAD_PIC_NAME())){
			//移轉圖片
			String imageRoot = (String) imConfiguration.get("imageRoot");
			try {
				ImFileUtil.deleteFile(imageRoot + "/Image/"+ro.getPIC_NAME());
				ImFileUtil.moveAndCopyFile(imageRoot + "/Image/" + ro.getPIC_NAME(), 
						imageRoot + "/Image/temp/" + exTbBcModelM.getNEW_UPLOAD_PIC_NAME());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * 刪除好事曆
	 * @param exTbBcModelM
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteExTbBcModelM(ExTbBcModelMEntity exTbBcModelM){
		logger.debug("*** into deleteExTbBcModelM () ***");
		exTbBcModelMDao.deleteExTbBcModelM(exTbBcModelM);
		exTbBcModelMDao.deleteExTbBcLogM(exTbBcModelM);
	}
	
	
	/**
	 * 
	 * 根據id 取得好事曆單筆資料
	 * @param BC_M_ID
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public ExTbBcModelMEntity getExTbBcModelMById(String BC_M_ID){
		logger.debug("*** into getExTbBcModelMById () ***");
		return exTbBcModelMDao.getExTbBcModelMById(BC_M_ID);
	}

	
	/**
	 * 首頁判斷是否有mail
	 * @param uid
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	public boolean checkHasMailByUid(String uid) {
		return exTbBcModelMDao.checkHasMailByUid(uid);
	}

	
	


	
	
	
}
