/**
 * Created by liwir_000 on 1/8/2017.
 */
;define(function (require) {
    var
        util = require("util"),
        AppView = require("appView").AppView;
    rs.data.page.totalRow = 200;
    /**
     * App
     */
    var App = AppView.extend({
        initialize:function (args) {
            AppView.prototype.initialize.apply(this,arguments)
        }
    });
    return {
        init:function (args) {

            return new App(args);
        }
    }
});