<%@page session="false" contentType="text/html" pageEncoding="UTF-8" import="java.util.*,javax.portlet.*,com.ibm.shdwebportlet.*" %>
<%@ page import="com.ibm.websphere.security.auth.WSSubject" %>
<%@ page import="com.shd.websso.*" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>        
<%@ taglib uri="http://www.ibm.com/xmlns/prod/websphere/portal/v6.1/portlet-client-model" prefix="portlet-client-model" %>  
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="wps"%>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="portal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="com.ibm.portal.model.*"%>
<%@page import="com.ibm.portal.navigation.*"%>
<%@page import="com.ibm.portal.identification.*"%>
<%@page import="com.ibm.portal.content.ContentNodeType"%>
<%@page import="java.io.*"%>
<%@page import="com.ibm.portal.ModelException"%>
<%@page import="com.ibm.portal.ObjectNotFoundException"%>
<%@page import="com.ibm.portal.content.ContentNode"%>
<%@page import="com.ibm.wps.l2.urlspihelper.servletrequest.ThemeURLHelper"%>

<portlet:defineObjects/>

<%!

public NavigationNode getNodeByName(NavigationModel nm, NavigationNode rootNode, String nodeUniqueName) throws ModelException {
    Iterator iter = nm.getChildren(rootNode);
    while(iter.hasNext()){
        NavigationNode node = (NavigationNode) iter.next();
        String uniqueName = node.getContentNode().getObjectID().getUniqueName();
        if (uniqueName!= null && uniqueName.equals(nodeUniqueName)) 
            return node;
    }
    return null;

}

public List<NavigationNode> getChildrenNodes(NavigationModel nm, NavigationNode parentNode) throws ModelException{
    List<NavigationNode> children = new ArrayList<NavigationNode>();
    Iterator iter = nm.getChildren(parentNode);
    while(iter.hasNext()){
        NavigationNode node = (NavigationNode) iter.next();
        children.add(node);
    }
    return children;
}

public String getId(Identification identification, NavigationNode node) throws com.ibm.portal.serialize.SerializationException{
    return identification.serialize( ( ( com.ibm.portal.Identifiable ) node ).getObjectID());
}

public boolean isHiddenPage(NavigationNode node){
    if (node instanceof com.ibm.portal.MetaDataProvider) {
        com.ibm.portal.MetaData iMetaData=((com.ibm.portal.MetaDataProvider) node).getMetaData();
        Object url=iMetaData.getValue("hide.from.menu");
        return (url != null && url.toString().equals("true"));
    }
    return false;
}

%>



<%
	com.shd.websso.M2KSSOPortletSessionBean sessionBean = 
	(com.shd.websso.M2KSSOPortletSessionBean)renderRequest.getPortletSession().getAttribute(ShdWebPortlet.SESSION_BEAN);
		
	PortletSession pSession = renderRequest.getPortletSession();
	String pSessionId = pSession.getId();
	System.out.println("pSessionId: " + pSessionId);

	String hSessionId = request.getSession().getId();
	System.out.println("hSessionId: " + hSessionId);
		
	PortletPreferences preferences = renderRequest.getPreferences();
    String ssoApType = "type=1";
    String ssoWidth = "100%";
    String ssoHeight = "160";
  	
 
 	String userName = WSSubject.getCallerPrincipal();
	if(userName==null) {userName="null";}
	System.out.println("WSSubject UserName : " + userName);

	HttpServletRequest httpreq = (HttpServletRequest) (com.ibm.ws.portletcontainer.portlet.PortletUtils.getHttpServletRequest(renderRequest));
	//HttpServletRequest httpreq = request;
	String reqServerName = "";
	String reqContextPath = "";
	String reqSessionId = "";
	ServletContext sc = null;
	int reqServerPort = 80;
	if (httpreq != null) {
		reqSessionId = httpreq.getRequestedSessionId();
		String reqProtocol = httpreq.getProtocol();
		reqServerName = httpreq.getServerName();
		reqServerPort = httpreq.getServerPort();
		reqContextPath = httpreq.getContextPath();
	}	

	// generate URL for serveResource()
	/**
	//javax.portlet.ResourceURL url = renderResponse.createResourceURL();
	//url.setResourceID("sso"); 
	**/

	String key= "12345678";
	key = M2KSSO.getInstance().getUserBean(userName).getHash();
