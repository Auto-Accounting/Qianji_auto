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

package cn.dreamn.qianji_auto.utils.runUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.ui.activity.ErrorActivity;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    // CrashHandler实例
    @SuppressLint("StaticFieldLeak")
    private static final CrashHandler INSTANCE = new CrashHandler();
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //nameString = BmobUserManager.getInstance(mContext).getCurrentUserName();
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        String error = handleException(ex);
        // 退出程序
// 跳转到崩溃提示Activity
        Intent intent = new Intent(mContext, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("errorInfo", error);
        mContext.startActivity(intent);
        System.exit(0);// 关闭已奔溃的app进程
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     */
    private String handleException(Throwable ex) {
        if (ex == null) {
            return "";
        }

        ex.printStackTrace();
        StringBuilder errorInfo = new StringBuilder("verCode:" + BuildConfig.VERSION_NAME + "\n");
        errorInfo.append("\nerror:\n").append(ex.getMessage()).append("\n");
        errorInfo.append("\nstackTrace:\n");
        StackTraceElement[] tree = ex.getStackTrace();
        int i = 0;
        for (StackTraceElement t : tree) {
            i++;
            errorInfo.append(i).append(".").append(t.toString()).append("\n");
        }

        return errorInfo.toString();

    }


}
