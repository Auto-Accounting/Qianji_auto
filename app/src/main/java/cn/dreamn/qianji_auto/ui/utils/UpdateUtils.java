package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.utils.task.ConsumptionTask;
import cn.dreamn.qianji_auto.ui.utils.task.LineUpTaskHelp;
import cn.dreamn.qianji_auto.ui.utils.task.RunBody;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class UpdateUtils {
    static LineUpTaskHelp lineUpTaskHelp;

    public static void checkUpdate(Context context, Update update) {
        Log.init("update");
        final int[] index = {0};
        lineUpTaskHelp = LineUpTaskHelp.getInstance();
        lineUpTaskHelp.setOnTaskListener(new LineUpTaskHelp.OnTaskListener() {
            @Override
            public void exNextTask(ConsumptionTask task) {
                task.runnable.run(context, task);
            }

            @Override
            public void noTask() {

            }
        });
        //任务监听启动
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == -1) {
                    update.onNoUpdate();
                    return;
                }
                //主线程调用
                RunBody runnable = (RunBody) msg.obj;
                ConsumptionTask task = new ConsumptionTask();
                task.taskNo = String.valueOf(index[0]++);
                task.runnable = runnable;
                lineUpTaskHelp.addTask(task);
                //任务队列加入
            }
        };
        checkAndroidUpdate(context, handler);
        checkXposedRegularUpdate(context);//不需要反馈给用户
        checkCateUpdate(context, handler);
    }

    public static void checkCateUpdate(Context context, Handler handler) {
        AutoBillWeb.getCategoryList(new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
                //检测失败
            }

            @Override
            public void onSuccessful(String data) {
                Log.i("data:" + data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                Category.getAllDataId(new Category.Regex() {
                    @Override
                    public void onGet(Bundle[] bundle) {
                        int index = 0;
                        StringBuilder log = new StringBuilder();
                        List<String> list = new ArrayList<>();
                        for (Bundle b : bundle) {
                            Log.i(b.toString());
                            String id = b.getString("dataId");
                            if (id == null || id.equals("")) continue;
                            String version = b.getString("version");
                            if (version == null || version.equals("")) continue;
                            String name = b.getString("name");
                            Log.i("dataId:" + id);
                            JSONObject jsonObject1 = jsonObject.getJSONObject(id);
                            if (jsonObject1 == null) continue;

                            int serverVer = Integer.parseInt(jsonObject1.getString("version"));
                            int localVer = Integer.parseInt(version);
                            Log.i("serverVer:" + serverVer + " localVer:" + localVer);
                            if (serverVer > localVer) {
                                //需要更新
                                index++;
                                log.append(index).append("、").append(name).append("[").append(localVer).append("→").append(serverVer).append("]").append("\n    ");
                                String updateLog = jsonObject1.getString("log");
                                if (updateLog == null || updateLog.equals(""))
                                    log.append("无更新日志");
                                else {
                                    log.append(updateLog);
                                }
                                log.append("\n\n");
                                Log.i(log.toString());
                                list.add(jsonObject1.getString("name"));
                            }
                        }
                        if (index == 0) {
                            HandlerUtil.send(handler, -1);
                            return;
                        }
                        RunBody runBody = (context1, task) -> {
                            if (context1 == null) {
                                Log.i("线程context已被销毁！");
                                return;
                            }
                            Log.i(log.toString());
                            BottomArea.msgAuto(context1, "自动分类规则更新", log.toString(), context1.getString(R.string.update_go), context1.getString(R.string.update_cancel), new BottomArea.MsgCallback() {
                                @Override
                                public void cancel() {
                                    if (lineUpTaskHelp != null)
                                        lineUpTaskHelp.exOk(task);
                                }

                                @Override
                                public void sure() {

                                    for (String l : list) {
                                        AutoBillWeb.getCategory(l, new AutoBillWeb.WebCallback() {
                                            @Override
                                            public void onFailure() {

                                            }

                                            @Override
                                            public void onSuccessful(String data) {
                                                RegularManager.restoreFromData(context, "", "category", data, code -> {
                                                });
                                            }
                                        });
                                    }
                                    if (lineUpTaskHelp != null)
                                        lineUpTaskHelp.exOk(task);
                                }
                            });
                        };
                        HandlerUtil.send(handler, runBody, 0);
                    }
                });
            }
        });
    }

    public static void checkSMSUpdate() {

    }

    public static void checkAppUpdate() {

    }

    public static void checkNoticeUpdate() {

    }

    public static void checkAndroidUpdate(Context context, Handler handler) {
        AutoBillWeb.getUpdate(new AutoBillWeb.WebCallback() {
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
                if (App.getAppVerCode() < jsonObject1.getInteger("verNum")) {
                    RunBody runBody = (context1, task) -> {
                        if (context1 == null) {
                            Log.i("线程context已被销毁！");
                            return;
                        }
                        BottomArea.msg(context1, context1.getString(R.string.new_version) + jsonObject.getString("version"), jsonObject1.getString("log"), context1.getString(R.string.update_go), context1.getString(R.string.update_cancel), new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {
                                if (lineUpTaskHelp != null)
                                    lineUpTaskHelp.exOk(task);
                            }

                            @Override
                            public void sure() {
                                Tool.goUrl(context1, jsonObject1.getString("download"));
                                if (lineUpTaskHelp != null)
                                    lineUpTaskHelp.exOk(task);
                            }
                        });
                    };
                    HandlerUtil.send(handler, runBody, 0);
                } else {
                    HandlerUtil.send(handler, -1);
                }
            }
        });
    }

    public static void checkXposedRegularUpdate(Context context) {
        if (!AppStatus.isXposed()) return;//不是Xp模式不需要
        AutoBillWeb.getCouldRegular(new AutoBillWeb.WebCallback() {
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
                    ToastUtils.show(R.string.update_wechat);
                    Log.d("微信适配文件已更新！");
                }

            }
        });
    }

    public interface Update {
        void onNoUpdate();

        void onUpdate(String title, String body, String url, int ver);
    }


}
