(function ($){ 
    if(!$ || !$.validator) {return};
    var money_tips = "请输入正确的金额";
    var mobile_tips = "请输入正确的手机号"; 
    var posInt_tips = "请输入一个正整数";
    var dateAfter_tips = "日期过小";
    var goods_amount_tips = "请输入正确格式";
    var alnum_tips = "请输入一位英文字母或数字";
    var eng_tips = "请输入两位英文字母";
    var amount_tips = "输入金额请大于所选金额之和";
    var amount_tips1 = "请选择金额";
    var date_mode1_tips = "请输入正确的日期格式";
    var date_integer_tips = "请输入正确整数格式";
    var file_tips = "";
    var qty_tips = "请至少填一个";
    jQuery.validator.addMethod("packQty", function(value, element) {
    	var $q1 = $("input[name=pack_qty]");
    	temp = false;
    	$.each($q1,function(k,v){
    		if(v.value){
    			temp = true;
    		}
    	});
    	return temp;
		}, qty_tips),
    jQuery.validator.addMethod("amount1", function(value, element) {
		var amount = $(".sum").text();
		if(""==amount && ""!=value){
			temp = false;
		}else if(""==amount && ""==value){
			temp = false;
		}
		
		else{
			temp = true;
		}
		return temp;
		}, amount_tips1),
    jQuery.validator.addMethod("amount", function(value, element) {
		var amount = $(".sum").text();
		if(""!=amount && ""!=value){
			if(parseInt(value)>parseInt(amount)){
				temp = false;
			}else{
				temp = true;
			}
		}
		return temp;
		}, amount_tips),
    jQuery.validator.addMethod("eng", function(value, element) {
		return this.optional(element) || /^[a-zA-Z]+$/.test(value);
		}, eng_tips),
    jQuery.validator.addMethod("alnum", function(value, element) {
		return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
		}, alnum_tips),
    jQuery.validator.addMethod("gaValid", function(value, element) { 
        var reg = /^(([0-9]{1,}|[0-9]{1,}.[0-9]{1,})|([a-zA-Z]*[ ][a-zA-Z]*[ ]([0-9]{1,}.[0-9]{1,}|[0-9])))$/;  
        value = value.replace(/\,/g,""); 
        return reg.test(value);
    }, goods_amount_tips);
    jQuery.validator.addMethod("money", function(value, element) {
    	var $element = $(element);  
    	var digit = $element.attr("data-valid-money-digit");
        digit = digit ? "{"+digit+"}" :"{1,}";
        var reg = new RegExp("^([0-9]{1,}\\.[0-9]"+digit+"|[0-9]{1,})$");
        value = value.replace(/,/g, ""); 
        return reg.test(value);
    }, money_tips); 
    jQuery.validator.addMethod("mobile", function(value, element) {
         var reg = /^1[34578]\d{9}$/;
         return value == "" || reg.test(value);
    }, mobile_tips);
    jQuery.validator.addMethod("posInt", function(value, element) { 
         var reg = /^[0-9]*$/;   
         return reg.test(value);  
    }, posInt_tips);
    jQuery.validator.addMethod("dateAfter", function(value, element) { 
        var tips = $(element).attr("data-error-tips");
        var tag = $(element).attr("data-tag");
        $.validator.messages["dateAfter"] = tips || dateAfter_tips;
        var tagVal = $(element.form).find(tag).val();
        var val = $(element).val();
        tagVal = new Date(tagVal);
        val = new Date(val);    
        if(tagVal > val){ return false; }
        return true;
    }, dateAfter_tips);
    jQuery.validator.addMethod("fileReq", function (value, element) {
    	return value == "" ? false : true;
    },file_tips);
    jQuery.validator.addMethod("dataMode1", function (value, element) {
        return value == "" ? true : /^[0-9]{8}$/.test(value);
    },date_mode1_tips);
    jQuery.validator.addMethod("integer", function (value, element) {
        return value == "" ? true : /(^[0-9]{1,}$|^-[0-9]{1,}$)/.test(value);
    },date_integer_tips);

})(window.jQuery);