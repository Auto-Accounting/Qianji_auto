package cn.dreamn.qianji_auto.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        //TODO 点击通知弹出待记账列表
        String bill=intent.getStringExtra("billinfo");
        if(bill==null){
            Log.i("通知没有携带数据！！！ ");
            return;
        }
        Log.i("通知数据：" + bill);
        BillInfo billInfo=BillInfo.parse(bill);
        MMKV mmkv=MMKV.defaultMMKV();
        switch (mmkv.getString("notice_click_window","edit")){
            case "edit":
                SendDataToApp.showFloatByAlert(context, billInfo);
                break;
            case "record":
               SendDataToApp.goApp(context,billInfo);
                break;
            case "close":
                break;
        }

    }
}
