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

package cn.dreamn.qianji_auto.core.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xutil.display.ScreenUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import cn.dreamn.qianji_auto.core.db.Helper.Assets;
import cn.dreamn.qianji_auto.core.db.Helper.AutoBills;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.core.db.Helper.Category;
import cn.dreamn.qianji_auto.core.db.Table.Cache;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat;
import cn.dreamn.qianji_auto.ui.floats.AutoFloatTip;
import cn.dreamn.qianji_auto.utils.tools.Logs;

import static cn.dreamn.qianji_auto.core.utils.Tools.goUrl;


public class CallAutoActivity {

    public static void call(Context context, BillInfo billInfo) {
        String cache = Caches.getCacheData("lastTime");

        if (cache.equals("")) cache = "0";
        long time = Long.parseLong(cache);
        long t = System.currentTimeMillis() - time;
        t = t / 1000;

        replaceWithSomeThing(billInfo);
        if (Caches.getCacheData("lastBill").equals(billInfo.toString()) && t < 2) {
            Logs.i("出现重复账单，该账单不计入。\n" + billInfo.dump());
            return;
        }
        Caches.AddOrUpdate("lastTime", String.valueOf(System.currentTimeMillis()));

        //重复账单过滤
        //多笔记账延时
        billInfo.setTime();//设置时间
        Logs.i("账单已捕获，账单信息:\n" + billInfo.dump());
        if (!billInfo.isAvaiable()) return;
        Caches.AddOrUpdate("lastBill", billInfo.toString());
        AutoBills.add(billInfo);
        Logs.i("账单已添加至账单列表。");
        run(context, billInfo);
    }

    public static void callNoAdd(Context context, BillInfo billInfo) {
        replaceWithSomeThing(billInfo);
        if (!billInfo.isAvaiable()) return;
        // Tasker.add(context, billInfo);
        run(context, billInfo);

    }

    public static void callNoAdd(Context context, BillInfo billInfo, boolean change) {
        replaceWithSomeThing(billInfo);
        if (!billInfo.isAvaiable()) return;
        // Tasker.add(context, billInfo);
        //  run(context, billInfo);
        if (change) {
            showFloat(context, billInfo);
        }

    }

    public static void replaceWithSomeThing(BillInfo billInfo) {


        billInfo.setAccountName(Assets.getMap(BillTools.dealPayTool(billInfo.getAccountName())));//设置一号资产
        billInfo.setAccountName2(Assets.getMap(BillTools.dealPayTool(billInfo.getAccountName2())));//设置二号资产

        if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals("")) {
            billInfo.setShopRemark(billInfo.getSource());
        } else if (billInfo.getShopAccount() == null || billInfo.getShopAccount().equals("")) {
            billInfo.setShopAccount(billInfo.getSource());
        }

        billInfo.setRemark(Remark.getRemark(billInfo));//设置备注

        String cate = Category.getCategory(billInfo);
        if (cate.equals("NotFind")) {
            billInfo.setCateName("其它");//设置自动分类

        } else {
            billInfo.setCateName(cate);//设置自动分类
        }

