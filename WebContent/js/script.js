var statesdemo = {
		state0: {
			html:'<p>原因:</p><div class="field"><textarea id="HISTORY_COMMENT" name="HISTORY_COMMENT" rows="4" style="width:90%;"></textarea></div>',
			buttons: { 取消: 0, 送出: 1 },
			focus: 2,
			submit:function(e,v,m,f){ 
				if(v==0) 
					$.prompt.close();
				else if(v==1)								
					return true; //we're done
				return false; 
			}
		}
};

(function($) {
  $.fn.menumaker = function(options) {
      
      var cssmenu = $(this), settings = $.extend({
        title: "Menu",
        format: "dropdown",
        sticky: false
      }, options);

      return this.each(function() {
        cssmenu.prepend('<div id="menu-button">' + settings.title + '</div>');
        $(this).find("#menu-button").on('click', function(){
          $(this).toggleClass('menu-opened');
          var mainmenu = $(this).next('ul');
          if (mainmenu.hasClass('open')) { 
            mainmenu.hide().removeClass('open');
          }
          else {
            mainmenu.show().addClass('open');
            if (settings.format === "dropdown") {
              mainmenu.find('ul').show();
            }
          }
        });

        cssmenu.find('li ul').parent().addClass('has-sub');

        multiTg = function() {
          cssmenu.find(".has-sub").prepend('<span class="submenu-button"></span>');
          cssmenu.find('.submenu-button').on('click', function() {
            $(this).toggleClass('submenu-opened');
            if ($(this).siblings('ul').hasClass('open')) {
              $(this).siblings('ul').removeClass('open').hide();
            }
            else {
              $(this).siblings('ul').addClass('open').show();
            }
          });
        };

        if (settings.format === 'multitoggle') multiTg();
        else cssmenu.addClass('dropdown');

        if (settings.sticky === true) cssmenu.css('position', 'fixed');

        resizeFix = function() {
          if ($( window ).width() > 768) {
            cssmenu.find('li.sMenu').show();
          }

          if ($(window).width() <= 768) {
            cssmenu.find('li.sMenu').hide().removeClass('open');
          }
		  
		  if ($( window ).width() > 768) {
            cssmenu.find('li.sUser').css('float', 'right');
          }

          if ($(window).width() <= 768) {
            cssmenu.find('li.sUser').css('float', 'left');
          }
		  
        };
        resizeFix();
        return $(window).on('resize', resizeFix);

      });
  };
})(jQuery);

(function($){
$(document).ready(function(){

$("#cssmenu").menumaker({
   title: "Menu",
   format: "multitoggle"
});

});
})(jQuery);
