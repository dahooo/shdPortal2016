/**
 * <pre>
 * 分頁
 * 1. 在 server 要接收:
 * 		A. getParameter( "currentPage" )
 * 		B. getParameter( "pageSize" ) * 
 * </pre>
 * 
 */
var debug = false;

/**
 * <pre>
 * 分頁的 plugin
 * 1.  加入的 form, 其 id 為 conditionBlockId + 'Form'
 * 2.  加入的 分頁模板, 其 id 為 conditionBlockId + 'Template' 
 * </pre>
 */
( function( $ ){
	$.fn.paging = function( settings ){
		var defaultSettings = { 
			url 			 		: '', 					// a url(required)
			conditionBlockId 		: 'conditionBlock', 	// 將會取得到被這個 conditionBlockId 包覆起來的條件
			searchButtonId 			: 'searchButton', 		// 按下查詢的按鈕(required)
			resultBodyId 			: 'resultBody', 		// Result Body id(required)
			pageSize 				: '10', 				// 預設一頁有10筆(optioned)
			firstPageId 			: 'firstPage', 			// 第一頁 id			
			previousPageId 			: 'previousPage', 		// 上一頁 id
			nextPageId 				: 'nextPage', 			// 下一頁 id
			lastPageId 				: 'lastPage', 			// 最後一頁 id
			pageAlwaysShow			: false,				// 設定第一頁, 上一頁, 下一頁, 最後一頁是否永遠都顯示
			pageAlwaysLink			: false,				// 設定第一頁, 上一頁, 下一頁, 最後一頁是否永遠都有 hyperLink
			adjustPageSizeId 		: 'adjustPageSize',		// 調整每頁筆數
			currentPageWordingId 	: 'currentPageWording', // 目前頁碼字串 id
			totalPageWordingId 		: 'totalPageWording', 	// 總頁數字串 id
			firstNumberWordingId 	: 'firstNumberWording', // 每頁的第一筆字串, 如每頁十筆, 在第二頁時, 這個字串就為 11
			lastNumberWordingId 	: 'lastNumberWording', 	// 每頁的最後一筆字串, 如每頁十筆, 在第二頁時, 這個字串就為 20
			totalRowWordingId 		: 'totalRowWording', 	// 總筆數字串
			loadmask				: '', 					// load mask
			doWhenNotFoundData		: function(){},			//當查無資料時, 可以自訂要處理的事
			getResultBodyHtml 		: function( seqNo, entry ){ alert( 'getResultBodyHtml is required' );	}, //  取得到 result body 的資料
			getPagingHtml 			: function(){ return getDefaultPagingHtmlByPluginPaging(); }, // 取得預設的 html(option)
			doOtherAfterQuery 		: function( data ){}, 	// 查詢後要做的其它事(option)
			eachPage 				: { id : 'eachPage', size : '5', prefix : '|&nbsp;', delim : '&nbsp;|&nbsp;', suffix : '&nbsp|' } 
		};

		var newSettings = $.extend( defaultSettings, settings );
		
		newSettings.ids = new Array();
		
		validRequiredByPluginPaging( newSettings );
		validByPluginPaging( newSettings );
		initiateEventByPagingUtils( newSettings );

		return this.each( function( index ){
			var $template = $( newSettings.getPagingHtml() );
			
			newSettings.ids[ index ] = $(this).attr( 'id' );
			
			$template.attr( 'id', newSettings.conditionBlockId + 'Template' ).hide();
			$( this ).append( $template );
			
			
			bindPagingAction(newSettings);
		} );
	};
} )( jQuery );

/**
 * 檢查需要的參數
 * 
 * @param newSettings
 */
function validRequiredByPluginPaging( newSettings ){
	if( newSettings.url.length == 0 )
		alert( 'PagingUtils, url is required' );

	if( newSettings.conditionBlockId.length == 0 )
		alert( 'PagingUtils, conditionBlockId is required' );

	if( newSettings.searchButtonId.length == 0 )
		alert( 'PagingUtils, searchButtonId is required' );

	if( newSettings.resultBodyId.length == 0 )
		alert( 'PagingUtils. resultBodyId is required' );

	if( newSettings.eachPage.length == 0 )
		alert( 'PagingUtils. eachPage is required' );

	if( newSettings.eachPage.id.length == 0 )
		alert( 'PagingUtils. eachPage.id is required' );

	if( newSettings.eachPage.size.length == 0 )
		alert( 'PagingUtils. eachPage.size is required' );

	if( newSettings.eachPage.prefix.length == 0 )
		alert( 'PagingUtils. eachPage.prefix is required' );

	if( newSettings.eachPage.delim.length == 0 )
		alert( 'PagingUtils. eachPage.delim is required' );

	if( newSettings.eachPage.suffix.length == 0 )
		alert( 'PagingUtils. eachPage.suffix is required' );
}

