<%@page session="false" contentType="text/html" pageEncoding="UTF8" import="java.util.*,javax.portlet.*,com.ibm.shdwebportlet.*" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>        
<%@taglib uri="http://www.ibm.com/xmlns/prod/websphere/portal/v6.1/portlet-client-model" prefix="portlet-client-model" %>        
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="wps"%>
<%@ taglib uri="/WEB-INF/tld/portal.tld" prefix="portal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<portlet:defineObjects/>

<link href="<%= renderRequest.getContextPath()%>/css/bc.css" rel="stylesheet" type="text/css">
<link href="<%= renderRequest.getContextPath()%>/css/tabs.css" rel="stylesheet" type="text/css" />
<link href="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css" />

<link href="<%= renderRequest.getContextPath()%>/css/jquery-impromptu.min.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/jquery.ev.pagination.template.js"></script>
<script type="text/javascript" src="<%= renderRequest.getContextPath()%>/utils/loadmask/jquery.loadmask.js"></script>

<script src="<%= renderRequest.getContextPath()%>/js/jquery.form.js" ></script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="<%= renderRequest.getContextPath()%>/js/script.js"></script>
<script src="<%= renderRequest.getContextPath()%>/js/fileUploadScript.js" ></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery.json.min.js" ></script>
<script src="<%= renderRequest.getContextPath()%>/js/jquery-impromptu.min.js" ></script>
<script>
	var detailId = '${BC_M_ID}';

	var contextRoot = '<%= renderRequest.getContextPath()%>'+'/shdBcServlet';
	
	function picUploadButton(){
		if (!jQuery('#myfile').val()) {
			alert('請上傳圖片');
		}else{
			mask('BCDetailTableImg');
			$('#UploadForm').submit();
		}
	}
	
	function newBC(){
		$('#editTable_1').hide();
		$('.tabs').hide();
		$('#newBcDetailContent').show();
		$('#tempBtn').show();
		$('#deleteTempBtn').hide();
	}


	function backToMainListNoneQuery(){
		$('#editTable_1').show();
		$('.tabs').show();
		$('#bcDetailContent').hide();
		$('#newBcDetailContent').hide();
	}

	function backToMainList(){
		$('#editTable_1').show();
		$('.tabs').show();
		$('#bcDetailContent').hide();
		$('#newBcDetailContent').hide();
			
		$('#searchButton2').trigger("click");
		$('#status5Button').trigger("click");
		$('#status4Button').trigger("click");
		$('#status0Button').trigger("click");
		$('#unreleaseButton').trigger("click");
		resetNewBcForm();
	}
	
	
	function getEditBCDetail(BC_M_ID){
		$('#editTable_1').hide();
		$('.tabs').hide();
		$('#bcDetailContent').hide();
		$('#newBcDetailContent').show();
		
		mask('newBcDetailContent');
		$.post(contextRoot, {
				'action' : "getExTbBcModelMById",
				'BC_M_ID' : BC_M_ID
			}, function(data) {
				unmask('newBcDetailContent');
				var dataObj = $.parseJSON(data);
				if (dataObj.result) {
					var ett = dataObj.data;
					var level = dataObj.levelData;
					if(level.empNo == ett.CREATED_ID){
						$('#tempBtn').show();
						$('#deleteTempBtn').show();
					}
					var now = new Date();
					$('#BC_M_ID').val(ett.BC_M_ID);
					$('#NAME').val(ett.NAME);
					$('#uploadedImg').empty().append('<img width="300" src="/wps/PA_shdWeb/imageServlet?imageName='+ett.PIC_NAME+'&time='+now.getTime()+'" >');
					$('#SENIORITY').val(ett.SENIORITY);
					$('#BC_GROUP').val(ett.BC_GROUP);
					$('#STORE_NAME').val(ett.STORE_NAME);
					$('#CREATED_DATE_STR').val(ett.CREATED_DATEStr);
					$('#TOPIC').val(ett.TOPIC);
					$('#INTRODUCTION').val(ett.INTRODUCTION);
					$('#NEW_UPLOAD_PIC_NAME').val(ett.PIC_NAME);
				}
		});
		
		
	}
	
	
	
	
	
	function getBCDetail(BC_M_ID,isFromIndex){
		
		$('.tabs').hide();
		$('#bcDetailContent').show();
		$('#newBcDetailContent').hide();
		$('#editTable_1').hide();
		
		mask('bcDetailContent');
		$.post(contextRoot, {
				'action' : "getExTbBcModelMById",
				'BC_M_ID' : BC_M_ID
			}, function(data) {
				unmask('bcDetailContent');
				var dataObj = $.parseJSON(data);
				if (dataObj.result) {
					var level = dataObj.levelData;
					var ett = dataObj.data;
					var now = new Date();
					$('#detail_1').empty().append(ett.NAME);
					$('#detail_2').empty().append('<div class="bcPicDiv" style="background-image: url(/wps/PA_shdWeb/imageServlet?imageName='+ett.PIC_NAME+'&time='+now.getTime()+');"></div>');
					$('#detail_3').empty().append(ett.SENIORITY);
					$('#detail_4').empty().append(ett.BC_GROUP);
					$('#detail_5').empty().append(ett.STORE_NAME);
					$('#detail_6').empty().append(ett.CREATED_DATEStr);
					$('#detail_8').empty().append(ett.TOPIC);
					$('#detail_9').empty().append(ett.INTRODUCTION);
					if(ett.EDIT_STATUS != 6){
						//get log
						getHistoryLog(BC_M_ID);
					}else{
						$('#detail_10').empty();
						$('#detail_title10').hide();
					}
					var btnHtml = ''; 
					if(ett.EDIT_STATUS == 1 || ett.EDIT_STATUS == 3 || ett.EDIT_STATUS == 7){
						if(level.roleLevel == 'A'){
							btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'5\');" id="btn4" class="bcButton" value="未入選"/>';
							btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'6\');" id="btn5" class="bcButton" value="發佈"/>';
						}
					}
					if(ett.EDIT_STATUS == 1 || ett.EDIT_STATUS == 3 || ett.EDIT_STATUS == 7){
						if(level.empNo != ett.CREATED_ID && level.roleLevel != 'A' && level.roleLevel != 'D' && level.empNo != ett.UPDATED_ID){
							btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'3\');" id="btn2" class="bcButton" value="同意"/>';	
						}
						if(level.empNo != ett.CREATED_ID && level.roleLevel != 'D' && level.empNo != ett.UPDATED_ID){
							btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'2\');" id="btn2" class="bcButton" value="退回"/>';
						}
					}
					
					if(ett.EDIT_STATUS == 1 || ett.EDIT_STATUS == 3 || ett.EDIT_STATUS == 7){
						if(level.empNo != ett.CREATED_ID && level.roleLevel != 'D' && level.empNo != ett.UPDATED_ID){
							btnHtml += '<input type="button" onclick="getEditBCDetail(\''+BC_M_ID+'\');" id="btn3" class="bcButton" value="編輯後送出"/>';
						}
					}
					
					if(ett.EDIT_STATUS != 5 && ett.EDIT_STATUS != 4 && ett.EDIT_STATUS == 2 && level.empNo == ett.CREATED_ID){//被退回而且是本人
							btnHtml += '<input type="button" onclick="getEditBCDetail(\''+BC_M_ID+'\');" id="btn3" class="bcButton" value="編輯後送出"/>';
							btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'4\');" id="btn2" class="bcButton" value="作廢"/>';
					}
					
					if(ett.EDIT_STATUS != 5 && ett.EDIT_STATUS != 4 && ett.EDIT_STATUS != 6 && level.roleLevel == 'A' && level.empNo == ett.CREATED_ID ){//塗老師等級本人
						btnHtml += '<input type="button" onclick="getEditBCDetail(\''+BC_M_ID+'\');" id="btn3" class="bcButton" value="編輯後送出"/>';
						btnHtml += '<input type="button" onclick="setBcStatus(\''+BC_M_ID+'\',\'4\');" id="btn2" class="bcButton" value="作廢"/>';
					}
					
					if(isFromIndex){
						btnHtml += '<input type="button" onclick="backToMainList();" class="bcButton" value="▲回到列表"/>'; 
					}else{
						btnHtml += '<input type="button" onclick="backToMainListNoneQuery();" class="bcButton" value="▲回到列表"/>'; 
					}
					
					
					
					
					$('#editBcControlBtns').empty().append(btnHtml);
				}
		});
		
		
	}
	
	function getHistoryLog(BC_M_ID){
		$('#detail_10').empty();
		$.post(contextRoot, {
					'action' : "getHistoryLog",
					'BC_M_ID' : BC_M_ID
		}, function(data) {
			var dataObj = $.parseJSON(data);
			if (dataObj.result) {
				var resultList = dataObj.data;
				for(var i=0; i< resultList.length; i++){
					var logs = resultList[i];
				
					var htmlStr = '<li><div class="AOstatus" >'+logs.ACTIONStr+'</div>';
	                    htmlStr +='<p class="AOname">'+logs.empCname+'('+logs.USER_ID+') ．<span class="AOtime">'+logs.CREATED_TIMEStr+'</span></p>';
	                    htmlStr +='<p class="AOcontent" >';
	                    htmlStr += logs.CONTENT;
	                    htmlStr +='</p></li>';
	                    
						$('#detail_title10').show();
						$('#detail_10').append(htmlStr);	
				}
			}
		});
	}
	
	
	function add3Dots(string, limit){
	  	var dots = "...";
	  	if(string.length > limit  && $( window ).width() < 400){
	    	string = string.substring(0,limit) + dots;
	  	}
	    return string;
	}

	
	function getShowStatusTemplate(seq, o) {
		var now = new Date();
		var html = '';
	
		html += '<div class="bcListRwd" onclick="getBCDetail(\''+o.BC_M_ID+'\')">';
		html += '<div class="bcPicDiv" style="background-image: url(/wps/PA_shdWeb/imageServlet?imageName='+o.PIC_NAME+'&time='+now.getTime()+');"></div>';

        html += '<div class="bcListRwdContent">';
        if(o.EDIT_STATUS == '2'){
        	html += '<span class="status_bubble">'+o.EDIT_STATUSStr+'By '+o.empCname+'</span>';
        }else{
        	html += '<span style="background-color: green;" class="status_bubble">'+o.EDIT_STATUSStr+'By '+o.empCname+'</span>';
        }
        html += '<span class="bcListRwdTime">發布日期｜'+o.CREATED_DATEStr+'</span>';
        html += '<h2 class="bcListRwdTitle">'+add3Dots(o.TOPIC,19)+'</h2>';
        html += '<p class="bcListRwdName">作者: '+o.NAME+'</p>';
        html += '<p class="bcListRwdName">課別: '+o.BC_GROUP+'</p>';
        html += '<p class="bcListRwdName">店名: '+o.STORE_NAME+'</p>';
        html += '</div>';
        html += '</div>';
	
		return html;
	}
	
	function getEditTemplate(seq, o) {
		var now = new Date();
		var html = '';
		html += '<div class="bcListRwd" onclick="getEditBCDetail(\''+o.BC_M_ID+'\')">';
		html += '<div class="bcPicDiv" style="background-image: url(/wps/PA_shdWeb/imageServlet?imageName='+o.PIC_NAME+'&time='+now.getTime()+');"></div>';

        html += '<div class="bcListRwdContent">';
        if(o.EDIT_STATUS == '2'){
        	html += '<span class="status_bubble">'+o.EDIT_STATUSStr+'By '+o.empCname+'</span>';
        }else{
        	html += '<span style="background-color: green;" class="status_bubble">'+o.EDIT_STATUSStr+'By '+o.empCname+'</span>';
        }
        html += '<span class="bcListRwdTime">發布日期｜'+o.CREATED_DATEStr+'</span>';
        html += '<h2 class="bcListRwdTitle">'+add3Dots(o.TOPIC,19)+'</h2>';
        html += '<p class="bcListRwdName">作者: '+o.NAME+'</p>';
        html += '<p class="bcListRwdName">課別: '+o.BC_GROUP+'</p>';
        html += '<p class="bcListRwdName">店名: '+o.STORE_NAME+'</p>';
        html += '</div>';
        html += '</div>';
	
		return html;
	}
	
	
	
	function getReleaseTemplate(seq, o) {
		var now = new Date();
		var html = '';
		
		html += '<div class="bcListRwd" onclick="getBCDetail(\''+o.BC_M_ID+'\')">';
        html += '<div class="bcPicDiv" style="background-image: url(/wps/PA_shdWeb/imageServlet?imageName='+o.PIC_NAME+'&time='+now.getTime()+');"></div>';
        html += '<div class="bcListRwdContent">';
        html += '<span class="bcListRwdTime">發布日期｜'+o.CREATED_DATEStr+'</span>';
        html += '<h2 class="bcListRwdTitle">'+add3Dots(o.TOPIC,19)+'</h2>';
        html += '<p class="bcListRwdName">作者: '+o.NAME+'</p>';
        html += '<p class="bcListRwdName">課別: '+o.BC_GROUP+'</p>';
        html += '<p class="bcListRwdName">店名: '+o.STORE_NAME+'</p>';
        html += '</div>';
        html += '</div>';
	
		return html;
	}
	
	$(document).ready(function(){
		$( "#CREATED_DATE_STR" ).datepicker({ dateFormat: 'yy-mm-dd' });
		
		$.post(contextRoot, {'action' : "getBcLevel"}, function(data) {
			var dataObj = $.parseJSON(data);
			if (dataObj.result) {
				if(dataObj.bvLevel.roleLevel == "D" || 
					dataObj.bvLevel.roleLevel == "C" ||
					dataObj.bvLevel.roleLevel == "B" ||
					dataObj.bvLevel.roleLevel == "A"){
					
					$('.tLi').show();
					$('#editTable_1').empty().append('<input type="button" onclick="newBC();" class="bcButton" value="新增好事例" />');
				}else{
					$('.tLi').hide();
				}
			}
		});
		
		
		$('#release_pagination').paging({
				url : contextRoot,
				conditionBlockId : 'searchForm2',
				searchButtonId : 'searchButton2',
				resultBodyId  : 'releaseBody',
				pageAlwaysShow : true,
				pageSize : 4,
				pageAlwaysLink : true,
				getResultBodyHtml : function(seq, entry) {
					return getReleaseTemplate(seq, entry);
				},
				getPagingHtml : function() {
					return getBackendTemplate2();
				},
				doWhenNotFoundData : function(e) {
				},
				doOtherAfterQuery : function(e) {
				},
				loadmask : 'bcListContent',
				eachPage : {
					id : 'eachPage',
					size : '5',
					prefix : '&nbsp;',
					delim : '&nbsp;',
					suffix : '&nbsp'
				}
		});
		
		
		$('#status5_pagination').paging({
			url : contextRoot,
			conditionBlockId : 'status5Form',
			searchButtonId : 'status5Button',
			resultBodyId  : 'status5Body',
			pageAlwaysShow : true,
			pageSize : 4,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getShowStatusTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			},
			doOtherAfterQuery : function(data) {
			},
			loadmask : 'bcStatus5Content',
			eachPage : {
				id : 'eachPage',
				size : '5',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
		});
		
		
		$('#unrelease_pagination').paging({
			url : contextRoot,
			conditionBlockId : 'unreleaseForm',
			searchButtonId : 'unreleaseButton',
			resultBodyId  : 'unreleaseResultBody',
			pageAlwaysShow : true,
			pageSize : 4,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getShowStatusTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			},
			doOtherAfterQuery : function(data) {
				if(data.pagination.totalRow > 0){
					$('.noti_bubble').show().empty().append(data.pagination.totalRow);
				}else{
					$('.noti_bubble').hide();
				}
			},
			loadmask : 'bcUnReleaseContent',
			eachPage : {
				id : 'eachPage',
				size : '5',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
		});
		
		
		
		$('#status4_pagination').paging({
			url : contextRoot,
			conditionBlockId : 'status4Form',
			searchButtonId : 'status4Button',
			resultBodyId  : 'status4Body',
			pageAlwaysShow : true,
			pageSize : 4,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getShowStatusTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			},
			doOtherAfterQuery : function() {
				
			},
			loadmask : 'bcStatus4Content',
			eachPage : {
				id : 'eachPage',
				size : '5',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
		});
		
		$('#status0_pagination').paging({
			url : contextRoot,
			conditionBlockId : 'status0Form',
			searchButtonId : 'status0Button',
			resultBodyId  : 'status0Body',
			pageAlwaysShow : true,
			pageSize : 4,
			pageAlwaysLink : true,
			getResultBodyHtml : function(seq, entry) {
				return getEditTemplate(seq, entry);
			},
			getPagingHtml : function() {
				return getBackendTemplate2();
			},
			doWhenNotFoundData : function() {
			},
			doOtherAfterQuery : function() {
				
			},
			loadmask : 'bcStatus0Content',
			eachPage : {
				id : 'eachPage',
				size : '5',
				prefix : '&nbsp;',
				delim : '&nbsp;',
				suffix : '&nbsp'
			}
		});
		
		
		
		if(detailId.length > 0){
			getBCDetail(detailId,true);
		}else{
			$('#searchButton2').trigger("click");
			$('#status5Button').trigger("click");
			$('#status4Button').trigger("click");
			$('#status0Button').trigger("click");
			$('#unreleaseButton').trigger("click");
		}
	});

	function checkValue(obj){
		if(obj.val() == ""){ 
			obj.css("border", "1px solid red");
			return 1;
		}else{
			obj.css("border",'');
			return 0;
		}
	}
	
	function getShdBcDataParam(shdBcDataParam){
		var validCount = 0;
		//validCount += checkValue($('#NAME'));
		//validCount += checkValue($('#BC_GROUP'));
		//validCount += checkValue($('#SENIORITY'));
		//validCount += checkValue($('#STORE_NAME'));
		//validCount += checkValue($('#CREATED_DATE_STR'));
		//validCount += checkValue($('#TOPIC'));
		//validCount += checkValue($('#INTRODUCTION'));

		if($('#NEW_UPLOAD_PIC_NAME').val() == ""){ 
			$('#message').empty().append("<span style='color:red;'>*請上傳圖片</span>");
			validCount += 1;
		}else{
			$('#message').empty();
		}
		if(validCount == 0){
			shdBcDataParam.BC_M_ID = $('#BC_M_ID').val();
			shdBcDataParam.NAME = $('#NAME').val();
			shdBcDataParam.BC_GROUP = $('#BC_GROUP').val();
			shdBcDataParam.SENIORITY = $('#SENIORITY').val();
			shdBcDataParam.STORE_NAME = $('#STORE_NAME').val();
			shdBcDataParam.TOPIC = $('#TOPIC').val();
			shdBcDataParam.NEW_UPLOAD_PIC_NAME = $('#NEW_UPLOAD_PIC_NAME').val();
			shdBcDataParam.INTRODUCTION = $('#INTRODUCTION').val();
			return shdBcDataParam;	
		}else{
			alert("請填入必填欄位.");
			return false;
		}
	}
	function resetNewBcForm(){
		$('#BC_M_ID').val("");
		$('#NAME').val("").css("border",'');
		$('#BC_GROUP').val("").css("border",'');
		$('#SENIORITY').val("").css("border",'');
		$('#STORE_NAME').val("").css("border",'');
		$('#CREATED_DATE_STR').val("").css("border",'');
		$('#TOPIC').val("").css("border",'');
		$('#NEW_UPLOAD_PIC_NAME').val("");
		$('#INTRODUCTION').val("").css("border",'');
		$('#HISTORY_COMMENT').val("");
		$('#uploadedImg').empty();
		$('#message').empty();
		$("#UploadForm").trigger('reset');
		
		$('#detail_1').empty();
		$('#detail_2').empty();
		$('#detail_3').empty();
		$('#detail_4').empty();
		$('#detail_5').empty();
		$('#detail_6').empty();
		$('#detail_8').empty();
		$('#detail_9').empty();
		$('#detail_10').empty();
		$('#editBcControlBtns').empty();
	}
	
	
	
	function deleteTempBcData(){
		var shdBcDataParam = new Object;

		if(confirm("您確定要刪除草稿嗎?")){
			shdBcDataParam.BC_M_ID = $('#BC_M_ID').val();
			mask('shdBcDataTable');
			$.post(contextRoot, {
				'action' : "deleteTempExTbBcModelM",
				'data' : $.toJSON(shdBcDataParam)
			}, function(data) {
				unmask('shdBcDataTable');
				var dataObj = $.parseJSON(data);
				if (dataObj.result) {
					alert("刪除完畢!");
					backToMainList();
				}else{
					alert("刪除失敗!");
				}
			});	
		}
	}

	function tempBcData(){
		var shdBcDataParam = new Object;
		if(getShdBcDataParam(shdBcDataParam) != false){
			var shdBcDataParam = getShdBcDataParam(shdBcDataParam);
			mask('shdBcDataTable');
			$.post(contextRoot, {
					'action' : "tempExTbBcModelM",
					'data' : $.toJSON(shdBcDataParam)
				}, function(data) {
					unmask('shdBcDataTable');
					var dataObj = $.parseJSON(data);
					if (dataObj.result) {
						alert("儲存完畢!");
						backToMainList();
					}else{
						alert("儲存失敗!");
					}
			});
		}
	}



	function saveBcData(){
		var shdBcDataParam = new Object;
		if(getShdBcDataParam(shdBcDataParam) != false){
			var shdBcDataParam = getShdBcDataParam(shdBcDataParam);
			mask('shdBcDataTable');
			$.post(contextRoot, {
					'action' : "saveExTbBcModelM",
					'data' : $.toJSON(shdBcDataParam)
				}, function(data) {
					unmask('shdBcDataTable');
					var dataObj = $.parseJSON(data);
					if (dataObj.result) {
						alert("儲存完畢!");
						backToMainList();
					}else{
						alert("儲存失敗!");
					}
			});
		}
	}
	
	function setBcStatus(BC_M_ID,status){
		var shdBcDataParam = new Object;
		shdBcDataParam.BC_M_ID = BC_M_ID;
		shdBcDataParam.EDIT_STATUS = status;
		
		$.prompt(statesdemo,{
			close: function(e,v,m,f){
				if(f != undefined){
					shdBcDataParam.HISTORY_COMMENT = f.HISTORY_COMMENT;
					mask('shdBcDataTable');
					$.post(contextRoot, {
								'action' : "updateExTbBcModelMStatus",
								'data' : $.toJSON(shdBcDataParam)
					}, function(data) {
						unmask('shdBcDataTable');
						var dataObj = $.parseJSON(data);
						if (dataObj.result) {
							alert("已送出!");
							backToMainList();
						}else{
							alert("送出失敗!");
						}
					});
				}
			}
		});
	}
