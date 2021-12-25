package cn.dreamn.qianji_auto.data.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class Data {
    static int InitCode = 3;//需要重新初始化的code

    static boolean isInit() {
        MMKV mmkv = MMKV.defaultMMKV();
        int code = mmkv.getInt("InitCode", 0);
        return code == InitCode;
    }

    public static void init(Context context) {
        if (isInit()) return;
        //释放初始化信息
        setDefaultApps(context);
        setDefaultRegulars(context);
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("InitCode", InitCode);

    }

    static void setDefaultApps(Context context) {
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, "apps", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("apps", "com.tencent.mm,com.tencent.mobileqq,com.eg.android.AlipayGphone,com.jingdong.app.mall,com.unionpay").apply();
    }

    static void setDefaultRegulars(Context context) {
        String category = Tool.getAssert(context, "regulars/category.json", "regulars/category.json");
        String app_regular = Tool.getAssert(context, "regulars/app_regular.json", "regulars/app_regular.json");
        String help_regular = Tool.getAssert(context, "regulars/helper_regular.json", "regulars/helper_regular.json");
        RegularManager.restoreFromData(context, "", "category", category, code -> {
        });
        RegularManager.restoreFromData(context, "", "app", app_regular, code -> {
        });
        RegularManager.restoreFromData(context, "", "helper", help_regular, code -> {
        });
    }



}
