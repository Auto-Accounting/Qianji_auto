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

package cn.dreamn.qianji_auto.database.Helper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.tencent.mmkv.MMKV;


import java.util.ArrayList;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.BookName;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


public class BookNames {


    public static String getDefault() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("defaultBookName", "默认账本");
    }

    public static void change(String bookName) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("defaultBookName", bookName);
    }

    interface getBookBundle{
        void onGet(Bundle bundle);
    }
    interface getBookBundles{
        void onGet(Bundle[] bundle);
    }
    interface getBookStrings{
        void onGet(String[] bundle);
    }
    interface getBookInt{
        void onGet(int length);
    }
    public static void getOne(String name,getBookBundle getBook) {
        Task.onThread(()->{
            BookName[] bookNames = DbManger.db.BookNameDao().get(name);
            Bundle bundle = new Bundle();
            if (bookNames != null && bookNames.length != 0) {
                bundle.putString("icon", bookNames[0].icon);
                bundle.putString("name", bookNames[0].name);
                bundle.putString("book_id", bookNames[0].book_id);
            }
           getBook.onGet(bundle);
        });

    }

    public static void getAll(getBookStrings getBook) {

        Task.onThread(()->{
            BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        String[] result = new String[bookNames.length + 1];
        for (int i = 0; i < bookNames.length; i++) {
            result[i] = bookNames[i].name;
        }
        result[bookNames.length] = "默认账本";
            getBook.onGet(result);
        });
    }



    public static void getAllIcon(Boolean add,getBookBundles getBook) {
        Task.onThread(()->{
            BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();

        for (BookName bookName : bookNames) {
            Bundle bundle = new Bundle();
            bundle.putString("name", bookName.name);
            bundle.putInt("id", bookName.id);
            String bid = bookName.book_id;
            if (bid == null || bid.equals("")) bid = "-1";
            bundle.putString("book_id", bid);
            bundle.putString("cover", bookName.icon);
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


            getBook.onGet(bundleArrayList.toArray(new Bundle[0]));
        });

    }

    public static void del(int id) {
        Task.onThread(()->DbManger.db.BookNameDao().del(id));
    }

    public static void upd(int id, String bookName) {
        Task.onThread(()->DbManger.db.BookNameDao().update(id, bookName));
    }

    public static void add(String bookName) {

        Task.onThread(()->DbManger.db.BookNameDao().add(bookName, "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2", String.valueOf(System.currentTimeMillis())));
    }

    public static void add(String bookName, String icon,final String book_id) {
        Task.onThread(()->{

        String bid=book_id;
        if (bid == null || bid.equals("")) {
            bid = String.valueOf(System.currentTimeMillis());
        }
        DbManger.db.BookNameDao().add(bookName, icon, bid);
        });
    }

    public static void clean() {
        Task.onThread(()->DbManger.db.BookNameDao().clean());
    }

    public static void getAllLen(getBookInt getBook) {
        Task.onThread(()->{
        BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        getBook.onGet(bookNames.length);

        });

    }


    public interface BookSelect {
        void onSelect(Bundle bundle);
    }

   /* public static void showBookSelect(Context context, String title, BookSelect bookSelect) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.float_list2, null);

        //final TextView list_title = textEntryView.findViewById(R.id.list_title);

        final ListView list_view = textEntryView.findViewById(R.id.list_view);

        Bundle[] item = getAllIcon(true);

        ListAdapter3 listAdapter3 = new ListAdapter3(context, R.layout.list_item2, item);//listdata和str均可
        list_view.setAdapter(listAdapter3);

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(textEntryView, false)
                .title(title)
                .show();


        list_view.setOnItemClickListener((parent, view, position, id) -> {
            if (bookSelect != null) {
                bookSelect.onSelect(item[position]);
                dialog.dismiss();
            }
        });


    }*/
}
