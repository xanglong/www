(function(){
	var $tree = $('#menus').xtree();
	$('#page').xgrid();
	var $nav = $('#nav').xnav({
		'left':[{'icon':'x-icon-home','title':'首页','url':'/ui/home.html'}],
		'right':[{'icon':'x-icon-email','title':'邮箱'},{'icon':'x-icon-user','title':'用户'},{'icon':'x-icon-cog','title':'设置'}]
	});
	$tree.children('ul').on('click', 'a', function(e) {
		var $a = $(this), url = $a.data('url');
		url ? $nav.add({'icon':$a.children('i').attr('class'),'name':$a.children('span').html(),'url':url}) : '';
	});
	var $tabs = $('#tabsWrap');
	var $left = $nav.children('.x-nav-left');
	var $center = $nav.children('.x-nav-center');
	var $right = $nav.children('.x-nav-right');
	var functions = {
		'navClick': function($el) {
			var url = $el.data('url'), tabid = $el.data('tabid');
			$tabs.children('.x-active').removeClass('x-active');
			$center.children('.x-active').removeClass('x-active');
			$el.addClass('x-active');
			if (url) {
				if (tabid) {
					$('#'+tabid).addClass('x-active');
				} else {
					var uuid = XL.base.uuid(2);
					$el.attr('data-tabid', uuid);
					$tabs.append('<iframe id="'+uuid+'" class="x-tab x-active" src="'+url+'"></iframe>');
				}
			}
		}
	};
	$left.on('click', 'a', function(e){
		functions.navClick($(this));
	});
	$center.on('click', 'button', function(e){
		var $target = $(e.target), $button = $(this);
		if ($target.hasClass('x-nav-close')) {
			$('#'+$button.data('tabid')).remove();
			var $next = $button.next(), $prev = $button.prev();
			if ($next.length > 0) {
				$next.click();
			} else if ($prev.length > 0) {
				$prev.click();
			} else {
				functions.navClick($left.children('.x-icon-home'));
			}
			$button.remove();
			$nav.resize();
		} else {
			functions.navClick($button);
		}
	});
	//打开正在开发的页面
	$tree.find('.x-icon-edit-text').click();
})();