</script>

			<div class="newBCButton" id="editTable_1">
				
			</div>
			
			
			<ul class="tabs">
		        <li>
		          <input type="radio" checked name="tabs" id="tab1">
		          <label for="tab1">列表</label>
		          <div id="tab-content1" class="tab-content animated fadeIn">
		   			
		   			<article id="bcListContent" class="bc_content">
				
		                <!-- BC好事例============== -->
		                <section class="entry-container">
		                    <div class="bc-entry-content">
		                    	
		                        <form id="searchForm2" name="searchForm2" method="post" action="">
			                		<input id="action" type="hidden" name="action" value="getReleaseExTbBcModelMPagination"/>
									<input id="searchButton2" type="button" name="searchButton2" value="重新整理" style="display:none;"/>
			                        
			                        <div id="releaseBody"></div>
		                        </form>
		                    </div>
		  
		                </section>
					<div id="release_pagination" class="pagesStyle"></div>
		            </article>
		   			
		   			
		   			
		          </div>
		        </li>
		        <li class="tLi">
		          <input type="radio" name="tabs" id="tab2">
		          <label for="tab2">待審核<span class="noti_bubble">0</span></label>
		          <div id="tab-content2" class="tab-content animated fadeIn">
		          	
		          	<article id="bcUnReleaseContent" class="bc_content">
		                <section class="entry-container">
		                    <div class="bc-entry-content">
		                        <form id="unreleaseForm" name="unreleaseForm" method="post" action="">
			                		<input id="action" type="hidden" name="action" value="getUnReleaseExTbBcModelMPagination"/>
									<input id="unreleaseButton" type="button" name="unreleaseButton" value="重新整理" style="display:none;"/>
			                        
			                        <div id="unreleaseResultBody"></div>
		                        </form>
		                    </div>
		  
		                </section>
					<div id="unrelease_pagination" class="pagesStyle"></div>
		            </article>
		          	
		          	
		          </div>
		        </li>
		        <li class="tLi">
		          <input type="radio" name="tabs" id="tab3">
		          <label for="tab3">未入選</label>
		          <div id="tab-content3" class="tab-content animated fadeIn">
		            
		            <article id="bcStatus5Content" class="bc_content">
              
		                <!-- BC好事例============== -->
		                <section class="entry-container">
		                    <div class="bc-entry-content">
		                        <form id="status5Form" name="status5Form" method="post" action="">
			                		<input id="action" type="hidden" name="action" value="getStatus5ExTbBcModelMPagination"/>
									<input id="status5Button" type="button" name="status5Button" value="重新整理" style="display:none;"/>
			                        
			                        <div id="status5Body"></div>
		                        </form>
		                    </div>
		  
		                </section>
					<div id="status5_pagination" class="pagesStyle"></div>
		            </article>
		            
		            
		          </div>
		        </li>
		        
		        <li class="tLi">
		          <input type="radio" name="tabs" id="tab4">
		          <label for="tab4">作廢</label>
		          <div id="tab-content3" class="tab-content animated fadeIn">
		            
		            <article id="bcStatus4Content" class="bc_content">
              
		                <!-- BC好事例============== -->
		                <section class="entry-container">
		                    <div class="bc-entry-content">
		                        <form id="status4Form" name="status4Form" method="post" action="">
			                		<input id="action" type="hidden" name="action" value="getStatus4ExTbBcModelMPagination"/>
									<input id="status4Button" type="button" name="status4Button" value="重新整理" style="display:none;"/>
			                        
			                        <div id="status4Body"></div>
		                        </form>
		                    </div>
		  
		                </section>
					<div id="status4_pagination" class="pagesStyle"></div>
		            </article>
		            
		            
		          </div>
		        </li>
		        
		        <li class="tLi">
		          <input type="radio" name="tabs" id="tab5">
		          <label for="tab5">草稿</label>
		          <div id="tab-content3" class="tab-content animated fadeIn">
		            
		            <article id="bcStatus0Content" class="bc_content">
              
		                <!-- BC好事例============== -->
		                <section class="entry-container">
		                    <div class="bc-entry-content">
		                        <form id="status0Form" name="status0Form" method="post" action="">
			                		<input id="action" type="hidden" name="action" value="getStatus0ExTbBcModelMPagination"/>
									<input id="status0Button" type="button" name="status0Button" value="重新整理" style="display:none;"/>
			                        
			                        <div id="status0Body"></div>
		                        </form>
		                    </div>
		  
		                </section>
					<div id="status0_pagination" class="pagesStyle"></div>
		            </article>
		            
		            
		          </div>
		        </li>
		        
		</ul>
	    <br clear="all" />




			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
            
            
            
            
            
            
            
            
            
			
			
			
			
