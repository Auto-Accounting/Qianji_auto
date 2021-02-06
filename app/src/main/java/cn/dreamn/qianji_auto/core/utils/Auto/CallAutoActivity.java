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

package cn.dreamn.qianji_auto.core.utils.Auto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xutil.display.ScreenUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import cn.dreamn.qianji_auto.core.db.Asset;
import cn.dreamn.qianji_auto.core.db.AutoBill;
import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.AutoBills;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat;
import cn.dreamn.qianji_auto.ui.floats.AutoFloatTip;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

import static cn.dreamn.qianji_auto.core.utils.Tools.goUrl;


public class CallAutoActivity {

    public  static  void call(Context context, BillInfo billInfo){
        billInfo = replaceWithSomeThing(billInfo);
        if(!billInfo.isAvaiable())return;
        Logs.i("账单获取成功，账单信息:\n" + billInfo.dump());
        AutoBills.add(billInfo);
        Tasker.add(context, billInfo);

    }

    public static void callNoAdd(Context context, BillInfo billInfo) {
        billInfo = replaceWithSomeThing(billInfo);
        if (!billInfo.isAvaiable()) return;
        Tasker.add(context, billInfo);

    }

    public static BillInfo replaceWithSomeThing(BillInfo billInfo) {

        billInfo.setTime();//设置时间
        billInfo.setAccountName(Assets.getMap(billInfo.getAccountName()));//设置一号资产
        billInfo.setAccountName2(Assets.getMap(billInfo.getAccountName2()));//设置二号资产
        billInfo.setRemark(Remark.getRemark(billInfo.getShopAccount(), billInfo.getShopRemark()));//设置备注
        billInfo.setCateName(Category.getCategory(billInfo.getShopAccount(), billInfo.getShopRemark(), BillInfo.getTypeName(billInfo.getType())));//设置自动分类
        billInfo.setBookName(BookNames.getDefault());//设置自动记账的账本名
        return billInfo;
    }


    //角标超时
    public static String getTimeout() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("auto_timeout", "10");
    }

    //角标检查
    public static Boolean getCheck() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getBoolean("auto_check",true);
    }
    //显示角标
    public static void showTip(Context context,BillInfo billInfo){
        try {
            Logs.d("唤起自动记账面板角标");
            AutoFloatTip autoFloatTip=new AutoFloatTip(context);
            autoFloatTip.setData(billInfo);
            autoFloatTip.setWindowManagerParams( ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight()/2-100,700,200);
            autoFloatTip.show();
        }catch (Exception e){
            Logs.i("请授予悬浮窗权限！"+e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");
            Caches.AddOrUpdate("float_lock","false");
        }
    }
    //显示悬浮记账
    public static void showFloat(Context context,BillInfo billInfo){
        try {
            Logs.d("唤起自动记账面板");
            AutoFloat autoFloat=new AutoFloat(context);
            autoFloat.setData(billInfo);
            autoFloat.setWindowManagerParams(0,0, ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight());
            autoFloat.show();
        }catch (Exception e){
            Logs.i("请授予悬浮窗权限！"+e.toString());
            ToastUtils.toast("请授予悬浮窗权限！");
            Caches.AddOrUpdate("float_lock","false");
        }

    }

    public static void goQianji(Context context,BillInfo billInfo){
        Caches.update("float_lock","false");
        goUrl(context,billInfo.getQianJi().trim());
    }


    public static void jump(Context context,BillInfo billInfo){
        if(getCheck()){
            showFloat(context,billInfo);
        }else{
            goQianji( context, billInfo);
        }
    }


    public static void run(Context context, BillInfo billInfo) {
        MMKV mmkv = MMKV.defaultMMKV();

        Logs.i("悬浮窗弹出，账单初始信息：" + billInfo.dump());
        if(billInfo.getIsSilent()){
            if(mmkv.getBoolean("autoIncome",false)){
                goQianji(context,billInfo);
            }else{
                //通知处理
                Tools.sendNotify(context,"记账提醒","￥"+billInfo.getMoney()+" - "+billInfo.getRemark(),billInfo.getQianJi());
            }
            return;
        }else{
            if(mmkv.getBoolean("autoPay",false)){
                goQianji(context,billInfo);
                return;
            }
        }

        if(getTimeout().equals("0")){
            jump(context,billInfo);
        }else{
            showTip(context,billInfo);
        }

    }
}
