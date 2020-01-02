(function() {
	var XL = {
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
				return typeof obj != null && typeof obj == 'object' && !(obj instanceof Array);
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
				var cvalue = XL.base.getCookie(cname);
					document.cookie= cname + '=' + cvalue + ';expires=' + exp.toGMTString() + ';path=/';
			},
			'uuid': function() {
				var uuid = '';
				for (var i = 0; i < 8; i++) {
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
				if (XL.base.isBlank(pattern)) {
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
			}
		},
		'tree': {
			'init': function(options) {
				var $tree = typeof options == 'string' ? $(options) : options instanceof jQuery ? options : create(options);
				if (!$tree.hasClass('xl-tree')) {
					return;
				}
				function create(p) {
					
				}
				XL.tree.toolbar.init($tree);
				XL.tree.node.click($tree);
			},
			'toolbar': {
				'init': function($tree) {
					XL.tree.toolbar.system.init($tree);
					XL.tree.toolbar.custom.init($tree);
				},
				'system':{
					'init': function($tree) {
						$tree.children('.xl-tree-tool-system').on('click', 'a', function(e) {
							var $a = $(this);
							if ($a.hasClass('xl-icon-collapse')) {
								XL.tree.toolbar.system.collapse($tree);
							} else if ($a.hasClass('xl-icon-link')) {
								XL.tree.toolbar.system.link($tree);
							}
						});
						XL.tree.toolbar.system.search($tree);
					},
					'collapse': function($tree) {
						var $root = $tree.children('ul');
						$root.find('.xl-icon-arrow-bottom').removeClass('xl-icon-arrow-bottom').addClass('xl-icon-arrow-right');
						$root.find('ul').hide();
					}, 
					'link': function($tree) {
						
					},
					'search': function($tree) {
						var runSid = null, $root = $tree.children('ul');
						var searchInput = $tree.children('.xl-tree-tool-system').children('input')[0];
						searchInput.oninput = function(e) {
							var sid = setTimeout(function() {
								if (runSid != null) {
									clearTimeout(runSid);
									runSid = null;
								}
								runSid = sid;
								$root.find('span').each(function(index, value) {
									var $em = $(value).children('em');
									if ($em.length > 0) {
										value.outerHTML = value.outerHTML.replace(/<em>/g, '').replace(/<\/em>/g, '');
									}
								});
								if (searchInput.value == '') {
									$root.find('li').show();
								} else {
									$root.find('li').hide();
									var text = searchInput.value, textLower = text.toLowerCase();
									$root.find('span').each(function(index, value) {
										var ctx = value.textContent, ctxLower = ctx.toLowerCase();
										if (ctxLower.indexOf(textLower) != -1) {
											var words = ctxLower.split(textLower), newCtx = '', wordIndex = 0;
											for (var i = 0; i < words.length - 1; i++) {
												var start = wordIndex + words[i].length, end = start + textLower.length;
												newCtx += ctx.substring(wordIndex, start) + '<em>' + ctx.substring(start, end) + '</em>';
												wordIndex = end;
											}
											var $span = $(value);
											var $parent = $span.parent();
											while (!$parent.hasClass('xl-tree')) {
												if ($parent[0].tagName == 'LI') {
													if (!$parent.is(':hidden')) {
														break;
													}
													$parent.show();
												}
												$parent = $parent.parent();
											}
											$span.html(newCtx + ctx.substring(wordIndex));
										}
									});
								}
							}, 50);
						};
					}
				},
				'custom': {
					'init': function($tree) {
						
					}
				}
			},
			'node': {
				'click': function($tree) {
					var $root = $tree.children('ul');
					$root.on('click', function(e) {
						var $tag = $(e.target), tagName = e.target.tagName, isArrow = false;
						if (tagName == 'UL' || tagName == 'LI') {
							return;
						}
						if (tagName == 'I') {
							if ($tag.hasClass('xl-icon-arrow-bottom')) {
								isArrow = true;
								$tag.parent().children('ul').hide();
								$tag.removeClass('xl-icon-arrow-bottom').addClass('xl-icon-arrow-right');
							} else if ($tag.hasClass('xl-icon-arrow-right')) {
								isArrow = true;
								$tag.parent().children('ul').show();
								$tag.removeClass('xl-icon-arrow-right').addClass('xl-icon-arrow-bottom');
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
			}
		},
		'grid': {},
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
				p = $.extend({'template':null,'style':'info','autoclose':false,'position':'top-center','icon':true,'group':false,'onOpen':false,'onClose':false}, p);
				function remove($elm) {
					p.onClose ? p.onClose() : '';
					$elm.removeClass('xl-msg-alert-in');
					setTimeout(function(){$elm.remove();}, 390);
				};
				var $spopGroup = $('#xl_msg_alert_' + p.group);
				$spopGroup.length > 0 ? remove($spopGroup) : '';
				var $container = $('#xl_msg_alert_' + p.position);
				var icon = (!p.icon) ? '' : '<i class="xl-msg-alert-icon xl-msg-alert-icon--' + p.style +'"></i>';
				var layout ='<div class="xl-msg-alert-close">&times;</div>' + icon + '<div class="xl-msg-alert-body">' + p.template + '</div>';
				if ($container.length == 0) {
					var popContainer = document.createElement('div');
					popContainer.setAttribute('class', 'xl-msg-alert-container xl-msg-alert-' + p.position);
					popContainer.id = 'xl_msg_alert_' + p.position.replace('-', '_');
					document.body.appendChild(popContainer);
					$container = $('#xl_msg_alert_' + p.position.replace('-', '_'));
				}
				var pop = document.createElement('div');
				pop.setAttribute('class', 'xl-msg-alert xl-msg-alert-out xl-msg-alert-in xl-msg-alert-' + p.style);
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
				$(pop).find('.xl-msg-alert-close').on('click', function(event){
					sid ? clearTimeout(sid) : '';
					remove($(pop));
				});
			}
		},
	};
	window.XL = XL;
})();