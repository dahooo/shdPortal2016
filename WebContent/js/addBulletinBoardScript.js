var cleditor;
var relativeNode;
var selectedNode;

$(document).ready(function() {
	$("#CONTENT").cleditor({width:800, height:600});
	cleditor = $("#CONTENT").cleditor()[0];
	cleditor.focus();
	
	
	$('#newNewsDetailContent').hide();
	
	$("#nowDateTimeString").empty().append(getNewDateTimeString());
	$( "#END_DATE_STR" ).datepicker({ dateFormat: 'yy-mm-dd' ,minDate: 0});
	$( "#CREATED_DATE_STAR" ).datepicker({ dateFormat: 'yy-mm-dd' });
	$( "#CREATED_DATE_END" ).datepicker({ dateFormat: 'yy-mm-dd' });
		
	
	$.get(contextRoot,
	{ 'action' : "getBulletinBoardTypeList"},
	function(data){
		dataObj = $.parseJSON(data);
		
		if (dataObj.result) {
			var resultList = dataObj.data;
			for(var i=0; i< resultList.length; i++){
				var option1 = $('<option />');
				option1.val(resultList[i].NEWSTYPE_ID);
				option1.text(resultList[i].NAME);
				$("#NEWS_TYPE").append( option1 );
			}

			var defaultOpt = $('<option />');
			defaultOpt.val("");
			defaultOpt.text("全部");
			
			$("#newsType").append( defaultOpt );
			for(var i=0; i< resultList.length; i++){
				var option = $('<option />');
				option.val(resultList[i].NEWSTYPE_ID);
				option.text(resultList[i].NAME);
				$("#newsType").append( option );
			}
			
			$('#newsButton').trigger("click");	
		} else {
			alert("資料無法取得：" + data);
		}
	});
	
	
	var options = {
		beforeSend : function() {
			// clear everything
			$("#progressbar").width('0%');
			$("#message").empty();
			$("#percent").html("0%");
		},
		uploadProgress : function(event, position, total, percentComplete) {
			$("#message").fadeIn();
			$("#progressbox").fadeIn();
			$("#progressbar").width(percentComplete + '%');
			$("#percent").html(percentComplete + '%');
	
			// change message text and % to red after 50%
			if (percentComplete > 50) {
				$("#message").html("<font color='red'>檔案上傳中... </font>");
			}
		},
		dataType:  'json',
		success:   function(response) {
			$("#UploadForm").trigger('reset');
			if(response.result){
				$("#progressbox").fadeOut();
				$("#message").html("<font color='blue'>"+response.msg+"</font>");
				$("#message").fadeOut();
				htmlTemplate(response);
			}else{
				$("#progressbox").fadeOut();
				$("#progressbar").width('0%');
				$("#percent").html('0%');
				$("#message").html("<font color='red'>"+response.errorMsg+"</font>");
			}
		}
	};
	$("#UploadForm").ajaxForm(options);
	

	$('#news_pagination').paging({
		url : contextRoot,
		conditionBlockId : 'newsForm',
		searchButtonId : 'newsButton',
		resultBodyId  : 'newsBody',
		pageAlwaysShow : true,
		pageSize : 10,
		pageAlwaysLink : true,
		getResultBodyHtml : function(seq, entry) {
			return getNewsTemplate(seq, entry);
		},
		getPagingHtml : function() {
			return getBackendTemplate2();
		},
		doWhenNotFoundData : function(e) {
		},
		doOtherAfterQuery : function(e) {
		},
		loadmask : 'newsListContent',
		eachPage : {
			id : 'eachPage',
			size : '10',
			prefix : '&nbsp;',
			delim : '&nbsp;',
			suffix : '&nbsp'
		}
	});
	
});

