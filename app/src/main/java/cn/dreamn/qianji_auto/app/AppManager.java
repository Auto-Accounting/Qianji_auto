package cn.dreamn.qianji_auto.app;

import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;

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
     * @param billInfo
     * @return
     */
    public static String sendToApp(BillInfo billInfo){
        MMKV mmkv=MMKV.defaultMMKV();
        String app=mmkv.getString("bookApp","com.mutangtech.qianji");
        for (IApp iApp : AppList.getInstance().getList()) {
            if(iApp.getPackPageName().equals(app)){
                iApp.sendToApp(billInfo);
                break;
            }
        }
        return "";
    }

    /*
    * 进行数据同步
     */
    public static void Async(){
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("isAsync",false);//设置为未同步
        String app=getApp();
        for (IApp iApp : AppList.getInstance().getList()) {
            if(iApp.getPackPageName().equals(app)){
                iApp.asyncData();
                break;
            }
        }

    }


    public static void setApp(String appPackage){
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("bookApp",appPackage);
    }

    public static String getApp() {
        MMKV mmkv=MMKV.defaultMMKV();
        return mmkv.getString("bookApp","com.mutangtech.qianji");
    }

    public static Bundle getAppInfo(){
        String app=getApp();
        for (IApp iApp : AppList.getInstance().getList()) {
            if(iApp.getPackPageName().equals(app)){
                Bundle bundle=new Bundle();
                bundle.putString("appName",iApp.getAppName());
                bundle.putString("appPackage",iApp.getPackPageName());
                bundle.putInt("appIcon",iApp.getAppIcon());
                bundle.putString("appAsync",iApp.getAsyncDesc());
                return bundle;
            }
        }
        return null;
    }
}
