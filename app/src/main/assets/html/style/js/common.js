layui.define('form', function (exports) {
    let form = layui.form, flag = null;
    layui.$.prototype.serializeObject=function(){
        const obj = {};
        layui.$.each(this.serializeArray(),function(index,param){
            if(!(param.name in obj)){
                obj[param.name]=param.value;
            }
        });
        return obj;
    };

    const obj = {
        autoSave: function (dom, name,editor) {
            flag = setInterval(function () {
                if(editor!==undefined){
                    editor.save();
                }
               // let data1 = JSON.stringify(form.val(dom));
                const params = JSON.stringify(layui.$('#' + dom).serializeObject());
              //  console.log("data::::"+data1)
               // console.log("params::::"+params)
                localStorage.setItem(name, params);
            }, 1000);
        },
        cleanSave: function (name) {
            clearInterval(flag);
            localStorage.setItem(name, null);
        },
        restore: function (dom, name,editor) {
            let data = JSON.parse(localStorage.getItem(name));

            if (data != null) {
                if(editor!==undefined){
                    editor.setValue(data.code);
                }
                form.val(dom, data);
                form.render();
            }
        },
        restoreFromData: function (dom, data,editor) {
            let res = JSON.parse(data);
            //console.log("数据恢复");
            if (res != null) {
              //  console.log(res);
                if(editor!==undefined){
                    editor.setValue(res.code);
                }
                form.val(dom, res);
                form.render();
            }
        }
    };
    exports('common', obj);
});