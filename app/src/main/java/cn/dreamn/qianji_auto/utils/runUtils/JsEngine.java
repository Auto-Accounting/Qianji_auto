package cn.dreamn.qianji_auto.utils.runUtils;


import com.hjq.toast.ToastUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import cn.dreamn.qianji_auto.R;

public class JsEngine {
    public static String run(String jsCode) {

        Log.m("js运行代码", jsCode);
        Context rhino = Context.enter();

        rhino.setOptimizationLevel(-1);
        Object result = null;
        try {
            Scriptable scope = rhino.initStandardObjects();
            result = rhino.evaluateString(scope, jsCode, "JavaScript", 1, null);
        } catch (Throwable e) {
            Log.i("JS执行错误:" + e.toString());
            ToastUtils.show(R.string.js_error);
        } finally {
            Context.exit();
        }
        if (result != null) return result.toString();
        return "Undefined";
    }
}
