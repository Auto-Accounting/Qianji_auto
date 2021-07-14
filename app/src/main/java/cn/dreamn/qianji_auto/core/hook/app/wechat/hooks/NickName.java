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

package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class NickName {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();

        //使用两种方案获取昵称~
        try {
            //获取昵称
            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.d.ad", mAppClassLoader, "setMMTitle", CharSequence.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    String UserName = param.args[0].toString();
                    if (UserName == null || UserName.equals("")) return;
                    utils.writeData("cache_userName", UserName);
                    utils.log("微信名：" + UserName);
                }
            });
        } catch (Error | Exception e) {
            try {
                //获取昵称
                XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.ChattingUIFragment", mAppClassLoader, "setMMTitle", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String UserName = param.args[0].toString();
                        if (UserName == null || UserName.equals("")) return;
                        utils.writeData("cache_userName", UserName);
                        utils.log("微信名：" + UserName);
                    }
                });
            } catch (Error | Exception e2) {
                utils.log("hook错误！获取不到昵称。错误：" + e2.toString());
            }
        }


        //
    }


}
