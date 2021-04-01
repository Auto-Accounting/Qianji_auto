package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.Helper.Caches;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class QianJi implements IApp {
    private static QianJi qianJi;

    public static QianJi getInstance(){
        if(qianJi==null)
            qianJi=new QianJi();
        return qianJi;
    }
    @Override
    public String getPackPageName() {
        return "com.mutangtech.qianji";
    }

    @Override
    public String getAppName() {
        return "钱迹";
    }



    @Override
    public int getAppIcon() {
        return R.drawable.logo_qianji;
    }

    @Override
    public void sendToApp(Context context,BillInfo billInfo) {
        Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==0){
                    Caches.AddOrUpdate("show_tip", "false");
                    Caches.AddOrUpdate("float_time", String.valueOf(System.currentTimeMillis()));
                    Tool.goUrl(context, getQianJi(billInfo));
                    Toast.makeText(context, "记账成功！金额￥" + billInfo.getMoney() + "。", Toast.LENGTH_LONG).show();
                }
            }
        };
        delay(context,mHandler);
    }

    @Override
    public void asyncData() {

    }

    private void delay(Context context,Handler mHandler){
        Task.onMain(()->{
            Caches.getCacheData("float_time","",cache -> {
                if (!cache.equals("")) {
                    long time = Long.parseLong(cache);
                    long t = System.currentTimeMillis() - time;
                    t = t / 1000;
                    if (t < 11) {
                        long finalT = t;
                        Caches.getCacheData("show_tip","false", cache1 -> {
                            if(!cache1.equals("true")){
                                Toast.makeText(context, "距离上一次发起请求时间为" + finalT + "s,稍后为您自动记账。", Toast.LENGTH_LONG).show();
                                Caches.AddOrUpdate("show_tip", "true");
                            }
                            new Handler().postDelayed(() -> {
                                delay(context, mHandler);
                            }, finalT * 100);
                        });
                        return;

                    }

                }
                mHandler.sendEmptyMessage(0);
            });
        });
    }

    public String getQianJi(BillInfo billInfo) {


        String url = "qianji://publicapi/addbill?&type=" + billInfo.getType(true) + "&money=" + billInfo.getMoney();

        if (billInfo.getTime() != null) {
            url += "&time=" + billInfo.getTime();
        }
        if (billInfo.getRemark() != null) {
            url += "&remark=" + billInfo.getRemark();
        }
        if (billInfo.getCateName() != null) {
            url += "&catename=" + billInfo.getCateName();
        }
        url += "&catechoose=" + billInfo.getCateChoose();

        url += "&catetheme=auto";

        if (billInfo.getBookName()!= null && !billInfo.getBookName().equals("默认账本")) {
            url += "&bookname=" + billInfo.getBookName();
        }

        if (billInfo.getAccountName() != null && !billInfo.getAccountName().equals("")) {
            url += "&accountname=" + billInfo.getAccountName();
        }
        if (billInfo.getAccountName2() != null && !billInfo.getAccountName2().equals("")) {
            url += "&accountname2=" + billInfo.getAccountName2();
        }
        if (billInfo.getFee() != null && !billInfo.getFee().equals("")) {
            url += "&fee=" + billInfo.getFee();
        }
        Log.d("钱迹URL:" + url);
        return url;
    }


    @Override
    public String getAsyncDesc() {
        return "你可以一键将钱迹中的资产、账本、分类等数据同步到自动记账，期间手机会跳转到钱迹，完成同步后将自动跳回本页面。 \n\n ⚠️ 同步前，请确保开启所有权限，并将钱迹加入XPOSED模块作用域中。";
    }
}