%>

<% 
	StringBuffer sbRemoteUrl1 = new StringBuffer();
	StringBuffer sbRemoteUrl2 = new StringBuffer();
	StringBuffer eFormUrl = new StringBuffer();
	if (preferences != null) {
		sbRemoteUrl1.append("?");
  		sbRemoteUrl1.append("userid=");
  		sbRemoteUrl1.append(userName);
  		sbRemoteUrl1.append("&");
  		sbRemoteUrl1.append("key=");
  		sbRemoteUrl1.append(key);
  		sbRemoteUrl1.append("&");
  		sbRemoteUrl1.append("type=2");
  		
		sbRemoteUrl2.append("?");
  		sbRemoteUrl2.append("userid=");
  		sbRemoteUrl2.append(userName);
  		sbRemoteUrl2.append("&");
  		sbRemoteUrl2.append("key=");
  		sbRemoteUrl2.append(key);
  		sbRemoteUrl2.append("&");
  		sbRemoteUrl2.append("type=1");
  		
  		eFormUrl.append("?");
  		eFormUrl.append("userid=");
  		eFormUrl.append(userName);
  		eFormUrl.append("&");
  		eFormUrl.append("key=");
  		eFormUrl.append(key);
%>  
<%
	} else {
%><%}%>
<link href="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.template.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.js"></script>

<script src="<%= renderRequest.getContextPath()%>/js/jquery.form.js" ></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery.json.min.js" ></script>

<script>
var fileContextRoot = '<%=renderRequest.getContextPath() + "/fileServlet"%>';

function goToBcDetail(id){
	<wps:urlGeneration contentNode="shd.bc">
	var bcLink = '<% wpsURL.write(out); %>';						
	</wps:urlGeneration>
	
	var link = bcLink + '?BC_M_ID='+id;
	
	window.location = link;
}


function goToNewsDetail(id){
	<wps:urlGeneration contentNode="shd.news">
	var bLink = '<% wpsURL.write(out); %>';						
	</wps:urlGeneration>
	
	var link = bLink + '?NEWS_ID='+id;
	
	window.location = link;
}


function goToBulletinBoardDetail(id){
	<wps:urlGeneration contentNode="shd.bulletinBoard">
	var bLink = '<% wpsURL.write(out); %>';						
	</wps:urlGeneration>
	
	var link = bLink + '?NEWS_ID='+id;
	
	window.location = link;
}






function formatContent(content){
	if(content.length > 55){
		return content.substring(0, 55)+"...";
	}else{
		return content;
	}
}


function getBcTemplate(seq, o) {
		var now = new Date();
		var html = '';
		
		html += '<div class="BCnews" onclick="goToBcDetail(\''+o.BC_M_ID+'\')">';
        html += '                    <table class="BCtable">';
        html += '                        <tr>';
        html += '                            <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名｜</th>';
        html += '                            <td style="width:120px;">'+o.NAME+'</td>';
        html += '                            <td class="memPhoto" rowspan="4" style="width:100px;">';
        html += '<div class="picDiv" style="background-image: url(/wps/PA_shdWeb/imageServlet?imageName='+o.PIC_NAME+'&time='+now.getTime()+');"></div></td>';
        html += '                        </tr>';
        
        html += '                        <tr>';
        html += '                            <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年資｜</th>';
        html += '                            <td>'+o.SENIORITY+'年</td>';
        html += '                        </tr>';
        
        html += '                        <tr>';
        html += '                            <th style="width: 100px;">發佈日期｜</th>';
        html += '                            <td>'+o.CREATED_DATEStr+'</td>';
        html += '                        </tr>';
        html += '                        <tr>';
        html += '                            <th>自我介紹｜</th>';
        html += '                        </tr>';
        html += '                        <tr>';
        html += '                            <td colspan="3">'+formatContent(o.INTRODUCTION);
        html += '                            </td>';
        html += '                        </tr>';
        html += '                    </table>';
        html += '                </div><!-- 一則好事例end -->';

		return html;
}

