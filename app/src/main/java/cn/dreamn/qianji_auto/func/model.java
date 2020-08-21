package cn.dreamn.qianji_auto.func;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class model {


    public static final Map<String,String> farmePackages= ImmutableMap.of(
            "xposed","de.robv.android.xposed.installer",
            "taichi","me.weishu.exp",
            "edxposed","org.meowcat.edxposed.manager"
    );

    public static String farmework="xposed";//判断加载的框架


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
    public static boolean isInstallFarmeWork(Context context){
        for (Map.Entry<String, String> entry : farmePackages.entrySet()) {

            if(!getAppVersionName(context, entry.getValue()).equals("")){
                farmework=entry.getKey();//框架已经安装
                return true;
            }
        }

        return false;
    }
    //激活状态检查
    public static boolean isActvive(Context context){

        if(farmework.equals("taichi"))return taichiActive(context);
        else return xposedActive();

    }


    //xp 激活状态检查
    private static boolean xposedActive() {
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
