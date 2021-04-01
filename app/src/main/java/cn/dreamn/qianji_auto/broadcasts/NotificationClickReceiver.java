package cn.dreamn.qianji_auto.broadcasts;

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

        Log.i("通知被点击啦！！！ ");
        String bill=intent.getStringExtra("billinfo");
        if(bill==null){
            Log.i("通知没有携带数据！！！ ");
            return;
        }

        BillInfo billInfo=BillInfo.parse(bill);
        MMKV mmkv=MMKV.defaultMMKV();
        switch (mmkv.getString("notice_click_window","edit")){
            case "edit":
                SendDataToApp.showFloat(context,billInfo);
                break;
            case "record":
               SendDataToApp.goApp(context,billInfo);
                break;
            case "close":
                break;
        }

    }
}
