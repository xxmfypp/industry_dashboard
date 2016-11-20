/**
 * Created by Gavin Li on 11/4/2015.
 */
;define("common/util", function (require) {

	var
		ui = require("./ui"),
		emptyFn = new Function();
	return {
		loadModule: function (modules) {
			var
				defer = $.Deferred(),
				modules = modules || [],
				len;
			if (!_.isArray(modules)) {
				_.isString(modules) && (modules = {"module": modules});
				modules = [modules];
			}
			len = modules.length;
			$.each(modules, function (k, m) {
				//invoke before callback
				_.isFunction(m["before"]) && m["before"]();
				seajs.use(m["module"], function (mod) {
					var args = m["args"] || [], initObj;
					if (mod && mod.init) {
						!_.isArray(args) && (args = [args]);
						initObj = mod.init.apply(mod, args);
					}
					--len || defer.resolve(mod, initObj);
				});
			});
			return defer;
		},
		/**
		 * formatNumber
		 * @param number
		 * @param thousands_sep
		 * @returns {string|*} 9,123,456
		 */
		formatNumber: function (number, thousands_sep) {
			number = String(number);
			var decimal = (number.match(/\.\d+/) || [])[0] || "";
			number = number.replace(/\.\d+/, '');
			thousands_sep = thousands_sep || ",";
			if (number && number.length > 3) {
				number = number.split("").reverse().join("").replace(/(\d{3})(?=\d)/g, "$1" + thousands_sep).split("").reverse().join("");
			}
			return number + decimal
		},
		sum: function () {
			var arr = _.flatten(Array.prototype.slice.apply(arguments));
			return _.reduce(arr, function (a, b) {
				return a + b
			})
		},
		/**
		 *var args = urlArgs();
		 *var q = args.q || "";
		 *var n = args.n ? parseInt(args.n) : 10;
		 */
		urlArgs: function () {
			var args = {};
			var query = location.search ? location.search : location.hash;
			if (!query) {
				return args;
			}
			query = query.substring(1);
			var pairs = query.split('&');
			for (var i = 0; i < pairs.length; i++) {
				var pos = pairs[i].indexOf('=');
				if (pos == -1) continue;
				var name = pairs[i].substring  (0, pos);
				var value = pairs[i].substring(pos + 1);
				value = decodeURIComponent(value);
				args[name] = value;
			}
			return args;
		},
		/**
		 * parseUrlParam
		 * @param url
		 * @returns {*}
		 */
		parseUrlParam: function (url) {
			var reg = /[\?&]?([^=]*)=([^&]+)/g, args, match;
			url === undefined && (url = location.search || location.hash)
			if (url) {
				args = {};
				while (match = reg.exec(url)) {
					args[match[1]] = match[2];
				}
			}
			return args;
		},
		/**
		 * objToUrlParam
		 * @param obj
		 */
		objToUrlParam: function (obj) {
			var param = _.pairs(obj).map(function (arr) {
				arr[1] = encodeURIComponent(arr[1]);
				return arr.join("=")
			}).join("&")
			return param ? "?" + param : ""
		},
		getUrlQuery: function (name) {
			var
				sRE = "(?:[\\?&])" + name + "=([^&]*);?",
				oRE = new RegExp(sRE),
				query = location.search ? location.search : location.hash;
			if (oRE.test(query)) {
				return decodeURIComponent(RegExp["$1"]);
			} else {
				return null;
			}
		},
		siteSuffix: function () {
			return window.location.pathname.match(/^\/([^\/]+)/)[1];
		},
		getCookie: function (sName) {
			var
				sRE = "(?:; )?" + sName + "=([^;]*);?",
				oRE = new RegExp(sRE);
			if (oRE.test(document.cookie)) {
				return decodeURIComponent(RegExp["$1"]);
			} else {
				return null;
			}
		},
		setCookie: function (sName, sValue, oExpires, sPath, sDomain, bSecure) {
			var sCookie = sName + "=" + encodeURIComponent(sValue), d;
			if (oExpires) {
				if (typeof oExpires == "string") {
					d = new Date();
					d.setTime(new Date().getTime() + parseInt(oExpires));
					oExpires = d.toGMTString();
				} else {
					oExpires = oExpires.toGMTString();
				}
				sCookie += "; expires=" + oExpires;
			}
			if (sPath) {
				sCookie += "; path=" + sPath;
			}
			if (sDomain) {
				sCookie += "; domain=" + sDomain;
			}
			if (bSecure) {
				sCookie += "; secure";
			}
			document.cookie = sCookie;
		},
		removeCookie: function (sName, sPath, sDomain, bSecure) {
			this.setCookie(sName, "", new Date(0), sPath, sDomain, bSecure);
		},
		successTip: function (title, msg, callBack, times) {
			this.alert(title, msg, callBack, times, "icon-success text-primary", "btn-primary");
		},
		filterScript:function(text){
			var rt = text
					.replace(/<\s*script[^>]*>[\s\S]*?<\/\s*script\s*>/ig,'')
					.replace(/on(?:(?:click)|(?:load)|(?:error)|(?:change)|(?:mousedown)|(?:mouseup)|(?:mousemove))\s*=('|")[\s\S]*?\1/ig,'')
			return rt
		},
		alert: function (title, msg, callBack, times, tipIcon, btnClass) {
			callBack = callBack || emptyFn;
			tipIcon = tipIcon || "icon-error text-primary";
			var msgCot = "<div style='line-height: 4rem;'>" +
				"<span class='iconfont " + tipIcon + "' style='font-size: 3rem;margin: -7px 1rem 0 0; vertical-align: middle;display: inline-block;'></span>" +
					"<span style='word-break: break-all;display: inline-block;line-height: 1.5;width: -webkit-calc(100% - 6rem);width: -moz-calc(100% - 6rem);width: calc(100% - 6rem);vertical-align: middle;'>" + msg +
				"</span></div>";
			var dialog = new ui.Dialog({
				title: title,
				content: msgCot,
				hasMark: true,
				close: callBack,
				position: "top",
				buttons: {
					"确认": {
						"className": "btn " + (btnClass || "btn-primary"),
						"callback": function(){
							this.options.close = null;
							callBack();
						}
					}
				}
			}), timer;
			if (times) {
				dialog.$(".btn").append("<em>" + times + "</em>");
				timer = setInterval(function () {
					dialog.$(".btn em").text(--times);
					times < 1 && (dialog.close(), clearInterval(timer));
				}, 1000);
				dialog.bind("close", function () {
					clearInterval(timer);
				});
			}
			return dialog
		},
		confirm: function (title, msg, okFn, cancelFn) {
			okFn = okFn || emptyFn;
			cancelFn = cancelFn || emptyFn;
			var msgCot = "<div style='line-height: 4rem;'>" +
				"<span class='iconfont icon-help text-primary' style='font-size: 3rem;margin: -7px 1rem 0 0; vertical-align: middle;display: inline-block;'></span>" +
					"<span style='word-break: break-all;display: inline-block;line-height: 1.5;width: -webkit-calc(100% - 6rem);width: -moz-calc(100% - 6rem);width: calc(100% - 6rem);vertical-align: middle;'>" + msg +
				"</span></div>";
			return new ui.Dialog({
				title: title,
				content: msgCot,
				hasMark: true,
				position: "top",
				buttons: {
					"取消": {
						"className": "btn-default",
						"callback": cancelFn
					},
					"确认": {
						"className": "btn-primary",
						"callback":  okFn
					}
				}
			})
		},
		formSerializeJson: function (form) {
			var o = {},checkBoxes;
			checkBoxes = $(form).find("input[type='checkbox']:not(:checked)").map(function(index,ipt){var ob = {value:""}; ob.name = ipt.name;return ob});
			checkBoxes = [].slice.call(checkBoxes);
			_.each(checkBoxes.concat($(form).serializeArray()), function (obj) {
				o[obj.name] = o[obj.name] ? ([o[obj.name],obj.value].join(",")) :obj.value;
				return o
			});
			o.tha = new Date().getTime();
			return o
		},
		getFileSize: function (element) {
			var file;
			if (element && element.files && (file = element.files[0])) {
				return file.size;
			}
			return 0
		},
		parseFileSize: function (size) {
			var sizeName = ["B", "K", "M", "G", "T", "E"], val = size, curN, desc;
			do {
				curN = sizeName.shift();
				if (curN) {
					desc = val.toFixed(2) + curN;
				}
			} while ((val = val / 1024) > 0.9 && curN)
			return desc
		},
		timeRun: function (secs, el, ctx) {
			var defer = $.Deferred(), update, ctx = ctx || window;
			if (secs) {
				update = function () {
					$(el).text(secs);
					secs-- ? ctx.timeTip = setTimeout(update, 1000) : defer.resolve("");
				};
				update();
			} else {
				defer.resolve("")
			}
			return defer;
		},
		popupCenter: function (url, title, w, h) {
			// Fixes dual-screen position                         Most browsers      Firefox
			var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
			var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;
			var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
			var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
			w = w||1230;
			h = h||700;
			var left = ((width / 2) - (w / 2)) + dualScreenLeft;
			var top = ((height / 2) - (h / 2)) + dualScreenTop;
			var newWindow = window.open(url, title, 'scrollbars=yes, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);
			// Puts focus on the newWindow
/*			title && newWindow.document && _.delay(function(){
				newWindow.document.title = title
			},300);*/
			title && (newWindow.onload = function(){
				newWindow.document.title = title
			});
			if (window.focus) {
				newWindow.focus();
			}
			return newWindow;
		},
		//导出Excel 主要为了判空处理
		exportExcel:function(url){
			var callbackTime = new Date().getTime(),ctx = this;
			if(!$("#export_iframe").length){
				$('<iframe name="export_iframe" id="export_iframe" style="width: 1px;height: 1px;position: absolute;top: -1000px;"></iframe>').appendTo(document.body);
			}
			window[callbackTime] = function(obj){
				ctx.alert("系统提示",obj.message,null,3);
				delete window[callbackTime];
			};
			url = url + (!!~url.indexOf("?")?"&":"?") + "callback=" + callbackTime;
			window.open(url,"export_iframe");
		}
	}
});

