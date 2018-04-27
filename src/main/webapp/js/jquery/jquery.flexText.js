/**
 * jQuery flexText: Auto-height textareas
 * --------------------------------------
 * Requires: jQuery 1.7+
 * Usage example: $('textarea').flexText()
 * Info: https://github.com/alexdunphy/flexible-textareas
 */
;(function ($) {
    function FT(elem) {
        this.$textarea = $(elem);
        this._init();
    }
    FT.prototype = {
        _init: function () {
            var _this = this;
            this.$textarea.wrap('<div class="flex-text-wrap" />').before('<pre><span /><br /></pre>');
            this.$span = this.$textarea.prev().find('span');
            this.$textarea.on('input propertychange keyup change', function () {
                _this._mirror();
            });
            $.valHooks.textarea = { 
                get: function (elem) {
                    return elem.value.replace(/\r?\n/g, "\r\n");
                }
            };
            this._mirror();
        }
        ,_mirror: function () {
            this.$span.text(this.$textarea.val());
        } 
    };
    $.fn.flexText = function () {
        return this.each(function () {
            if (!$.data(this, 'flexText')) {
                $.data(this, 'flexText', new FT(this));
            } 
        }); 
    };
})(jQuery);