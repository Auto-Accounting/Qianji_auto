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

package cn.dreamn.qianji_auto.data.database.Helper;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.BookName;
import cn.dreamn.qianji_auto.ui.adapter.BookSelectListAdapter;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


public class BookNames {


    public static String getDefault() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("defaultBookName", "默认账本");
    }

    public static void change(String bookName) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("defaultBookName", bookName);
    }


    public static void getIcon(String bookName, TaskThread.TaskResult taskResult) {

        TaskThread.onThread(() -> {
            BookName[] bookNames = Db.db.BookNameDao().get(bookName);
            if (bookNames.length <= 0) {
                taskResult.onEnd("http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
            } else {
                taskResult.onEnd(bookNames[0].icon);
            }

        });
    }


    public static void getAllIcon(Boolean isAdd, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            BookName[] bookNames = Db.db.BookNameDao().getAll();
            ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            boolean add = isAdd;
            for (BookName bookName : bookNames) {
                Bundle bundle = new Bundle();
                if (bookName.name.equals("默认账本")) {
                    add = false;
                }
                bundle.putString("name", bookName.name);
                bundle.putInt("id", bookName.id);
                String bid = bookName.book_id;
                if (bid == null || bid.equals("")) bid = "-1";
                bundle.putString("book_id", bid);
                //http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2
                if (bookName.icon == null || bookName.icon.equals("")) {
                    bundle.putString("cover", "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
                } else {
                    bundle.putString("cover", bookName.icon);
                }
                bundleArrayList.add(bundle);
            }


            if (bundleArrayList.size() == 0 && add) {
                Bundle bundle = new Bundle();
                bundle.putString("name", "默认账本");
                bundle.putInt("id", -1);
                bundle.putString("book_id", "-1");
                bundle.putString("cover", "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
                bundleArrayList.add(bundle);
            }
            taskResult.onEnd(bundleArrayList);
        });

    }


    public static void showBookSelect(Context context, String title, boolean isFloat, TaskThread.TaskResult taskResult) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_data, null);

        //final TextView list_title = textEntryView.findViewById(R.id.list_title);

        final ListView list_view = textEntryView.findViewById(R.id.list_view);

        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<Bundle> books = (ArrayList<Bundle>) msg.obj;
                if (books.size() == 1 && !isFloat) {
                    taskResult.onEnd(books.get(0));
                    return;
                }
                BookSelectListAdapter adapter = new BookSelectListAdapter(context, books);//listdata和str均可
                list_view.setAdapter(adapter);


                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(context, bottomSheet);

                dialog.cornerRadius(15f, null);
                dialog.title(null, title);

                DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                        false, true, false, false);
                if (isFloat) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                    } else {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    }
                }
                dialog.cancelable(false);
                dialog.show();


                list_view.setOnItemClickListener((parent, view, position, id) -> {
                    taskResult.onEnd(books.get(position));
                    dialog.dismiss();
                });
                // dialog.setOnCancelListener(dialog1 -> getOne.onSelect(null));
            }
        };

        getAllIcon(true, books -> {
            HandlerUtil.send(mHandler, books, 0);
        });


    }
}
