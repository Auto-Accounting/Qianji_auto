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

package cn.dreamn.qianji_auto.core.hook;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class HookBase implements IHooker {
    private Integer mHookCount = 0;
    private Integer mHookCountIndex = 1;//HOOK第几个进程
    protected ClassLoader mAppClassLoader;
    protected Context mContext;



    public static final String SEND_ACTION = "cn.dreamn.qianji_auto.XPOSED";




  protected String TAG="Qianji-Auto";

    /**
     * @param packPagename String 需要hook的包名
     * @param processName String 序号hook的进程名
     * @param hookCountIndex 第几个进程才HOOK，一般为1，vxp可能要写2，如果不知道，那就写0，都去hook
     */
    public void hook(String packPagename, String processName, Integer hookCountIndex) {
        if (!getPackPageName().contentEquals(packPagename)) {
            return;
        }
        mHookCountIndex = hookCountIndex;
        try {
            hookMainInOtherAppContext(processName);
        } catch (Throwable e) {
            Log.e(TAG, "重大错误：" + e.getMessage());
        }
    }

    private void hookMainInOtherAppContext(final String processName) {
        XposedHelpers.findAndHookMethod(ContextWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mContext = (Context) param.args[0];
                mAppClassLoader = mContext.getClassLoader();
                if (getPackPageName().equals(processName)) {
                    Logi("hooked 进程名 "+processName);
                    mHookCount = mHookCount + 1;
                    Logi("hooked mHookCount 进程数 ->"+mHookCount+" mHookCountIndex 需要hook的进程ID "+mHookCountIndex);
                    if (mHookCount.equals(mHookCountIndex)) {
                        try {
                            Logi("try hook");
                            hookFirst();

                        } catch (Error | Exception e) {
                            Logi(e.toString());
                        }
                    }
                }
            }
        });
    }


    /**
     * 发送广播通知
     * @param bundle
     */
    public void send(Bundle bundle){
        Logi("广播给自动记账："+bundle.toString());
        Intent intent = new Intent(SEND_ACTION);
        intent.setPackage("cn.dreamn.qianji_auto");
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    /**
     * 打印日志
     * @param msg
     */
    public void Logi(String msg){
        Log.i("Qianji-"+getAppName(),msg);
    }
    /**
     * 打印日志
     * @param msg
     */
    public void Logi(String msg,boolean xp){
        Logi(msg);
        if(xp)XposedBridge.log("Qianji-"+getAppName()+" -> "+msg);
    }
}
