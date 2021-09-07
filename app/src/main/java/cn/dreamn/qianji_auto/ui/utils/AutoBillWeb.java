package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoBillWeb {

    private static final String branch = "dev";

    private static final String baseUrl = "https://cdn.jsdelivr.net/gh/dreamncn/Qianji_auto@" + branch;

    public static void getCategoryList(WebCallback callback) {
        String url = baseUrl + "/cloud/category/list.json";
        OkHttpClient okHttpClient = new OkHttpClient();
        final CacheControl.Builder builder = new CacheControl.Builder();
        builder.maxAge(30, TimeUnit.MINUTES);
        CacheControl cache = builder.build();
        final Request request = new Request.Builder().cacheControl(cache).url(url).build();
        final Call call = okHttpClient.newCall(request);//
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public static void getCategory(String name, WebCallback callback) {
        String url = baseUrl + "/cloud/category/data/" + name + ".json";
        OkHttpClient okHttpClient = new OkHttpClient();
        final CacheControl.Builder builder = new CacheControl.Builder();
        builder.maxAge(30, TimeUnit.MINUTES);
        CacheControl cache = builder.build();
        final Request request = new Request.Builder().cacheControl(cache).url(url).build();
        final Call call = okHttpClient.newCall(request);//
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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


    public static void getDataList(String type, WebCallback callback) {
        //短信规则可以共用
        //通知规则可以共用
        //app规则不行

        String addUrl = "/reg/" + type + "/";
        if (type.equals("app")) {
            addUrl += AppStatus.getActiveMode() + "/";
        }
        addUrl += "list.json";

        String url = baseUrl + addUrl;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();

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

    public static void getData(String type, String app, WebCallback callback) {
        //短信规则可以共用
        //通知规则可以共用
        //app规则不行

        String addUrl = "/reg/" + type + "/";
        if (type.equals("app")) {
            addUrl += AppStatus.getActiveMode() + "/";
        }
        addUrl += "/data/" + app + ".json";

        String url = baseUrl + addUrl;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();

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

    public static void getUpdate(WebCallback callback) {
        String url = baseUrl + "/cloud/version.json";
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

    public static void update(Context context) {
        update(context, null);
    }

    public static void update(Context context, CallbackWith callback) {
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                //新版本更新
                JSONObject jsonObject = (JSONObject) msg.obj;


                BottomArea.msg(context, context.getString(R.string.new_version) + jsonObject.getString("version"), jsonObject.getString("log"), context.getString(R.string.update_go), context.getString(R.string.update_cancel), new BottomArea.MsgCallback() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void sure() {
                        Tool.goUrl(context, jsonObject.getString("download"));
                    }
                });
            }
        };
        AutoBillWeb.getUpdate(new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccessful(String data) {
                //
                JSONObject jsonObject = JSONArray.parseObject(data);
                MMKV mmkv = MMKV.defaultMMKV();
                String channel = mmkv.getString("version_channel", "stable");
                JSONObject jsonObject1 = jsonObject.getJSONObject(channel);
                if (App.getAppVerCode() < jsonObject1.getInteger("verNum")) {
                    Message message = new Message();
                    message.obj = jsonObject1;
                    mHandler.sendMessage(message);
                } else {
                    if (callback != null) {
                        callback.onUpdateEnd();
                    }
                }
                Log.m("更新数据：" + data);
            }
        });
    }

    public interface CallbackWith {
        void onUpdateEnd();
    }

    public interface WebCallback {
        void onFailure();

        void onSuccessful(String data);
    }

}
