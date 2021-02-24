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

package cn.dreamn.qianji_auto.core.db.Helper;

import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.db.Table.Asset2;
import cn.dreamn.qianji_auto.core.db.Table.BookName;
import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;

public class BookNames {
    public static String getDefault() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("defaultBookName", "默认账本");
    }

    public static void change(String bookName) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("defaultBookName", bookName);
    }

    public static String getPic(String name) {
        BookName[] bookNames = DbManger.db.BookNameDao().get(name);
        if (bookNames != null && bookNames.length != 0)
            return bookNames[0].icon;
        return "";
    }

    public static String[] getAll() {
        BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        String[] result = new String[bookNames.length + 1];
        for (int i = 0; i < bookNames.length; i++) {
            result[i] = bookNames[i].name;
        }
        result[bookNames.length] = "默认账本";
        return result;
    }

    public static Bundle[] getAllIcon() {
        BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (BookName bookName : bookNames) {
            Bundle bundle = new Bundle();
            bundle.putString("name", bookName.name);
            bundle.putInt("id", bookName.id);
            bundle.putString("cover", bookName.icon);
            bundleArrayList.add(bundle);

        }

        Bundle bundle = new Bundle();
        bundle.putString("name", "默认账本");
        bundle.putInt("id", -1);
        bundle.putString("cover", " http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
        bundleArrayList.add(bundle);
        return bundleArrayList.toArray(new Bundle[0]);


    }

    public static Bundle[] getAllIcon(Boolean add) {
        BookName[] bookNames = DbManger.db.BookNameDao().getAll();
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (BookName bookName : bookNames) {
            Bundle bundle = new Bundle();
            bundle.putString("name", bookName.name);
            bundle.putInt("id", bookName.id);
            bundle.putString("cover", bookName.icon);
            bundleArrayList.add(bundle);

        }

        if (add) {
            Bundle bundle = new Bundle();
            bundle.putString("name", "默认账本");
            bundle.putInt("id", -1);
            bundle.putString("cover", " http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
            bundleArrayList.add(bundle);
        }

        return bundleArrayList.toArray(new Bundle[0]);


    }

    public static BookName[] getAllWith() {
        return DbManger.db.BookNameDao().getAll();
    }

    public static void del(int id) {
        DbManger.db.BookNameDao().del(id);
    }

    public static void upd(int id, String bookName) {
        DbManger.db.BookNameDao().update(id, bookName);
    }

    public static void add(String bookName) {
        DbManger.db.BookNameDao().add(bookName);
    }

    public static void add(String bookName, String icon) {
        DbManger.db.BookNameDao().add(bookName, icon);
    }

    public static void clean() {
        DbManger.db.BookNameDao().clean();
    }
}
