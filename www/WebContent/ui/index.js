(function(){
	XL.tree.init({'$tree': $('#menus')});
	$('#page').xgrid();
	var $nav = $('#nav').xnav({
		'left':[{'icon':'x-icon-home','title':'首页','url':'/ui/home.html'}],
		'center':[
			{'icon':'x-icon-font','name':'字体','url':'/ui/element/font.html'},
			{'icon':'x-icon-icon','name':'图标','url':'/ui/element/icon.html'},
			{'icon':'x-icon-button','name':'按钮','url':'/ui/element/button.html'},
			{'icon':'x-icon-switch','name':'开关','url':'/ui/element/switch.html'},
			{'icon':'x-icon-tag','name':'标签','url':'/ui/element/tag.html'},
			{'icon':'x-icon-progress','name':'进度条','url':'/ui/element/progress.html'},
			{'icon':'x-icon-radio','name':'单选框','url':'/ui/element/radio.html'},
			{'icon':'x-icon-checkbox','name':'复选框','url':'/ui/element/checkbox.html'},
			{'icon':'x-icon-font','name':'字体','url':'/ui/element/font.html'},
			{'icon':'x-icon-icon','name':'图标','url':'/ui/element/icon.html'},
			{'icon':'x-icon-button','name':'按钮','url':'/ui/element/button.html'},
			{'icon':'x-icon-switch','name':'开关','url':'/ui/element/switch.html'},
			{'icon':'x-icon-tag','name':'标签','url':'/ui/element/tag.html'},
			{'icon':'x-icon-progress','name':'进度条','url':'/ui/element/progress.html'},
			{'icon':'x-icon-radio','name':'单选框','url':'/ui/element/radio.html'},
			{'icon':'x-icon-checkbox','name':'复选框','url':'/ui/element/checkbox.html'},
			{'icon':'x-icon-font','name':'字体','url':'/ui/element/font.html'},
			{'icon':'x-icon-icon','name':'图标','url':'/ui/element/icon.html'},
			{'icon':'x-icon-button','name':'按钮','url':'/ui/element/button.html'},
			{'icon':'x-icon-switch','name':'开关','url':'/ui/element/switch.html'},
			{'icon':'x-icon-tag','name':'标签','url':'/ui/element/tag.html'},
			{'icon':'x-icon-progress','name':'进度条','url':'/ui/element/progress.html'},
			{'icon':'x-icon-radio','name':'单选框','url':'/ui/element/radio.html'},
			{'icon':'x-icon-checkbox','name':'复选框','url':'/ui/element/checkbox.html'}
		],
		'right':[{'icon':'x-icon-email','title':'邮箱'},{'icon':'x-icon-user','title':'用户'},{'icon':'x-icon-cog','title':'设置'}]
	});
	var $tabs = $('#tabsWrap');
	var $left = $nav.children('.x-nav-left');
	var $center = $nav.children('.x-nav-center');
	var $right = $nav.children('.x-nav-right');
	function navClick($el) {
		var url = $el.data('url'), tabid = $el.data('tabid');
		$tabs.children('.active').removeClass('active');
		$center.children('.active').removeClass('active');
		$el.addClass('active');
		if (url) {
			if (tabid) {
				$('#'+tabid).addClass('active');
			} else {
				var uuid = XL.base.uuid(2);
				$el.attr('data-tabid', uuid);
				$tabs.append('<iframe id="'+uuid+'" class="x-tab active" src="'+url+'"></iframe>');
			}
		}
	}
	navClick($left.children('.x-icon-home'));
	$left.on('click', 'a', function(e){
		navClick($(this));
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
				navClick($left.children('.x-icon-home'));
			}
			$button.remove();
			$nav.resize();
		} else {
			navClick($button);
		}
	});
	$('#menus').on('click', 'a', function(e){
		var $a = $(this);
		$nav.add({'icon':$a.children('i').attr('class'),'name':$a.children('span').html(),'url':$a.data('href')});
	});
})();