/**
 * 檢驗參數是否合法
 * 
 * @param newSettings
 */
function validByPluginPaging( newSettings ){
	if( $( '#' + newSettings.conditionBlockId ).length == 0 )
		alert( 'can not find any dom element, when id=' + newSettings.conditionBlockId );
	
	if( $( '#' + newSettings.searchButtonId ).length == 0 )
		alert( 'can not find any dom element, when id=' + newSettings.searchButtonId );
	
	if( $( '#' + newSettings.resultBodyId ).length == 0 )
		alert( 'can not find any dom element, when id=' + newSettings.resultBodyId );
}

/**
 * 產生 form, id= serachId + 'Form'
 * 
 * @param conditionBlockId
 * @returns
 */
function generateForm( conditionBlockId ){
	var $form = $( '<form method="post"></form>' ).attr( 'id', conditionBlockId + 'Form' );
	appendHiddenElement2Form( $form, conditionBlockId );

	$( 'body' ).append( $form );

	return $form;
}

/**
 * 將 conditionBlockId 被包覆的條件, 加入到 form 並把 currentPage 和 pasgeSize 加入到 form
 * 
 * @param $form
 * @param conditionBlockId
 * @returns {String}
 */
function appendHiddenElement2Form( $form, conditionBlockId ){
	var html = '';
	html += '<input type="hidden" name="currentPage" id="currentPage"/>';
	html += '<input type="hidden" name="pageSize" id="pageSize"/>';
	html += getHiddenElementByCondtion( conditionBlockId );

	$form.append( html );
}

/**
 * <pre>
 * 取得被 conditionBlockId 包覆的條件, 目前有 
 * 1. input type=text
 * 2. input type=radio
 * 3. select
 * </pre>
 * 
 * @param conditionBlockId
 */
function getHiddenElementByCondtion( conditionBlockId ){
	var html = '';

	$( '#' + conditionBlockId ).find("input[type=text], input[type=hidden], input[type=radio]:checked, input[type=checkbox]:checked, select").each( function(){
		var name = $( this ).attr( 'name' );
		var value = $( this ).val();
		html += '<input type="hidden" name="' + name + '" value="' + value + '"/>';
	} );
	return html;
}

function mask( id ){
	if( $( '#' + id ).length != 0 )
		$( '#' + id ).mask( 'Loading...', 500 );
}

function unmask( id ){
	if( $( '#' + id ).length != 0 )
		$( '#' + id ).unmask();
}


/**
 * 取得分頁 style 的預設值
 * 
 * @returns {String}
 */
function getDefaultPagingHtmlByPluginPaging(){
	var html = '';
	html += '<table width="100%">';
	html += '  <tr>';
	html += '    <td align="right">';
	html += '      第<span id="currentPageWording"></span>頁/共<span id="totalPageWording"></span>頁';
	html += '      <a href="#" id="firstPage">第一頁</a>';
	html += '      <a href="#" id="previousPage">上一頁</a>';
	html += '      <a href="#" id="nextPage">下一頁</a>';
	html += '      <a href="#" id="lastPage">最後一頁</a>';
	html += '      每頁<input type="text" id="adjustPageSize" size="3" maxlength="3" />筆';
	html += '    </td>';
	html += '  </tr>';
	html += '</table>';

	return html;
}

/**
 * 對 event 初始化
 * 
 * @param newSettings
 */
function initiateEventByPagingUtils( newSettings ){
	if( debug )
		alert( 'initiateEventByPagingUtils' );
	
	
	var $resultBody = $( '#' + newSettings.resultBodyId );
	var $template = $( '#' + newSettings.conditionBlockId + 'Template' );
	var $form = $( '#' + newSettings.conditionBlockId + 'Form' );

	$resultBody.empty();

	// 按下搜尋健
	$( '#' + newSettings.searchButtonId ).click( function(){
		if( debug )
			alert( 'click search Button' );

		$form.remove();

		$form = generateForm( newSettings.conditionBlockId );
		$( '#pageSize', $form ).val( newSettings.pageSize );

		queryByPluginPaging( newSettings, 1 );
	} );

	
	
}

