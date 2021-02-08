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

package cn.dreamn.qianji_auto.core.hook.app;

import android.os.Bundle;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class SmsHook extends HookBase {
    private static SmsHook alipayHook;
    public static synchronized SmsHook getInstance() {
        if (alipayHook == null) {
            alipayHook = new SmsHook();
        }
        return alipayHook;
    }
    @Override
    public void hookFirst() throws Error {
        Logi("hooked msg");
        XposedHelpers.findAndHookMethod("com.android.internal.telephony.gsm.SmsMessage$PduParser",
                mAppClassLoader,"getUserDataUCS2",int.class,
                new XC_MethodHook(){
                    /**
                     * 拦截SmsMessage的内部类PduParser的getUserDataUCS2方法，该方法返回类型为String
                     * String getUserDataUCS2(int byteCount)
                     *
                     */
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        try {
                            String message = param.getResult().toString();

                            //短信识别
                            Bundle bundle=new Bundle();
                            bundle.putString("type", Receive.SMS);
                            bundle.putString("data",message);
                            bundle.putString("from", Receive.SMS);
                            bundle.putString("title", "SMS");
                            send(bundle);



                            //return;
                        } catch (Exception e) {
                            Logi(e.toString());
                        }

                    }
                });
    }


    @Override
    public String getPackPageName() {
        return "com.android.mms";
    }

    @Override
    public String getAppName() {
        return "短信";
    }

    @Override
    public String[] getAppVer() {
        return null;//全系支持
    }
}
