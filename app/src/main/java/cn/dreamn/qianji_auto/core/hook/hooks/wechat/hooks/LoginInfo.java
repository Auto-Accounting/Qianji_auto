package cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class LoginInfo {
    public static void init(Utils utils) {

        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.tencent.mm.ui.LauncherUI", utils.getClassLoader()), "onCreate", Bundle.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences sharedPreferences = ((Activity) param.thisObject).getSharedPreferences("com.tencent.mm_preferences", 0);
            Map<String,?> map =   sharedPreferences.getAll();

                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    utils.log("遍历数据：" + entry.getKey() + "---" +  entry.getValue());
                 //   System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                }


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
                loadRegularsFromWeb(utils);
            }
        });
    }

    private static void loadRegularsFromWeb(Utils utils) {
        utils.log("正在请求微信适配数据~");
        String json = utils.readDataByApp("wechat", "red");

        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONObject jsonObject1 = jsonObject.getJSONObject("wechat");
            if (jsonObject1.containsKey(utils.getVerName())) {
                String jsData = jsonObject1.getJSONObject(utils.getVerName()).getJSONObject("red").toJSONString();
                if (!utils.readData("red").equals(jsData)) {
                    utils.writeData("red", jsData);
                    utils.toast("自动记账：微信适配文件更新！");
                    //不一样才需要加载
                }

            } else {
                // utils.toast("自动记账：当前版本微信尚未适配！");
                utils.log("当前版本微信尚未适配！微信版本：" + utils.getVerName());
            }

        } catch (Throwable e) {
            e.printStackTrace();
            utils.log("发生错误！" + e.toString());
        }
    }
}
