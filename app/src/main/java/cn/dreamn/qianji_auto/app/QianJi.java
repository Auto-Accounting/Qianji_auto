package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.RootUtils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class QianJi implements IApp {
    private static QianJi qianJi;
    static long time = 0;
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
                    time = System.currentTimeMillis();
                    if (RootUtils.hasRootPermission()) {
                        RootUtils.exec(new String[]{"am start \"" + getQianJi(billInfo) + "\""});
                    } else {
                        Tool.goUrl(context, getQianJi(billInfo));
                    }

                    //TODO 4.0新增：多币种记账支持，此处预留修改位。
                    ToastUtils.show(String.format(context.getString(R.string.book_success), billInfo.getMoney()));
                }
            }
        };
        if (AppStatus.isXposed()) {
            mHandler.sendEmptyMessage(0);
        } else {
            delay(mHandler);
        }


    }

    private void delay(Handler handler) {
        long m = System.currentTimeMillis() - time;
        if (m < 3000) {
            ToastUtils.show("稍后为您记账！");
            TaskThread.onMain(m, new Runnable() {
                @Override
                public void run() {
                    delay(handler);
                }
            });
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void asyncDataBefore(Context context) {
        if (AppStatus.isXposed()) {
            Log.i("自动记账同步", "同步开始");
            RootUtils.exec(new String[]{"am force-stop com.mutangtech.qianji"});
            //杀死其他应用
            //  Tool.stopApp(context,"com.mutangtech.qianji");
            Intent intent = new Intent();
            intent.setClassName("com.mutangtech.qianji", "com.mutangtech.qianji.ui.main.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("needAsync", "true");
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("needAsync", true);
            Log.i("自动记账同步", "正在前往钱迹");
            context.startActivity(intent);
        } else {
            ToastUtils.show(R.string.not_support);
        }
    }


    @Override
    public void asyncDataAfter(Context context, Bundle extData) {
        // ToastUtils.show("收到钱迹数据！正在后台同步中...");
        String json = extData.getString("data");
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray asset = jsonObject.getJSONArray("asset");
        JSONArray category = jsonObject.getJSONArray("category");
        JSONArray userBook = jsonObject.getJSONArray("userBook");

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    Log.i((String) msg.obj);
                } else {
                    ToastUtils.show(R.string.async_success);
                }

            }
        };

        if (asset == null || category == null || userBook == null) {
            Log.i("钱迹数据信息无效");
            return;
        }

        TaskThread.onThread(() -> {
            Db.db.CategoryDao().clean();

            for (int i = 0; i < category.size(); i++) {
                HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + category.size() + "）正在处理【分类数据】", 1);
                JSONObject jsonObject1 = category.getJSONObject(i);
                String name = jsonObject1.getString("name");
                String icon = jsonObject1.getString("icon");
                String level = jsonObject1.getString("level");
                String type = jsonObject1.getString("type");
                String self_id = jsonObject1.getString("id");
                String parent_id = jsonObject1.getString("parent");
                String book_id = jsonObject1.getString("book_id");
                String sort = jsonObject1.getString("sort");

                if (self_id == null || self_id.equals("")) {
                    self_id = String.valueOf(System.currentTimeMillis());
                }
                if (sort == null || sort.equals("")) {
                    sort = "500";
                }
                String self = self_id;

                Db.db.CategoryDao().add(name, icon, level, type, self, parent_id, book_id, sort);

            }

            Log.i("分类数据处理完毕");

            //资产数据处理
            Db.db.AssetDao().clean();

            for (int i = 0; i < asset.size(); i++) {
                HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + asset.size() + "）正在处理【资产数据】", 1);
                JSONObject jsonObject1 = asset.getJSONObject(i);
                if (jsonObject1.getString("type").equals("5"))
                    continue;
                Db.db.AssetDao().add(jsonObject1.getString("name"), jsonObject1.getString("icon"), jsonObject1.getInteger("sort"));

            }

            Log.i("资产数据处理完毕");

            Db.db.BookNameDao().clean();
            for (int i = 0; i < userBook.size(); i++) {
                HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + userBook.size() + "）正在处理【账本数据】", 1);
                JSONObject jsonObject1 = userBook.getJSONObject(i);
                String bookName = jsonObject1.getString("name");
                String icon = jsonObject1.getString("cover");
                String bid = jsonObject1.getString("id");
                if (bid == null || bid.equals("")) {
                    bid = String.valueOf(System.currentTimeMillis());
                }
                Db.db.BookNameDao().add(bookName, icon, bid);
            }

            Log.i("账本数据处理完毕");

            HandlerUtil.send(mHandler, 0);
        });

        RootUtils.exec(new String[]{"am force-stop com.mutangtech.qianji"});
        // TODO 4.0新增: 处理钱迹账单报销数据，并加入数据库，此处预留修改位。

    }


    public String getQianJi(BillInfo billInfo) {

        String url = "qianji://publicapi/addbill?&type=" + billInfo.getType(true) + "&money=" + billInfo.getMoney();
        MMKV mmkv = MMKV.defaultMMKV();
        //懒人模式
        if (mmkv.getBoolean("lazy_mode", true)) {
            return url + "&catechoose=1";
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
        if (billInfo.getFee() != null && !billInfo.getFee().equals("") && !billInfo.getFee().equals("0")) {
            url += "&fee=" + billInfo.getFee();
        }
        Log.i("钱迹URL:" + url);
        return url;
    }


    @Override
    public String getAsyncDesc(Context context) {
        if (AppStatus.xposedActive(context)) {
            return context.getResources().getString(R.string.qianji_async_desc);
        }
        return context.getResources().getString(R.string.qianji_async_no_support);
    }
}
