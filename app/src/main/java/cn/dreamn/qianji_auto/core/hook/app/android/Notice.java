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

package cn.dreamn.qianji_auto.core.hook.app.android;

import android.app.Notification;
import android.os.Bundle;

import java.util.Arrays;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.utils.runUtils.Cmd;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Notice extends HookBase {


    @Override
    public void hookBefore() {

    }

    @Override
    public void hookFirst() throws Error {
        final Class<?> clazz = XposedHelpers.findClass(
                "android.app.NotificationManager", utils.getClassLoader());
        XposedHelpers.findAndHookMethod(clazz, "notify"
                , String.class, int.class, Notification.class
                , new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        utils.log("methodHookParam.args:  " + Arrays.toString(param.args));
                        //通过param拿到第三个入参notification对象
                        Notification notification = (Notification) param.args[2];

                        //获得包名
                        String aPackage = notification.contentView.getPackage();
                        Bundle bundle = notification.extras;
                        String title = (String) bundle.get("android.title");
                        String text = (String) bundle.get("android.text");
                        utils.log(bundle.toString());
                        //收到支付宝支付通知后,自动拉起支付宝
                        if (aPackage.contains("com.eg.android.AlipayGphone")) {
                            Cmd.exec(new String[]{
                                    "am force-stop com.eg.android.AlipayGphone",
                                    "sleep 1",
                                    "am start -n com.eg.android.AlipayGphone/com.eg.android.AlipayGphone.AlipayLogin"
                            });
                        }
                        utils.log("loadpackage" + aPackage);
                        utils.log("tickerText" + notification.tickerText.toString());
                        utils.log("title" + title);
                        utils.log("text" + text);
                    }
                });

    }

    @Override
    public String getPackPageName() {
        return null;
    }

    @Override
    public String getAppName() {
        return "通知";
    }

    @Override
    public String[] getAppVer() {
        return null;
    }

    @Override
    public Integer getHookIndex() {
        return 2;
    }
}
