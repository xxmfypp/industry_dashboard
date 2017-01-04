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
		rs = window.rs,
		role = rs.role;
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
		initialize: function () {

		},
		activeMenu: function (path) {
            path == "index" && (path = "");
				this.$(".nav-item.active").removeClass("active");
				this.$(".nav-item[href='#"+path+"']").addClass("active");
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
	 * MainApp
	 */
	var MainApp = Layout.extend({
		events:{
			"click .js-logout-btn":"logout"
		},
		/**
		 * url = {path}?{query}#{hash}
		 */
		initialize: function (options) {
			this.router = new AppRouter();
			this.menu = new MenuView({
				el: $(".js-top-nav")
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
			this.changeUrl(role == "UPLOAD"?"upload":"index");
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
				this.pageAjax = this.loadHtml(subPage.path, subPage.hash)
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
		loadHtml:function (path,hash) {
			var
				ctx = this,
				subPage = this.subPage.toJSON(),
				queryData = util.parseUrlParam(subPage.query),
				container = $("#module-page-container"),
				url;
			url = hash === "api"? (rs.apiRoot + path):(rs.staticRoot + "template/" + path + ".html");
			return rs.ajax({
				url: url,
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