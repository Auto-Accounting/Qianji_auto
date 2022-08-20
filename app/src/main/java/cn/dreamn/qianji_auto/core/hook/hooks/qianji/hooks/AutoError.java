package cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class AutoError {
    public static void init(Utils utils, JSONArray jsonArray) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> AutoTaskLog = mAppClassLoader.loadClass("com.mutangtech.qianji.data.model.AutoTaskLog");
        Class<?> WebViewActivity = mAppClassLoader.loadClass("com.mutangtech.qianji.ui.webview.WebViewActivity");

        XC_MethodHook methodHook = new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                String string = (String) param.args[0];
                //钱迹弹出错误信息，转发给自动记账处理~
                if (string != null) {
                    utils.log("钱迹错误捕获： " + string, true);
                    if (string.contains("key=type;value=998")) {
                        Activity activity = (Activity) param.thisObject;
                        TaskThread.onMain(500, () -> XposedHelpers.callMethod(activity, "finishAndRemoveTask"));
                        //XposedHelpers.callMethod(activity, "finishAndRemoveTask");
                        //param.setThrowable(new Throwable("ooo"));
                        // XposedHelpers.callMethod(utils.getContext(), "finish");
                        return;
                    }

                    String url = getSolvedUrl(string);
                    Intent v0 = new Intent(utils.getContext(), WebViewActivity);
                    v0.putExtra("param_web_url", url);
                    v0.putExtra("param_web_title", "自动记账错误解决方案");
                    v0.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    utils.getContext().startActivity(v0);
                    //使用钱迹的WebView
                    //加载解决方案
                    Toast.makeText(utils.getContext(), string + "\n发生了错误，正在为您加载解决方案！", Toast.LENGTH_LONG).show();


                }


            }
        };


        String cls =jsonArray.getString(0);
        String method = jsonArray.getString(1);

        try {
            utils.log("钱迹 AutoError.init Hook<" + cls + "." + method + "> ");
            XposedHelpers.findAndHookMethod(cls, mAppClassLoader, method, String.class, AutoTaskLog, methodHook);
        } catch (Exception e) {
            utils.log("钱迹 AutoError.init Hook <" + cls + "." + method + "> HookError " + e);
        }
    }

    public static String getSolvedUrl(String problems) {
        String url = "https://auto.ankio.net/pages/2db3aa/#%E8%B5%84%E4%BA%A7%E8%AE%BE%E7%BD%AE%E9%94%99%E8%AF%AF";
        if (problems.contains("bookname")) {
            url = "https://auto.ankio.net/pages/2db3aa/#%E8%B4%A6%E6%9C%AC%E8%AE%BE%E7%BD%AE%E9%94%99%E8%AF%AF";
        } else if (problems.contains("accountname")) {
            url = "https://auto.ankio.net/pages/2db3aa/#%E8%B5%84%E4%BA%A7%E8%AE%BE%E7%BD%AE%E9%94%99%E8%AF%AF";
        }
        return url;
    }
}
