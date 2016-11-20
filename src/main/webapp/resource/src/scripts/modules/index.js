/**
 * Created by liwir_000 on 11/20/2016.
 */
;define(function (require) {
    var View = Backbone.View;

    /**
     * App
     */
    var App = View.extend({
        initialize:function () {
            this.initChart()
        },
        //模拟数据
        initChart:function () {
            this.chart1();
            this.chart2();
            this.chart3();
            this.chart4();
        },
        chart1:function () {
            this.chart1 = echarts.init(this.$(".chart-item:eq(0)")[0]);
            this.chart1.setOption({
                title: {
                    text: '堆叠区域图'
                },
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎']
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
                        data : ['周一','周二','周三','周四','周五','周六','周日']
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'邮件营销',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:[120, 132, 101, 134, 90, 230, 210]
                    },
                    {
                        name:'联盟广告',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:[220, 182, 191, 234, 290, 330, 310]
                    },
                    {
                        name:'视频广告',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:[150, 232, 201, 154, 190, 330, 410]
                    },
                    {
                        name:'直接访问',
                        type:'line',
                        stack: '总量',
                        areaStyle: {normal: {}},
                        data:[320, 332, 301, 334, 390, 330, 320]
                    },
                    {
                        name:'搜索引擎',
                        type:'line',
                        stack: '总量',
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        },
                        areaStyle: {normal: {}},
                        data:[820, 932, 901, 934, 1290, 1330, 1320]
                    }
                ]
            })
        },
        chart2:function () {
            function randomData() {
                return Math.round(Math.random()*1000);
            }

            this.chart2 = echarts.init(this.$(".chart-item:eq(1)")[0]);
            this.chart2.setOption({
                title: {
                    text: 'iphone销量',
                    subtext: '纯属虚构',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data:['iphone3','iphone4']
                },
                visualMap: {
                    min: 0,
                    max: 2500,
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
                        name: 'iphone3',
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
                        data:[
                            {name: '北京',value: randomData() },
                            {name: '天津',value: randomData() },
                            {name: '上海',value: randomData() },
                            {name: '重庆',value: randomData() },
                            {name: '河北',value: randomData() },
                            {name: '河南',value: randomData() },
                            {name: '云南',value: randomData() },
                            {name: '辽宁',value: randomData() },
                            {name: '黑龙江',value: randomData() },
                            {name: '湖南',value: randomData() },
                            {name: '安徽',value: randomData() },
                            {name: '山东',value: randomData() },
                            {name: '新疆',value: randomData() },
                            {name: '江苏',value: randomData() },
                            {name: '浙江',value: randomData() },
                            {name: '江西',value: randomData() },
                            {name: '湖北',value: randomData() },
                            {name: '广西',value: randomData() },
                            {name: '甘肃',value: randomData() },
                            {name: '山西',value: randomData() },
                            {name: '内蒙古',value: randomData() },
                            {name: '陕西',value: randomData() },
                            {name: '吉林',value: randomData() },
                            {name: '福建',value: randomData() },
                            {name: '贵州',value: randomData() },
                            {name: '广东',value: randomData() },
                            {name: '青海',value: randomData() },
                            {name: '西藏',value: randomData() },
                            {name: '四川',value: randomData() },
                            {name: '宁夏',value: randomData() },
                            {name: '海南',value: randomData() },
                            {name: '台湾',value: randomData() },
                            {name: '香港',value: randomData() },
                            {name: '澳门',value: randomData() }
                        ]
                    },
                    {
                        name: 'iphone4',
                        type: 'map',
                        mapType: 'china',
                        label: {
                            normal: {
                                show: true
                            },
                            emphasis: {
                                show: true
                            }
                        },
                        data:[
                            {name: '北京',value: randomData() },
                            {name: '天津',value: randomData() },
                            {name: '上海',value: randomData() },
                            {name: '重庆',value: randomData() },
                            {name: '河北',value: randomData() },
                            {name: '安徽',value: randomData() },
                            {name: '新疆',value: randomData() },
                            {name: '浙江',value: randomData() },
                            {name: '江西',value: randomData() },
                            {name: '山西',value: randomData() },
                            {name: '内蒙古',value: randomData() },
                            {name: '吉林',value: randomData() },
                            {name: '福建',value: randomData() },
                            {name: '广东',value: randomData() },
                            {name: '西藏',value: randomData() },
                            {name: '四川',value: randomData() },
                            {name: '宁夏',value: randomData() },
                            {name: '香港',value: randomData() },
                            {name: '澳门',value: randomData() }
                        ]
                    }
                ]
            })
        },
        chart3:function () {
            this.chart3 = echarts.init(this.$(".chart-item:eq(2)")[0]);
            this.chart3.setOption({
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data:['直达','营销广告','搜索引擎','邮件营销','联盟广告','视频广告','百度','谷歌','必应','其他']
                },
                series: [
                    {
                        name:'访问来源',
                        type:'pie',
                        selectedMode: 'single',
                        radius: [0, '30%'],

                        label: {
                            normal: {
                                position: 'inner'
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        data:[
                            {value:335, name:'直达', selected:true},
                            {value:679, name:'营销广告'},
                            {value:1548, name:'搜索引擎'}
                        ]
                    },
                    {
                        name:'访问来源',
                        type:'pie',
                        radius: ['40%', '55%'],

                        data:[
                            {value:335, name:'直达'},
                            {value:310, name:'邮件营销'},
                            {value:234, name:'联盟广告'},
                            {value:135, name:'视频广告'},
                            {value:1048, name:'百度'},
                            {value:251, name:'谷歌'},
                            {value:147, name:'必应'},
                            {value:102, name:'其他'}
                        ]
                    }
                ]
            })
        },
        chart4:function () {
            this.chart4 = echarts.init(this.$(".chart-item:eq(3)")[0]);
            this.chart4.setOption({
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
                xAxis : [
                    {
                        type : 'category',
                        data : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                        axisTick: {
                            alignWithLabel: true
                        }
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'直接访问',
                        type:'bar',
                        barWidth: '60%',
                        data:[10, 52, 200, 334, 390, 330, 220]
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