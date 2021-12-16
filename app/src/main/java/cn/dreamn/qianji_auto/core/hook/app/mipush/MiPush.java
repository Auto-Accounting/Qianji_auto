/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.hook.app.mipush;

import android.app.Notification;
import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.template.app.AppBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class MiPush extends AppBase {


    @Override
    public void hookBefore() {
        utils.log("hook miPush");
    }

    @Override
    public void hookFirst() throws Error {
        utils.log("测试测试");
        try {
            ClassLoader classLoader = utils.getClassLoader();
            //Class<?> gVar = XposedHelpers.findClass("com.xiaomi.push.service.l",classLoader);
            //public static void E(Context context, String str, int i, String str2, Notification notification) {
            XposedHelpers.findAndHookMethod("com.xiaomi.push.service.l", mAppClassLoader, "E", Context.class, String.class, int.class, String.class, Notification.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    String s1 = (String) param.args[1];
                    String s2 = (String) param.args[3];
                    Notification notification = (Notification) param.args[4];
                    utils.log(notification.toString());
                    utils.log(s1);
                    utils.log(s2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getPackPageName() {
        return "com.xiaomi.xmsf";
    }

    @Override
    public String getAppName() {
        return "小米服务";
    }


    @Override
    public Integer getHookIndex() {
        return 1;
    }


}
