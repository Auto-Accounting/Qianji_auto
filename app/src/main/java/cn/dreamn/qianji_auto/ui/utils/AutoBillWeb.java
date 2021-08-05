package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
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

    public static void sendLog(String log, WebCallback callback) {
        String url = "https://qianji.ankio.net/api_log.json";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        urlBuilder.addQueryParameter("log", B64.encode(log));
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

    public static void getUpdate(WebCallback callback) {
        String url = "https://cdn.jsdelivr.net/gh/dreamncn/Qianji_auto@dev/version.json";

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

    public static void update(Context context) {
        update(context, null);
    }

    public static void update(Context context, CallbackWith callback) {
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                //新版本更新
                JSONObject jsonObject = (JSONObject) msg.obj;


                LayoutInflater factory = LayoutInflater.from(context);
                final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
                dialog.title(null, "新版本 " + jsonObject.getString("version"));

                TextView textView_body = textEntryView.findViewById(R.id.textView_body);
                textView_body.setText(jsonObject.getString("log"));


                Button button_next = textEntryView.findViewById(R.id.button_next);
                Button button_last = textEntryView.findViewById(R.id.button_last);

                button_next.setOnClickListener(v -> {

                    Tool.goUrl(context, jsonObject.getString("download"));
                    dialog.dismiss();
                });

                button_last.setOnClickListener(v -> {

                    dialog.dismiss();
                });

                DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                        false, true, false, false);

                dialog.cornerRadius(15f, null);
                dialog.show();
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


    public static void httpSend(Context context, BaseFragment baseFragment, String support, String data) {
        LoadingDialog dialog = new LoadingDialog(context, "正在提交，请稍候...");
        dialog.show();
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                dialog.close();
                switch (msg.what) {
                    case -1:
                        ToastUtils.show("访问服务器失败！");
                        break;
                    case 1:
                        AutoBillWeb.goToLogin(baseFragment);
                        break;
                    case 2:
                        ToastUtils.show((String) msg.obj);
                        break;
                    case 0:
                        ToastUtils.show((String) msg.obj);
                        break;
                }
            }
        };
        Task.onThread(() -> {
            AutoBillWeb.sendData(support, data, new AutoBillWeb.WebCallback() {
                @Override
                public void onFailure() {
                    mHandler.sendEmptyMessage(-1);
                }

                @Override
                public void onSuccessful(String data) {
                    Log.m("响应数据：" + data);
                    com.alibaba.fastjson.JSONObject jsonObject1 = JSONObject.parseObject(data);
                    switch (jsonObject1.getInteger("code")) {
                        case 402:
                            mHandler.sendEmptyMessage(1);
                            break;
                        case 403:
                        case 404:
                            Message message = new Message();
                            message.what = 2;
                            message.obj = jsonObject1.getString("msg");
                            mHandler.sendMessage(message);
                            break;
                        case 0:
                            Message message1 = new Message();
                            message1.what = 0;
                            message1.obj = jsonObject1.getString("msg");
                            mHandler.sendMessage(message1);
                            break;
                    }
                }
            });
        });

    }

    public static void sendData(String support, String data, WebCallback webCallback) {

        String url = "https://qianji.ankio.net/api_" + support + ".json";


        Log.d(data);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        urlBuilder.addQueryParameter("data", data);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Cookie", getCookie())//添加登录cookie访问，直接获取连接是不需要cookie的
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