function getAllDept(NEWS_M_ID){
	mask('tree3');
	$.post(contextRoot, {
		'action' : "getALLDeptList",
		'NEWS_M_ID' : NEWS_M_ID
	}, function(data) {
		var dataObj = $.parseJSON(data);
		if (dataObj.result) {
			$("#tree3").dynatree({
						checkbox : true,
						selectMode : 3,
						children : dataObj.data,
						onSelect : function(select, node) {
						    $(".dynatree-partsel:not(.dynatree-selected)").each(function () {
						        var node = $.ui.dynatree.getNode(this);
						        relativeNode.push(node.data.key);
						    });
						    
						    selectedNode = $.map(node.tree.getSelectedNodes(), function(node) {
								return node.data.key;
							});
						},
						onPostInit: function(isReloading, isError) {
							relativeNode = new Array();
				            selectedNode = new Array();
				            var tree = $('#tree3').dynatree('getTree');
				            var selNodes = tree.getSelectedNodes();

				            if(selNodes.length > 0){
				            	$(".dynatree-partsel:not(.dynatree-selected)").each(function () {
							        var node = $.ui.dynatree.getNode(this);
							        relativeNode.push(node.data.key);
							    });  	
				            	selectedNode = $.map(selNodes, function(node) {
				            		node.makeVisible();
									return node.data.key;
								});
				            }
				            unmask('tree3');
				        },
						onDblClick : function(node, event) {
							node.toggleSelect();
						},
						onKeydown : function(node, event) {
							if (event.which == 32) {
								node.toggleSelect();
								return false;
							}
						},
						cookieId : "dynatree-Cb3",
						idPrefix : "dynatree-Cb3-"
			});
		}
		$("#tree3").dynatree("getTree").reload();
	});
	
}


function deleteFileButton(id){
	$('#'+id).remove();
}


function htmlTemplate(response){
	var imageExtArr = ["gif", "jpg" ,"png","GIF", "JPG" ,"PNG"];
	var ext = imageExtArr.indexOf(response.ext);
	
	var now = new Date();
	var htmlStr = "";

	htmlStr += "<tr id='"+response.fileName+"'>";
	htmlStr += "<td style='width: 48px;'>";
	if(ext == -1){
		htmlStr += '<div class="fileType">'+response.ext+'</div>';
	}else{
		htmlStr += '<img width="80" src="'+fileContextRoot+'?isTemp=true&fileName='+response.fileName+"."+response.ext+'&time='+now.getTime()+'" />';
	}
	htmlStr += "</td>";
	
	htmlStr += "<td>";
	htmlStr += '檔名:<input class="fileDataInput" size="60" type="text" name="NEW_UPLOAD_FILE_NAME" value="'+response.fileName+'"/><br/>';
	if(ext != -1){
		htmlStr += '圖片網址:<br/>'+urlRoot+'?isTemp=true&fileName='+response.fileName+"."+response.ext+"";
	}
	
	htmlStr += '<input class="fileDataInput" style="display:none;" type="text" name="NEW_UPLOAD_FILE_ID" value="'+response.fileName+'"/>';
	htmlStr += '<input class="fileDataInput" style="display:none;" type="text" name="NEW_UPLOAD_FILE_EXT" value="'+response.ext+'"/>';
	htmlStr += "</td>";
	
	htmlStr += "<td style='width: 10%;'>";
	htmlStr += "<input type='button' class='bcButton' value='x' onclick='deleteFileButton(\""+response.fileName+"\")'/>";
	htmlStr += "</td>";
	htmlStr += "</tr>";
	
	$("#filesTable").append(htmlStr);
}

function fileUploadButton(){
	if (!jQuery('#myfile').val()) {
		alert('請上傳檔案');
	}else{
		$('#UploadForm').submit();
	}
}

function addNews(){
	$('#newNewsDetailContent').show();
	$('#newsListContent').hide();
	getAllDept(null);
}


function backToMainList(){
	$('#newNewsDetailContent').hide();
	$('#newsListContent').show();
	$('#newsButton').trigger("click");	
	resetNewBcForm();
}


