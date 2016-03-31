<%@page session="false" contentType="text/html" pageEncoding="UTF-8" import="java.util.*,javax.portlet.*,com.ibm.shdwebportlet.*" %>
<%@ page import="com.ibm.websphere.security.auth.WSSubject" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>        
<%@taglib uri="http://www.ibm.com/xmlns/prod/websphere/portal/v6.1/portlet-client-model" prefix="portlet-client-model" %>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="wps"%>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="portal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<portlet:defineObjects/>

<link href="<%= renderRequest.getContextPath()%>/css/bc.css" rel="stylesheet" type="text/css">
<link href="<%= renderRequest.getContextPath()%>/css/news.css" rel="stylesheet" type="text/css" />
<link href="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css" />
<link href="<%= renderRequest.getContextPath()%>/js/cle/jquery.cleditor.css" rel="stylesheet" type="text/css" id="skinSheet">
<link href="<%= renderRequest.getContextPath()%>/js/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">

<script>
<%
String userName = WSSubject.getCallerPrincipal();
%>
var uId = '<%=userName%>';

var urlRoot = '<%= renderRequest.getScheme() +
 "://" + renderRequest.getServerName() 
 +":"+ renderRequest.getServerPort() + renderRequest.getContextPath() + "/fileServlet" %>';

var contextRoot = '<%= renderRequest.getContextPath()+"/shdNewsServlet"%>';
var fileContextRoot = '<%= renderRequest.getContextPath()+"/fileServlet"%>';

</script>


<article id="newsListContent" class="bc_content">
              	<table style="margin-top: 10px;width:100%;">
					<tr>
						<td align="right">	
							<input type="button" onclick="addNews();" class="bcButton" value="新增電子佈告欄" />		
						</td>
					</tr>
				</table>
                <section class="entry-container">
                    <h3 class="entry-title">電子佈告欄列表</h3>
                    <div class="bc-entry-content">
                    	
                        <form id="newsForm" name="newsForm" method="post" action="">
                        	<input id="action" type="hidden" name="action" value="getBulletinBoardAdminPagination"/>
            				<input id="isFilterByEndDate" type="hidden" name="isFilterByEndDate" value="false"/>
                        	<table class="newsTable">
							    <tr>
							        <td>篩選作者或標題</td>
							        <td colspan="2"><input id="authorName" type="text" name="authorName"/></td>
							    </tr>
							    <tr>
							        <td>篩選日期</td>
							        <td colspan="2">
							        	<input id="CREATED_DATE_STAR" type="text" name="CREATED_DATE_STAR" value="" placeholder="請輸入起始日期"/>
							        	到
							        	<input id="CREATED_DATE_END" type="text" name="CREATED_DATE_END" value="" placeholder="請輸入結束日期"/>
							        </td>
							    </tr>
							    <tr>
							        <td>篩選類別</td>
							        <td>
							        	<select id="newsType" name="newsType" ></select>
							        </td>
							        <td><input id="newsButton" type="button" name="newsButton" value="篩選" class="bcButton"/></td>
							    </tr>
							</table>
                        	
	                		
							
	                        
	                        <table class="newsTable">	
								<tr>
									<th class="item_01">日期</th>
									<th class="item_01">類別</th>
									<th class="item_01">標題</th>
									<th class="itme_01">作者</th>
									<th class="itme_01"></th>
								</tr>
								<tbody id="newsBody">
								
								</tbody>			
							</table>
	                        
                        </form>
                    </div>
  
                </section>
			
				<br clear="all" />
				<div id="news_pagination" align="center" class="pagesStyle"></div>
            </article>
        	
        
          
            
            <!-- new news -->
            <article id="newNewsDetailContent" class="bc_content">
            	<input id="NEWS_M_ID" type="text" name="NEWS_M_ID" value="" style="display:none;"/>
                <section class="entry-container">
                    <h3 class="entry-title">編輯電子佈告欄</h3>
                    <div class="bc-entry-content">
                    
                    	<table class="newsTable">
							<tr>
						        <td>標題</td>
						        <td colspan="3"><input size="80" id="TOPIC" type="text" name="TOPIC" value="" placeholder="請輸入標題"/></td>
						    </tr>
						    <tr>
						        <td>類別</td>
						        <td colspan="3"><select id="NEWS_TYPE" name="NEWS_TYPE" ></select></td>
						    </tr>
						    <tr>
						        <td>日期</td>
						        <td id="nowDateTimeString"></td>
						        <td>下架日期</td>
						        <td><input id="END_DATE_STR" type="text" name="END_DATE_STR" value="" placeholder="請輸入下架日期"/></td>
						    </tr>
						    
						    <tr style="display:none;">
						        <td>原文網址</td>
						        <td colspan="3"><input size="80" id="SOURCE_URL" type="text" name="SOURCE_URL" value="" placeholder="請輸入原文網址"/></td>
						    </tr>
						    
						    <tr>
						        <td>選擇部門</td>
						        <td colspan="3">
						        	<div id="treeMsg"></div>
						        	<div id="tree3"></div>
						        </td>
						    </tr>
						    
						    <tr>
						        <td>內容</td>
						        <td colspan="3">
						        	<textarea id="CONTENT" name="CONTENT"></textarea>
						        </td>
						    </tr>
						    
						    <tr>
						        <td>新附檔</td>
						        <td colspan="3">
						        	檔案大小請勿超過5MB
						        	<form id="UploadForm" action="<%= renderRequest.getContextPath()%>/UploadFileImage" method="post" enctype="multipart/form-data">
							        	<input type="file" size="60" id="myfile" name="myfile" class="bcButton"> 
										<input type="button" value="上傳檔案" class="bcButton" onclick="fileUploadButton();">
							        	
							        	<div id="progressbox" style="display:none;">
											<div id="progressbar"></div>
											<div id="percent">0%</div>
										</div>
										<div id="message"></div>
						        	</form>
						        	
						        	<table id="filesTable" class="newsTable">
						        	</table>
						        </td>
						    </tr>
						    
						    <tr>
						        <td colspan="4" align="center">
						        	<input type="button" class="bcButton" value="儲存" onclick="saveBcData();"/>
                        			<input type="button" onclick="backToMainList();" class="bcButton" value="取消"/>
						        </td>
						    </tr>
						    
						    
						    <tr>
						        <td>原附檔</td>
						        <td colspan="3">
						        	<table id="originFilesTable" class="newsTable">
						        	</table>
						        </td>
						    </tr>
						    
						    
						    
						</table>
                  
                        
                    </div>
  
                </section>
			<br clear="all" />
            </article>
            
            

<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.template.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.js"></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery.form.js" ></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="<%= renderRequest.getContextPath()%>/js/fileUploadScript.js" ></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery.json.min.js" ></script>
<script src="<%= renderRequest.getContextPath()%>/js/cle/jquery.cleditor.js" type="text/javascript"></script>
<script src="<%= renderRequest.getContextPath()%>/js/addBulletinBoardScript.js" ></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery.dynatree.js" ></script>