function getDownloadFile(FILE_ID,FILE_TYPE){
	var now = new Date();

	window.location  = fileContextRoot+'?isDownload=true&fileName='+FILE_ID+"."+FILE_TYPE+'&time='+now.getTime();
}


function getNewsTemplate(seq, o) {
		var html = '';
		if(o.NEWS_TYPE == 2){
			html += '<li style="cursor: pointer;font-size: 15px;" onclick="getDownloadFile(\''+o.FILE_ID+'\',\''+o.FILE_TYPE+'\')">【'+o.CREATED_DATEStr+" "+o.NEWS_TYPE_NAME+'】'+o.TOPIC+'</li>';
		}else{
			html += '<li style="cursor: pointer;font-size: 15px;" onclick="goToNewsDetail(\''+o.NEWS_M_ID+'\')">【'+o.CREATED_DATEStr+" "+o.NEWS_TYPE_NAME+'】'+o.TOPIC+'</li>';
		}
		return html;
}

function getBulletinBoardTemplate(seq, o) {
		var html = '';
		html += '<li style="cursor: pointer;" onclick="goToBulletinBoardDetail(\''+o.NEWS_M_ID+'\')">【'+o.CREATED_DATEStr+" "+o.NEWS_TYPE_NAME+'】'+o.TOPIC+'</li>';
		return html;
}


function getBannerURL(i){
	var contextRoot = '<%= renderRequest.getContextPath()%>';
	$.post(contextRoot+"/imageServlet", {'isUrl' : "true","urlName":"banner_url_"+i}, function(data) {
		var dataObj = $.parseJSON(data);
		if (dataObj.result) {
			var url = dataObj.data;
			$('#banner_link'+i).attr('href',url);	
		}
	});
}


$(document).ready(function(){
	var contextRoot = '<%= renderRequest.getContextPath()%>';
	
	var eFormUrlStr = "http://eform.shiseido.com.tw/login.php<%=eFormUrl.toString()%>";
	
	$(self).parent().find( "li.hideFlag" ).each(function(){
		if($(this).css("display") == "none" ){
			$(this).show();
			$(self).html("▲LESS");
		}else{
			$(this).hide();
			$(self).html("▼MORE");
		}
    });

    $("#navbar").find( "li a:contains('資源預約')" ).each(function(){
		$(this).attr("href",eFormUrlStr).attr("target", "_blank");;
    });

	for(var i =1;i<6;i++){
		getBannerURL(i);
	}
	
	//檢查是否登入者含有eamil
	$.post(contextRoot+"/shdBcServlet", {'action' : "checkHasMail"}, function(data) {
		var dataObj = $.parseJSON(data);
		if (dataObj.result) {
			var result = dataObj.data;
			if(result){
				$('#sidenav').show();
			}else{
				$('#sidenav').hide();
				$('#content').width("95%");
				
			}
		}else{
			$('#sidenav').hide();
			$('#content').width("95%");
		}
	});
	
	//取得發佈中筆數
	$('#unrelease_pagination').paging({
			url : contextRoot+"/shdBcServlet" ,
			conditionBlockId : 'unreleaseForm',
			searchButtonId : 'unreleaseButton',
			resultBodyId  : 'unreleaseResultBody',
			pageAlwaysShow : true,
			pageSize : 4,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
			},
			getPagingHtml : function() {
			},
			doWhenNotFoundData : function() {
			},
			doOtherAfterQuery : function(data) {
				if(data.pagination.totalRow > 0){
					$('.noti_bubble').show().empty().append(data.pagination.totalRow+"筆待審核");
				}else{
					$('.noti_bubble').hide();
				}
			},
			eachPage : {
				id : 'eachPage',
				size : '10',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
	});
	$('#unreleaseButton').trigger("click");
	
	//BC列表
	$('#bc_pagination').paging({
			url : contextRoot+"/shdBcServlet" ,
			conditionBlockId : 'bcForm',
			searchButtonId : 'bcButton',
			resultBodyId  : 'bcResultBody',
			pageAlwaysShow : true,
			pageSize : 2,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getBcTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			
			},
			doOtherAfterQuery : function() {
			},
			loadmask : 'bcResultBody',
			eachPage : {
				id : 'eachPage',
				size : '2',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
	});
	$('#bcButton').trigger("click");
	
	
	//企業新聞
	$('#news_pagination').paging({
			url : contextRoot+"/shdNewsServlet" ,
			conditionBlockId : 'newsForm',
			searchButtonId : 'newsButton',
			resultBodyId  : 'newsResultBody',
			pageAlwaysShow : true,
			pageSize : 3,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getNewsTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			
			},
			doOtherAfterQuery : function() {
			},
			loadmask : 'newsResultBody',
			eachPage : {
				id : 'eachPage',
				size : '3',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
	});
	$('#newsButton').trigger("click");
	
	
	//電子佈告欄
	$('#bulletinBoard_pagination').paging({
			url : contextRoot+"/shdNewsServlet" ,
			conditionBlockId : 'bulletinBoardForm',
			searchButtonId : 'bulletinBoardButton',
			resultBodyId  : 'bulletinBoardResultBody',
			pageAlwaysShow : true,
			pageSize : 3,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getBulletinBoardTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			
			},
			doOtherAfterQuery : function() {
			},
			loadmask : 'bulletinBoardResultBody',
			eachPage : {
				id : 'eachPage',
				size : '3',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
	});
	$('#bulletinBoardButton').trigger("click");
	
});

