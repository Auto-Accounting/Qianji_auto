package cn.dreamn.qianji_auto.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.dreamn.qianji_auto.ui.floats.AutoBills;

public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        AutoBills autoBills = new AutoBills(context);
        autoBills.show();


    }
}
