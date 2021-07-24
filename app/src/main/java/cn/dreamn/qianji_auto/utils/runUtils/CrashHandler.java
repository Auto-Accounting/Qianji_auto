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
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        /*if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            Log.d("异常处理有问题~");
            mDefaultHandler.uncaughtException(thread, ex);
        } */
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // getInstance().getSpUtil().setCrashLog(true);// 每次进入应用检查，是否有log，有则上传
        // 使用Toast来显示异常信息
        StringBuilder errorInfo = new StringBuilder("verCode:" + BuildConfig.VERSION_NAME + "\n");
        errorInfo.append("\nerror:\n").append(ex.getMessage()).append("\n");
        errorInfo.append("\nstackTrace:\n");
        StackTraceElement[] tree = ex.getStackTrace();
        int i = 0;
        for (StackTraceElement t : tree) {
            i++;
            /* errorInfo.append(String.format("[stackTrace]File(%s)Class(%s)Method(%s)Line(%s)\n", t.getFileName(), t.getClassName(), t.getMethodName(), t.getLineNumber()));*/
            errorInfo.append(i).append(".").append(t.toString()).append("\n");
        }
        Intent intent = new Intent(mContext, ErrorActivity.class);
        intent.putExtra("errorInfo", errorInfo.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

        return true;
    }


}