function showMoreLink(self){
	$(self).parent().find( "li.hideFlag" ).each(function(){
		if($(this).css("display") == "none" ){
			$(this).show();
			$(self).html("▲LESS");
		}else{
			$(this).hide();
			$(self).html("▼MORE");
		}
    });
}

</script>



<style>
.BCtable{
	cursor:pointer ;
	width: 100%;
}

.picDiv{
    height:100px;
    background-repeat: no-repeat;
    background-size: contain;
    background-position: right top;
}

.noti_bubble {
    position:absolute;    /* This breaks the div from the normal HTML document. */
    margin-left:160px;
    padding:3px;
    background-color:#dc0d17; /* you could use a background image if you'd like as well */
    color:white;
    font-weight:bold;
   	border-radius:4px;
    box-shadow:1px 1px 1px gray;
    display: none;
    text-shadow: 0 -1px 0 rgba(0, 0, 0, .4);
}

</style>

<div style="display:none;">
<form id="unreleaseForm" name="unreleaseForm" method="post" action="">
	<input id="action" type="hidden" name="action" value="getUnReleaseExTbBcModelMPagination"/>
	<input id="unreleaseButton" type="button" name="unreleaseButton" value="重新整理" style="display:none;"/>
			                        
	<div id="unreleaseResultBody"></div>
</form>
<div id="unrelease_pagination" class="pagesStyle"></div>
</div>


