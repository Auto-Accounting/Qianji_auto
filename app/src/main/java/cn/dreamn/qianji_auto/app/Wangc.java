package cn.dreamn.qianji_auto.app;

import static cn.dreamn.qianji_auto.core.broadcast.AppBroadcast.BROADCAST_ASYNC;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.Toaster;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.data.database.Table.Category;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.RootUtils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class Wangc implements IApp {
    private static Wangc wangc;
    static long time = 0;

    public static Wangc getInstance(){
        if(wangc==null)
            wangc=new Wangc();
        return wangc;
    }
    @Override
    public String getPackPageName() {
        return "com.wangc.bill";
    }

    @Override
    public String getAppName() {
        return "一木记账";
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
                    if (billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
                        Intent intent = new Intent();
                        intent.setClassName("com.wangc.bill", "com.wangc.bill.activity.SchemeAddActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.parse(getQianJi(billInfo));
                        intent.setData(uri);
                        context.startActivity(intent);
                    } else {
                        if (RootUtils.hasRootPermission()) {
                            RootUtils.exec(new String[]{"am start \"" + getQianJi(billInfo) + "\""});
                        } else {
                            Tool.goUrl(context, getQianJi(billInfo));
                        }
                    }

                    //TODO 4.0新增：多币种记账支持，此处预留修改位。
                    Toaster.show(String.format(context.getString(R.string.book_success), billInfo.getMoney()));
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
            Toaster.show("稍后为您记账！");
            TaskThread.onMain(m, () -> delay(handler));
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void asyncDataBefore(Context context, int type) {
        if (AppStatus.isXposed()) {
            Log.i("一木记账:自动记账同步", "同步开始");

            RootUtils.exec(new String[]{"am force-stop com.wangc.bill"});
            //杀死其他应用
            //  Tool.stopApp(context,"com.wangc.bill");
            Intent intent = new Intent();
            intent.setClassName("com.wangc.bill", "com.wangc.bill.activity.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("AutoSignal", type);
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("AutoSignal", type);
            Log.i("一木记账:自动记账同步", "正在前往一木记账");
            context.startActivity(intent);
        } else {
            Toaster.show(context.getResources().getString(R.string.not_support).replaceAll("钱迹", "一木记账"));
        }
    }


    private void doAsync(Bundle extData) {
        String json = extData.getString("data");
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray asset = jsonObject.getJSONArray("asset");
        JSONArray category = jsonObject.getJSONArray("category");
        JSONArray userBook = jsonObject.getJSONArray("userBook");
        List<String> userBookList = new ArrayList<>();

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    Log.i("一木记账:doAsync", (String) msg.obj);
                } else {
                    Toaster.show(R.string.async_success);
                }

            }
        };

        if (asset == null || category == null || userBook == null) {
            Log.i("一木记账:doAsync", "一木记账数据信息无效");
            return;
        }

        TaskThread.onThread(() -> {
            Db.db.CategoryDao().clean();

            for (int i = 0; i < category.size(); i++) {
                //HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + category.size() + "）正在处理【分类数据】", 1);
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

                Category[] category1 = Db.db.CategoryDao().getByName(name, type, book_id);

                if (category1 != null && category1.length > 0) continue;

                Db.db.CategoryDao().add(name, icon, level, type, self, parent_id, book_id, sort);

            }

            Log.i("一木记账:doAsync", "分类数据处理完毕");

            //资产数据处理
            Db.db.AssetDao().clean();

            for (int i = 0; i < asset.size(); i++) {
                //HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + asset.size() + "）正在处理【资产数据】", 1);
                JSONObject jsonObject1 = asset.getJSONObject(i);
                if (jsonObject1.getString("type").equals("5"))
                    continue;
                Db.db.AssetDao().add(jsonObject1.getString("name"), jsonObject1.getString("icon"), jsonObject1.getInteger("sort"), jsonObject1.getString("id"));
            }

            Log.i("一木记账:doAsync", "资产数据处理完毕");

            Db.db.BookNameDao().clean();
            for (int i = 0; i < userBook.size(); i++) {
                //HandlerUtil.send(mHandler, "（" + (i + 1) + "/" + userBook.size() + "）正在处理【账本数据】", 1);
                JSONObject jsonObject1 = userBook.getJSONObject(i);
                String bookName = jsonObject1.getString("name");
                userBookList.add(bookName);
                String icon = jsonObject1.getString("cover");
                String bid = jsonObject1.getString("id");
                if (bid == null || bid.equals("")) {
                    bid = String.valueOf(System.currentTimeMillis());
                }
                Db.db.BookNameDao().add(bookName, icon, bid);
            }

            Log.i("一木记账:doAsync", "账本数据处理完毕");

            if (userBookList.contains(BookNames.getDefault())) {
                BookNames.change(userBookList.get(0));
                Log.i("一木记账:doAsync", "默认账本未找到替换成 -> " + userBookList.get(0));
            }

            HandlerUtil.send(mHandler, 0);
        });


    }

    @Override
    public void asyncDataAfter(Context context, Bundle extData, int type) {
        // Toaster.show("收到一木记账数据！正在后台同步中...");
        if (type == BROADCAST_ASYNC) {
            doAsync(extData);
        }
        RootUtils.exec(new String[]{"am force-stop com.wangc.bill"});

    }


    public String getQianJi(BillInfo billInfo) {
        String url;
        if (billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            url = "yimu://api/transfer?&money=" + billInfo.getMoney();
        } else {
            url = "yimu://api/addbill?&money=" + billInfo.getMoney();
            String childCategory = billInfo.getCateName();
            String parentCategory = Db.db.CategoryDao().getParentCategory(billInfo).join();
            if (childCategory.equals(parentCategory)) {
                url += "&parentCategory=" + childCategory;
            } else {
                url += "&parentCategory=" + parentCategory + "&childCategory=" + childCategory;
            }
        }

        if (billInfo.getRemark() != null) {
            url += "&remark=" + billInfo.getRemark();
        }

        if (billInfo.getTime() != null) {
            url += "&time=" + billInfo.getTime();
        }

        if (billInfo.getBookName()!= null && !billInfo.getBookName().equals("默认账本")) {
            url += "&bookName=" + billInfo.getBookName();
        }

        if (billInfo.getAccountName() != null && !billInfo.getAccountName().equals("") && !billInfo.getAccountName().equals("无账户")) {
            url += "&asset=" + billInfo.getAccountName();
        }

        Log.i("一木记账:doAsync", "一木记账URL:" + url);
        return url;
    }


    @Override
    public String getAsyncDesc(Context context) {
        if (AppStatus.xposedActive(context)) {
            return context.getResources().getString(R.string.qianji_async_desc).replaceAll("钱迹", "一木记账");
        }
        return context.getResources().getString(R.string.qianji_async_no_support).replaceAll("钱迹", "一木记账");
    }
}
