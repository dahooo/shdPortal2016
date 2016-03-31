$(document).ready(function() {
	$("#nowDateTimeString").empty().append(getNewDateTimeString());
	$( "#END_DATE_STR" ).datepicker({ dateFormat: 'yy-mm-dd' });
	$( "#CREATED_DATE_STAR" ).datepicker({ dateFormat: 'yy-mm-dd' });
	$( "#CREATED_DATE_END" ).datepicker({ dateFormat: 'yy-mm-dd' });
		
	
	$.get(contextRoot,
	{ 'action' : "getBulletinBoardTypeList"},
	function(data){
		dataObj = $.parseJSON(data);
		
		if (dataObj.result) {

			var resultList = dataObj.data;
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
			
			if(detailId.length > 0){
				getNewsDetail(detailId);
			}else{
				$('#newsButton').trigger("click");
			}
		} else {
			alert("資料無法取得：" + data);
		}
	});
	
	
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




function getNewsDetail(NEWS_M_ID){
	mask('newsDetailContent');
	$.post(contextRoot, {
			'action' : "getNewsById",
			'NEWS_M_ID' : NEWS_M_ID
		}, function(data) {
			unmask('newsDetailContent');
			$('#newsListContent').hide();
			$('#newsDetailContent').show();
			var dataObj = $.parseJSON(data);
			if (dataObj.result) {
				var fileData = dataObj.fileData;
				var ett = dataObj.data;
				$('#detail_1').empty().append(ett.TOPIC);
				$('#detail_2').empty().append("類別:"+ett.NEWS_TYPE_NAME);
				$('#detail_3').empty().append("下架日期:"+ett.END_DATEStr);
				//$('#detail_4').empty().append("原文網址:<br/>"+ett.SOURCE_URL);
				$('#detail_5').empty().append(ett.CONTENT);
				$('#detail_6').empty().append(ett.CREATED_DATEStr);
				
				$("#filesTable").empty();
				for(var i=0; i< fileData.length; i++){
					getFileTemplate(fileData[i]);
				}
				
				
			}
	});
	
	
}



function backToMainList(){
	$('#newsListContent').show();
	$('#newsDetailContent').hide();
	$('#newsButton').trigger("click");
}


function getFileTemplate(response){
	var imageExtArr = ["gif", "jpg" ,"png","GIF", "JPG" ,"PNG"];
	var ext = imageExtArr.indexOf(response.FILE_TYPE);
	
	var now = new Date();
	var htmlStr = "";

	htmlStr += "<tr id='"+response.FILE_ID+"'>";
	htmlStr += "<td style='width: 48px;'>";
	if(ext == -1){
		htmlStr += '<div class="fileType">'+response.FILE_TYPE+'</div>';
	}else{
		htmlStr += '<img width="48" src="'+fileContextRoot+'?isTemp=false&fileName='+response.FILE_ID+"."+response.FILE_TYPE+'&time='+now.getTime()+'" />'
	}
	htmlStr += "</td>";
	
	htmlStr += "<td>";
	htmlStr += "<a title='下載' href='"+fileContextRoot+"?isDownload=true&fileName="+response.FILE_ID+"."+response.FILE_TYPE+'&time='+now.getTime()+"'>"+response.FILE_NAME+"."+response.FILE_TYPE+'</a><br/>';
	
	htmlStr += "</td>";
	
	htmlStr += "</tr>";
	
	$("#filesTable").append(htmlStr);
}


function getNewsTemplate(seq, o) {
	//var btn = '<input type="button" value="地址" onclick="getAddressByLatLng(\''+o.latitude+'\',\''+o.longitude+'\',\''+seq+'\')"/>';
	var html = '';
	html += '<div class="div_td" style="cursor: pointer;"  onclick="getNewsDetail(\''+o.NEWS_M_ID+'\')">';
	html += '<span class="AOtime" >'+ o.CREATED_DATEStr +'</span>';
	html += '<h2 class="newsListTitle" >' + o.TOPIC + '</h2> ';
	html += '<p>類別：'+o.NEWS_TYPE_NAME+'　，By '+o.empCname+'</p>';
	html += '</div>';
	return html;
}

function getNewDateTimeString(){
	var d = new Date();
	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	curr_month++;
	var curr_year = d.getFullYear();
	return curr_year+"/"+curr_month+"/"+curr_date
}
