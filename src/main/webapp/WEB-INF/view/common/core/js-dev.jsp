<!--<%@ page contentType="text/html;charset=UTF-8" language="java" %>-->
<!--[if lte IE 9]>
<%--<!-- build:js scripts/polyfill.js path=${root}-->--%>
<script src='${root}/scripts/polyfill/html5shiv.js'></script>
<script src='${root}/scripts/polyfill/json3.min.js'></script>
<script src='${root}/scripts/polyfill/es5-shim.min.js'></script>
<script src='${root}/scripts/polyfill/es5-sham.min.js'></script>
<script src='${root}/scripts/polyfill/rem.min.js'></script>
<script src='${root}/scripts/polyfill/calc.min.js'></script>
<script src='${root}/scripts/polyfill/respond.src.js'></script>
<script src='${root}/scripts/polyfill/placeholder-polyfill.js'></script>
<%--<!-- endbuild -->--%>
<![endif]-->
<!--[if lte IE 8]>
<script>
document.querySelector(".layout").style.height = (Math.max(document.body.clientHeight,document.documentElement.clientHeight) - 5 * 16) + "px";
</script>
<![endif]-->
<!-- build:js lib/libs.js path=${root}-->
<script src='${res}/lib/jquery/dist/jquery.min.js'></script>
<script src='${res}/lib/bootstrap-3.3.0/dist/js/bootstrap.min.js'></script>
<script src='${res}/lib/echarts/echarts.min.js'></script>
<script src='${res}/lib/moment/moment.min.js'></script>
<script src='${res}/lib/moment/moment-with-locales.min.js'></script>
<script src='${res}/lib/bootstrap-3.3.0/dist/js/bootstrap-datetimepicker.min.js'></script>
<script src='${res}/lib/underscore/underscore-min.js'></script>
<script src='${res}/lib/backbone/backbone-min.js'></script>
<script src='${res}/lib/layoutmanager/backbone.layoutmanager.js'></script>
<script src='${res}/lib/jquery-validation/dist/jquery.validate.js'></script>
<script src='${res}/lib/jquery-form/jquery.form.js'></script>
<script src='${res}/lib/spin/spin.min.js'></script>
<script src='${res}/lib/seajs/dist/sea.js'></script>
<!-- endbuild -->