function editNews(NEWS_M_ID){
	$('#newNewsDetailContent').show();
	$('#newsListContent').hide();
	
	mask('newNewsDetailContent');
	$.post(contextRoot, {
		'action' : "getBulletinBoardById",
		'NEWS_M_ID' : NEWS_M_ID
	}, function(data) {
		unmask('newNewsDetailContent');
		
		var dataObj = $.parseJSON(data);
		if (dataObj.result) {
			var fileData = dataObj.fileData;
			var ett = dataObj.data;
			
			$('#NEWS_M_ID').val(ett.NEWS_M_ID);
			$('#TOPIC').val(ett.TOPIC);
			$('#NEWS_TYPE').val(ett.NEWS_TYPE);
			$('#END_DATE_STR').val(ett.END_DATEStr);
			$('#SOURCE_URL').val(ett.SOURCE_URL);
			$('#CONTENT').val(ett.CONTENT).blur();
			$("#originFilesTable").empty();
			for(var i=0; i< fileData.length; i++){
				getFileTemplate(fileData[i],ett.NEWS_M_ID);
			}
			
			getAllDept(NEWS_M_ID);
		}
	});
}

function getFileTemplate(response,NEWS_M_ID){
	var imageExtArr = ["gif", "jpg" ,"png","GIF", "JPG" ,"PNG"];
	var ext = imageExtArr.indexOf(response.FILE_TYPE);
	
	var now = new Date();
	var htmlStr = "";

	htmlStr += "<tr id='"+response.FILE_ID+"'>";
	htmlStr += "<td style='width: 48px;'>";
	if(ext == -1){
		htmlStr += '<div class="fileType">'+response.FILE_TYPE+'</div>';
	}else{
		htmlStr += '<img width="48" src="'+fileContextRoot+'?isTemp=false&fileName='+response.FILE_ID+"."+response.FILE_TYPE+'&time='+now.getTime()+'" />';
	}
	htmlStr += "</td>";
	
	htmlStr += "<td>";
	htmlStr += "<a title='下載' href='"+fileContextRoot+"?isDownload=true&fileName="+response.FILE_ID+"."+response.FILE_TYPE+'&time='+now.getTime()+"'>"+response.FILE_NAME+"."+response.FILE_TYPE+'</a><br/>';
	if(ext != -1){
		htmlStr += '圖片網址:<br/>'+urlRoot+'?isTemp=false&fileName='+response.FILE_ID+"."+response.FILE_TYPE+"";
	}
	
	htmlStr += "</td>";
	htmlStr += "<td style='width: 10%;'>";
	htmlStr += "<input type='button' class='bcButton' value='x' onclick='deleteOriginFileButton(\""+response.FILE_M_ID+"\",\""+NEWS_M_ID+"\")'/>";
	htmlStr += "</td>";
	htmlStr += "</tr>";
	
	$("#originFilesTable").append(htmlStr);
}


function deleteOriginFileButton(FILE_M_ID,NEWS_ID){
	var r = confirm("刪除圖片可能導致內文圖片消失,您確定將此檔案刪除嗎?");
	if (r == true) {
		mask('newNewsDetailContent');
		$.post(contextRoot, {
			'action' : "deleteFileById",
			'FILE_M_ID' : FILE_M_ID
		}, function(data) {
			unmask('newNewsDetailContent');	
			var dataObj = $.parseJSON(data);
			if (dataObj.result) {
				editNews(NEWS_ID);
			}
		});
		
	} 
	
	
	
	
}

function deleteNews(NEWS_M_ID){
	var r = confirm("您確定將此新聞刪除嗎?");
	if (r == true) {
		mask('newsListContent');
		$.post(contextRoot, {
			'action' : "deleteNews",
			'NEWS_M_ID' : NEWS_M_ID
		}, function(data) {
			unmask('newsListContent');	
			var dataObj = $.parseJSON(data);
			if (dataObj.result) {
				backToMainList();
			}
		});
	}
}


