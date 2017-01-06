/**
 * Created by liwir_000 on 11/20/2016.
 */
;define(function (require) {
    var
        View = Backbone.View,
        util = require("util"),
        role = rs.role;

    require("common/china");
    /**
     * App
     */
    var App = View.extend({
        initialize:function () {
            //this.initChart();
            this.getData().then(function (resp) {
                //success
                this.data = resp.data;
                if(_.keys(this.data).length){
                    this.initChart();
                }else{
                    util.alert("失败提示","还<span class='text-danger'>没有上传</span>数据文件",function () {
                        role != "VIEW" &&(location.hash = "upload");
                    },5);
                }

            }.bind(this),function (xhr,status) {
                //error
                util.alert("失败提示",status,null,5);
            }.bind(this)).always(function () {

            }.bind(this))
        },
        getData:function () {
            return $.ajax({
                isSelfErrorTip:true,
                type:"get",
                dataType:"json",
                url:rs.apiRoot + "query"
                // url:rs.apiRoot + "resource/src/mockData/charts-2.json"
            });
        },
        //模拟数据
        initChart:function () {
            this.chartMaterielAmountByType();
            this.chartTotalPriceByStore();
            this.chartGoodsStockOutPriceTrend();
            this.chartAllotMaterialPriceByType();
            this.chartShipStockOut();
            this.chartStockInTotalPriceTrend();
            //this.chartUser();
            //this.chartGoodsCategory();
            //this.chartPriceDivide();
            //this.chartCollectTop5();
            //this.chartOrderPriceTrend();
            //this.chartOrderAreaNum();
        },
        //图标1.物料类型和数量 - 柱状图
        chartMaterielAmountByType:function () {
            var materielAmountByType = this.data.materielAmountByType;
            this.chartMaterielAmountByTypeEl = echarts.init(this.$(".chart-item:eq(0)")[0]);
            this.chartMaterielAmountByTypeEl.setOption({
                title: {
                    text: '物料类型和数量'
                },
                color: ['#5ab1ef','#ffb980','#b6a2de'],
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        data : materielAmountByType.xAxisData
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'出生日期',
                        type:'bar',
                        areaStyle: {normal: {}},
                        data:materielAmountByType.seriesData
                    }
                ]
            });

        },
        //图表2 各门店入库总金额 - 饼状图
        chartTotalPriceByStore:function () {
            var totalPriceByStore = this.data.totalPriceByStore,seriesData = totalPriceByStore.seriesData;
            this.chartTotalPriceByStoreEl = echarts.init(this.$(".chart-item:eq(1)")[0]);
            var newSeriesData = _.map(seriesData,function (obj) {
                return {
                    name:_.keys(obj)[0],
                    value:_.values(obj)[0]
                }
            })
            this.chartTotalPriceByStoreEl.setOption({
                title: {
                    text: '各门店入库总金额'
                },
                legend:{
                    data:_.keys(seriesData)
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                series: [
                    {
                        name:'入库总金额',
                        type:'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            normal: {
                                show: true,
                                position: ''
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: true
                            }
                        },
                        data:newSeriesData/*.seriesData*/
                    }
                ]
            });
        },
        //图表3 半成品出库金额趋势图 - 折线图
        chartGoodsStockOutPriceTrend:function () {
            var goodsStockOutPriceTrend = this.data.goodsStockOutPriceTrend;
            this.chartGoodsStockOutPriceTrendEl = echarts.init(this.$(".chart-item:eq(2)")[0]);
            this.chartGoodsStockOutPriceTrendEl.setOption({
                title: {
                    text: '半成品出库金额趋势图'
                },
                tooltip : {
                    trigger: 'axis'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : false,
                        data :goodsStockOutPriceTrend.xAxisData
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'价格',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:goodsStockOutPriceTrend.seriesData
                    }
                ]
            });
        },
        //图标4 调拨信息 物料类型和总价 按照物料类型，汇总总价 趋势图 - 柱状图
        chartAllotMaterialPriceByType:function () {
            var allotMaterialPriceByType = this.data.allotMaterialPriceByType,typeName = allotMaterialPriceByType.typeName,seriesData = allotMaterialPriceByType.seriesData;
            this.chartAllotMaterialPriceByTypeEl = echarts.init(this.$(".chart-item:eq(3)")[0]);
            var options = {
                title: {
                    text: '物料类型和总价'
                },
                legend:{
                    data:typeName
                },
                color: ['#5ab1ef', '#ffb980', '#b6a2de'],
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        data: allotMaterialPriceByType.xAxisData
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: []
            },
            series = options.series;
            _.each(typeName,function (name,index) {
                series.push({
                    name: name,
                    type: 'line',
                    areaStyle: {normal: {}},
                    data: seriesData[index]
                });
            });
            this.chartAllotMaterialPriceByTypeEl.setOption(options);
        },
        //图标5 配送出库表 时间 门店 总金额 - 堆叠柱状图
        chartShipStockOut:function () {
            var shipStockOut = this.data.shipStockOut,storeName = shipStockOut.storeName,seriesData = shipStockOut.seriesData;
            var options = {
                title: {
                    text: '配送出库表'
                },
                color: ['#5ab1ef','#ffb980','#b6a2de'],
                tooltip : {
                    trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        data : shipStockOut.xAxisData
                    }
                ],
                    yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : []
            },
            series = options.series;
            _.each(storeName,function (name,index) {
                series.push({
                    name:name,
                    type:'bar',
                    stack: '总价',
                    barMaxWidth:"40px",
                    data:seriesData[index] || [],
                    itemStyle:{
                        normal:{
                            shadowColor:"rgba(0,0,0,.2)",
                            shadowBlur:2
                        }
                    }
                });
            });
            this.chartShipStockOutEl = echarts.init(this.$(".chart-item:eq(4)")[0]);
            this.chartShipStockOutEl.setOption(options);
        },
        //图标6 入库单 入库单总价分部表 - 热力图
        chartStockInTotalPriceTrend:function () {
            var stockInTotalPriceTrend = this.data.stockInTotalPriceTrend,seriesData = stockInTotalPriceTrend.seriesData;
            this.chartStockInTotalPriceTrendEl = echarts.init(this.$(".chart-item:eq(5)")[0]);
            var data = [];
            _.each(seriesData,function (subArr,perId) {
                _.each(subArr,function (v,id) {
                    data.push([id,perId,v]);
                });
            })
            var option = {
                tooltip: {
                    position: 'top'
                },
                animation: false,
                grid: {
                    height: '50%',
                    y: '10%'
                },
                xAxis: {
                    type: 'category',
                    data: stockInTotalPriceTrend.xAxisData,
                    splitArea: {
                        show: true
                    }
                },
                yAxis: {
                    type: 'category',
                    data: stockInTotalPriceTrend.yAxisData,
                    splitArea: {
                        show: true
                    }
                },
                visualMap: {
                    min: 0,
                    max: 100,
                    calculable: true,
                    orient: 'horizontal',
                    left: 'center',
                    bottom: '15%'
                },
                series: [{
                    name: 'Punch Card',
                    type: 'heatmap',
                    data: data,
                    label: {
                        normal: {
                            show: true
                        }
                    },
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }]
            };
            this.chartStockInTotalPriceTrendEl.setOption(option);
        },




        chartUser:function () {
            var userBirth = this.data.birthdayData,
            userGender = this.data.genderData;
            this.chartUser = echarts.init(this.$(".js-user")[0]);
            this.chartUser.setOption({
                title: {
                    text: '用户数据'
                },
                legend:{
                    data:["性别","出生日期"]
                },
                color: ['#5ab1ef','#ffb980','#b6a2de'],
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        data : userBirth.xAxisData
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'性别',
                        type:'pie',
                        tooltip : {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c} ({d}%)'
                        },
                        center: ["15%","50%"],
                        radius : [0, "40%"],
                        label: {
                            normal: {
                                position: 'inner'
                            }
                        },
                        itemStyle :　{
                            normal : {
                                labelLine : {
                                    length : 20
                                }
                            }
                        },
                        data:userGender/*.seriesData*/
                    },
                    {
                        name:'出生日期',
                        type:'bar',
                        areaStyle: {normal: {}},
                        data:userBirth.seriesData
                    }
                ]
            })
        },
        chartGoodsCategory:function () {
            var goodsCategory = this.data.categoryData;
            this.chartGoodsCategory = echarts.init(this.$(".js-goodsCategory")[0]);
            this.chartGoodsCategory.setOption({
                title: {
                    text: '商品类别'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                series: [
                    {
                        name:'商品类别',
                        type:'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            normal: {
                                show: true,
                                position: ''
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: true
                            }
                        },
                        data:goodsCategory/*.seriesData*/
                    }
                ]
            })
        },
        chartPriceDivide:function () {
            var priceDivide = this.data.priceData;
            this.chartPriceDivide = echarts.init(this.$(".js-priceDivide")[0]);
            this.chartPriceDivide.setOption({
                title : {
                    text: '价格分布'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                series : [
                    {
                        name: '价格分布',
                        type: 'pie',
                        radius : '55%',
                        center: ['50%', '60%'],
                        data:priceDivide/*.seriesData*/,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            })
        },
        chartCollectTop5:function () {
            var collectTop5 = this.data.collectionData;
            this.chartCollectTop5 = echarts.init(this.$(".js-collectTop5")[0]);
            this.chartCollectTop5.setOption({
                title : {
                    text: '收藏记录Top5'
                },
                color: ['#3398DB'],
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                yAxis : [
                    {
                        type : 'category',
                        data : _.pluck(collectTop5,"name"),
                        axisTick: {
                            alignWithLabel: true
                        }
                    }
                ],
                xAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'数量',
                        type:'bar',
                        barWidth: '60%',
                        data:_.pluck(collectTop5,"value")
                    }
                ]
            })
        },
        chartOrderPriceTrend:function () {
            var orderPriceTrend = this.data.orderAmountData;
            this.chartOrderPriceTrend = echarts.init(this.$(".js-orderPriceTrend")[0]);
            this.chartOrderPriceTrend.setOption({
                title: {
                    text: '订单价格走势'
                },
                tooltip : {
                    trigger: 'axis'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : false,
                        data :orderPriceTrend.xAxisData
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'价格',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:orderPriceTrend.seriesData
                    }
                ]
            })
        },
        chartOrderAreaNum:function () {
            var orderAreaNum = this.data.orderAreaData;
            this.orderAreaNumChart = echarts.init(this.$(".js-orderAreaNum")[0]);
            this.orderAreaNumChart.setOption({
                title: {
                    text: '订单区域分布'
                },
                tooltip: {
                    trigger: 'item'
                },
                visualMap: {
                    min: 0,
                    max: 1000,
                    left: 'left',
                    top: 'bottom',
                    text: ['高','低'],           // 文本，默认为数值文本
                    calculable: true
                },
                toolbox: {
                    show: true,
                    orient: 'vertical',
                    left: 'right',
                    top: 'center',
                    feature: {
                        dataView: {readOnly: false},
                        restore: {},
                        saveAsImage: {}
                    }
                },
                series: [
                    {
                        name: '订单区域分布',
                        type: 'map',
                        mapType: 'china',
                        roam: false,
                        label: {
                            normal: {
                                show: true
                            },
                            emphasis: {
                                show: true
                            }
                        },
                        data:(function (dataList) {
                            _.each(dataList,function (data) {
                                data.name = data.name.replace(/省|市/g,"")
                            })
                            return dataList
                        })(orderAreaNum)
                    }
                ]
            })
        }
    });

    return {
        init:function (params) {
            return new App(params);
        }
    }
});