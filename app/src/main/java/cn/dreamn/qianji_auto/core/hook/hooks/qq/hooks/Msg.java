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

package cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks;

import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


public class Msg {
    public static void init(Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Context mContext = utils.getContext();

        final Class<?> text_msg_class = mAppClassLoader.loadClass("com.tencent.mobileqq.data.MessageForText");
        final Class<?> msg_record_class = mAppClassLoader.loadClass("com.tencent.mobileqq.data.MessageRecord");
        Class<?> base_chat_pie_class = XposedHelpers.findClassIfExists("com.tencent.mobileqq.activity.BaseChatPie", mAppClassLoader);
        if (base_chat_pie_class == null) {
            base_chat_pie_class = XposedHelpers.findClassIfExists("com.tencent.mobileqq.activity.aio.core.BaseChatPie", mAppClassLoader);
        }
        if (base_chat_pie_class == null) {
            utils.log("Incompatible version of mobileqq, QQHlepr won't work!");
            return;
        }

        //  utils.dumpClass(base_chat_pie_class);
        XposedHelpers.findAndHookMethod(base_chat_pie_class,
                "update", "java.util.Observable", "java.lang.Object",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // this will be called before the clock was updated by the original method
                        Object obj = param.args[1];
                        if (!text_msg_class.isInstance(obj)) return;
                        String msg_str = (String) msg_record_class.getMethod("toString").invoke(obj);
                        utils.log("msg str " + msg_str);
                       /* Field[] fields = msg_record_class.getDeclaredFields();
                        for(Field field:fields){
                            field.setAccessible(true);
                            utils.log("msg str " + field.get(obj));
                        }*/
  /*                      Field field = msg_record_class.getDeclaredField("istroop");
                        field.setAccessible(true);
                        int istroop = (int) field.get(obj);
                        if (istroop != 0) return;   // qun

                        field = msg_record_class.getDeclaredField("issend");
                        field.setAccessible(true);
                        int is_local_send = (int) field.get(obj);
                        if (is_local_send != 1) return;  // send from local


                        field = msg_record_class.getDeclaredField("time");
                        field.setAccessible(true);
                        long send_time = (long) field.get(obj);
                        intent.putExtra("time", send_time);

                        field = msg_record_class.getDeclaredField("selfuin");
                        field.setAccessible(true);
                        String selfuin = (String) field.get(obj);
                        intent.putExtra("selfuin", selfuin);

                        field = msg_record_class.getDeclaredField("senderuin");
                        field.setAccessible(true);
                        String senderuin = (String) field.get(obj);
                        intent.putExtra("senderuin", senderuin);

                        if (!senderuin.equals(selfuin)) {
                            return;
                        }


                        field = msg_record_class.getDeclaredField("frienduin");
                        field.setAccessible(true);
                        String frienduin = (String) field.get(obj);
                        intent.putExtra("frienduin", frienduin);


                        field = msg_record_class.getDeclaredField("msg");
                        field.setAccessible(true);
                        String msg = (String) field.get(obj);
                        intent.putExtra("msg", msg);

                        Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                        context.sendBroadcast(intent);
                        HookUtil.log("send qq msg to " + frienduin);*/

                    }
                });
    }


}
