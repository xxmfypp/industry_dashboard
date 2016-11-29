/**
 * Created by liwir_000 on 11/21/2016.
 */
;define(function (require) {
    var
        View = Backbone.View,
        util = require("util");;

    /**
     * App
     */
    var App = View.extend({
        events:{
          "change .js-upload-input":"upload"
        },
        initialize:function () {

        },
        upload:function (e) {
            var ctx = this;
            if($(e.currentTarget).val()){
                this.$('.js-upload-file-form').ajaxSubmit({
                    isSelfErrorTip:true,
                    beforeSend: function(xhr) {
                        ctx.uploadXhr = xhr;
                        var percentVal = '0%';
                        ctx.setProgress(percentVal);
                    },
                    uploadProgress: function(event, position, total, percentComplete) {
                        percentComplete > 100 && (percentComplete = 100);
                        var percentVal = percentComplete + '%';
                        ctx.setProgress(percentVal);
                    },
                    success: function(resp) {
                        ctx.currentFileName = resp.fileName;
                        ctx.setProgress('100%');
                        ctx.$(".js-progress-tip-txt").removeClass("text-danger").addClass("text-success").text("文件上传成功!");
                        _.delay(function () {
                            util.successTip("成功提示","文件长传成功，稍后将自动进入图表分析",function () {
                                location.hash = "";
                            },3);
                        },1000)

                    },
                    error:function(){
                        ctx.$(".js-progress-tip-txt").removeClass("text-success").addClass("text-danger").text("文件上传失败!");
                        util.alert("失败提示","上传文件失败请稍后再试",null,5);
                        //ctx.$(".js-upload-tip").html("<span class='text-danger'>上传失败</span>");
                    },
                    complete:function () {
                        _.delay(function () {
                            ctx.resetProgress();
                            ctx.$('.js-upload-file-form').resetForm();
                        },1000);

                    }
                });
            }
        },
        resetProgress:function(){
            this.$(".js-select-tip").show();
            this.$(".js-progress-tip-txt").removeClass("text-success text-danger")/*.removeClass("text-danger")*/.text("文件上传中...");
            this.$(".js-progress-tip").hide().find(".progress-bar").width(0).text("0%");
        },
        setProgress:function(val){
            this.$(".js-select-tip").hide();
            this.$(".js-progress-tip").show().find(".progress-bar").width(val).text(val);
        },
    });
    return{
        init:function (params) {
            return new App(params);
        }
    }
});