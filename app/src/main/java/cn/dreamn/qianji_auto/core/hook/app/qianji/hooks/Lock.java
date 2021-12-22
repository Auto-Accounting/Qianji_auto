package cn.dreamn.qianji_auto.core.hook.app.qianji.hooks;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.ui.activity.LockActivity;
import cn.dreamn.qianji_auto.ui.utils.AppFrontBackHelper;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Lock {
    public static void init(Utils utils) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String type = utils.readDataByApp("apps", "lock_qianji_style");
                if (type.equals("1")) {
                    Activity activity = (Activity) param.thisObject;
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            }


        });
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                String type = utils.readDataByApp("apps", "lock_qianji_style");
                if (!type.equals("1")) {
                    return;
                }
                AppFrontBackHelper helper = new AppFrontBackHelper();
                helper.register((Application) param.thisObject, new AppFrontBackHelper.OnAppStatusListener() {
                    @Override
                    public void onFront(Activity activity) {
                        //应用切到前台处理
                        Intent intent = activity.getIntent();
                        Bundle bundle = null;
                        if (intent != null) {
                            bundle = intent.getExtras();
                        }
                        String isLock = utils.readDataByApp("apps", "lock_qianji_style_lock");

                        if (isLock.equals("false")) return;
                        if (isLock.equals("true") || isLock.equals("") || intent == null || bundle == null || bundle.size() == 0) {
                            Intent intent1 = new Intent(activity, LockActivity.class);
                            utils.writeDataByData("apps", "lock_qianji_app", utils.getPackageName());
                            utils.writeDataByData("apps", "lock_qianji_lock", "true");
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LockActivity.doStartApplicationWithPackageName((Context) param.thisObject, BuildConfig.APPLICATION_ID, null);
                        }

                    }

                    @Override
                    public void onBack(Activity activity) {
                        //应用切到后台处理
                        utils.writeDataByData("apps", "lock_qianji_style_lock", "true");
                    }
                });
            }
        });

    }

}
