/**
 * Created by weirong.li on 2016/6/30 0030.
 */
;
define(function (require) {
	var Layout = Backbone.Layout,
		Model = Backbone.Model,
		View = Backbone.View,
		util = require("util"),
		ui = require("ui"),
		rs = window.rs;
	rs.util = util;
	/**
	 * AppRouter
	 */
	var AppRouter = Backbone.Router.extend({
		initialize: function () {
			//match (/path/)(?search)?(#get)
			this.route(
				/^(\/?[^\/\?&#]+(?:\/[^\/\?&#]*)*\/?)((?:[\?&][^\?&#]+=[^\?&#]*)+)?(?:#(.*))?$/,
				"changeUrl"
			);
		},
		routes: {
			"": "returnRoot"
		},
		changeUrl: function (path, query, hash) {
			this.trigger("change:url", path, query, hash);
			return this
		},
		returnRoot: function () {
			this.trigger("return:root");
		}
	});
	/**
	 * MenuView
	 *
	 */
	var MenuView = View.extend({
		events: {
			"click .js-menu-handle": "toggleExpand",
			"click .menu>.menu-item>.item-body": "toggleOpen"
		},
		initialize: function () {
			this.status = "expand";
			this.currentActiveSubMenu = null;
		},
		toggleExpand: function () {
			this.status = this.$(".js-top-menu")
					.toggleClass("expand collapsed")
					.attr("class")
					.match(/(?:expand)|(?:collapsed)/)[0];
		},
		toggleOpen: function (e) {
			//$(e.currentTarget).toggleClass("open");
			e.stopPropagation();
			var
				item = $(e.currentTarget).parent(),
				isOpen = item.hasClass("open");
			item[isOpen ? "removeClass" : "addClass"]("open")
				.find(".sub-menu")[isOpen ? "slideUp" : "slideDown"](200);
			//$(e.currentTarget).toggleClass("open").find(".sub-menu").slideToggle(200);
		},
		activeMenu: function (path) {
			var item,curMenuId, menuId,willActiveSubMenu;
			if (this.currentActiveSubMenu) {
				curMenuId = this.currentActiveSubMenu.removeClass("active").attr("menu");
			}
			if (path) {
				//路径 只取两层
				path = path.split("/").slice(0, 2).join("/");
				item = this.$(".sub-menu .item-body[href*='" + path + "']");
				if (item.length) {
					item.length > 1 && (item = item.filter(function(){return $(this).attr("href") == "#" + path}));
					menuId = item.addClass("active").attr("menu");
					if (curMenuId != menuId) {
						//curMenuId && this.$(".menu>.menu-item[index='" + curMenuId + "']").removeClass("active open").find(".sub-menu").slideUp(200);
						willActiveSubMenu = this.$(".menu>.menu-item[index='" + menuId + "']");
						this.closeAllOpenedMenu(willActiveSubMenu[0]);
						willActiveSubMenu
								.addClass("active open")
								.find(".sub-menu")[this.status == "expand"?"slideDown":"show"]();
					}
					this.currentActiveSubMenu = item;
				}else{
					this.currentActiveSubMenu = null;
					this.closeAllOpenedMenu();
				}
			}
		},
		closeAllOpenedMenu:function(exclude){
			this.$(".menu>.menu-item.open")
					.filter(function(id,el){ return !exclude || el !== exclude})
					.removeClass("active open")
					.find(".sub-menu")[this.status == "expand"?"slideUp":"hide"]();
		}
	});

	/**
	 * UrlModel
	 */
	var UrlModel = Model.extend({
		defaults: {
			path: "",
			query: "",
			hash: ""
		},
		url: function (obj) {
			var location = this.toJSON();
			obj && $.extend(location, obj);
			return location.path + location.query + (location.hash ? "#" + location.hash : "");
		}
	});
	/**
	 * AlterPwdFormView
	 *
	 */
	var AlterPwdFormView = ui.FormPopupView.extend({
		template: "#common-alter-pwd-tpl",
		name:"修改密码",
		afterOpen:function(){
			this.$(".js-popup-form").validate({
				rules:{
					newPwd:{
						pwd:true
					},
					newPwd2:{
						equalTo:"[name='newPwd']"
					}
				},
				messages:{
					newPwd2:{
						equalTo:"两次密码输入不一致"
					}
				}
			});
			return this;
		},
		valid:function(defer){
			if($(".js-popup-form").valid()){
				defer.resolve({});
			}
		},
		send:function(obj){
			var ctx = this;
			$(".js-popup-form").ajaxSubmit({
				success:function(data){
					if(Number(data.status) == 0){
						ctx.trigger("send:success","修改成功");
					}else{
						ctx.trigger("send:error",data.msg);
					}
				},
				error:function(msg,status){
					ctx.trigger("send:error","修改失败");
				}
			});
		},
		onSendSuccess:function(){
			util.successTip("成功提示","您的密码修改成功！ <a class='btn btn-link' href='"+ rs.apiRoot +"logout'>立刻登陆</a>",function(){
				location.href = rs.apiRoot +"logout";
			},3);
		}
	});
	/**
	 * MainApp
	 */
	var MainApp = Layout.extend({
		events:{
			"click .js-alter-pwd":"changePwd",
			"click .js-logout-btn":"logout"
		},
		/**
		 * url = {path}?{query}#{hash}
		 */
		initialize: function (options) {
			this.router = new AppRouter();
			this.menu = new MenuView({
				el: $("#main-menu-cot")
			});
			this.initSpinner();
			this.listenTo(this.router, "change:url", this.changeUrl.bind(this));
			this.listenTo(this.router, "return:root", this.returnRoot.bind(this));
			this.subPage = new UrlModel();
			this.currentApp = null;
			this.subPage.on("change:path", this.loadSubPage, this);
			this.subPage.on("change:query", this.changeQuery, this);
			this.subPage.on("change:hash", this.changeHash, this);

			rs.event.on("set:query", this.setQuery, this);
			rs.event.on("set:hash", this.setHash, this);
			rs.event.on("reload:subPage", this.loadHtml, this);
			Backbone.history.start();
		},
		initSpinner: function () {
			this.spinner = new Spinner({
				color: "#2b6ca8",
				shadow: false,
				lines: 10,
				width: 4
			});
		},
		changeUrl: function (path, query, hash) {
			path = path || "";
			query = query || "";
			hash = hash || "";
			var subPage = this.subPage;
			var prePath = subPage.get("path");
			var silent = path != prePath;
			subPage.set("query", query, {silent: silent});
			subPage.set("hash", hash, {silent: silent});
			//var data = util.parseUrlParam(query);
			subPage.set("path", path);
		},
		returnRoot: function () {
			//this.changeUrl("index");
		},
		toggleSpinning: _.throttle(function (dom, status) {
			if (status === "loading") {
				this.spinner.spin(dom);
				$(dom).addClass("loading")
			} else {
				$(dom).removeClass("loading");
				this.spinner.stop();
			}
		}, 300),
		loading: function () {
			this.toggleSpinning(this.$(".layout-main")[0], "loading");
		},
		loaded: function () {
			this.toggleSpinning(this.$(".layout-main")[0], "loaded");
		},
		loadSubPage: function () {
			var
				ctx = this,
				subPage = this.subPage.toJSON(),
				queryData = util.parseUrlParam(subPage.query),
				container = $("#module-page-container");
			this.loading();
			this.menu.activeMenu(subPage.path);
			this.pageAjax && this.pageAjax.abort();
			$.when(
				//加载模块js
				this.loadSubModule(subPage.path, subPage.hash),
				//加载页面HTML结构
				this.pageAjax = this.loadHtml()
				)
				.then(function (app) {
					if (app) {
						container.undelegate();
						if (app.init) {
							ctx.currentApp = app.init({
								el: container,
								query: queryData ||{},
								hash: subPage.hash,
								path: subPage.path
							});
							return
						}
					}
					ctx.currentApp = null;
				})
				.always(function () {
					ctx.loaded();
				});
		},
		loadSubModule: function (path, hash) {
			var
				def = $.Deferred(), parsePathArr, modulePath;
			if (path && hash != "nonejs") {
				//js 目录最多两层 多出的层级 用 “-”链接 org-manage/register/edit ===》 org-manage/register-edit
				parsePathArr = path.split("/");
				modulePath = parsePathArr.slice(0, 1);
				parsePathArr.length >1 && modulePath.push(parsePathArr.slice(1).join("-"));
				seajs.use("modules/" + modulePath.join("/"), function (app) {
					def.resolve(app);
				});
			} else {
				def.resolve(null);
			}
			return def;
		},
		loadHtml:function () {
			var
				ctx = this,
				subPage = this.subPage.toJSON(),
				queryData = util.parseUrlParam(subPage.query),
				container = $("#module-page-container");;
			return rs.ajax({
				url: rs.apiRoot + subPage.path,
				data: queryData,
				dataType: "text",
				success: function (html) {
					container.empty().html(html);
				},
				error:function(){
					var prePage = ctx.subPage.previousAttributes();
					if(prePage.path){
						ctx.subPage.set(prePage,{silent:true});
					}
				}
			})
		},
		changeQuery: function () {
			var subPage = this.subPage.toJSON();
			var query = util.parseUrlParam(subPage.query);
			this.currentApp && this.currentApp.trigger("change:query", query);
		},
		changeHash: function () {
			var subPage = this.subPage.toJSON();
			this.currentApp && this.currentApp.trigger("change:hash", subPage.hash);
		},
		setQuery: function (queryObj) {
			var subPage = this.subPage;
			if (queryObj) {
				clearTimeout(this.delayTime);

				this.delayTime = _.delay(function () {
					var hashUrl = subPage.url({
						"query": util.objToUrlParam(queryObj)
					});
					location.hash = hashUrl;
				}, 100);
			}else{
				location.hash = subPage.url({query:""});
			}
		},
		setHash: function (hashString) {
			if (hashString) {
				clearTimeout(this.delayTime);
				var subPage = this.subPage;
				this.delayTime = _.delay(function () {
					var hashUrl = subPage.url({
						"query": hashString
					});
					location.hash = hashUrl;
				}, 100);
			}
		},
		changePwd:function(){
			new AlterPwdFormView({
				title:"修改密码",
				model:new Model({})
			});
		},
		logout:function(){
			util.confirm("操作确认","即将 <span class='text-danger'>安全退出</span>，是否继续？",function(){
				location.replace(rs.apiRoot + "logout");
			});
		}
	});
	return {
		init: function (args) {
			rs.mainApp = new MainApp(args).render();
		}
	}
});