<!-- -------------------------------------------------------------------------- -->			
			
			
			<article id="bcDetailContent" class="bc_content" style="display:none;">
				<section class="entry-container">
                    <h3 class="entry-title">資生堂好事例</h3>
                    <div class="bc-entry-content">
                    
                        <div class="BCDetail">
                           <div class="BCDetailTable" style=" box-sizing:border-box ;  line-height:20px; " >
                               <span style="line-height:20px; display:inline-block ; color:#808080 ; font-size:10px; ">發布日期｜<span id="detail_6"></span></span>
						       <h3 class="BCDetailTableH3" id="detail_8"></h3> <!--Jane--> 
                                
                               <!-- 作者區 start -->
                               <div id="authorDIV" >
                               	   <span id="detail_2"></span>
                                   
                                   <ul class="authorUl" style=" text-align:left ; float:left ; width:70%; ">
                                       <li><span style="color:#aa002b ; font-weight:bolder ;" id="detail_1"></span></li>
                                       <li>年資<span id="detail_3"></span>年</li>
                                       <li id="detail_4"></li>
                                       <li id="detail_5"></li>
                                   </ul>
                               </div><!-- 作者區 end -->
                               
                               <p id="detail_9"></p>
                               			        
                            </div><!-- /BCDetailTable end-->
                            <br clear="all" />
                           

                            <div id="detail_title10" class="auditOpinion" style=" width:100%; margin:20px auto ; border:none ; border-top:1px solid #aaa ; " >
                                <h3 class="auditOpinion-title">審核意見</h3>
                                <div class="div_tr">
                                    <div class="div_td">
                                        <ul class="AOul" id="detail_10">
                                            
                                        </ul>
                                    </div>
                                </div>
                                <br clear="all" />
                            </div><!-- /auditOpinion end-->

                            <br clear="all" />
                            
                            
                            <div id="editBcControlBtns" class="BCDetailTable" style="margin-top:10px;width:100%; text-align:right ; border-top:2px solid #aa002b ; padding-top:3px;">
                                
                            </div><!-- /BCDetailTable end -->
                        
                        </div>
                        
                    </div>
  
                </section>
			
				<br clear="all" />
            </article>
            
            
            
            <!-- new bc -->
            
            
            <article id="newBcDetailContent" class="bc_content" style="display:none;">
                <section class="entry-container">
                    <h3 class="entry-title">編輯好事例</h3>
                    <div class="bc-entry-content">
                    <input id="BC_M_ID" type="text" name="BC_M_ID" value="" style="display:none;"/>
                    <div class="BCDetail">
                 		<div id="shdBcDataTable" class="BCDetailTable" style="" >
                        		<div  class="div_tr" >
                                    <div class="div_th">姓名</div>
                                    <div class="div_td">
                                        <input id="NAME" type="text" name="NAME" value="" placeholder="請輸入姓名">
                                    </div>
						        </div>
						        <div  class="div_tr" >
                                    <div class="div_th">年資</div>
                                    <div class="div_td">
                                        <input id="SENIORITY" type="text" name="SENIORITY" value="" placeholder="請輸入年資(數字)">
                                    </div>
						        </div>
						        <div  class="div_tr" >
                                    <div class="div_th">課別</div>
						            <div class="div_td">
                                        <input id="BC_GROUP" type="text" name="BC_GROUP" value="" placeholder="請輸入課別">
                                    </div>
						        </div>
						        <div  class="div_tr" >
						            <div class="div_th">店名</div>
						            <div class="div_td">
                                        <input id="STORE_NAME" type="text" name="STORE_NAME" value="" placeholder="請輸入店名">
                                    </div>
						        </div>
						        <div  class="div_tr" >
						            <div  class="div_th" >發布日期</div>
						            <div  class="div_td" >
                                        <input id="CREATED_DATE_STR" type="text" name="CREATED_DATE_STR" value="" placeholder="請輸入發布日期">
                                    </div>
						        </div>
                                
						        <div  class="div_tr" >
						            <div  class="div_th" >主題</div>
						            <div class="div_td">
						        	    <input id="TOPIC" type="text" name="TOPIC" value="" placeholder="請輸入主題">
						            </div>
						        </div>
						        <div  class="div_tr" >
						            <div  class="div_th" >內文</div>
						            <div class="div_td">
						        	    <textarea id="INTRODUCTION" placeholder="請輸入內文"></textarea>
						            </div>
						        </div>
                            </div><!-- /BCDetailTable end (1)-->  	
                    		
                    		<br clear="all" />   
                            <div class="BCDetailTableImg" id="BCDetailTableImg">
                                <div id="uploadedImg">
                                </div>
                                <form id="UploadForm" action="<%= renderRequest.getContextPath()%>/UploadFile" method="post" enctype="multipart/form-data">
                                    <input type="file" size="60" id="myfile" name="myfile" class="bcButton" style="box-sizing: border-box; width:100% ; background-color:#fff; color:#aaa; border:0; box-shadow:none;">
                                    <input type="button" value="上傳圖片" class="bcButton" onclick="picUploadButton();">
								
                                    <div id="message" style="padding-top:10px;">
                                    </div>
                                    <input id="NEW_UPLOAD_PIC_NAME" type="text" name="NEW_UPLOAD_PIC_NAME" value="" style="display:none;">
                                </form>
                            </div><!-- /BCDetailTable end (2)-->   
                    		
                    		
                    		<div class="BCDetailTable" style="margin-top:10px;width:100%; text-align:center ; padding-top:3px;">
                    			<input id="deleteTempBtn" type="button" class="bcButton" value="刪除草稿" onclick="deleteTempBcData();" style="display:none;">
                    			<input id="tempBtn" type="button" class="bcButton" value="儲存成草稿" onclick="tempBcData();" style="display:none;">
                                <input type="button" class="bcButton" value="儲存" onclick="saveBcData();">
                                <input type="button" onclick="backToMainList();" class="bcButton" value="取消">
                            </div><!-- /BCDetailTable end -->
                    		
                    	
                    		
                      
                        </div>
                        
                    </div>
  
                </section>
			<br clear="all" />
            </article>



