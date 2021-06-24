package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class LoginInfo {
    public static void init(Utils utils) {
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.tencent.mm.ui.LauncherUI", utils.getClassLoader()), "onCreate", Bundle.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences sharedPreferences = ((Activity) param.thisObject).getSharedPreferences("com.tencent.mm_preferences", 0);
                String login_weixin_username = sharedPreferences.getString("login_weixin_username", "null");
                String last_login_nick_name = sharedPreferences.getString("last_login_nick_name", "null");
                String login_user_name = sharedPreferences.getString("login_user_name", "null");
                String last_login_uin = sharedPreferences.getString("last_login_uin", "null");

                utils.writeData("login_weixin_username", login_weixin_username);
                utils.writeData("last_login_nick_name", last_login_nick_name);
                utils.writeData("login_user_name", login_user_name);
                utils.writeData("last_login_uin", last_login_uin);

                utils.log("获取微信信息成功：" + login_weixin_username + "---" + last_login_nick_name + "----" + login_user_name + "----" + last_login_uin);
                //    utils.log(login_weixin_username+"---"+last_login_nick_name+"----"+login_user_name+"----"+last_login_uin);
            }
        });
    }
}
