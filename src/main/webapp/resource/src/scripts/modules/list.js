/**
 * Created by liwir_000 on 1/8/2017.
 */
;define(function (require) {
    var
        util = require("util"),
        AppView = require("appView").AppView;

    var headerMap = {
        "id":"编号",
        "store_name":"门店名称",
        "docket_time":"单据日期",
        "docket_type":"单据类型",
        "docket_code":"单据编码",
        "materiel":"物料",
        "specifications":"规格",
        "materiel_type":"物料类别",
        "materiel_types":"物料类别",
        "materiel_specifications":"物料及规格",
        "unit":"单位",
        "income_no":"收料数量",
        "expenditure_no":"发料数量",
        "strip":"条只",
        "unit_price":"单价",
        "cost_unit_price":"成本单价",
        "sale_unit_price":"销售单价",
        "cost_amount":"成本金额",
        "sale_amount":"销售金额",
        "income_amount":"收入金额",
        "expenditure_amount":"发出金额",
        "income_department":"收料部门",
        "income_departments":"收料部门",
        "expenditure_department":"发料部门",
        "expenditure_departments":"发料部门",
        "distribution_department":"配送部门",
        "distribution_departments":"配送部门",
        "supplier":"供应商",
        "suppliers":"供应商",
        "voucher_no":"凭证号",
        "remark":"备注",
        "num":"收料数量",
    };
    /**
     * App
     */
    var App = AppView.extend({
        initialize:function (args) {
            this.constructor.__super__.initialize.apply(this, arguments);
            //合并父类的events
            if (this.constructor.prototype.events) {
                this.events = $.extend( true,{},this.constructor.__super__.events,this.events);
                this.delegateEvents();
            }
            this.tableTpl = {
              header:_.template($("#table-header-tpl").html()),
              body:_.template($("#table-body-tpl").html()),
              search:_.template($("#search-param-tpl").html())
            };
            this.currentListType = "";
            //切换列表查询方式
            this.bind("change:hash",this.changeDataListByType,this);
            this.changeDataListByType("query_store_storage");
        },
        changeDataListByType:function (listType) {
            if(!listType||this.currentListType == listType) return
            this.currentListType = listType;
            this.activeMenu();
            this.queryList(true);

        },
        //发送ajax call
        send:function () {
            return rs.ajax({
                cache:false,
                url:rs.apiRoot + this.currentListType,
                data:this.currentPrams,
                dataType: "JSON"
            });
        },
        //发送并触发渲染列表
        queryList:function (isChangeType) {
            rs.toggleSpinning(this.$(".table-wrap")[0],"loading");
            //rs.mainApp.spinner.spin(this.$(".table-wrap")[0]);
            $.when(this.send()).then(function (data) {
                this.renderList(data,isChangeType);
            }.bind(this)
            ).always(function(){
                rs.toggleSpinning(this.$(".table-wrap")[0],"loaded");
            }.bind(this));
        },
        //渲染列表
        renderList:function(data,isChangeType){
            var
                pages = data.pages,
                list = pages.list || [{}],
                columnNames = (list[0]||{}).columnNames||[],
                dataList = _.pluck(list,"columnValues");
            if(isChangeType){
                this.renderSearchParams(data.params);
            }
            this.renderListHeader(columnNames);
            this.renderListBody(dataList,columnNames);

            this.paginationView.set({totalCount:pages.totalRow});

        },
        renderListHeader:function (columnNames) {
            this.$(".js-data-list-header").html(this.tableTpl.header({
                headerMap:headerMap,
                columns:columnNames
            }));
        },
        renderListBody:function (dataList,columnNames) {
            this.$(".js-data-list-cot").html(this.tableTpl.body({
                dataList:dataList,
                columnLen:columnNames.length
            }));
        },
        renderSearchParams:function (params) {
            var paramsCot = this.$(".js-search-params").empty(),searchTpl = this.tableTpl.search;
            _.each(params,function (optionList,key) {
                paramsCot.append(searchTpl({
                    label:headerMap[key],
                    name:key,
                    optionList:optionList
                }));
            })
        },
        activeMenu:function () {
            this.$("#main-menu-cot li.active").removeClass("active");
            this.$("#main-menu-cot li." + this.currentListType).addClass("active");
        }
    });
    return {
        init:function (args) {

            return new App(args);
        }
    }
});