<script type="text/javascript">
    (function (global) {
        _.templateSettings = {
            evaluate: /<@([\s\S]+?)@>/g,
            interpolate: /<@=([\s\S]+?)@>/g,
            escape: /<@-([\s\S]+?)@>/g
        };
        // Set configuration
        var version = "0.1.6";
        seajs.config({
            alias: {
                "ui": "common/ui",
                "util": "common/util",
                "appView": "common/appView",
                "main": "main",

                "echarts": "${res}/lib/echarts/echarts-all"
            },
            dir:'${root}/scripts',
            base: '${root}/scripts',
            map: [['.js', '.js?v=' + version]]
            <%--map: [
                function(url){
                    var
                            basePath = location.protocol + "//" + location.host + "${root}/scripts/",
                            shortPath =  url.replace(basePath,"");
                    if(~"${root}".indexOf("dist") && /^modules/.test(shortPath) && !seajs.cache[url]){
                        return versionObj[shortPath] ? basePath + versionObj[shortPath]: url.replace(/\/dist\//,"/src/")
                    }else{
                        return url;
                    }
                }
            ]--%>
        });

        //Top namespace is rs
        var rs = global.rs = {
            staticRoot: '${root}/',
            apiRoot: '${ctx}/',
            name: "main",
            version: version,
            role:"${sessionScope.customerInfo.role}",
            event: _.extend({}, Backbone.Events),
            receiveMessage:function(event){
                var eventData = event.data.split("?"),
                    param = eventData[1] && rs.util.parseUrlParam(eventData[1]);
                rs.event.trigger(eventData[0],param);
            },
            JST: {},
            chartCommonOptions:{
                title:{
                    textStyle:{
                        fontFamily:"微软雅黑"
                    }
                },
                legend:{
                    data:[],
                    textStyle:{
                        fontFamily:"微软雅黑"
                    }
                },
                noDataLoadingOption:{
                    textStyle:{
                        fontFamily:"微软雅黑",
                        fontSize:18
                    }
                }
            },
            data: {
                app: {},
                page: {}
            }
        };
        /**
         * loadModule
         * @param modules
         */
        rs.loadModule = function (modules) {
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
        };

        Backbone.emulateHTTP = true;
//        Backbone.emulateJSON = true;
        rs.ajax = Backbone.ajax = function () {
            var
                    args = [].slice.call(arguments, 0),
                    options = args[0],
                    success = options.success,
                    error = options.error,
                    isSelfErrorTip = options.isSelfErrorTip;
            options.cache = false;
            options.success = function (resp, textStatus, xhr) {
                if (typeof resp == "string") {
                    try {
                        resp = JSON.parse(resp);
                    } catch (e) {
                    }
                }
                if (resp.status !== undefined && resp.status * 1 !== 0) {
                    if (isSelfErrorTip) {
                        error && error.call(options.context, xhr, resp.msg || resp.message || textStatus,resp);
                    } else {
                        seajs.use("util", function (util) {
                            util.alert("<span>失败提示</span>", resp.msg || resp.message, function () {
                                error && error.call(options.context, xhr, resp.msg || resp.message || textStatus,resp);
                            }, 3);
                        });
                    }
                    return xhr;
                }
                success && success.apply(options.context, arguments);
            };
            options.error = function (xhr, textStatus, errorThrown) {
                var
                        status = xhr.status,
                        resp = xhr.responseText,
                        respObj = {};
                if('abort' == textStatus) return
                if(status == 0 && textStatus == 'error') {textStatus = " 系统错误"}
                if (isSelfErrorTip && error && !~_.indexOf([401, 403], status)) {
                    error.call(options.context, xhr, textStatus, errorThrown);
                } else {
                    seajs.use("util", function (util) {
                        //<%--401:身份过期 403:无权限--%>
                        if (!!~_.indexOf([401, 403], status)) {
                            try {
                                respObj = JSON.parse(resp);
                            } catch (e) {
                            }
                        }
                        util.alert("<span>失败提示</span>", respObj.msg || respObj.message || (status + ":" + textStatus), function () {
                            status == 401 && location.replace("${ctx}/");//身份过期
                            status == 403 && history.back();//无权限
                        }, 3);
                    });
                }
            };
            return $.ajax.apply($, arguments);
        };
        //<%-- 请求html片段  --%>
        rs.ajaxFragment = function () {
            var
                args = [].slice.call(arguments, 0),
                options = args[0],
                data = options.data || (options.data = {});
            data.fragment = true;
            return rs.ajax.apply(rs, arguments);
        };

        $.extend($.validator.messages, {
            required: "不能为空",
            remote: "请修正此字段",
            email: "请输入有效的电子邮件地址",
            url: "请输入有效的网址",
            date: "请输入有效的日期",
            dateISO: "请输入有效的日期 (YYYY-MM-DD)",
            number: "请输入有效的数字",
            digits: "只能输入数字",
            creditcard: "请输入有效的信用卡号码",
            equalTo: "你的输入不相同",
            extension: "请输入有效的后缀",
            maxlength: $.validator.format("最多可以输入 {0} 个字符"),
            minlength: $.validator.format("最少要输入 {0} 个字符"),
            rangelength: $.validator.format("请输入长度在 {0} 到 {1} 之间的字符串"),
            range: $.validator.format("请输入范围在 {0} 到 {1} 之间的数值"),
            max: $.validator.format("请输入不大于 {0} 的数值"),
            min: $.validator.format("请输入不小于 {0} 的数值")

        });
        //<%--验证方法配置--%>
        $.validator.addMethod("required", function (value, element, param) {
            // Check if dependency is met
            if (!this.depend(param, element)) {
                return "dependency-mismatch";
            }
            if (element.nodeName.toLowerCase() === "select") {
                // Could be an array for select-multiple or a string, both are fine this way
                var val = $(element).val();
                return val && val.length > 0;
            }
            if (this.checkable(element)) {
                return this.getLength(value, element) > 0;
            }
            return $.trim(value).length > 0;
        }, "不能为空 ");
        $.validator.addMethod("url", function (value, element) {
            return this.optional(element) || /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test(value);
        }, "请输入有效的网址");
        $.validator.addMethod("mobile", function (value, element) {
            var tel = /^1\d{10}$/;
            return this.optional(element) || (tel.test($.trim(value)));
        }, "请输入有效的手机号码");
        $.validator.addMethod("bankCard", function(value, element) {
            var reg = /^\d{16,19}$/;
            return this.optional(element) || (reg.test($.trim(value)));
        }, "请输入有效的银行卡号");
        $.validator.addMethod("pwd", function (value, element) {
            var matchFn = function (v) {
                if (/^[\S]{8,20}$/.test(v)) {
                    return _.map([/\d/, /[a-z]/i, /[!@#\$%\^&\*]/], function (reg) {
                                return reg.test(v)
                            }).reduce(function (a, b) {
                                return (a | 0) + (b | 0)
                            }) > 1
                } else {
                    return false
                }
            };
            return this.optional(element) || matchFn(value);
        }, "请输入8~20位的字母、数字、特殊字符中的至少2种组合");
        $.validator.addMethod("idCard", function (value, element) {
            var idCardReg = /\d{6}(13|14|15|16|17|18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)/i;
            return this.optional(element) || (validateIdCard($.trim(value)));
        }, "请输入有效身份证号");
        $.validator.addMethod("ipv4More", function (value, element) {
            var reg = /^(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)$/i;
            return this.optional(element) || _.all(value.split(/\r\n|\n|\s+/), function (v) {
                        return reg.test(v)
                    });
        }, "请输入有效的IP地址.");
        $.validator.addMethod("idNo", function (value, element) {
            return this.optional(element) || /^[a-z0-9\-]+$/i.test(value);
        }, "请输入正确的证件号");
        $.validator.addMethod("alphanumeric", function (value, element) {
            return this.optional(element) || /^[a-z0-9]+$/i.test(value);
        }, "只能输入字母和数字");
        $.validator.addMethod("userName", function (value, element) {
            return this.optional(element) || /^\w+$/i.test(value);
        }, "只能输入字母和数字和下划线");
        $.validator.addMethod("decimal", function (value, element) {
            return this.optional(element) || /^\d+(?:\.\d{1,2})?$/i.test(value);
        }, "请输入有效的数值 , 最多保留两位小数");

        $.validator.addMethod("memberNo", function (value, element) {
            return this.optional(element) || $(element).attr("memberNo");
        }, "请选择有效机构");
        $.validator.addMethod("phoneChina", function (value, element) {
            return this.optional(element) || /^\d{3,4}(?:-)?\d{5,11}(?:-)?\d{3,5}$/i.test($.trim(value));
        }, "请输入有效的电话号码");
        $.validator.addMethod("chineseName", function (value, element) {
            /*return this.optional(element) || /^(?:(?:[\u4e00-\u9fa5])|(?:[a-zA-Z])){2,6}$/.test(value);*/
            return this.optional(element) || /[a-zA-Z]{1,30}|[\u4e00-\u9fa5]{1,30}$/.test(value);
        }, "请输入有效的姓名");
        function validateIdCard(idCard){
            //<%--15位和18位身份证号码的正则表达式--%>
            var regIdCard=/^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;
            var isPass = true;
            //<%--//如果通过该验证，说明身份证格式正确，但准确性还需计算--%>
            if(regIdCard.test(idCard)){
                if(idCard.length==18){
                    var idCardWi=new Array( 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 );
                    var idCardY=new Array( 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 );
                    var idCardWiSum=0;
                    for(var i=0;i<17;i++){
                        idCardWiSum+=idCard.substring(i,i+1)*idCardWi[i];
                    }

                    var idCardMod=idCardWiSum%11;
                    var idCardLast=idCard.substring(17);
                    if(idCardMod==2){
                        if(idCardLast=="X"||idCardLast=="x"){
                            isPass = true;
                        }else{
                            isPass = false;
                        }
                    }else{
                        //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                        if(idCardLast!=idCardY[idCardMod]){
                            isPass = false;
                        }
                    }
                }
            }else{
                isPass = false;
            }
            return isPass;
        }
        //<%-- 和弹出框的通信 --%>
        this.addEventListener && this.addEventListener("message", rs.receiveMessage, false);


    })(this);
</script>
<!-- build:js scripts/common.js path=${root}-->
<script src='${root}/scripts/common/ui.js'></script>
<script src='${root}/scripts/common/util.js'></script>
<script src='${root}/scripts/common/appView.js'></script>
<script src='${root}/scripts/main.js'></script>
<!-- endbuild -->
