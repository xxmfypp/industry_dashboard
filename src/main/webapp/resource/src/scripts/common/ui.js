/**
 * Created by Gavin Li on 2015/11/4.
 */
/**
 * User: gavin
 * Date: 3/27/15
 */
;define("common/ui",function(require){
    var
        Model = Backbone.Model,
        View = Backbone.View,
        Collection = Backbone.Collection,
        emptyFn = new Function();

    var sum = function(){
        var arr = _.flatten(Array.prototype.slice.apply(arguments));
        return _.reduce(arr,function(a,b){return a+b})
    };
    /**
     * DropdownMenu
     * new DropdownMenu({
     *      el:"",
     *      activeIndex:Number(),
     *      menuEl:$("#id")||"<ul>",
     *      subItemTpl:"<li>",
     *      subItems:["item text","item text"]||[$("<a href='http://www.noosh.com'></a>")],
     *      subEvents:[Function,Function]
     * });
     */
    var DropdownMenu = View.extend({
        events:{
            "click":"toggleShow",
            "click .menu-item":"activeItem"
        },
        initialize:function(){
            var options = this.options;
            this.activeIndex = options.activeIndex;
            this.subEvents = options.subEvents || [];
            this.renderMenuContainer();
            this.renderMenuItems();
            this.bindEvents();
        },
        renderMenuContainer: function(){
            var
                options = this.options,
                menuEl = options.menuEl || $('<ul class="grid-menu-list"></ul>');
            _.isElement(menuEl) && this.$el.append(menuEl);
            //Consider a string as class name
            _.isString(menuEl) && (menuEl = this.$(menuEl));
            this.menuEl = menuEl;
        },
        renderMenuItems: function(){
            var
                options = this.options,
            //list item template
                subItemTpl = options.subItemTpl || "<li class='menu-item'></li>",
            //subItems is an array like ["text","text"]
                subItems = options.subItems;
            if(subItems){
                _.each(
                    subItems,
                    $.proxy(
                        function(item,index){
                            this.menuEl.append($(subItemTpl).append(item).attr("index",index));
                        },
                        this
                    )
                );
            }else{
                //Add default class name
                this.menuEl.children().each(function(index){
                    $(this).addClass("menu-item").attr("index",index);
                });
            }
            //Initialize active item
            this.activeIndex !== undefined && $(this.menuEl.children().get(this.activeIndex)).addClass("active");
        },
        toggleShow: function(e){
            this.menuEl.fadeToggle();
            e.stopPropagation();
        },
        bindEvents: function(){
            $(document).click($.proxy(this.hide,this));
        },
        hide: function(){
            this.menuEl.fadeOut();
        },
        activeItem: function(e){
            e.stopPropagation();
            var
                index = $(e.currentTarget).attr("index")| 0,
                fn;
            if(index === this.activeIndex) return
            fn = this.subEvents[index];
            _.isFunction(fn) && fn();
            this.hide();
            if(this.activeIndex !== undefined){
                $(e.currentTarget).addClass("active");
                $(this.menuEl.children().get(this.activeIndex)).removeClass("active");
                this.activeIndex = index;
            }
        }
    });

    /**
     * Dialog
     */
    var Dialog = View.extend({
        template: _.template($("#common-dialog-tpl").html()),
        events:{
            "click":function(e){
                e.stopPropagation();
            },
            "click .close":"close",
            "click button[key]":"dispatchEvent"
        },
        defaultOptions:{
            title:"title",
            content:"",
            create:emptyFn,
            close:emptyFn,
            hasMark:false,
            draggable:true,
            position:"top",// top center left right
            buttons:null
        },
        defaultButtons:{
            "Close":{
                className:"btn-default",
                callback:emptyFn
            },
            "Ok":{
                className:"btn-primary",
                callback:emptyFn
            }
        },
        initialize: function(options){
            this.options = $.extend({},this.defaultOptions,options);
            this.render();
        },
        render: function(){
            var
                options = this.options,
                $box = $(this.template(this.options)),
                _this = this;
            this.setElement($box);
            this.mark = $("<div class='modal fade'>");
            this.markBackDrop = $("<div class='modal-backdrop fade'>");

            if(options.hasMark){
                this.$el.appendTo(this.mark);
                this.mark.appendTo(document.body).show();
                this.markBackDrop.appendTo(document.body).show();
                _.delay(function(){
                    _this.mark.addClass("in");
                    _this.markBackDrop.addClass("in");
                },100);

            }else{
                this.$el.appendTo(document.body);
            }


            options.create && options.create.call(this);
        },
        close: function(fn){
            var _this = this;
            this.mark.removeClass("in");
            this.markBackDrop.hide().removeClass("in")
            this.trigger("close");
            _.delay(function(){
                _this.$el.remove();
                _this.mark.remove();
                _this.markBackDrop.remove();
                _.isFunction(fn) && fn();
            },500);

            this.options.close && this.options.close.call(this);
        },
        setPosition: function(x,y){
            this.$el.css({
                left:x + "px",
                top:y + "px"
            });
        },
        dispatchEvent: function(e){
            var
                btn = $(e.currentTarget),
                key = btn.attr("key"),
                options = this.options,
                buttons = options.buttons,
                btnObj,
                callFn;
            if(!buttons){return}

            btnObj = buttons[key];
            callFn = btnObj.callback;
            if(_.isFunction(callFn) && callFn.call(this,btn) !== false){
                this.close();
            }
        }
    });


    /**
     *  PageListView
     * @type {*}
     */
    var PageListView = View.extend({
        className:"pagination",
        events:{
            "click .first":"first",
            "click .previous":"previous",
            "click .next":"next",
            "click .last":"last",
            "click .center .p-item":"goToPage",
            "keypress .p-ipt":function(e){
                var
                  key = e.charCode,
                  val = $.trim(e.currentTarget.value);
                if (key && !/\d/.test(String.fromCharCode(key))) {
                    e.preventDefault();
                }
                if(e.keyCode == 13){
                    this.setCurrentPage(this.$(".p-ipt").val() * 1);
                }
            },
            "click  .p-btn":function(){
                this.setCurrentPage(this.$(".p-ipt").val() * 1);
            }
        },
        render:function(){
            var text =  this.options.buttonText;
            this.options.hasTotalTag && this.$el.append('<span class="p-item total">共'+ this.page.get("totalCount") +'条</span>');
            text.first && this.$el.append('<a class="p-item first" href="javascript:;">'+ text.first +'</a>');
            text.previous && this.$el.append('<a class="p-item previous" href="javascript:;">'+ text.previous +'</a>');
            this.$el.append('<div class="center cot"></div>');
            text.next && this.$el.append('<a class="p-item next" href="javascript:;">'+ text.next +'</a>');
            text.last && this.$el.append('<a class="p-item last" href="javascript:;">'+ text.last +'</a>');
            this.options.hasDirectGoTo && this.$el.append('<span class="p-item">跳转至<input class="p-ipt"/>页</span><a href="javascript:;" class="p-btn">确定</a>');
            this.renderPage();

        },
        renderPage:function(){
            var
              showStyle = this.options.showStyle,
              len = showStyle && showStyle.length|| 0,
              cot = this.$('.center'),
              totalPageNum = this.page.get("totalPageNum"),
              currentPage = this.page.get("currentPage"),
              pages = _.range(1,totalPageNum + 1),
              showStack = [],
              headerP,
              headTail,
              centerP,
              centerLen,
              centerStartPos,
              midPost = Math.floor(totalPageNum/2);
            cot.empty();
            this.$el.css("display",(totalPageNum < 1?"none":"inline-block"));
            if(totalPageNum < 1) return;
            this.options.hasTotalTag && this.$(".total").html('共'+ this.page.get("totalCount") +'条');
            if(showStyle && len == 3 && (sum(showStyle) < totalPageNum)){
                [].push.apply(showStack,headerP = pages.slice(0,showStyle[0]));
                centerLen = showStyle[1] < 3 ? 3 : showStyle[1];
                //me.currentPage - headerP.slice(-1) < centerLen&&showStack.push(void(0));//void(0) will show "..."
                centerStartPos = (currentPage - midPost) +  (midPost - Math.ceil(centerLen/2));
                centerStartPos == currentPage && --centerStartPos;
                centerStartPos + centerLen -1  == currentPage && ++centerStartPos;
                headTail = headerP.slice(-1)[0];
                centerStartPos >  headTail + 1 && showStack.push(void(0));//void(0) will show "..."
                centerStartPos <= headTail && (centerStartPos = headTail + 1);
                totalPageNum - centerStartPos < showStyle[1] + showStyle[2] && (centerStartPos = pages.slice(-(showStyle[1] + showStyle[2]))[0]);
                centerP = pages.slice(centerStartPos - 1,centerStartPos - 1 + centerLen);
                [].push.apply(showStack,centerP);
                totalPageNum - (centerStartPos - 1) > showStyle[1] + showStyle[2] && showStack.push(void(0));
                [].push.apply(showStack,pages.slice(-showStyle[2]));
            }else{
                showStack = pages.slice(0);
            }
            _.each(showStack,function(pn){
                cot.append(
                  pn === undefined
                    ?'<a class="p-item text" href="javascript:;" >...</a>'
                    :('<a class="p-item ' + (pn == currentPage?'current':'') + '" href="javascript:;" page="$P">$P</a>').replace(/\$P/g,pn)
                );
            });
        },
        initialize:function(options){
            this.options = options;
            options.rpp || (options.rpp = 10);
            this.page = new Model({
                "currentPage":options.currentPage || 1,
                "rpp":options.rpp,
                "totalCount":options.totalCount,
                "totalPageNum":Math.ceil(options.totalCount/options.rpp)
            });
            this.page.on('change:currentPage',this.pageChange,this);
            this.page.on('change:totalCount change:rpp',this.resetPage,this);
            this.render();
        },
        set:function(pageObj){
            this.page.set(pageObj);
            return this;
        },
        setCurrentPage:function(currentPage){
            var totalPageNum = this.page.get("totalPageNum");
            (currentPage < 1) && (currentPage = 1);
            currentPage > totalPageNum && (currentPage = totalPageNum);
            this.page.set("currentPage",currentPage);
        },
        resetPage:function(){
            var pageM = this.page;
            pageM.set("totalPageNum",Math.ceil(pageM.get("totalCount")/pageM.get("rpp")));
            pageM.set({"currentPage":1},{silence:true});
            this.pageChange();
        },
        first:function(){
            this.setCurrentPage(1);
        },
        previous:function(){
            this.setCurrentPage(this.page.get("currentPage") - 1);
        },
        next:function(){
            this.setCurrentPage(this.page.get("currentPage") + 1);
        },
        last:function(){
            this.setCurrentPage(this.page.get("totalPageNum"));
        },
        goToPage:function(e){
            if($(e.currentTarget).hasClass('current')){return}
            var pageNum = $(e.currentTarget).attr("page");
            pageNum && this.setCurrentPage(pageNum * 1);
        },
        pageChange:function(){
            this.renderPage();
            this.trigger("pageChange",this.page.get("currentPage"),this.page.get("rpp"));
        }
    });
    /**
     * PageRppView
     * @type {*}
     */
    var PageRppView = View.extend({
        className:"setRpp",
        events:{
            "change .rpp-select":"changeRpp"
        },
        render:function(){
            this.$el.append(this.setting.template);
            var _select = this.$('.rpp-select');
            _.each(this.setting.options,function(rpp){
                _select.append('<option value="'+rpp+'">'+(rpp||'all')+'</option>');
            });
        },
        initialize: function(setting){
            this.setting = setting;
            this.render();
        },
        changeRpp:function(e){
            this.trigger("rppChanged",$(e.currentTarget).val()|0);
        }
    });
    /**
     * PaginationView
     * @type {*}
     */
    var PaginationView = View.extend({
        className:"pagination-cot",
        defaultOptions:{
            targetDom:"",
            //onInit:emptyFn,
            onPageChange:emptyFn,
            hasRpp:false,
            pageListSetting:{
                hasTotalTag:false,
                hasDirectGoTo:false,
                totalCount:0,
                showStyle:[1,4,1], //[left,center>2,right]
                buttonText:{
                    first:"",
                    previous:"",
                    next:"",
                    last:""
                },
                rpp:null
            },
            rppSetting:{
                options:[10,20,30,50],// "" is all
                template:'<label>每页显示条数 : <select class="rpp-select form-control"></select></label>'
            }
        },
        initialize: function(options){
            var
              _config,
              _pageListSetting,
              _rppSetting,
              me = this;
            if(!options.config){
                throw Error('please provide a pagination configuration');
                return
            }
            _config = this.config =  $.extend(true,{},this.defaultOptions,options.config);
            _pageListSetting = _config.pageListSetting;
            _rppSetting = _config.rppSetting;
            //bind config prototype to this
            _.each(_config,function(obj,key){me[key] = obj});
            //init rpp value
            _pageListSetting.rpp  ||(_pageListSetting.rpp =  _rppSetting.options[0]||10);
            this.pageList = new PageListView(_pageListSetting);
            _config.hasRpp &&  (this.pageRpp = new PageRppView(_rppSetting));
            this.set({
                totalCount:_pageListSetting.totalCount
            });
            this.render();
            this.initEvents();
        },
        set: function(pageObj){
            this.pageList.set(pageObj);
            return this;
        },
        render: function(){
            this.$el.append(this.pageList.el);
            if(this.config.hasRpp){
                this.$el.append(this.pageRpp.el);
                this.pageRpp.$(".rpp-select").val(this.config.pageListSetting.rpp || "");
            }
            $(this.targetDom).append(this.$el);
        },
        initEvents:function(){
            this.pageList.on("pageChange",function(pageNum,rpp){
                pageNum && this.onPageChange(pageNum,rpp);
            },this);
            this.pageRpp && this.pageRpp.on("rppChanged",function( rpp ){
                rpp =  !rpp  ? this.pageListSetting.totalCount : rpp;
                this.set({"rpp":rpp});
                this.trigger("rppChanged",rpp);
            },this);
        }
    });
    /**
     *FormPopupView
     */
    var FormPopupView = Backbone.Layout.extend({
        el: false,
        useRAF: false,
        manage: true,
        events: {
            "click .save-btn": "save"
        },
        name:"",
        defaultButtons:{
            "保存": {
                "className": "btn-primary save-btn",
                "callback": function () {
                    return false
                }
            },
            "取消": {
                "className": "btn-default cancel-btn",
                "callback": emptyFn
            }
        },
        initialize: function (options) {
            this.options = options;
            this.newFlag = this.model.isNew();
            this.makeDialog();
            //this.model.once("sync", this.saveSuccess, this);
            this.on("send:success",this.saveSuccess,this);
            this.on("send:error",this.errorTip,this);
        },
        makeDialog: function () {
            var
                model = this.model,
                title = this.options.title || ((model.isNew() ? "添加" : "编辑") + this.name),
                _this = this,
                buttons = this.options.buttons;
            this.dialog = new Dialog({
                title: title,
                hasMark: true,
                position: "center",
                close:this.close.bind(this)||emptyFn,
                create: function () {
                    var container = this.$(".modal-body");
                    _this.render().$el.appendTo(container);
                    _this.setElement(this.el);
                    _this.afterOpen(container);
                },
                buttons:buttons || this.defaultButtons
            });
        },
        //need to overwrite
        afterOpen:function(){
            return this;
        },
        //need to overwrite
        valid:function(defer){
            defer.resolve({});
        },
        //need to overwrite
        send:function(obj){

        },
        //need to overwrite
        onSendSuccess:function(){

        },
        validate: function () {
            var
                defer = $.Deferred();
            this.valid(defer);
            return defer;
        },
        save: function () {
            var
                _this = this;
            this.validate().then(function (obj) {
                _this.disableSaveBtn();
                _this.send(obj);
            });
        },
        close:function(){

        },
        saveSuccess: function (msg) {
            this.successTip(msg).then(function(){
                this.onSendSuccess();
            }.bind(this));

        },
        successTip: function (msg) {
            msg = msg || (this.newFlag ? "新增" : "修改") + "成功！";
            var _this = this,defer = $.Deferred();
            this.$(".inside-tip").removeClass("error").text(msg).fadeIn(300, function () {
                _.delay(function(){
                    _this.dialog.close();
                    defer.resolve("");
                },1000);
            });
            return defer;
        },
        errorTip: function (msg) {
            msg = msg || (this.newFlag ? "新增" : "修改") + "失败！";
            this.enableSaveBtn();
            clearTimeout(this.tipTimer);
            this.tipTimer = setTimeout(function(){
                this.$(".inside-tip").addClass("error").text(msg).fadeIn(500).delay(2000).fadeOut(500);
            },500);
        },
        toggleEnable:_.throttle(function (status) {
            if(status === "disable"){
                //this.$(".save-btn").append("<span>...</span>");
                this.$(".save-btn").attr("disabled", true);
            }else{
                //this.$(".save-btn span").remove();
                this.$(".save-btn").removeAttr("disabled");
            }
        }, 400),
        disableSaveBtn: function () {
            this.toggleEnable("disable");
        },
        enableSaveBtn: function () {
            this.toggleEnable("enable");
        }
    });
	/**
     * AutoComplete
     */
    var AutoComplete = View.extend({
        events:{
          "keydown":function(){
              $(this.options.target).val("");
              this.$el.removeAttr('memberNo');
          }
        },
        initialize:function(options){
            this.cache = {};
            this.options = options;
            this.initAutoComplete();
            this.select = this.options.select ||emptyFn;

        },
        source:function(request, response){
            var term = request.term,
                cache = this.cache,
                ctx = this,
                isActive = this.options.isActive || '1',
                isAll = this.options.isAll || '1';
            if (cache[term]) {
                response(cache[term]);
                return;
            }
            $.getJSON( rs.apiRoot + "org-manage/member/getValue", {
                memberParam:request.term,
                isActive:isActive,
                isAll:isAll
            }, function( data, status, xhr ) {
                data = ctx.dateParse(data);
                cache[term] = data;
                response( data );
            });
        },
        dateParse:function(data){
            return data.map(function(obj){
                return {
                    id:obj.memberNo,
                    label:obj.memberName,
                    value:obj.memberName
                }
            });
        },
        initAutoComplete:function(){
            var ctx = this;
            this.$el.attr("autocomplete","off");
            this.$el.autocomplete({
                source:this.source.bind(this),
                delay:this.options.delay||400,
                minLength:this.options.minLength || 1,
                select:function(ui,data){
                    ctx.$el.attr("memberNo",data.item.id);
                    ctx.select.apply(null,arguments);
                    if(ctx.options.target){
                        $(ctx.options.target).val(data.item.id);
                    }
                    ui.stopPropagation();
                }
            });
        }
    })
    return {
        DropdownMenu: DropdownMenu,
        Dialog: Dialog,
        PaginationView:PaginationView,
        FormPopupView:FormPopupView,
        AutoComplete:AutoComplete
    }
});