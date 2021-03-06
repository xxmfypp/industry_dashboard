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
                //url:rs.apiRoot + "resource/src/mockData/charts.json"
            });
        },
        //模拟数据
        initChart:function () {
            this.chartUser();
            this.chartGoodsCategory();
            this.chartPriceDivide();
            this.chartCollectTop5();
            this.chartOrderPriceTrend();
            this.chartOrderAreaNum();
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
                    left: '30%',
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