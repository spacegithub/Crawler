(function (){
    var cell = '<div class="list-cell">5</div>'; 
    var status = '<div class="dataList none"><div class="input-search-box"><div class="input-search-parent"><input class="input-search"/></div></div><div class="list-panle"><div class="list-box"></div><div class="loading none">加载中...</div><div class="not-found none">无数据</div><div><span class="clear">清空</span></div></div></div>'

    function DataList(config){
        this.config = $.extend({url: null, parame: null , targetInput: null, status: $(status)}, config);
        this.init();  
    }

    DataList.prototype = {
        init: function (){
            var self = this;
            if(!this.config.url) {
                console.log("data list url is null!!");
                return;
            }
            if(!this.config.targetInput) {
                console.log("targetInput is null!!");
                return;
            }
            this.config.status.css("width", this.config.targetInput.width());
            if(this.config.width){
                this.config.status.css("width", this.config.width);
            }
            if(!$.isFunction(this.config.getParameter)){
                this.config.getParameter = function (){
                    return self.config.status.find(".input-search").val();
                }
            }
            this.bind();
            this.state = this.state();
            this.state.init();
            $(document.body).append(this.config.status);
        },
        bind: function (){
            var self = this;
            var time = null;

            this.config.targetInput.bind("focus", function (){
                var x = $(this).offset().left;
                var y = $(this).offset().top;
                var h = this.offsetHeight;
                var bt = $(this).css("border-top-width").replace("px", "");
                var bb = $(this).css("border-bottom-width").replace("px", ""); 
                bt = parseFloat(bt);
                bb = parseFloat(bb);
                bt = isNaN(bt) ? 0 : bt;
                bb = isNaN(bb) ? 0 : bb;
                y += h + bt + bb;
                self.config.status.css("left", x);
                self.config.status.css("top", y); 
                self.state.init();
                $.isFunction(self.config.focus) && self.config.focus(self);
                self.config.status.show();
            });

            // hide
            $(document.body).bind("click", function (){
                self.config.status.hide();
            });
            
            // input search
            this.config.status.find(".input-search").bind("input", function (){
                if(time != null) {
                    clearTimeout(time);
                }
                time = setTimeout(function (){
                    self.startSearch(self.config.getParameter(self.config.status.find(".input-search").get(0)));
                }, 888);
            });

            // clear
            this.config.status.find("span.clear").click(function () {
                self.config.status.find(".input-search").val("");
                self.config.targetInput.val("");
            });

            $.bindMultiple({
                // prevent up
                $objs: [this.config.status, this.config.targetInput],
                type: "click",
                callbk: function (){ 
                    return false;
                } 
            }); 
        },
        startSearch: function (parameter){
            var self = this;
            this.state.loading();
            this.getList(parameter, function (info){
                self.state.list();
                self.fullList(info);
            });
        },
        getList: function (parameter, fn){
            $.ajax({
                url: this.config.url,
                data: parameter,
                type: this.config.ajaxType || "GET",
                success: function (info) {
                    fn(info);
                },
                error: function (e) {
                    console.log("net error : " + e);
                    fn(e)
                }
            });
        },
        fullList: function (info){
            if($.isFunction(this.config.resultForMat)){
                info = this.config.resultForMat(info); 
            }

            if(!$.isArray(info)) {
                console.log("result is not array!!");
            }

            var self = this; 

            if(info === null || info === undefined || info.length === 0) {
                self.state.notFound();
                return; 
            }  

            self.config.status.find(".list-box").empty();

            $.each(info, function (key, item){
                var text = "";
                if($.isFunction(self.config.itemForMat)){
                    text = self.config.itemForMat(item);
                }
                var $cell = $(cell);
                $cell.text(text);
                self.config.status.find(".list-box").append($cell);
                $cell.click(function (){
                    if($.isFunction(self.config.clickCallbk)){
                        self.config.clickCallbk.call(self.config.targetInput.get(0), item, $cell);
                        self.config.status.hide();
                    }
                });
            });
        },
        state: function (){
            var self = this; 
            return {
                loading: function (){
                    self.config.status.find(".list-box, .not-found").hide();
                    self.config.status.find(".loading").show();
                },
                notFound: function (){
                    self.config.status.find(".list-box, loading").hide();
                    self.config.status.find(".not-found").show();
                },
                list: function (){
                    self.config.status.find(".not-found, .loading").hide(); 
                    self.config.status.find(".list-box").show();
                },
                init: function (){ 
                    self.config.status.find(".list-box, .not-found .loading").hide();
                    self.config.status.find(".input-search").val(""); 
                    self.config.status.find(".list-box").empty();
                }
            }
        }
    };

    $.fn.dataList = function (config){
        var self = this;
        config = $.extend({}, config);
        self.each(function (){
            config.targetInput = $(this); 
            self.data("dataList", new DataList(config));
        }); 
    };

    $.bindMultiple = function(){
        for(var i = 0; i < arguments.length; i++) (function (object){
            if(object.$objs && object.type && object.callbk) {
                if($.isArray(object.$objs)) {
                    for(var j = 0; j < object.$objs.length; j++){
                        var $objItem = object.$objs[j]; 
                        if($objItem instanceof jQuery) { 
                            $objItem.bind(object.type, object.callbk);
                        }  
                    }
                } 
            }
        })(arguments[i]);
    }
})();