<article id="content">
                <!-- 企業新聞============== -->
                <section class="entry-container">
                    <h3 class="entry-title" >
                    	
                    <wps:urlGeneration contentNode="shd.news">
                    <a href="<% wpsURL.write(out); %>" class="more-link" style="color: #444444;">
                    	企業新聞
                    </a>
                    </wps:urlGeneration>
                    
                    </h3>
	                <div class="entry-content" >
	                	<form id="newsForm" name="newsForm" method="post" action="">
	                		<input id="action" type="hidden" name="action" value="getNewsPagination"/>
	                		<input id="authorName" type="hidden" name="authorName" value=""/>
	                		<input id="isFilterByEndDate" type="hidden" name="isFilterByEndDate" value="true"/>
	                		<input id="CREATED_DATE_STAR" type="hidden" name="CREATED_DATE_STAR" value=""/>
	                		<input id="CREATED_DATE_END" type="hidden" name="CREATED_DATE_END" value=""/>
	                		<input id="newsType" type="hidden" name="newsType" value=""/>
							<input id="newsButton" type="button" name="bcButton" value="" style="display:none;"/>
	                        <ul id="newsResultBody">
	                        
	                        </ul>
                        </form>
	                
	                </div>
	                <p class="entry-more-link">
	                   	<wps:urlGeneration contentNode="shd.news">
							<a href="<% wpsURL.write(out); %>" class="more-link">
								►More
							</a>
						</wps:urlGeneration>
	                </p>
                </section>
                <!-- 我的新聞============== -->
                <section class="entry-container">
                    <h3 class="entry-title">
                    
                    <wps:urlGeneration contentNode="shd.bulletinBoard">
	                    <a href="<% wpsURL.write(out); %>" class="more-link" style="color: #444444;">
	                    	電子佈告欄
	                    </a>
                    </wps:urlGeneration>
                    </h3>
                    <div class="entry-content">
                        <form id="bulletinBoardForm" name="bulletinBoardForm" method="post" action="">
	                		<input id="action" type="hidden" name="action" value="getBulletinBoardPagination"/>
	                		<input id="authorName" type="hidden" name="authorName" value=""/>
	                		<input id="isFilterByEndDate" type="hidden" name="isFilterByEndDate" value="true"/>
	                		<input id="CREATED_DATE_STAR" type="hidden" name="CREATED_DATE_STAR" value=""/>
	                		<input id="CREATED_DATE_END" type="hidden" name="CREATED_DATE_END" value=""/>
	                		<input id="newsType" type="hidden" name="newsType" value=""/>
							<input id="bulletinBoardButton" type="button" name="bcButton" value="" style="display:none;"/>
	                        <ul id="bulletinBoardResultBody">
	                        
	                        </ul>
                        </form>
                    </div>
                    <p class="entry-more-link">
	                   	<wps:urlGeneration contentNode="shd.bulletinBoard">
							<a href="<% wpsURL.write(out); %>" class="more-link">
								►More
							</a>
						</wps:urlGeneration>
	                </p>
                </section>

                <!-- BC好事例============== -->
                <section class="entry-container">
                	<span class="noti_bubble">0筆待審核</span>
                    <h3 class="entry-title">
                    <wps:urlGeneration contentNode="shd.bc">
                    <a href="<% wpsURL.write(out); %>" class="more-link" style="color: #444444;">
                    	資生堂好事例
                    </a>
                    </wps:urlGeneration>
                    </h3>
                    <div class="entry-content">
                        
                        <form id="bcForm" name="bcForm" method="post" action="">
	                		<input id="EDIT_STATUS" type="hidden" name="EDIT_STATUS" value="6"/>
	                		<input id="action" type="hidden" name="action" value="getExTbBcModelMByStatusPagination"/>
							<input id="bcButton" type="button" name="bcButton" value="重新整理" style="display:none;"/>
	                        <div id="bcResultBody"></div>
                        </form>
                        <br clear="all" />

                    </div>
                    <p class="entry-more-link">
                    	<wps:urlGeneration contentNode="shd.bc">
							<a href="<% wpsURL.write(out); %>" class="more-link">
								►More
							</a>
						</wps:urlGeneration>
                    </p>
                </section>
                <!-- 群組快捷選單============== -->
                <section class="entry-container">
                
                        
<%	
	javax.naming.Context ctx = new javax.naming.InitialContext();
	NavigationModelHome nmHome = (NavigationModelHome)ctx.lookup("portal:service/model/NavigationModel");  
	NavigationModel nm = nmHome.getNavigationModelProvider().getNavigationModel(request, response);

	Identification identification = (Identification) ctx.lookup( "portal:service/Identification" );
    NavigationNode rootNode = (NavigationNode) nm.getRoot();
	NavigationNode myRootNode = getNodeByName(nm, rootNode, "shd.portal.Home");
%>

