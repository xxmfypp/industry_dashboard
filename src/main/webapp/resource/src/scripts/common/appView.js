/**
 * Created by weirong.li on 2016/8/17 0017.
 */
;define(function(require,exports){
	var
		View = Backbone.View,
		Model = Backbone.Model,
		ui = require("ui"),
		util = require("util"),
		rs = window.rs,
		pageData = rs.data.page;

	exports.AppView =  View.extend({
		events:{
			"click .js-query-btn":function(){
				var param = util.formSerializeJson(this.$(".js-query-form"));
				this.queryParam.set(param);
				this.searchModel.set(param);
			}
		},
		initialize:function(options){
			this.options = options;
			this.on("change:query",this.startQuery,this);
			//用于重置分页
			this.queryParam = new Model();
			//查询需要的参数
			this.searchModel = new Model({
				pageNumber: pageData.pageNumber || 1,
				pageSize: pageData.pageSize || 10
			});
			this.currentPrams = this.searchModel.toJSON();
			this.queryParam.bind("change", function () {
				this.searchModel.set({pageNumber: 1}, {silent: true});
				this.paginationView && this.paginationView.set({currentPage: 1});
			}, this);

			this.searchModel.bind("change", this.setQuery, this);
			this.initPaginationView();
		},
		initPaginationView: function () {
			var _this = this;
			this.paginationView = new ui.PaginationView({
				config: {
					targetDom: this.$(".pagination-cot-wrap"),
					onPageChange: function (pageNum) {
						_this.searchModel.set("pageNumber", pageNum || 1);
					},
					hasRpp:true,
					pageListSetting: {
						hasTotalTag:true,
						totalCount: pageData.totalRow,
						currentPage:pageData.pageNumber || 1,
						showStyle: [1, 3, 1], //[left,center>2,right]
						buttonText: {
							first: "<<",
							previous: "<",
							next: ">",
							last: ">>"
						},
						rpp:pageData.pageSize  || this.searchModel.get("pageSize")
					}
				}
			});
			this.paginationView.bind("rppChanged",function(rpp){
				this.searchModel.set("pageSize",rpp);
			},this)
		},
		setQuery:function(){
			rs.event.trigger("set:query",this.searchModel.toJSON());
		},
		startQuery:function(prams){
			this.currentPrams = prams;
			this.queryListHtml();
		},
		queryListHtml:function(){
			rs.mainApp.toggleSpinning(this.$(".table-wrap")[0],"loading");
			//rs.mainApp.spinner.spin(this.$(".table-wrap")[0]);
			$.when(this.send()).then(
				this.renderList.bind(this)
			).always(function(){
				rs.mainApp.toggleSpinning(this.$(".table-wrap")[0],"loaded");
			}.bind(this));
		},
		send:function(){
			return rs.ajaxFragment({
				cache:false,
				url:rs.apiRoot + this.options.path,
				data:this.currentPrams,
				dataType: "text"
			});
			this.afterRender();
		},
		renderList:function(html){
			this.$(".js-data-list-cot").html(html);
			this.paginationView.set({totalCount:pageData.totalRow});
		},
		afterRender:function(){}
	});
});