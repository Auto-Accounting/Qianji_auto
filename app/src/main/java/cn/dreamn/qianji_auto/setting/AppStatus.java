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

package cn.dreamn.qianji_auto.setting;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.helper.base.ApiUtil;
import cn.dreamn.qianji_auto.core.helper.star.AutoService;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import kotlin.jvm.internal.Intrinsics;


public class AppStatus {

    public static boolean isDebug() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getBoolean("debug", false);
    }

    public static boolean isDebug(Context context) {
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, "app", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("debug", false);
    }

    public static void setDebug(Context context, boolean b) {
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, "app", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("debug", b).apply();
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("debug", b);
    }

    public static boolean isActive(Context context) {
        if (getActiveMode().equals("helper")) {
            return defaultActive(context);
        } else return xposedActive(context);

    }

    public static String getActiveMode() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("helper_choose", "xposed");
    }

    public static boolean isXposed() {
        MMKV mmkv = MMKV.defaultMMKV();
        return Objects.requireNonNull(mmkv.getString("helper_choose", "xposed")).equals("xposed");
    }

    public static String getFrameWork(Context context) {
        if (getActiveMode().equals("helper")) {
            return context.getString(R.string.mode_helper_name);
        } else return AppInfo.getFrameWork(context);
    }

    public static boolean defaultActive(Context context) {
        return ApiUtil.isAccessibilityServiceOn(context, AutoService.class);

        //return false;
    }

    public static boolean xposedActive(Context context) {

        String frame = AppInfo.getFrameWork(context);
        if (frame.equals(context.getString(R.string.frame_taichi)))
            return taichiActive(context);
        if (frame.equals(context.getString(R.string.frame_bug)))
            return bugActive(context);
        return false;
    }

    private static boolean bugActive(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");

        try {
            Context appContext = context.createPackageContext("com.bug.xposed", Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            File mods = appContext.getFileStreamPath("mods");
            Intrinsics.checkExpressionValueIsNotNull(appContext, "appContext");
            Class<?> bugSerializeClass = appContext.getClassLoader().loadClass("com.bug.utils.BugSerialize");
            Class<?> modClass = appContext.getClassLoader().loadClass("com.bug.xposed.ModConfig$Mod");
            Method deserialize = bugSerializeClass.getDeclaredMethod("deserialize", InputStream.class);
            Intrinsics.checkExpressionValueIsNotNull(mods, "mods");
            FileInputStream fileInputStream = new FileInputStream(mods);
            Object array = deserialize.invoke(null, fileInputStream);
            if (array == null) {
                throw new Exception("null cannot be cast to non-null type kotlin.collections.ArrayList<*> /* = java.util.ArrayList<*> */");
            }

            ArrayList list = (ArrayList) array;

            for (Object mod : list) {
                if (Intrinsics.areEqual(modClass.getMethod("getPkg").invoke(mod), AppInfo.getAppPackage())) {
                    return true;
                }
            }
        } catch (Throwable e) {
            Log.d("应用转生获取应用状态失败！" + e.toString());
            //e.printStackTrace();
        }

        return false;
    }

    private static boolean taichiActive(Context context) {

        boolean isExp = false;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return false;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }

            if (result == null) {
                return false;
            }
            isExp = result.getBoolean("active", false);
        } catch (Throwable ignored) {
        }
        return isExp;
    }
}
