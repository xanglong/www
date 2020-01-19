(function() {
	var XL = {
		'getLang' : function(code, words) {
			if (!xlang) {
				XL.msg.alert('语言包未加载', 'error');
			}
			var type = XL.base.getCookie('lang') || 'zh_cn';
			if (!xlang[type]) {
				XL.msg.alert(type + '语言包未加载', 'error');
			}
			var text = xlang[type][code];
			if (words) {
				for (var i=0; i < words.length; i++) {
					text = text.replace('{'+i+'}', words[i]);
				}
			}
			return text;
		},
		'base': {
			'os': (function() {
				var ua = navigator.userAgent.toLowerCase();
					return {
						'isIpad': /ipad/.test(ua),
						'isIphone': /iphone os/.test(ua),
						'isAndroid': /android/.test(ua),
						'isWinCe': /windows ce/.test(ua),
						'isWinPhone': /windows mobile/.test(ua),
						'isWin2000': /windows nt 5.0/.test(ua),
						'isXP': /windows nt 5.1/.test(ua),
						'isVista': /windows nt 6.0/.test(ua),
						'isWin7': /windows nt 6.1/.test(ua),
						'isWin8': /windows nt 6.2/.test(ua),
						'isWin8.1': /windows nt 6.3/.test(ua),
						'isWin10': /windows nt 10.0/.test(ua)
					};
				}()),
			'browser': (function() {
				var ua = navigator.userAgent.toLowerCase();
				return {
					'isUC': /ucweb/.test(ua),
					'isChrome': /chrome/.test(ua),
					'isFirefox': /firefox/.test(ua),
					'isOpera': /opera/.test(ua),
					'isSafire': /safari/.test(ua) && !/chrome/.test(ua),
					'is360': /360se/.test(ua),
					'isBaidu': /bidubrowser/.test(ua),
					'isSougou': /metasr/.test(ua),
					'isIE6': /msie 6.0/.test(ua),
					'isIE7': /msie 7.0/.test(ua),
					'isIE8': /msie 8.0/.test(ua),
					'isIE9': /msie 9.0/.test(ua),
					'isIE10': /msie 10.0/.test(ua),
					'isIE11': /msie 11.0/.test(ua),
					'isLB': /lbbrowser/.test(ua),
					'isWX': /micromessenger/.test(ua),
					'isQQ': /qqbrowser/.test(ua)
				};
			}()),
			'isBlank': function(text) {
				return !(typeof text == 'string' && text.trim().length > 0 || typeof text == 'number');
			},
			'isObject': function(obj) {
				return obj != null && typeof obj == 'object' && !(obj instanceof Array);
			},
			'getObject': function(json) {
				var data = null;
				try {
					data = JSON.parse(json);
				} catch(e) {
					data = json;
				}
				return data;
			},
			'right': function(str, len) {
				var res = '';
				for (var i = 0; i < len; i++) {
					res = str[str.length - 1 - i] + res;
				}
				return res;
			},
			'getUrl': function(uri) {
				var l = location;
				return l.protocol + '//' + l.host + (l.port ? ':' + l.port : '') + uri;
			},
			'getQuery': function() {
				var search = location.search;
				search = search.length > 0 ? search.substring(1) : search;
				var querys = search.split('&');
				var query = {};
				for (var i = 0; i < querys.length; i++) {
					var q = querys[i];
					var idx = q.indexOf('=');
					var key = q.substring(0, idx);
					var value = q.substring(idx + 1);
					query[key] = value;
				}
				return query;
			},
			'setCookie': function(cname, cvalue) {
				var exp = new Date();
				exp.setTime(exp.getTime() + 8640000000000);
				document.cookie = cname + '=' + encodeURIComponent(cvalue) + ';expires=' + exp.toGMTString() + ';path=/';
				return true;
			},
			'getCookie': function(cname) {
				var name = cname + '=';
				var ca = document.cookie.split(';');
				for(var i = 0; i < ca.length; i++) {
					var c = ca[i].trim();
					if (c.indexOf(name) == 0) {
						return c.substring(name.length, c.length);
					}
				}
				return '';
			},
			'delCookie': function(cname) {
				var exp = new Date();
				exp.setTime(exp.getTime() - 1);
				var cvalue = $.base.getCookie(cname);
					document.cookie= cname + '=' + cvalue + ';expires=' + exp.toGMTString() + ';path=/';
			},
			'uuid': function(num) {
				var uuid = '', num = num || 8;
				for (var i = 0; i < num; i++) {
					uuid +=  (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
				}
				return uuid.toUpperCase();
			},
		},
		'date': {
			'getDate': function(date) {
				function add0(m) {
					return m < 10 ? '0' + m : m;
				}
				date = new Date(date);
				var y = date.getFullYear();
				var M = date.getMonth() + 1;
				var d = date.getDate();
				return y + '-' + add0(M) + '-' + add0(d);
			},
			'addDate': function(days) { 
				var date = new Date();
				date.setDate(date.getDate() + days);
				return XL.date.getDate(date);
			},
			'getDateTime' : function(date) {
				date = new Date(date);
				var y = date.getFullYear();
				var M = date.getMonth() + 1;
				var d = date.getDate();
				var h = date.getHours();
				var m = date.getMinutes();
				var s = date.getSeconds();
				return y + '-' + add0(M) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(m) + ':' + add0(s);
			},
			'addDateTime': function(days) {
				var date = new Date();
				date.setDate(date.getDate() + days);
				return XL.date.getDateTime(date);
			},
			'compareTime': function(str1, str2) {
				var date1 = Date.parse(str1.replace(/-/g, '/'));
				var date2 = Date.parse(str2.replace(/-/g, '/'));
				return date1 > date2 ? 1 : date1 < date2 ? -1 : 0;
			},
			'isLeapYear': function(year) {
				return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
			},
			'getDays': function(year, month) {
				return [31, (XL.date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
			},
			'dayOfTheYear': function(date) { 
				date = new Date(date);
				var year = date.getFullYear();
				var month = date.getMonth();
				var days = date.getDate();
				var daysArr = [31, (XL.date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
				for (var i = 0; i < month; i++) {
					days += daysArr[i];
				}
				return days;
			},
			'getZone': function(date) {
				date = new Date(date); 
			    date = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate())); 
			    var arr = date.toString().match(/([A-Z]+)([-+]\d+:?\d+)/); 
			    return {'name': arr[1], 'value': arr[2]}; 
			},
			'format': function(dateTime, pattern) {
				var date = new Date(dateTime);
				if ($.base.isBlank(pattern)) {
					return date.toLocaleString();
				}
				return pattern.replace(/([a-z])\1*/ig, function(match, char) {
					switch(char) {
						case 'G': return '';
						case 'Y': return date.getFullYear();
						case 'y': return date.getFullYear();
						case 'M': 
							var M = date.getMonth() + 1;
							return (M < 10 && match.length >= 2) ? '0' + M : M;
						case 'd':
							var d = date.getDate();
							return (d < 10 && match.length >= 2) ? '0' + d : d;
						case 'k': return date.getHours();
						case 'H':
							var H = date.getHours();
							return (H < 10 && match.length >= 2) ? '0' + H : H;
						case 'm':
							var m = date.getMinutes();
							return (m < 10 && match.length >= 2) ? '0' + m : m;
						case 's':
							var s = date.getSeconds();
							return (s < 10 && match.length >= 2) ? '0' + s : s;
						case 'S': return date.getMilliseconds();
						case 'E': return ['日','一','二','三','四','五','六'][date.getDay()]; 
						case 'D': return XL.date.dayOfTheYear(date); 
						case 'F': return Math.floor(date.getDate() / 7); 
						case 'w': return Math.ceil(XL.date.dayOfTheYear(date) / 7); 
						case 'W': return Math.ceil(date.getDate() / 7); 
						case 'a': return date.getHours() < 12 ? '上午' : '下午'; 
						case 'h':
							var h = date.getHours() % 12 || 12;
							return (h < 10 && match.length >= 2) ? '0' + h : h; 
						case 'K': return date.getHours() % 12; 
						case 'z': return XL.date.getZone(date)['name']; 
						case 'Z': return XL.date.getZone(date)['value']; 
						case 'u': return '';; 
						case 'X': return '';
						default: return '';
					} 
				}); 
			},
		},
		'msg': {
			'tip': function(options) {
				
			},
			'alert': function(template, style) {
				var p;
				template = template || '';
				if (typeof template === 'string' || typeof style === 'string') {
					p = {'template': template, 'style': style};
				} else if (typeof template === 'object') {
					p = template;
				}
				p = $.extend({'template':null,'style':'info','autoclose':3000,'position':'top-center','icon':true,'group':false,'onOpen':false,'onClose':false}, p);
				function remove($elm) {
					p.onClose ? p.onClose() : '';
					$elm.removeClass('x-msg-alert-in');
					setTimeout(function() {$elm.remove();}, 390);
				};
				var $spopGroup = $('#xl_msg_alert_' + p.group);
				$spopGroup.length > 0 ? remove($spopGroup) : '';
				var $container = $('#xl_msg_alert_' + p.position);
				var icon = (!p.icon) ? '' : '<i class="x-msg-alert-icon x-msg-alert-icon--' + p.style +'"></i>';
				var layout ='<div class="x-msg-alert-close">&times;</div>' + icon + '<div class="x-msg-alert-body">' + p.template + '</div>';
				if ($container.length == 0) {
					var popContainer = document.createElement('div');
					popContainer.setAttribute('class', 'x-msg-alert-container x-msg-alert-' + p.position);
					popContainer.id = 'xl_msg_alert_' + p.position.replace('-', '_');
					document.body.appendChild(popContainer);
					$container = $('#xl_msg_alert_' + p.position.replace('-', '_'));
				}
				var pop = document.createElement('div');
				pop.setAttribute('class', 'x-msg-alert x-msg-alert-out x-msg-alert-in x-msg-alert-' + p.style);
				if (p.group && typeof p.group === 'string') {
					pop.id = 'xl_msg_alert_' + p.group;
				}
				pop.innerHTML = layout;
				$container.append(pop);
				if (p.onOpen) {p.onOpen();}
				var sid = null;
				if (p.autoclose && typeof p.autoclose === 'number') {
					sid = setTimeout(remove.bind(this, $(pop)), p.autoclose);
				}
				$(pop).find('.x-msg-alert-close').on('click', function(event){
					sid ? clearTimeout(sid) : '';
					remove($(pop));
				});
			}
		},
		'dom': {
			'getAll': function (options) {
				var attr = '', style = '', data = '';
				if (XL.base.isObject(options)) {
					attr = XL.dom.getAttr(options['attr']);
					style = XL.dom.getStyle(options['style']);
					data = XL.dom.getData(options['data']);
				}
				return attr + style + data;
			},
			'getAttr': function(attrObj) {
				var attr = '';
				if (XL.base.isObject(attrObj)) {
					for (var key in attrObj) {
						attr += key + '="' + attrObj[key] + '" ';
					}
				}
				return attr;
			},
			'getStyle': function(styleObj) {
				var style = '';
				if (XL.base.isObject(styleObj)) {
					style += 'style="'
					for (var key in styleObj) {
						style += key + ':' + styleObj[key] + ';';
					}
					style += '"';
				}
				return style;
			},
			'getData': function(dataObj) {
				var data = '';
				if (XL.base.isObject(dataObj)) {
					for (var key in dataObj) {
						data += 'data-' + key + '="' + dataObj[key] + '" ';
					}
				}
				return data;
			},
			'create': function (options) {
				var element = '';
				if (XL.base.isObject(options)) {
					var tag = options.tag;
					if (options.tag) {
						element = '<' + tag + ' ' + XL.dom.getAll(options) + '>' + (options.name || '');
						var children = options.children;
						if (children instanceof Array) {
							for (var i = 0; i < children.length; i++) {
								element += XL.dom.create(children[i]);
							}
						}
						element += '</' + tag + '>';
					}
				}
				return element;
			},
			'bind': function($el, options) {
				var event = options.event;
				if (XL.base.isObject(event)) {
					for (var key in event) {
						var method = event[key];
						if (typeof method == 'function') {
							$el.on(key, function(e) {
								method.call(this, e, $el)
							});
						}
					}
				}
				$el.children().each(function(index, value) {
					var children = options.children;
					children ? XL.dom.bind($(value), children[index]) : '';
				});
			},
			'noSelect': function() {
				getSelection ? getSelection().removeAllRanges() : document.selection.empty();
			}
		}
	}
	window.XL = XL;
	$.fn.extend({
		'xhtml': function(options) {
			if (!XL.base.isObject(options)) return this;
			options.attr ? this.attr(options.attr) : '';
			options.style ? this.css(options.css) : '';
			options.data ? this.data(options.data) : '';
			return this.each(function(index, value) {
				var innerHTML = '', children = options.children;
				if (children instanceof Array) {
					for (var i = 0; i < children.length; i++) {
						innerHTML += XL.dom.create(children[i]);
					}
				}
				value.innerHTML = innerHTML;
				XL.dom.bind($(value), options);
			});
		},
		'xtree': function(options) {
			var $this = this;
			if ($this.children().length == 0) {
				
			}
			$this.find('a').each(function(index, value){
				value.title = value.title || $(value).children('span').html();
			});
			$this.children('.x-tree-tool-system').on('click', 'a', function(e) {
				var $a = $(this);
				if ($a.hasClass('x-icon-collapse')) {
					var $root = $this.children('ul');
					$root.find('.x-icon-arrow-bottom').removeClass('x-icon-arrow-bottom').addClass('x-icon-arrow-right');
					$root.find('ul').hide();
				}
			});
			var functions = {
				'click': function() {
					var $root = $this.children('ul');
					$root.on('click', function(e) {
						var $tag = $(e.target), tagName = e.target.tagName, isArrow = false;
						if (tagName == 'UL' || tagName == 'LI') {
							return;
						}
						if (tagName == 'I') {
							if ($tag.hasClass('x-icon-arrow-bottom')) {
								isArrow = true;
								$tag.parent().children('ul').hide();
								$tag.removeClass('x-icon-arrow-bottom').addClass('x-icon-arrow-right');
							} else if ($tag.hasClass('x-icon-arrow-right')) {
								isArrow = true;
								$tag.parent().children('ul').show();
								$tag.removeClass('x-icon-arrow-right').addClass('x-icon-arrow-bottom');
							}
						}
						if (!isArrow) {
							var $a = $tag.closest('a');
							if (e.ctrlKey) {
								getSelection ? getSelection().removeAllRanges() : $root[0].selection.empty();
								$a.hasClass('active') ? $a.removeClass('active') : $a.addClass('active');
							} else if (e.shiftKey) {
								getSelection ? getSelection().removeAllRanges() : $root[0].selection.empty();
								var $lastActive = $root.find('.last-active');
								if ($lastActive.length == 0) {
									$a.addClass('active');
								} else {
									var $as = $root.find('a'), start, end, last, current;
									var lastActive = $lastActive[0], currentActive = $a[0];
									for (var i = 0; i < $as.length; i++) {
										var ai = $as[i];
										if (ai == lastActive) last = i;
										if (ai == currentActive) current = i;
									}
									if (current < last) {
										start = current;
										end = last;
									} else {
										start = last;
										end = current;
									}
									$as.removeClass('active').slice(start, end + 1).addClass('active');
								} 
							} else {
								$root.find('.active').removeClass('active');
								$a.addClass('active');
							}
							if (!e.shiftKey) {
								$root.find('.last-active').removeClass('last-active');
								$a.addClass('last-active');
							}
						}
					});
				}
			};
			var $toolSystem = $this.children('.x-tree-tool-system');
			if ($toolSystem.length > 0) {
				var $input = $toolSystem.children('input');
				if ($input.length > 0) {
					$this.xsearch({'$input':$input});
				}
			}
			functions.click();
			return $this;
		},
		'xform': function() {
			
		},
		'xtable': function() {
			
		},
		'xpop': function() {
			
		},
		'xgrid': function() {
			function check($children) {
				if ($children.hasClass('x-grid')) {
					var $childrens = $children.children();
					if ($childrens.length == 1) {
						return check1($children);
					} else if ($childrens.length == 2) {
						return check2($children);
					} else if ($childrens.length >= 3) {
						return check3($children);
					}
				}
				return true;
			}
			function check1($children) {
				$children.children('hr').remove();
				var $childrens = $children.children();
				if ($childrens.length == 1) {
					return check($childrens.eq(0));
				}
				return true;
			}
			function check2($children) {
				$children.children('hr').remove();
				var $childrens = $children.children();
				if ($childrens.length == 1) {
					return check1($children);
				} else if ($childrens.length == 2) {
					if (!$children.hasClass('x-grid-left-right') && !$children.hasClass('x-grid-top-bottom')) {
						$children.addClass('x-grid-left-right');
					}
					var $first = $childrens.eq(0), $last = $childrens.eq(1);
					if ($first.hasClass('x-grid') && $last.hasClass('x-grid')) {
						$first.after('<hr class="x-grid-hr">');
						if (!check($first)) {
							return false;
						}
						if (!check($last)) {
							return false;
						}
					}
				}
				return true;
			}
			function check3($children) {
				$children.children('hr').remove();
				var $childrens = $children.children();
				if ($childrens.length == 1) {
					return check1($children);
				} else if ($childrens.length == 2) {
					return check2($children);
				} else if ($childrens.length > 2) {
					$children.addClass('x-grid-error');
					XL.msg.alert(XL.getLang('0001'), 'error');
					return false;
				}
				return true;
			}
			function dragHr($hr) {
				if ($hr.attr('ready')) return; else $hr.attr('ready', true);
				$hr.on('mousedown.drag', function(e1) {
					XL.dom.noSelect();
					var cursor = $hr.css('cursor'), x1 = e1.pageX, y1 = e1.pageY, x2, y2;
					var $parent = $hr.parent(), $grids = $parent.children('.x-grid');
					var $body = $('body');
					$body.append('<div class="x-mask-drag"></div>');
					var $mask = $body.children('.x-mask-drag');
					var $first = $grids.eq(0), $last = $grids.eq(1);
					$mask.on('mousemove.drag', function(e2) {
						x2 = e2.pageX, y2 = e2.pageY;
						if (cursor == 'ew-resize') {
							$mask.css('cursor', 'ew-resize');
							var precent = ($first.width() + x2 - x1) / ($parent.width() - 4) * 100;
							precent = precent > 95 ? 95 : precent < 5 ? 5 : precent;
							$first.css('cssText', 'width:calc(' + precent + '% - 1px)');
							$last.css('cssText', 'width:calc(' + (100 - precent) + '% - 1px)');
						} else if (cursor == 'ns-resize') {
							$mask.css('cursor', 'ns-resize');
							var precent = ($first.height() + y2 - y1) / ($parent.height() - 4) * 100;
							precent = precent > 95 ? 95 : precent < 5 ? 5 : precent;
							$first.css('cssText', 'height:calc(' + precent + '% - 1px)');
							precent = precent > 5 ? 5 : precent;
							$last.css('cssText', 'height:calc(' + (100 - precent) + '% - 1px)');
						}
						XL.dom.noSelect();
						x1 = x2, y1 = y2;
					}).on('mouseup', function(e2){
						$mask.remove();
		            });
				})
			}
			return this.each(function(index, value) {
				var $grid = $(value);
				$grid.hasClass('x-grid') ? '' : $grid.addClass('x-grid');
				if (check($grid)) {
					$grid.css('overflow', 'hidden');
					$grid.find('.x-grid-hr').each(function(i, v){
						dragHr($(v))
					});
				}
			});
		},
		'xnavs': function(options) {
			var $this = this.addClass('x-no-select');
			if ($this.children().length == 0) {
				if (!XL.base.isObject(options)) return $this;
				var left = options.left, center = options.center, right = options.right;
				var p = {'children':[]};
				if (left instanceof Array) {
					var pLeft = {'tag':'div','attr':{'class':'x-nav x-nav-left'},'style':{'width':36*left.length+'px'},'children':[]};
					for (var i = 0; i < left.length; i++) {
						var leftObj = left[i];
						var pLeftChild = {'tag':'a','data':{},'name':leftObj.name,'title':leftObj.title,'attr':{'class':leftObj.icon},'event':leftObj.event};
						leftObj.url ? pLeftChild.data.url = leftObj.url : '';
						pLeft.children.push(pLeftChild);
					}
					p.children.push(pLeft);
				}
				var pCenterWidth = 'calc(100% - '+(36*(left.length+right.length)+2)+'px)';
				var pCenter = {'tag':'div','attr':{'class':'x-nav x-nav-center'},'style':{'width':pCenterWidth},'children':[]};
				if (center instanceof Array) {
					for (var i = 0; i < center.length; i++) {
						var centerObj = center[i];
						pCenter.children.push({
							'tag':'button','data':{'url':centerObj.url},'attr':{'title':centerObj.title||centerObj.name},'event':centerObj.event,
							'children':[{'tag':'i','attr':{'class':centerObj.icon}},{'tag':'span','name':centerObj.name},{'tag':'span','attr':{'class':'x-nav-close'},'name':'✖'}]
						});
					}
				}
				p.children.push(pCenter);
				if (right instanceof Array) {
					var pRight = {'tag':'div','attr':{'class':'x-nav x-nav-right'},'style':{'width':36*right.length+'px'},'children':[]};
					for (var i = 0; i < right.length; i++) {
						var rightObj = right[i];
						var pRightChild = {'tag':'a','data':{},'name':rightObj.name,'title':rightObj.title,'attr':{'class':rightObj.icon},'event':rightObj.event};
						rightObj.url ? pRightChild.data.url = rightObj.url : '';
						pRight.children.push(pRightChild);
					}
					p.children.push(pRight);
				}
				$this.xhtml(p);
			}
			$.extend($this, {
				'add': function(data) {
					var $center = $this.children('.x-nav-center'), isBreak = false;
					var $buttons = $center.children();
					if ($buttons.eq(0).outerWidth() < 66) {
						XL.msg.alert(XL.getLang('0004'), 'warning');
						return;
					}
					$buttons.each(function(index, value){
						var $button = $(value);
						if ($button.data('url') == data.url) {
							$button.click();
							isBreak = true;
							return false;
						}
					});
					if (isBreak) return;
					$center.append('<button title="'+(data.title||data.name)+'" data-url="'+data.url+'"><i class="'+data.icon+'"></i><span>'+data.name+'</span><span class="x-nav-close">✖</span></button>');
					$this.resize();
					$center.children().last().click();
				},
				'resize': function() {
					var $center = $this.children('.x-nav-center');
					var $buttons = $center.children().css('width','');
					var $last = $buttons.last();
					if ($last.length > 0 && $last.offset().top > 3) {
						$buttons.css('cssText', 'width:calc('+(100/$buttons.length)+'% - '+(3+3/$buttons.length)+'px');
					}
				}
			});
			$this.resize();
			return $this;
		},
		'xsearch': function(options) {
			var $input = options.$input;
			if ($input == null || !($input instanceof jQuery) || $input.length != 1 || $input[0].tagName != 'INPUT') {
				XL.msg.alert(XL.getLang('0005'), 'error');
				return;
			}
			options = $.extend(true, {'nodeType':'span','hide':false,'closest':'li'}, options);
			var functions = {
				'clearSearch': function($this) {
					$this.find('.x-highlight').each(function(index, value) {
						value.outerHTML = value.textContent;
					});
					$this.find('.x-search-hide').removeClass('x-search-hide');
					$this.find('.x-search-find').removeClass('x-search-find');
				},
				'search': function($tags, text) {
					var textLower = text.toLowerCase();
					for (var i = 0; i < $tags.length; i++) {
						var tag = $tags[i], $tag = $tags.eq(i), newInnerHTML = '';
						var childNodes = tag.childNodes;
						for (var j = 0; j < childNodes.length; j++) {
							var childNode = childNodes[j];
							if (childNode.nodeName == '#text') {
								var ctx = childNode.textContent, ctxLower = ctx.toLowerCase();
								if (ctxLower.indexOf(textLower) != -1) {
									$tag.hasClass('x-search-find') ? '' : $tag.addClass('x-search-find');
									var words = ctxLower.split(textLower), newCtx = '', wordIndex = 0;
									for (var k = 0; k < words.length - 1; k++) {
										var start = wordIndex + words[k].length, end = start + textLower.length;
										newCtx += ctx.substring(wordIndex, start) + '<em class="x-highlight">' + ctx.substring(start, end) + '</em>';
										wordIndex = end;
									}
									newInnerHTML += newCtx + ctx.substring(wordIndex);
								} else {
									newInnerHTML += ctx;
								}
							} else {
								newInnerHTML += childNode.outerHTML;
							}
						}
						tag.innerHTML = newInnerHTML;
						if (options.hide) {
							var $closest = options.closest ? $tag.closest(options.closest) : $tag;
							if ($tag.hasClass('x-search-find')) {
								$closest.removeClass('x-search-hide');
							} else {
								$closest.hasClass('x-search-find') ? '' : $closest.addClass('x-search-hide');
							}
						}
					}
				},
				'deepSearch': function($this, text) {
					$this.children().each(function(index, value) {
						var $value = $(value), $childens = $value.children();
						if ($childens.length > 0) {
							$childens.each(function(i, v) {
								functions.deepSearch($(v), text);
							});
						} else {
							functions.search($value, text);
						}
					});
				},
				'xtreeSearch': function($this, text) {
					var $root = $this.children('ul');
					if (text.length == 0) {
						$root.find('ul').removeClass('x-search-hide').removeClass('x-search-show');
						$root.find('li').removeClass('x-search-hide').removeClass('x-search-show');
					} else {
						$root.find('ul').addClass('x-search-hide');
						$root.find('li').addClass('x-search-hide');
						var textLower = text.toLowerCase();
						$root.find('span').each(function(index, value) {
							var ctx = value.textContent, ctxLower = ctx.toLowerCase();
							if (ctxLower.indexOf(textLower) != -1) {
								var words = ctxLower.split(textLower), newCtx = '', wordIndex = 0;
								for (var i = 0; i < words.length - 1; i++) {
									var start = wordIndex + words[i].length, end = start + textLower.length;
									newCtx += ctx.substring(wordIndex, start) + '<em class="x-highlight">' + ctx.substring(start, end) + '</em>';
									wordIndex = end;
								}
								var $span = $(value);
								var $parent = $span.parent();
								while (!$parent.hasClass('x-tree')) {
									var tagName = $parent[0].tagName;
									if (tagName == 'LI' || tagName == 'UL') {
										$parent.removeClass('x-search-hide').addClass('x-search-show');
									}
									$parent = $parent.parent();
								}
								$span.html(newCtx + ctx.substring(wordIndex));
							}
						});
					}
				}
			};
			var $this = $(this), input = $input[0], runSid = null;
			input.oninput = function(e) {
				var sid = setTimeout(function() {
					if (runSid != null) {
						clearTimeout(runSid);
						runSid = null;
					}
					runSid = sid;
					functions.clearSearch($this);
					var text = input.value.trim();
					if ($this.hasClass('x-tree')) {
						functions.xtreeSearch($this, text);
					}
					if (text.length > 0) {
						if (options.nodeType) {
							functions.search($this.find(options.nodeType), text);
						} else {
							functions.deepSearch($this, text);
						}
					}
				}, 50);
			};
			return $this;
		}
	});
})();