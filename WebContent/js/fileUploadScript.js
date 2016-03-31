$(document).ready(function() {
		
	var options = {
		
		beforeSend : function() {
			// clear everything
		},
		uploadProgress : function(event, position, total, percentComplete) {
			// change message text and % to red after 50%
			if (percentComplete > 10) {
				$("#message").html("<font color='red'>圖片上傳中.. </font>");
			}
		},
		dataType:  'json',
		success:   function(response) {
			unmask('BCDetailTableImg');
			if(response.result){
				$("#NEW_UPLOAD_PIC_NAME").val(response.fileName);
				$("#message").html("<font color='blue'>"+response.msg+"</font>");
				var now = new Date();
				$("#uploadedImg").html('<img width="300" src="/wps/PA_shdWeb/imageServlet?isTemp=true&imageName='+response.fileName+'&time='+now.getTime()+'" >');
			}else{
				$("#message").html("<font color='red'>"+response.errorMsg+"</font>");
			}
		}
	};
	$("#UploadForm").ajaxForm(options);
});