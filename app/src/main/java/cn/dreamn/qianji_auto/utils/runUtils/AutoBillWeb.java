package cn.dreamn.qianji_auto.utils.runUtils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoBillWeb {

    public static void getWebData(String url, Context mContext, WebCallback callback) {
        if (mContext == null) {
            callback.onFailure();
            return;
        }
        //web访问增加缓存
        ACache mCache = ACache.get(mContext);
        mCache.clear();
      /*  if (!AppStatus.isDebug()) {
            String response = mCache.getAsString(url);
            if (response != null) {
                callback.onSuccessful(response);
                return;
            }
        } else {
            mCache.clear();
            //清空缓存
        }*/
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

        if (!fullUrl.startsWith("http")) {
            ToastUtils.show("加速源错误！");
            return;
        }

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

                Log.i("自动记账:Web", "Path：" + url + "\nBase:" + baseUrl + "\nResult:" + string + "\n");
                if (response.code() == 200 && response.isSuccessful()) {
                    mCache.put(url, string, ACache.TIME_HOUR);
                    callback.onSuccessful(string);
                } else {
                    callback.onFailure();
                    Log.d("自动记账:Web", "Server Error:\n" + string);
                }
                call.cancel();
            }
        });
    }

    public static void getCouldRegular(Context mContext, WebCallback callback) {
        String url = "/xposed/com.tencent.mm.json";
        getWebData(url, mContext, callback);
    }


    public static void getList(Context mContext, WebCallback callback) {
        String addUrl = "/regulars/list.json";
        getWebData(addUrl, mContext, callback);
    }

    public static void getById(String dataId, Context mContext, WebCallback callback) {

        String addUrl = "/regulars/" + dataId + ".json";
        getWebData(addUrl, mContext, callback);
    }


    public static void getUpdate(Context mContext, WebCallback callback) {
        String url = "/version/version.json";
        getWebData(url, mContext, callback);
    }


    public interface WebCallback {
        void onFailure();

        void onSuccessful(String data);
    }

}
