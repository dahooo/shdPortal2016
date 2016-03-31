<%@page session="false" contentType="text/html" pageEncoding="UTF-8" import="java.util.*,javax.portlet.*,com.ibm.shdwebportlet.*" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>        
<%@taglib uri="http://www.ibm.com/xmlns/prod/websphere/portal/v6.1/portlet-client-model" prefix="portlet-client-model" %>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="wps"%>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="portal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
<portlet:defineObjects/>
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css" />
<link href="<%=renderRequest.getContextPath()%>/css/bc.css" rel="stylesheet" type="text/css">
<link href="<%=renderRequest.getContextPath()%>/css/news_web.css" rel="stylesheet" type="text/css" />
<link href="<%=renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.css" type="text/css" rel="stylesheet" />

<script>
var detailId = '${NEWS_ID}';
var urlRoot = '<%=renderRequest.getScheme() + "://"
					+ renderRequest.getServerName() + ":"
					+ renderRequest.getServerPort()
					+ renderRequest.getContextPath() + "/fileServlet"%>';

var contextRoot = '<%=renderRequest.getContextPath() + "/shdNewsServlet"%>';
var fileContextRoot = '<%=renderRequest.getContextPath() + "/fileServlet"%>';

</script>


<article id="newsDetailContent" class="bc_content" style="display:none;">

<section class="entry-container">
<h3 class="entry-title">電子佈告欄</h3>
<div class="bc-entry-content">

	<div class="BCDetailTable">
		<span class="AOtime" id="detail_6"></span>
		<h2 id="detail_1" class="newsListTitle"
			style="font-size: 24px; padding: 10px 0 0 0; margin-bottom: 20px; line-height: 26px;"></h2>

		<div id="detail_5"></div>

		<div class="URLfrom" id="detail_4"></div>
	</div>
	<!-- /BCDetailTable end -->
</div>
</section>

<br clear="all" />

<div class="newsTable">
	<div class="div_td" style="color: #909090;">
		<span id="detail_2"></span> | <span style="margin-left: 10px;"
			id="detail_3"></span> <br />
		<table id="filesTable" class="newsTable"></table>
	</div>
</div>

<div style="width: 100%; text-align: right;">
	<input type="button" onclick="backToMainList();" class="bcButton" value="▲回到列表">
</div>


</article>


<article id="newsListContent" class="bc_content"> <section
	class="entry-container">
<h3 class="entry-title">電子佈告欄列表</h3>
<div class="news-entry-content">
	<div class="newsTable">
		<div id="newsBody"></div>
	</div>

</div>

<div id="news_pagination" align="center" class="pagesStyle"></div>

<form id="newsForm" name="newsForm" method="post" action="">
	<input id="action" type="hidden" name="action" value="getBulletinBoardPagination" />
	<input id="isFilterByEndDate" type="hidden" name="isFilterByEndDate" value="true"/>
	<div class="newsTable">
		<div class="div_tr">
			<div class="div_td">
				<input id="authorName" type="text" name="authorName"
					placeholder="篩選作者或標題">
			</div>
		</div>
		<div class="div_tr">
			<div class="div_td">
				<input id="CREATED_DATE_STAR" type="text" name="CREATED_DATE_STAR"
					value="" placeholder="請輸入起始日期" /> &nbsp;<input id="CREATED_DATE_END"
					type="text" name="CREATED_DATE_END" value="" placeholder="請輸入結束日期" />
			</div>
		</div>
		<div class="div_tr">
			<div class="div_td">
				<select id="newsType" name="newsType"></select>
			</div>
			<div class="div_td">
				<input id="newsButton" type="button" name="newsButton" value="篩選"
					class="bcButton">
			</div>
		</div>
	</div>
	<!-- /newsTable end-->

</form>

</section>

<br clear="all" />

</article>



<script type="text/javascript" src="<%=renderRequest.getContextPath()%>/utils/jquery.ev.pagination.js"></script>
<script type="text/javascript" src="<%=renderRequest.getContextPath()%>/utils/jquery.ev.pagination.template.js"></script>
<script type="text/javascript" src="<%=renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.js"></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="<%=renderRequest.getContextPath()%>/js/jquery.json.min.js" ></script>
<script src="<%=renderRequest.getContextPath()%>/js/cle/jquery.cleditor.js" type="text/javascript"></script>
<script src="<%=renderRequest.getContextPath()%>/js/bulletinBoardJsScript.js" ></script>