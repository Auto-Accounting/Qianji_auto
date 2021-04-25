package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.runUtils.ACache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoBillWeb {
    public static String getCookie() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("login_cookie", "");
    }

    public static void setCookie(String cookie) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("login_cookie", cookie);
    }

    public static void getCategoryCache(Context context, String name, WebCallback callback) {
        ACache mCache = ACache.get(context);
        JSONObject value = mCache.getAsJSONObject("Category");
        if (value == null) {
            //缓存失效
            getCategoryWeb(name, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callback.onFailure();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    mCache.put("Category", (Serializable) response.body());
                    callback.onResponse();
                }
            });
        }
    }

    public static void getCategoryWeb(String name, Callback callback) {
        String url = "https://qianji.ankio.net/api_category.json";
        if (name != null) {
            url += "?name=" + name;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                //   .addHeader("Cookie",getCookie())//添加登录cookie访问，直接获取连接是不需要cookie的
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public interface WebCallback {
        void onFailure();

        void onResponse();
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