function bindPagingAction( newSettings ){
	if( debug )
		alert( 'bindPagingAction' );
	
	var $template = $( '#' + newSettings.conditionBlockId + 'Template' );
	var $form = $( '#' + newSettings.conditionBlockId + 'Form' );
	
	$( '[id=' + newSettings.firstPageId + ']', $template ).on( 'click', function(){
		if( debug )
			alert( 'click firstPage Button' );

		queryByPluginPaging( newSettings, $( this ).attr( 'page' ) );
	} );

	$( '[id=' + newSettings.previousPageId + ']', $template ).on( 'click', function(){
		if( debug )
			alert( 'click previousPage Button' );

		queryByPluginPaging( newSettings, $( this ).attr( 'page' ) );
	} );

	$( '[id=' + newSettings.nextPageId + ']', $template ).on( 'click', function(){
		if( debug )
			alert( 'click nextPage Button' );

		queryByPluginPaging( newSettings, $( this ).attr( 'page' ) );
	} );

	$( '[id=' + newSettings.lastPageId + ']', $template ).on( 'click', function(){
		if( debug )
			alert( 'click lastPage Button' );

		queryByPluginPaging( newSettings, $( this ).attr( 'page' ) );
	} );

	$( '[id=' + newSettings.adjustPageSizeId + ']', $template ).on( 'keyup', function( key ){
		if( key.keyCode == 13 ){
			if( debug )
				alert( '調整每頁筆數' );

			$( '#pageSize', $form ).val( $( this ).val() );

			queryByPluginPaging( newSettings, 1 );
		}
	} );
	
	$( '[id=refreshPage]', $template ).on( 'click', function(){
		if( debug )
			alert( 'click each page button' );

		queryByPluginPaging( newSettings, $( '#currentPage' ).val() );
	} );
}


/**
 * 查詢資料
 * 
 * @param newSettings
 * @param currentPage 傳給 controller 的 current page
 */
function queryByPluginPaging( newSettings, currentPage ){
	if( debug )
		alert( 'currentPage:' + currentPage );
	
	var $resultBody = $( '#' + newSettings.resultBodyId );
	var $form = $( '#' + newSettings.conditionBlockId + 'Form' );

	$resultBody.empty();
	
	mask( newSettings.loadmask );
	
	$( '#currentPage', $form ).val( currentPage );

	$.post( newSettings.url, $form.serialize(), function( data ){
		try{
			data = $.parseJSON( data );
		}catch( error ){
			alert( error );
			return false;
		}

		if( data.result ){
			// 查無資料
			if( data.pagination.totalRow == 0 ){
				$.each( newSettings.ids, function(){
					$( '#' + this ).hide();
				});
				
				newSettings.doWhenNotFoundData();
				newSettings.doOtherAfterQuery( data );
			}
			else{
				$.each( newSettings.ids, function(){
					$( '#' + this ).show();
				});
				
				$( '#' + newSettings.search + 'Template' ).show();
	
				initiatePageByPluginPaging( data.pagination, newSettings );
				doResultTable( data.pagination, newSettings );
	
				newSettings.doOtherAfterQuery( data );
			}			
		}else
			alert( data.errorMsg );
		
		unmask( newSettings.loadmask );
	} );
}

/**
 * 查詢後, 分頁模板的初始化
 * 
 * @param pagination
 * @param newSettings
 */