function getNewsTemplate(seq, o) {
	var deleteBtn = "<input type='button' class='bcButton' value='刪除' onclick='deleteNews(\""+o.NEWS_M_ID+"\")'/>";
	var btn = '<input type="button" value="編輯" class="bcButton" onclick="editNews(\''+o.NEWS_M_ID+'\');"/>';
	var html = '';
	html += '<tr>';
	html += '    	<td>' + o.CREATED_DATEStr + '</td>';
	html += '    	<td>' + o.NEWS_TYPE_NAME + '</td>';
	html += '    	<td>' + o.TOPIC + '</td>';
	html += '    	<td>' + o.empCname + '</td>';
	if(uId == o.CREATED_ID){
		html += '    	<td>' + btn + deleteBtn + '</td>';
	}else{
		html += '    	<td>&nbsp;</td>';
	}
	html += '</tr>';
	return html;
}

function getNewDateTimeString(){
	var d = new Date();
	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	curr_month++;
	var curr_year = d.getFullYear();
	return curr_year+"/"+curr_month+"/"+curr_date;
}


function checkValue(obj){
	if(obj.val() == ""){ 
		obj.css("border", "1px solid red");
		return 1;
	}else{
		obj.css("border",'');
		return 0;
	}
}


function getShdNewsDataParam(shdNewsDataParam){
	var validCount = 0;
	var selectCount = 0;
	validCount += checkValue($('#TOPIC'));
	validCount += checkValue($('#NEWS_TYPE'));
	validCount += checkValue($('#END_DATE_STR'));
	
	selectCount = relativeNode.length + selectedNode.length;

	if(selectCount == 0 || validCount ==1){
		alert('請填寫必填欄位');
	}
	
	if(selectCount == 0){
		$('#tree3').css("border", "1px solid red");
		$('#treeMsg').empty().append("<font style='color:red'>*請選擇部門</font>");
		return false;
	}else{
		$('#tree3').css("border",'');
		$('#treeMsg').empty();
	}
	
	if(validCount == 0){
		shdNewsDataParam.NEWS_M_ID = $('#NEWS_M_ID').val();
		shdNewsDataParam.TOPIC = $('#TOPIC').val();
		shdNewsDataParam.NEWS_TYPE = $('#NEWS_TYPE').val();
		shdNewsDataParam.END_DATE_STR = $('#END_DATE_STR').val();
		shdNewsDataParam.SOURCE_URL = $('#SOURCE_URL').val();
		shdNewsDataParam.CONTENT = $('#CONTENT').val();
		return shdNewsDataParam;	
	}else{
		return false;
	}
}
function resetNewBcForm(){
	$('#NEWS_M_ID').val("");
	$('#TOPIC').val("").css("border",'');
	$('#NEWS_TYPE').val("").css("border",'');
	$('#END_DATE_STR').val("").css("border",'');
	$('#SOURCE_URL').val("").css("border",'');
	$('#message').empty();
	$("#UploadForm").trigger('reset');
	cleditor.clear();
	$('#filesTable').empty();
	$('#originFilesTable').empty();
	$('#allNames').empty();
	$('#tree3').css("border",'');
	$('#treeMsg').empty();
}






function saveBcData(){
	var fileDataArr = [];
	var values = {};
	$("#filesTable td:has(input.fileDataInput)").each(function(index) {// Iterate over inputs
    	values = new Object;
        $(this).children().each(function(){
        	if($(this).attr('name') != undefined){
        		values[$(this).attr('name')]=$(this).val();
        		
        	}
        });
        fileDataArr.push(values);
  	});
  
	var shdNewsDataParam = new Object;
	if(getShdNewsDataParam(shdNewsDataParam) != false){
		var shdNewsDataParam = getShdNewsDataParam(shdNewsDataParam);
		shdNewsDataParam.uploadFiles = JSON.stringify(fileDataArr);
		shdNewsDataParam.relativeDeptNode = relativeNode.join(",");
		shdNewsDataParam.selectedDeptNode = selectedNode.join(",");
		
		mask('newNewsDetailContent');
		$.post(contextRoot, {
				'action' : "saveBulletinBoardData",
				'data' : $.toJSON(shdNewsDataParam)
			}, function(data) {
				unmask('newNewsDetailContent');
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
