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

package cn.dreamn.qianji_auto.core.async_qianji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.LoadingDialog;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.Assets;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.hook.app.qianji.Data;
import cn.dreamn.qianji_auto.utils.tools.FileUtils;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

public class Async {
    private Context mContext;
    private View mView;
    LoadingDialog mLoadingDialog;
    private final Handler uiHandler = new Handler(Looper.myLooper()) {

        public void handleMessage(Message msg) {
            String m = (String) msg.obj;
            mLoadingDialog.setTitle(m);
        }

    };

    public Async(Context context, View view, Bundle extData) {
        mView = view;
        mContext = context;
        mLoadingDialog = WidgetUtils.getLoadingDialog(mContext)
                .setIconScale(0.4F)
                .setLoadingIcon(R.drawable.logo_qianji)

                .setLoadingSpeed(8);
        mLoadingDialog.setTitle("数据同步中");
        mLoadingDialog.show();

        new AsyncQianjiTask().execute(extData);
    }

    public void sendMsg(String msg) {
        Message message = new Message();
        message.obj = msg;
        uiHandler.sendMessage(message);
    }

    public boolean async(Bundle extData) {
        if (extData == null) return false;

        ArrayList<Data> asset = extData.getParcelableArrayList("asset");

        ArrayList<Data> category = extData.getParcelableArrayList("category");

        ArrayList<Data> userBook = extData.getParcelableArrayList("userBook");

        sendMsg("已获取账本信息");
        if (asset != null && category != null && userBook != null) {
            Logs.i("钱迹数据信息有效");
            sendMsg("钱迹数据信息有效！");
            Assets.cleanAsset();
            for (int i = 0; i < asset.size(); i++) {
                sendMsg("正在导入第" + (i + 1) + "条资产中");
                Data d = asset.get(i);
                Bundle m = d.get();
                Assets.addAsset(m.getString("name"), m.getString("icon"), m.getInt("sort"));
            }
            CategoryNames.clean();
            for (int i = 0; i < category.size(); i++) {
                sendMsg("正在导入第" + (i + 1) + "条分类中");
                Data d = asset.get(i);
                Bundle m = d.get();
                CategoryNames.insert(m.getString("name"), m.getString("icon"), m.getString("level"), m.getString("type"), m.getString("id"), m.getString("parent"), m.getString("book_id"), null);
            }
            BookNames.clean();
            for (int i = 0; i < userBook.size(); i++) {
                sendMsg("正在导入第" + (i + 1) + "条账本中");
                Data d = asset.get(i);
                Bundle m = d.get();
                BookNames.add(m.getString("name"), m.getString("cover"), m.getString("id"));
            }

            Logs.i("钱迹数据同步完毕");
            sendMsg("钱迹数据同步完毕！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    @SuppressLint("StaticFieldLeak")
    class AsyncQianjiTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... objects) {
            return async((Bundle) objects[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mLoadingDialog.dismiss();
            if (aBoolean) {
                SnackbarUtils.Long(mView, "导入成功。").info().show();
            } else {
                SnackbarUtils.Long(mView, "导入失败，请查看日志。").danger().show();
            }
        }
    }
}
