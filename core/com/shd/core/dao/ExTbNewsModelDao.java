package com.shd.core.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.shd.core.entity.ExTbNewsModelMEntity;
import com.shd.core.pojo.ExTbNewsModelM;
import com.shd.core.utils.BasePojoDAO;
import com.shd.core.utils.Pagination;

@Repository
public class ExTbNewsModelDao extends BasePojoDAO<ExTbNewsModelM, Long> {
	
	@Override
	protected Class<ExTbNewsModelM> getPojoClass() {
		return ExTbNewsModelM.class;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ExTbNewsModelMEntity> getDeptListByParentDeptCode(String parentDeptCode) {
		SQLQuery query = getNamedSQLQuery("news.query.getDeptListByParentDeptCode");
		query.setParameter("parentDeptCode", parentDeptCode);
		
		query.addScalar("DeptCode", new StringType());		
		query.addScalar("DeptCname", new StringType());
		query.addScalar("ParentDeptNo", new StringType());
		query.addScalar("DeptLevel", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		
		return query.list();
	}
	
	
	
	
	public List<ExTbNewsModelMEntity> getNewsTypeIdByName(String newsTypeName) {
		SQLQuery query = getNamedSQLQuery("news.query.getNewsTypeIdByName");
		query.setParameter("newsTypeName", newsTypeName);
		
		query.addScalar("NEWSTYPE_ID", new LongType());		
		query.addScalar("NAME", new StringType());
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		return query.list();
	}
	

	@SuppressWarnings("unchecked")
	public List<ExTbNewsModelMEntity> getDeptListByDeptLevel(String deptLevel) {
		SQLQuery query = getNamedSQLQuery("news.query.getDeptListByDeptLevel");
		query.setParameter("deptLevel", deptLevel);
		
		query.addScalar("DeptCode", new StringType());		
		query.addScalar("DeptCname", new StringType());
		query.addScalar("ParentDeptNo", new StringType());
		query.addScalar("DeptLevel", new StringType());

		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		
		return query.list();
	}


	@SuppressWarnings("unchecked")
	public Pagination<ExTbNewsModelMEntity> getNewsPagination(String isFilterByEndDate, String authorName, Date startDate, Date endDate, Long newsType,
			int currentPage, int pageSize) {
		
		
		SQLQuery query = null;
		if("true".equals(isFilterByEndDate)){
			query = getNamedSQLQuery("news.query.getNewsPaginationFilterByEndDate");
		}else{
			query = getNamedSQLQuery("news.query.getNewsPagination");
		}
		
		if(authorName != null){
			query.setParameter("authorName", "%"+authorName+"%");
		}else{
			query.setParameter("authorName", null);
		}
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("newsType", newsType);
		
		query.addScalar("NEWS_M_ID", new LongType());		
		query.addScalar("TOPIC", new StringType());
		query.addScalar("NEWS_TYPE", new LongType());
		query.addScalar("END_DATE", new TimestampType());
		query.addScalar("CREATED_DATE", new TimestampType());
		query.addScalar("CREATED_ID", new StringType());
		query.addScalar("UPDATED_DATE", new TimestampType());
		query.addScalar("UPDATED_ID", new StringType());
		query.addScalar("SOURCE_URL", new StringType());
		query.addScalar("CONTENT", new StringType());
		query.addScalar("NEWS_TYPE_NAME", new StringType());
		query.addScalar("empCname", new StringType());
		
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		return this.processPagination(currentPage, pageSize, query);
	}


	public List<ExTbNewsModelMEntity> getNewsTypeList(Long newsType_parent_id) {
		SQLQuery query = getNamedSQLQuery("news.query.getNewsTypeList");
		query.setParameter("newsType_parent_id", newsType_parent_id);
		
		query.addScalar("NEWSTYPE_ID", new LongType());		
		query.addScalar("NAME", new StringType());
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		return query.list();
	}


	public ExTbNewsModelMEntity getNewsById(Long newsId) {
		SQLQuery query = getNamedSQLQuery("news.query.getNewsById");
		query.setParameter("newsId", newsId);
		
		query.addScalar("NEWS_M_ID", new LongType());		
		query.addScalar("TOPIC", new StringType());
		query.addScalar("NEWS_TYPE", new LongType());
		query.addScalar("END_DATE", new TimestampType());
		query.addScalar("CREATED_DATE", new TimestampType());
		query.addScalar("CREATED_ID", new StringType());
		query.addScalar("UPDATED_DATE", new TimestampType());
		query.addScalar("UPDATED_ID", new StringType());
		query.addScalar("SOURCE_URL", new StringType());
		query.addScalar("CONTENT", new StringType());
		query.addScalar("NEWS_TYPE_NAME", new StringType());
		
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));	
		return (ExTbNewsModelMEntity) query.uniqueResult();
	}


	public Pagination<ExTbNewsModelMEntity> getBulletinBoardPagination(String isFilterByEndDate,String empNo,
			String authorName, Date startDate, Date endDate, Long newsType,
			int currentPage, int pageSize) {

		SQLQuery query = null;
		if("true".equals(isFilterByEndDate)){
			query = getNamedSQLQuery("news.query.getBulletinBoardPaginationFilterByEndDate");
		}else{
			query = getNamedSQLQuery("news.query.getBulletinBoardPagination");
		}
		
		if(authorName != null){
			query.setParameter("authorName", "%"+authorName+"%");
		}else{
			query.setParameter("authorName", null);
		}
		
		query.setParameter("empNo", empNo);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("newsType", newsType);
		
		query.addScalar("NEWS_M_ID", new LongType());		
		query.addScalar("TOPIC", new StringType());
		query.addScalar("NEWS_TYPE", new LongType());
		query.addScalar("END_DATE", new TimestampType());
		query.addScalar("CREATED_DATE", new TimestampType());
		query.addScalar("CREATED_ID", new StringType());
		query.addScalar("UPDATED_DATE", new TimestampType());
		query.addScalar("UPDATED_ID", new StringType());
		query.addScalar("SOURCE_URL", new StringType());
		query.addScalar("CONTENT", new StringType());
		query.addScalar("NEWS_TYPE_NAME", new StringType());
		query.addScalar("empCname", new StringType());
		
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		return this.processPagination(currentPage, pageSize, query);
	}
	
	
	public Pagination<ExTbNewsModelMEntity> getBulletinBoardAdminPagination(
			String authorName, Date startDate, Date endDate, Long newsType,
			int currentPage, int pageSize) {
		SQLQuery query = getNamedSQLQuery("news.query.getBulletinBoardAdminPagination");
		if(authorName != null){
			query.setParameter("authorName", "%"+authorName+"%");
		}else{
			query.setParameter("authorName", null);
		}
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("newsType", newsType);
		
		query.addScalar("NEWS_M_ID", new LongType());		
		query.addScalar("TOPIC", new StringType());
		query.addScalar("NEWS_TYPE", new LongType());
		query.addScalar("END_DATE", new TimestampType());
		query.addScalar("CREATED_DATE", new TimestampType());
		query.addScalar("CREATED_ID", new StringType());
		query.addScalar("UPDATED_DATE", new TimestampType());
		query.addScalar("UPDATED_ID", new StringType());
		query.addScalar("SOURCE_URL", new StringType());
		query.addScalar("CONTENT", new StringType());
		query.addScalar("NEWS_TYPE_NAME", new StringType());
		query.addScalar("empCname", new StringType());
		
		
		query.setResultTransformer(Transformers.aliasToBean(ExTbNewsModelMEntity.class));
		return this.processPagination(currentPage, pageSize, query);
	}

	

	

}