        billInfo.setBookName(BookNames.getDefault());//设置自动记账的账本名


    }


    //角标超时
    public static String getTimeout() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("auto_timeout", "10");
    }


    //显示角标
    public static void showTip(Context context, BillInfo billInfo) {


        try {
            Logs.d("唤起自动记账面板角标");
            AutoFloatTip autoFloatTip = new AutoFloatTip(context);
            autoFloatTip.setData(billInfo);

            //自适应大小
            String str = BillTools.getCustomBill(billInfo);

            int minLength = str.length() * 20;

            autoFloatTip.setWindowManagerParams(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() / 2 - 100, 350 + minLength, 150);
            autoFloatTip.show();
        } catch (Exception e) {
            Logs.i("请授予悬浮窗权限！" + e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");
            Caches.AddOrUpdate("float_lock", "false");
        }
    }

    //显示悬浮记账
    public static void showFloat(Context context, BillInfo billInfo) {
        MMKV mmkv = MMKV.defaultMMKV();

        if (!mmkv.getBoolean("auto_style", true)) {
            Logs.i("唤起自动钱迹分类面板");
            billInfo.setCateChoose(true);
            goQianji(context, billInfo);
            return;
        }
        try {
            Logs.d("唤起自动记账面板");
            AutoFloat autoFloat = new AutoFloat(context);
            autoFloat.setData(billInfo);
            if (ScreenUtils.getScreenWidth() > ScreenUtils.getScreenHeight()) {
                autoFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenHeight() - 100, ScreenUtils.getScreenWidth());
            } else {
                autoFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() - 100);
            }
            autoFloat.show();
        } catch (Exception e) {
            Logs.i("请授予悬浮窗权限！" + e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");

        }

    }

    public static void goQianji(Context context, BillInfo billInfo) {

        new Handler(Looper.getMainLooper()).post(() -> {

            Caches.AddOrUpdate("float_lock", "false");
            String cache = Caches.getCacheData("float_time");
            if (!cache.equals("")) {
                long time = Long.parseLong(cache);
                long t = System.currentTimeMillis() - time;
                t = t / 1000;

                if (t < 11) {
                    if (!Caches.getCacheData("show_tip").equals("true")) {
                        Toast.makeText(context, "距离上一次发起请求时间为" + t + "s,稍后为您自动记账。", Toast.LENGTH_LONG).show();
                        //    XToastUtils.info("距离上一次发起请求时间为" + t + "s,稍后为您自动记账。");
                        Caches.AddOrUpdate("show_tip", "true");
                    }

                    //延迟时间
                    new Handler().postDelayed(() -> {
                        goQianji(context, billInfo);
                    }, t * 100);
                    return;
                }
            }
            Caches.AddOrUpdate("show_tip", "false");
            Caches.AddOrUpdate("float_time", String.valueOf(System.currentTimeMillis()));
            Logs.i("对钱迹发起请求 :" + billInfo.getQianJi().trim());

            try {
                goUrl(context, billInfo.getQianJi().trim());
                Toast.makeText(context, "记账成功！金额￥" + billInfo.getMoney() + "。", Toast.LENGTH_LONG).show();

                //  XToastUtils.success("自动记账成功！金额￥"+billInfo.getMoney()+"。");
            } catch (Exception e) {
                Logs.i(e.toString());
            }

        });


    }




    public static void run(Context context, BillInfo billInfo) {
        Logs.i("唤起自动记账面板...");
        /*Cache cache = Caches.getOne("float_lock", "0");

        Logs.d("Qianji_check", "记账检查...");
        if (cache != null && cache.cacheData.equals("true")) {
            Logs.d("Qianji_check", "记账已锁定...退出中");
            Logs.i("自动记账面板锁定，退出记账。");
            return;
        }*/
//Logs.i("唤起自动记账面板...");

        MMKV mmkv = MMKV.defaultMMKV();

        Logs.i("记账请求发起，账单初始信息：\n" + billInfo.dump());
        if (billInfo.getIsSilent()) {
            Logs.i("账单为 后台交易");
            if (mmkv.getBoolean("autoIncome", false)) {
                Logs.i("全自动模式->直接对钱迹发起请求");
                goQianji(context, billInfo);
            } else {
                Logs.i("半自动模式->发出记账通知");
                //通知处理
                Tools.sendNotify(context, "记账提醒", "￥" + billInfo.getMoney() + " - " + billInfo.getRemark(), billInfo.getQianJi());
            }
            return;
        } else {
            Logs.i("账单为 前台交易");
            if (mmkv.getBoolean("autoPay", false)) {
                Logs.i("全自动模式->直接对钱迹发起请求");
                goQianji(context, billInfo);
                return;
            }
        }
        Caches.AddOrUpdate("float_lock", "true");
        Logs.i("半自动模式 -> 下一步");
        if (getTimeout().equals("0")) {
            // MMKV mmkv=MMKV.defaultMMKV();
            if (!mmkv.getBoolean("auto_float_end_double", true)) {
                Logs.i("倒计时结束直接发起请求");
                //这是倒计时结束
                CallAutoActivity.goQianji(context, billInfo);
                return;
            }
            Logs.i("直接弹出悬浮窗");
            showFloat(context, billInfo);
        } else {
            Logs.i("存在超时，弹出超时面板");
            showTip(context, billInfo);
        }

    }
}
