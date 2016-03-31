function getBackendTemplate2(){
	var html = '';
	html += '<div style=" text-align:center ;" id="searchForm2Template">';
	html += '	    <a href="#" id="firstPage" page="1">';
	html += '        <svg class="firstPage" viewBox="0 0 27 21">';
	html += '            <g>';
	html += '                <polygon fill="#820025" points="21.882,18.103 8.714,10.5 21.882,2.897 	" />';
	html += '                <line fill="none" stroke="#820025" stroke-width="2" stroke-miterlimit="10" x1="6.583" y1="3.729" x2="6.583" y2="17.147" />';
	html += '            </g>';
	html += '        </svg>';
	html += '    </a>';
	html += '    <a href="#" id="previousPage" page="1">';
	html += '        <svg class="previousPage" viewBox="0 0 27 21">';
	html += '            <g>';
	html += '                <polygon fill="#820025" points="20.084,18.103 6.916,10.5 20.084,2.897 	" />';
	html += '            </g>';
	html += '        </svg>';
	html += '    </a>';
	
	html += '    <span id="eachPage" class="eachPage"></span>';
    
	html += '    <a href="#" id="nextPage" page="2">';
	html += '        <svg class="nextPage" viewBox="0 0 27 21">';
	html += '            <g>';
	html += '                <polygon fill="#820025" points="6.916,18.103 20.084,10.5 6.916,2.897 	" />';
	html += '            </g>';
	html += '        </svg>';
	html += '    </a>';
	html += '    <a href="#" id="lastPage" page="2">';
	html += '        <svg class="lastPage" viewBox="0 0 27 21">';
	html += '            <g>';
	html += '                <polygon fill="#820025" points="5.851,18.103 19.019,10.5 5.851,2.897 	" />';
	html += '                <line fill="none" stroke="#820025" stroke-width="2" stroke-miterlimit="10" x1="21.149" y1="3.729" x2="21.149" y2="17.147" />';
	html += '            </g>';
	html += '        </svg>';
	html += '    </a>';
	html += '    <span id="totalRowWording" class="totalRowWording"></span>';
	html += '</div>';
	
	
	

	
	return html;
}

