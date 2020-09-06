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

package cn.dreamn.qianji_auto.utils.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import com.xuexiang.xutil.app.AppUtils;

import cn.dreamn.qianji_auto.R;

import static com.xuexiang.xui.utils.ResUtils.getString;

public class app {


    public static String getAppVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo.versionName;
            }
        }
        return "";
    }
    //判断是否安装某个框架
    public static String getFrameWork(Context context){
        String farmework="xposed";//判断加载的框架
        Map<String,String> farmePackages= ImmutableMap.of(
                "xposed","de.robv.android.xposed.installer",
                "taichi","me.weishu.exp",
                "edxposed","org.meowcat.edxposed.manager"
        );
        for (Map.Entry<String, String> entry : farmePackages.entrySet()) {

            if(!getAppVersionName(context, entry.getValue()).equals("")){
                farmework=entry.getKey();//框架已经安装
                break;
            }
        }

        switch (farmework){
            case "xposed":return "Xposed";
            case "taichi":return getString(R.string.frame_taichi);
            case "edxposed":return "EdXposed";
            default:return getString(R.string.frame_unknow);
        }
    }
    public static String getAppVerName(){
        return AppUtils.getAppVersionName();
    }
    public static int getAppVerCode(){
        return AppUtils.getAppVersionCode();
    }
}
