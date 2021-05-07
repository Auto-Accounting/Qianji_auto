package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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




    public static void getCategoryWeb(String name, WebCallback callback) {
        String url = "https://qianji.ankio.net/api_category.json";
        if (name != null) {
            url += "?name=" + name;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        final CacheControl.Builder builder = new CacheControl.Builder();
        builder.maxAge(30, TimeUnit.MINUTES);
        CacheControl cache = builder.build();
        final Request request = new Request.Builder().cacheControl(cache).url(url).build();
        final Call call = okHttpClient.newCall(request);//
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    callback.onSuccessful(string);
                } else {
                    callback.onFailure();
                    Log.d("服务器响应错误");
                }
            }
        });
}
    public static void getDataWeb(String name, String type, String app, WebCallback callback) {
        String url = "https://qianji.ankio.net/api_data.json";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        if (name != null) {
            urlBuilder.addQueryParameter("name", name);
        }
        if (type != null) {
            urlBuilder.addQueryParameter("type", type);
        }
        if (app != null) {
            urlBuilder.addQueryParameter("app", app);
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        final CacheControl.Builder builder = new CacheControl.Builder();
        builder.maxAge(30, TimeUnit.MINUTES);
        CacheControl cache = builder.build();






        final Request request = new Request.Builder().cacheControl(cache).url(urlBuilder.build()).get().build();
        final Call call = okHttpClient.newCall(request);//
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    callback.onSuccessful(string);
                } else {
                    callback.onFailure();
                    Log.d("服务器响应错误");
                }
            }
        });
    }
    public interface WebCallback {
        void onFailure();
        void onSuccessful(String data);
    }


    public static void sendData() {

    }

    public static void getSupport(String data,WebCallback webCallback) {
        String url = "https://qianji.ankio.net/api_support.json";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        urlBuilder.addQueryParameter("data", data);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Cookie",getCookie())//添加登录cookie访问，直接获取连接是不需要cookie的
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                webCallback.onFailure();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                webCallback.onSuccessful(response.body().string());
            }
        });
    }

    public static void goToLogin(BaseFragment baseFragment) {
        WebViewFragment.openUrl(baseFragment, "https://qianji.ankio.net/login");
    }
}
