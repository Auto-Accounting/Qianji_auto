package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.Objects;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoBillWeb {

    public static void getWebData(String url, WebCallback callback) {
        MMKV mmkv = MMKV.defaultMMKV();
        String baseUrl;
        String baseUrlName = mmkv.getString("baseUrlName", "ghProxy");
        switch (Objects.requireNonNull(baseUrlName)) {
            case "jsDelivr":
                baseUrl = "https://cdn.jsdelivr.net/gh/dreamncn/AutoResource@master";
                break;
            case "Raw":
                baseUrl = "https://raw.githubusercontent.com/dreamncn/AutoResource/master";
                break;
            case "FastGit":
                baseUrl = "https://raw.fastgit.org/dreamncn/AutoResource/master";
                break;
            case "ghProxy":
                baseUrl = "https://ghproxy.com/https://raw.githubusercontent.com/dreamncn/AutoResource/master";
                break;
            default:
                baseUrl = mmkv.getString("baseUrl", "https://cdn.jsdelivr.net/gh/dreamncn/AutoResource@master");
        }

        String fullUrl = baseUrl + url;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(fullUrl).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure();
                e.printStackTrace();
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String string = response.body().string();

                Log.m("网页返回结果->", "路径：" + url + "\nBase:" + baseUrl + "\nResult:" + string + "\n");
                if (response.code() == 200 && response.isSuccessful()) {


                    callback.onSuccessful(string);
                } else {
                    callback.onFailure();
                    Log.d("服务器响应错误" + string);
                }
                call.cancel();
            }
        });
    }

    public static void getCouldRegular(WebCallback callback) {
        String url = "/regular.json";
        getWebData(url, callback);
    }

    public static void getCategoryList(WebCallback callback) {
        String url = "/category/list.json";
        getWebData(url, callback);
    }

    public static void getCategory(String name, WebCallback callback) {
        String url = "/category/data/" + name + ".json";
        getWebData(url, callback);
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
        String url = addUrl;
        getWebData(url, callback);
    }

    public static void getDataListByApp(String type, String app, WebCallback callback) {
        //短信规则可以共用
        //通知规则可以共用
        //app规则不行
        String addUrl = "/reg/" + type + "/";
        if (type.equals("app")) {
            addUrl += AppStatus.getActiveMode() + "/";
        }
        addUrl += "/data/" + app + "/list.json";
        String url = addUrl;
        getWebData(url, callback);
    }

    public static void getData(String type, String app, String name, WebCallback callback) {
        //短信规则可以共用
        //通知规则可以共用
        //app规则不行

        String addUrl = "/reg/" + type + "/";
        if (type.equals("app")) {
            addUrl += AppStatus.getActiveMode() + "/";
        }
        addUrl += "/data/" + app + "/" + name + ".json";

        String url = addUrl;

        getWebData(url, callback);
    }

    public static void getUpdate(WebCallback callback) {
        String url = "/version.json";
        getWebData(url, callback);
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
                // Log.m("更新数据：" + data);
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
