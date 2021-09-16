package cn.dreamn.qianji_auto.utils.runUtils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

public class Data {
    static int InitCode = 1;//需要重新初始化的code
    static int UpdateCode = 1;//需要升级的Code

    static boolean isInit() {
        MMKV mmkv = MMKV.defaultMMKV();
        int code = mmkv.getInt("InitCode", 0);
        return code == InitCode;
    }

    static boolean isUpdate() {
        MMKV mmkv = MMKV.defaultMMKV();
        int code = mmkv.getInt("UpdateCode", 0);
        return code == UpdateCode;
    }

    public static void init(Context context) {
        if (isInit()) return;
        //释放初始化信息
        setDefaultApps(context);
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("InitCode", InitCode);
    }

    static void setDefaultApps(Context context) {
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, "apps", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("apps", "com.tencent.mm,com.tencent.mobileqq,com.eg.android.AlipayGphone,com.jingdong.app.mall,com.unionpay").apply();
    }


    static void update() {
        update(UpdateCode);
    }

    static void update(int code) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("UpdateCode", code);
    }
}
