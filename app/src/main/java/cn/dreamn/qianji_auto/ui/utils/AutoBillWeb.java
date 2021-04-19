package cn.dreamn.qianji_auto.ui.utils;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;

public class AutoBillWeb {
    public static String getCookie() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("login_cookie", "");
    }

    public static void setCookie(String cookie) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("login_cookie", cookie);
    }

    public static void getCategoryList(String name) {

    }

    public static void getAppData(String name, String appPackageName, String type) {

    }

    public static void sendData() {

    }

    public static void getSupport() {

    }

    public static void isLogin() {

    }

    public static void goToLogin(BaseFragment baseFragment) {
        WebViewFragment.openUrl(baseFragment, "https://qianji.ankio.net/login");
    }
}
