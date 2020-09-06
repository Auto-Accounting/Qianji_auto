/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.utils.active;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.file.Storage;
import cn.dreamn.qianji_auto.utils.tools.app;

import static com.xuexiang.xui.utils.ResUtils.getString;

//判断插件激活
public class core {


    public static  boolean isActive(Context context){
        if(getActiveMode().equals("default")){
            return defaultActive();
        }else return xposedActive(context);
    }

    public static String getActiveMode(){
        return Storage.type(Storage.Set).get("mode","xposed");
    }

    public static String getFrameWork(Context context){
        if(getActiveMode().equals("default")){
            return getString(R.string.frame_default);
        }else return app.getFrameWork(context);
    }
    private static boolean defaultActive(){
        //TODO 暂时留空
        return false;
    }
    private static boolean xposedActive(Context context){
        String farmwork=app.getFrameWork(context);
        if(farmwork.equals(getString(R.string.frame_taichi)))return taichiActive(context);
        return false;
    }
    private  static boolean taichiActive(Context context) {

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
