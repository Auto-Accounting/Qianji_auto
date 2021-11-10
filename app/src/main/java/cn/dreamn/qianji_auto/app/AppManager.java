package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class AppManager {
    /**
     * 获取自动记账支持的所有APP
     * @return
     */
    public static Bundle[] getAllApps() {
        try{
            List<Bundle> mList = new ArrayList<>();
            for (IApp iApp : AppList.getInstance().getList()) {
                Bundle bundle=new Bundle();
                bundle.putString("appName",iApp.getAppName());
                bundle.putString("appPackage",iApp.getPackPageName());
                bundle.putInt("appIcon",iApp.getAppIcon());
                mList.add(bundle);
            }
            return mList.toArray(new Bundle[0]);
        }catch (Exception ignored){

        }

        return null;
    }


    /**
     * 发送数据给支持的app
     *
     * @param billInfo
     */
    public static void sendToApp(Context context, BillInfo billInfo) {
        String app = getApp();
        Log.i("发送给app" + app);
        for (IApp iApp : AppList.getInstance().getList()) {
            if (iApp.getPackPageName().equals(app)) {
                iApp.sendToApp(context, billInfo);
                break;
            }
        }
    }

    /*
     * 进行数据同步
     */
    public static void Async(Context context) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("needAsync", true);//设置为同步
        String app = getApp();
        //   Log.i("选择的App",app);
        for (IApp iApp : AppList.getInstance().getList()) {
            // Log.i("遍历的App",iApp.getPackPageName());
            if (iApp.getPackPageName().equals(app)) {
                iApp.asyncDataBefore(context);
                break;
            }
        }

    }

    /*
     * 进行数据同步
     */
    public static void AsyncEnd(Context context, Bundle bundle) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("needAsync", false);//设置为未同步
        String app = getApp();
        for (IApp iApp : AppList.getInstance().getList()) {
            if (iApp.getPackPageName().equals(app)) {
                Log.d("收到广播的同步消息");
                iApp.asyncDataAfter(context, bundle);
                break;
            }
        }

    }

    public static void setApp(String appPackage){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("bookApp", appPackage);
    }

    public static String getApp() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("bookApp", "com.mutangtech.qianji");
    }


    public static Bundle getAppInfo(Context context) {
        String app = getApp();
        for (IApp iApp : AppList.getInstance().getList()) {
            if (iApp.getPackPageName().equals(app)) {
                Bundle bundle = new Bundle();
                bundle.putString("appName", iApp.getAppName());
                bundle.putString("appPackage", iApp.getPackPageName());
                bundle.putInt("appIcon", iApp.getAppIcon());
                bundle.putString("appAsync", iApp.getAsyncDesc(context));
                return bundle;
            }
        }
        return null;
    }
}
