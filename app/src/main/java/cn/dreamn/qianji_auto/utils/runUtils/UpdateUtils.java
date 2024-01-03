package cn.dreamn.qianji_auto.utils.runUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.Toaster;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;


public class UpdateUtils {

    public static void checkUpdate(Context context, Update update) {

        //任务监听启动
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == -1) {
                    update.onNoUpdate();
                    return;
                }
                Runnable runnable = (Runnable) msg.obj;
                if (runnable == null) return;
                runnable.run();
            }
        };
        try {
            checkAndroidUpdate(context, handler);
            checkXposedRegularUpdate(context);//不需要反馈给用户

        } catch (Throwable e) {
            Log.i("网络访问发生了错误！" + e.toString());
            e.printStackTrace();
        }
    }


    public static void checkAndroidUpdate(Context context, Handler handler) {
        AutoBillWeb.getUpdate(context, new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
            }

            @Override
            public void onSuccessful(String data) {
                JSONObject jsonObject = JSONArray.parseObject(data);
                MMKV mmkv = MMKV.defaultMMKV();

                String channel = mmkv.getString("version_channel", "stable");
                channel = "beta";//临时强制更新
                JSONObject jsonObject1 = jsonObject.getJSONObject(channel);
                //增加下次不再检测
                if (App.getAppVerCode() < jsonObject1.getInteger("verNum") && mmkv.getInt("updateVersion", 0) != jsonObject1.getInteger("verNum")) {
                    if (context == null) {
                        Log.i("线程context已被销毁！");
                        return;
                    }
                    Runnable runnable = () -> BottomArea.msgAuto(context, context.getString(R.string.new_version) + jsonObject1.getString("version"), jsonObject1.getString("log"), context.getString(R.string.update_go), context.getString(R.string.update_cancel), new BottomArea.MsgCallback() {
                        @Override
                        public void cancel() {
                            MMKV mmkv1 = MMKV.defaultMMKV();
                            mmkv1.encode("updateVersion", jsonObject1.getInteger("verNum"));
                        }

                        @Override
                        public void sure() {
                            Tool.goUrl(context, jsonObject1.getString("download"));

                        }
                    });
                    HandlerUtil.send(handler, runnable, 0);
                } else {
                    HandlerUtil.send(handler, -1);
                }
            }
        });
    }

    public static void checkXposedRegularUpdate(Context context) {
        if (!AppStatus.isXposed()) return;//不是Xp模式不需要
        AutoBillWeb.getCouldRegular(context, new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
                Log.d("尝试更新微信适配文件失败");
            }

            @Override
            public void onSuccessful(String data) {
                Log.d(data);
                SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, "wechat", Context.MODE_PRIVATE);
                if (!sharedPreferences.getString("red", "").contains(data)) {
                    sharedPreferences.edit().putString("red", data).apply();
                    Toaster.show(R.string.update_wechat);
                    Log.d("微信适配文件已更新！");
                }

            }
        });
    }

    public interface Update {
        void onNoUpdate();
    }


}
