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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.database.Helper.Caches;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat;
import cn.dreamn.qianji_auto.ui.floats.AutoFloatTip;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


public class SendDataToApp {



    public static void call(Context context, BillInfo billInfo) {
        Log.i(billInfo.toString());
        Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
               if(msg.what==0){
                   Caches.AddOrUpdate(billInfo.getRawMd5(), "receive");
                   billInfo.setTime();
                   BillReplace.addMoreInfo(this,billInfo);
               }else if(msg.what==2){
                   BillInfo billInfo2=(BillInfo)msg.obj;
                   BillReplace.replaceRemark(billInfo2);
                   Log.i("账单已捕获，账单信息:\n" + billInfo2.dump());
                   if (!billInfo2.isAvaiable()) return;
                   AutoBills.add(billInfo2);
                   Log.i("账单已添加至账单列表。");
                   run(context,billInfo2);
               }
            }
        };
        checkCache(mHandler,billInfo);
    }

    public static void checkCache(Handler mHandler,BillInfo billInfo){

        String md5= billInfo.getRawMd5();
        Caches.getCacheData(md5, "", cache -> {
            if(!cache.equals("")){
                Log.i("出现重复账单，该账单不计入。\n" + billInfo.dump());
                mHandler.sendEmptyMessage(1);
                return;
            }
            mHandler.sendEmptyMessage(0);
        });
    }


    public static void callNoAdd(Context context, BillInfo billInfo) {
     //   Log.m(billInfo.dump());
        Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
               if(msg.what==2){
                    BillInfo billInfo2=(BillInfo)msg.obj;
                    BillReplace.replaceRemark(billInfo2);
                    if (!billInfo2.isAvaiable()) return;
                   showFloatByAlert(context, billInfo);
                }
            }
        };
        BillReplace.addMoreInfo(mHandler,billInfo);

    }


    //角标超时
    public static String getTimeout() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("auto_timeout", "10");
    }


    //显示角标
    public static void showTip(Context context, BillInfo billInfo) {
        try {
            Log.i("唤起自动记账面板角标");
            AutoFloatTip autoFloatTip = new AutoFloatTip(context);
            autoFloatTip.setData(billInfo);

            //自适应大小
            String str = BillTools.getCustomBill(billInfo);

            int minLength = str.length() * 20;

            autoFloatTip.setWindowManagerParams(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context) / 2 - 100, 350 + minLength, 150);

            autoFloatTip.show();
        } catch (Exception e) {
            Log.i("请授予悬浮窗权限！" + e.toString());
            ToastUtils.show("请授予悬浮窗权限！");
        }
    }

    public static void showFloatByAlert(Context context, BillInfo billInfo) {
        MMKV mmkv = MMKV.defaultMMKV();

        if (!mmkv.getBoolean("auto_style", true)) {
            Log.i("唤起自动钱迹分类面板");
            billInfo.setCateChoose(true);
            goApp(context, billInfo);
            return;
        }
        try {

            AutoFloat autoFloat2 = new AutoFloat(context);
            autoFloat2.setData(billInfo);
            autoFloat2.show();


        } catch (Exception e) {
            Log.i("请授予悬浮窗权限！" + e.toString());
            ToastUtils.show("请授予悬浮窗权限！");

        }
    }


    public static void goApp(Context context, BillInfo billInfo) {
        Log.i("前往记账app");
        Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i("前往记账app2");
                AppManager.sendToApp(context, billInfo);
            }
        };
        Category.getCategory(billInfo, cate -> {
            if (cate.equals("NotFound")) {
                MMKV mmkv = MMKV.defaultMMKV();
                if (mmkv.getBoolean("auto_sort", false)) {
                    Category.setCateJs(billInfo, billInfo.getCateName());
                }
            }
            mHandler.sendEmptyMessage(0);

        });
    }






    public static void run(Context context, BillInfo billInfo) {
        Log.i("唤起自动记账面板...");


        MMKV mmkv = MMKV.defaultMMKV();

        Log.i("记账请求发起，账单初始信息：\n" + billInfo.dump());
        if (billInfo.getIsSilent()) {
            Log.i("账单为 后台交易");
            if (mmkv.getBoolean("autoIncome", false)) {
                Log.i("全自动模式->直接对钱迹发起请求");
                goApp(context, billInfo);
            } else {
                Log.i("半自动模式->发出记账通知");
                //通知处理
                Tool.notice(context, "记账提醒", "￥" + billInfo.getMoney() + " - " + billInfo.getRemark(), billInfo);
            }
            return;
        } else {
            Log.i("账单为 前台交易");
            if (mmkv.getBoolean("autoPay", false)) {
                Log.i("全自动模式->直接对钱迹发起请求");
                goApp(context, billInfo);
                return;
            }
        }
        Log.i("半自动模式 -> 下一步");
        if (getTimeout().equals("0")) {
            end(context,billInfo);
        } else {
            Log.i("存在超时，弹出超时面板");
            showTip(context, billInfo);
        }

    }

    public static void end(Context context, BillInfo billInfo){
        MMKV mmkv = MMKV.defaultMMKV();
        switch (mmkv.getString("end_window","edit")){
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
