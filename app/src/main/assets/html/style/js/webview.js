layui.define(function (exports) {
    const obj = {
        selectCategory: function (type) {
            window.AndroidJS.selectCategory(type);
        },
        testCategory: function (regulars) {
            window.AndroidJS.testCategory(regulars);
         },
        toast: function (msg) {
            window.AndroidJS.toast(msg);
        },
        testRegular: function (regulars,testData) {
            window.AndroidJS.testRegular(regulars,testData);
        },
        selectTime:function(dom){
            window.AndroidJS.selectTime(dom);
        },
        saveCategory:function (js,data){
            window.AndroidJS.save(js,data);
        },
        initData:function () {
            window.AndroidJS.initData();
        }
    };
    exports('webview', obj);
});