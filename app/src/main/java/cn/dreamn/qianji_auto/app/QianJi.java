package cn.dreamn.qianji_auto.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.liuguangqiang.cookie.CookieBar;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.Helper.Caches;
import cn.dreamn.qianji_auto.setting.AppStatus;
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
                if (msg.what == 0) {
                    Caches.AddOrUpdate("show_tip", "false");
                    Caches.AddOrUpdate("float_time", String.valueOf(System.currentTimeMillis()));
                    Tool.goUrl(context, getQianJi(billInfo));
                    Toast.makeText(context, "记账成功！金额￥" + billInfo.getMoney() + "。", Toast.LENGTH_LONG).show();
                }
            }
        };
        //如果不是xp模式需要延时
        if (!AppStatus.xposedActive(context)) {
            delay(context, mHandler);
        } else {
            mHandler.sendEmptyMessage(0);
        }

    }

    @Override
    public void asyncDataBefore(Context context) {
        if (AppStatus.xposedActive(context)) {
            Intent intent = new Intent();
            intent.setClassName("com.mutangtech.qianji", "com.mutangtech.qianji.ui.main.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("needAsync", "true");
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("needAsync", true);
            context.startActivity(intent);
        } else {
            new CookieBar.Builder((Activity) context)
                    .setTitle("无法进行同步")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(String.format("钱迹只支持已激活用户同步，无障碍用户需要手动配置。"))
                    .setAction("我知道了", () -> {
                    }).setDuration(10000)
                    .show();
        }
    }

    @Override
    public void asyncDataAfter(Context context, Bundle billInfo) {

    }

    private void delay(Context context, Handler mHandler) {

        Task.onMain(() -> {
            Caches.getCacheData("float_time", "", cache -> {
                if (!cache.equals("")) {
                    long time = Long.parseLong(cache);
                    long t = System.currentTimeMillis() - time;
                    t = t / 1000;
                    if (t < 4) {
                        long finalT = t;
                        Caches.getCacheData("show_tip", "false", cache1 -> {
                            if (!cache1.equals("true")) {
                                Looper.prepare();
                                Toast.makeText(context, "距离上一次发起请求时间为" + finalT + "s,稍后为您自动记账。", Toast.LENGTH_LONG).show();
                                Looper.loop();
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
        MMKV mmkv = MMKV.defaultMMKV();
        //懒人模式
        if (mmkv.getBoolean("lazy_mode", true)) {
            return url;
        }

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
        Log.i("钱迹URL:" + url);
        return url;
    }


    @Override
    public String getAsyncDesc() {
        return "你可以一键将钱迹中的资产、账本、分类等数据同步到自动记账，期间手机会跳转到钱迹，完成同步后将自动跳回本页面。 \n\n ⚠️ 同步前，请确保开启所有权限，并将钱迹加入XPOSED模块作用域中。";
    }
}
