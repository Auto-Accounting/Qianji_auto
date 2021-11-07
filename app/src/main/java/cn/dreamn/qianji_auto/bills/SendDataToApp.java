/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.bills;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat;
import cn.dreamn.qianji_auto.ui.floats.AutoFloatTip;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


public class SendDataToApp {

    private static final String TAG = "记账流程";

    public static void call(Context context, BillInfo billInfo) {
        Log.i(TAG, "当前进入记账流程");
        Log.i("原始数据：" + billInfo.toString());

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 2) {
                    BillInfo billInfo2 = (BillInfo) msg.obj;
                    BillReplace.replaceRemark(billInfo2);
                    Log.i(TAG, "账单已捕获，账单信息:\n" + billInfo2.dump());
                    if (!billInfo2.isAvaiable()) return;
                    AutoBills.add(billInfo2);
                    Log.i(TAG, "账单已添加至账单列表。");
                    run(context, billInfo2);
                }
            }
        };
        Log.i(TAG, "账单信息更新补充");
        BillReplace.addMoreInfo(mHandler, billInfo);
    }




    public static void callNoAdd(Context context, BillInfo billInfo) {
        //   Log.i(billInfo.dump());
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 2) {
                    BillInfo billInfo2 = (BillInfo) msg.obj;
                    BillReplace.replaceRemark(billInfo2);
                    if (!billInfo2.isAvaiable()) return;
                    showFloatByAlert(context, billInfo2);
                }
            }
        };
        BillReplace.addMoreInfo(mHandler,billInfo);

    }


    //角标超时
    public static Integer getTimeout() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getInt("auto_timeout", 10);
    }


    //显示角标
    public static void showTip(Context context, BillInfo billInfo) {
        try {
            Log.i(TAG, "唤起自动记账面板角标");
            AutoFloatTip autoFloatTip = new AutoFloatTip(context);
            autoFloatTip.setData(billInfo);

            //自适应大小
            String str = BillTools.getCustomBill(billInfo);

            int minLength = str.length() * 20;

            autoFloatTip.setWindowManagerParams(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context) / 2 - 100, 380 + minLength, 150);

            autoFloatTip.show();
        } catch (Exception e) {
            Log.i(TAG, "请授予悬浮窗权限！" + e.toString());
            ToastUtils.show(R.string.float_tip);
            PermissionUtils permissionUtils = new PermissionUtils(context);
            permissionUtils.grant(PermissionUtils.Float);
        }
    }

    public static void showFloatByAlert(Context context, BillInfo billInfo) {

        try {

            AutoFloat autoFloat2 = new AutoFloat(context);
            autoFloat2.setData(billInfo);
            autoFloat2.show();


        } catch (Exception e) {
            Log.i(TAG, "请授予悬浮窗权限！" + e.toString());
            ToastUtils.show(R.string.float_tip);
            PermissionUtils permissionUtils = new PermissionUtils(context);
            permissionUtils.grant(PermissionUtils.Float);

        }
    }


    public static void goApp(Context context, BillInfo billInfo) {
        TaskThread.onThread(() -> {
            Db.db.AutoBillDao().update(billInfo.toString());
        });
        MMKV mmkv = MMKV.defaultMMKV();
        if (!mmkv.getBoolean("auto_style", true)) {
            Log.i(TAG, "唤起钱迹分类面板");
            billInfo.setCateChoose(true);//选择钱迹分类
            AppManager.sendToApp(context, billInfo);
            return;
        }
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i(TAG, "前往记账app2");
                AppManager.sendToApp(context, billInfo);

            }
        };

        TaskThread.onThread(() -> {
            RegularCenter
                    .getInstance("category")
                    .run(billInfo, null, obj -> {
                        String cate = (String) obj;
                        Log.i("再次获取cate:" + cate + "数据： " + billInfo.toString());
                        if (cate.equals("NotFound")) {
                            if (mmkv.getBoolean("auto_sort", false)) {
                                RegularCenter.setCateJs(billInfo, billInfo.getCateName());
                            }
                        }
                        int id = billInfo.getId();

                        if (id != 0) {
                            Db.db.AutoBillDao().update(id, billInfo.toString());
                        }
                        HandlerUtil.send(mHandler, 0);
                    });
        });

    }

    public static void run(Context context1, BillInfo billInfo) {

        if (billInfo.isAuto()) {
            Log.i(TAG, "自动记录账单...");
            goApp(context1, billInfo);
            return;
        }
        Log.i(TAG, "唤起自动记账面板...");
        MMKV mmkv = MMKV.defaultMMKV();
        Log.i(TAG, "记账请求发起，账单初始信息：\n" + billInfo.dump());

        if (isReception(context1)) {
            Log.i(TAG, "当前处于锁屏状态");
            if (mmkv.getBoolean("autoIncome", false)) {
                Log.i(TAG, "全自动模式->直接对钱迹发起请求");
                goApp(context1, billInfo);
            } else {
                Log.i(TAG, "半自动模式->发出记账通知");
                //通知处理
                //   Tool.notice(context1, context1.getString(R.string.notice_name), "￥" + billInfo.getMoney() + " - " + billInfo.getRemark(), billInfo);
            }
        } else {
            Log.i(TAG, "当前处于前台状态");
            if (mmkv.getBoolean("autoPay", false)) {
                Log.i(TAG, "全自动模式->直接对钱迹发起请求");
                goApp(context1, billInfo);

                return;
            }
            Log.i(TAG, "半自动模式 -> 下一步");
            if (getTimeout() == 0) {
                end(context1, billInfo);
            } else {
                Log.i("存在超时，弹出超时面板");
                showTip(context1, billInfo);
            }
        }

    }

    /**
     * 如果flag为true，表示有两种状态：a、屏幕是黑的 b、目前正处于解锁状态 。
     * 如果flag为false，表示目前未锁屏
     *
     * @param context
     * @return
     */
    private static boolean isReception(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();

    }

    public static void end(Context context, BillInfo billInfo) {
        MMKV mmkv = MMKV.defaultMMKV();
        switch (mmkv.getString("end_window", "edit")) {
            case "edit":
                showFloatByAlert(context, billInfo);
                break;
            case "record":
                goApp(context, billInfo);
                break;
            case "close":

                break;
        }
    }
}