<%
						
	if (rootNode != null && nm.hasChildren(rootNode)) {
	myRootNode = getNodeByName(nm, rootNode, "shd.more.tag");
	for (NavigationNode commPage: getChildrenNodes(nm, myRootNode )){
		String cTitle = commPage.getTitle(Locale.TRADITIONAL_CHINESE);
		String cId = getId(identification, commPage);
		if (getChildrenNodes(nm,commPage ).size() > 0){
%>
					<div class="groupLink" style="min-height: 170px;">
                    <h3 class="group-title" style="font-size: 100%;"><c:out value="<%=cTitle%>"/></h3>
                    <ul class="group-list" style="margin-bottom: 10px;">
<% 
					int commCount = 0;
					for (NavigationNode commChildPage: getChildrenNodes(nm,commPage )){
						commCount++;
						String ccTitle = commChildPage.getTitle(Locale.TRADITIONAL_CHINESE);
						String ccId = getId(identification, commChildPage);
						if(commCount >=7){
%>
					<li class="hideFlag" style="display:none;">
					<wps:urlGeneration contentNode="<%=ccId%>">
						<a style="color:#05386b;" href="<% wpsURL.write(out); %>">
							<c:out value="<%=ccTitle%>"/>
						</a>
						</wps:urlGeneration>	
					</li>	
					<%}else{%>	
					<li>
					<wps:urlGeneration contentNode="<%=ccId%>">
						<a style="color:#05386b;" href="<% wpsURL.write(out); %>">
							<c:out value="<%=ccTitle%>"/>
						</a>
						</wps:urlGeneration>	
					</li>	
					<%}}%>
					
					
					
					 </ul>
					 <% if(commCount > 7){%> <p class="group-more-link" style="cursor: pointer;" onclick="showMoreLink(this);">▼MORE</p>   <% } %>
                    	
                    </div>
<%}}}%>
                        

                       
                    
                   
                    
     
                    
                    <!-- 一行 end -->

                </section>
                <section class="entry-container">
                
                <!-- 
                	<iframe src="http://172.29.1.94/ibmcognos/cgi-bin/cognosisapi.dll?b_action=cognosViewer&ui.action=run&ui.object=%2fcontent%2ffolder%5b%40name%3d%27BI%20Phase%20II%27%5d%2ffolder%5b%40name%3d%27Dashboard%20%E7%AE%A1%E7%90%86%E5%84%80%E8%A1%A8%E6%9D%BF%27%5d%2ffolder%5b%40name%3d%27ISS%E5%88%86%E6%9E%90%27%5d%2freport%5b%40name%3d%27201.ISS%E4%BD%94%E6%AF%94%E5%88%86%E6%9E%90test%27%5d&ui.name=201.ISS%E4%BD%94%E6%AF%94%E5%88%86%E6%9E%90test&run.outputFormat=&run.prompt=false&p_P_Channel=%5BISS(Cube)%5D.%5B%E7%B5%84%E7%B9%94%5D.%5B%E4%BE%9D%E9%80%9A%E8%B7%AF%E5%8D%80%E5%88%86%5D.%5B%E5%A4%A7%E9%80%9A%E8%B7%AF%5D-%3E%3A%5BPC%5D.%5B%40MEMBER%5D.%5B1%5D&CAMNamespace=MSAD&CAMUsername=BI_TOP&CAMPassword=KP2wsx!QAZ
" width="500" height="600" scrolling="yes" frameborder="0"> 
					</iframe> 
                 -->
                </section>
                
                
                
                <br clear="all" />
            </article>


            <aside id="sidenav">
                <!-- 郵件信箱============== -->
                <div class="aRow">
                	
                	<IFRAME src="<%="https://my.shiseido.com.tw/cgi-bin/shiseido_portal"+sbRemoteUrl1.toString() %>" width="100%" height="160px"></IFRAME>
                
                   
                </div>

                <!-- 行事曆============== -->
                <div class="aRow">
                    <IFRAME src="<%="https://my.shiseido.com.tw/cgi-bin/shiseido_portal"+sbRemoteUrl2.toString() %>" width="100%" height="160px"></IFRAME>
                </div>
                <br clear="all" />
            </aside>



