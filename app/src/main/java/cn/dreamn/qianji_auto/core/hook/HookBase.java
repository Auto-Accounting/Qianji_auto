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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class HookBase implements IHooker {
    private Integer mHookCount = 0;
    private Integer mHookCountIndex = 1;//HOOK第几个进程
    protected ClassLoader mAppClassLoader;
    protected Context mContext;



    public static final String SEND_ACTION = "cn.dreamn.qianji_auto.XPOSED";

    public static final String SEND_LOG_ACTION = "cn.dreamn.qianji_auto.XPOSED_LOG";


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
                    if (mHookCountIndex==0||mHookCount.equals(mHookCountIndex)) {
                        try {
                            if(!compare()){
                                String string=String.format("当前应用[%s]版本[%s]可能不受支持！您可以继续使用，但可能部分功能不支持。支持的版本为：%s",getAppName(),getVerName(mContext),Arrays.toString(getAppVer()));

                                Toast.makeText(mContext,string,Toast.LENGTH_LONG).show();
                                Logi(string,false);
                            }
                            Logi("尝试 hook "+getAppName()+" 进程",false);
                            hookFirst();
                            Logi("hook "+getAppName()+" 完毕",false);
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
        sendBroadcast(SEND_ACTION,bundle);
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
        if(xp)XposedBridge.log("Qianji-"+getAppName()+" -> "+msg);
        else{
            //发送到自动记账日志
            Log.i("Qianji-"+getAppName(),msg);
            Bundle bundle=new Bundle();
            bundle.putString("tag","Qianji-"+getAppName());
            bundle.putString("msg",msg);
            sendBroadcast(SEND_LOG_ACTION,bundle);
        }
    }

    private void sendBroadcast(String Action,Bundle bundle){
        Intent intent = new Intent(Action);
        intent.setPackage("cn.dreamn.qianji_auto");
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent,null);
    }

    /**
     * 获取版本名
     * @param context
     * @return
     */
    private  String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 判断是否是支持版本
     * @return
     */
    private boolean compare(){
        String[] version=getAppVer();
        if(version==null){
            //表示全版本支持
            return true;
        }
        String string=getVerName(mContext);
        Logi("当前应用版本："+string+" 支持的版本为"+ Arrays.toString(version));
        for (String s : version) {
            if (s.equals(string)) return true;
        }
        return false;
    }

}