function initiatePageByPluginPaging( pagination, newSettings ){
	var currentPage = pagination.currentPage;
	var totalPage = pagination.totalPage;

	$( '[id=' + newSettings.conditionBlockId + 'Template]' ).each( function(){
		var $template = $( this );

		$template.show();

		$( '#adjustPageSize', $template ).val( pagination.pageSize );

		$( '#' + newSettings.firstPageId, $template ).attr( 'page', 1 );
		$( '#' + newSettings.previousPageId, $template ).attr( 'page', ( currentPage == 1 ) ? 1 : currentPage - 1 );
		$( '#' + newSettings.nextPageId, $template ).attr( 'page', ( currentPage == totalPage ) ? totalPage : currentPage + 1 );
		$( '#' + newSettings.lastPageId, $template ).attr( 'page', totalPage );

		$( '#currentPageWording', $template ).text( currentPage );
		$( '#totalPageWording', $template ).text( totalPage );
		$( '#firstNumberWording', $template ).text( pagination.currentPageFirstNumber );
		$( '#lastNumberWording', $template ).text( pagination.currentPageLastNumber );
		$( '#totalRowWording', $template ).text( "共"+pagination.totalRow+"筆" );

		showOrHideTagInWrapTagByPluginPaging( !pagination.isFirstPage || newSettings.pageAlwaysShow, newSettings.firstPageId, $template );
		showOrHideTagInWrapTagByPluginPaging( !pagination.isLastPage || newSettings.pageAlwaysShow, newSettings.lastPageId, $template );
		showOrHideTagInWrapTagByPluginPaging( pagination.hasNextPage || newSettings.pageAlwaysShow, newSettings.nextPageId, $template );
		showOrHideTagInWrapTagByPluginPaging( pagination.hasPreviousPage || newSettings.pageAlwaysShow, newSettings.previousPageId, $template );
		
		addOrRemoveHyperLinkByPluginPaging( !pagination.isFirstPage || newSettings.pageAlwaysLink, newSettings.firstPageId, $template );
		addOrRemoveHyperLinkByPluginPaging( !pagination.isLastPage || newSettings.pageAlwaysLink, newSettings.lastPageId, $template );
		addOrRemoveHyperLinkByPluginPaging( pagination.hasNextPage || newSettings.pageAlwaysLink, newSettings.nextPageId, $template );
		addOrRemoveHyperLinkByPluginPaging( pagination.hasPreviousPage || newSettings.pageAlwaysLink, newSettings.previousPageId, $template );

		processEachPage( currentPage, totalPage, newSettings, $template );
	} );
}

/**
 * 針對 result table 來處理相關顯示資料
 * 
 * @param pagination
 * @param newSettings
 */
function doResultTable( pagination, newSettings ){	
	var $resultBody = $( '#' + newSettings.resultBodyId );

	$.each( pagination.currentList, function( index, entry ){		
		$resultBody.append( newSettings.getResultBodyHtml( pagination.currentPageFirstNumber + index, entry ) );	
	} );
}

/**
 * 顯示或隱藏 element
 * 
 * @param isShow 是否顯示
 * @param idName a id name
 * @param $wrapedTag 包覆的 tags
 */
function showOrHideTagInWrapTagByPluginPaging( isShow, idName, $wrapedTag ){
	if( isShow )
		$( '#' + idName, $wrapedTag ).show();
	else
		$( '#' + idName, $wrapedTag ).hide();
}

function addOrRemoveHyperLinkByPluginPaging( isAdd, idName, $wrapedTag ){
	if( isAdd )
		$( '#' + idName, $wrapedTag ).attr( 'href', '#' );
	else
		$( '#' + idName, $wrapedTag ).removeAttr( 'href' );		
}

function processEachPage( currentPage, totalPage, newSettings, $template ){
	var size = newSettings.eachPage.size;
	var $eachPage = $( '#' + newSettings.eachPage.id, $template );

	$eachPage.empty();

	var block = parseInt( ( totalPage - 1 ) / size );

	if( ( block + 1 ) * size - currentPage < size )
		size = ( totalPage - 1 ) % size + 1;

	var html = newSettings.eachPage.prefix;

	var pageNo;

	for( var i = 0 ; i < size ; i++ ){
		pageNo = parseInt( ( currentPage - 1 ) / size ) * size + 1 + i;

		if( i != 0 )
			html += newSettings.eachPage.delim;

		if( currentPage == pageNo )
			html += '<span style="font-weight: bold;color:red">' + pageNo + '</span>';
		else
			html += '<a href="#" name="pageNo" page="' + pageNo + '">' + pageNo + '</a>';
	}

	html += newSettings.eachPage.suffix;

	$eachPage.append( html );
	
	$( '[name=pageNo]', $template ).on( 'click', function(){
		if( debug )
			alert( 'click each page button' );

		queryByPluginPaging( newSettings, $( this ).attr( 'page' ) );
	} );
	
}