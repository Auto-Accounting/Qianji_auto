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

package cn.dreamn.qianji_auto.core.hook.hooks.wangc;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.data.local.FileUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class DBHelper {

    private final Utils utils;
    boolean isObj = false;
    //SQLiteDatabase
    private SQLiteDatabase db;

    @SuppressLint("SdCardPath")
    public DBHelper(Utils utils) {
        this.utils = utils;
        openDb();
    }

    public DBHelper(SQLiteDatabase db, Utils utils) {
        this.db = db;
        this.utils = utils;
        isObj = true;
    }

    public SQLiteDatabase getDb() {
        if (db == null || !db.isOpen()) {
            openDb();
        }
        return db;
    }

    private void openDb() {
        isObj = false;
        utils.log("一木记账-Copy 开始复制文件", false);
        @SuppressLint("SdCardPath") String NEW_PATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.wangc.bill/wangc_copy/Custom.db";
        FileUtils.del(NEW_PATH);//尝试删掉这里的文件
        @SuppressLint("SdCardPath") boolean copyed = FileUtils.copyFile("/data/user/0/com.wangc.bill/databases/Custom.db", NEW_PATH);
        if (copyed) {
            utils.log("一木记账-Copy 文件复制成功", false);
        } else {
            utils.log("一木记账-Copy 文件复制失败", false);
        }
        db = SQLiteDatabase.openOrCreateDatabase(NEW_PATH, null);
    }

    @SuppressLint("Range")
    public String getAllTables() {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;", null);
        StringBuilder str = new StringBuilder();
        while (cursor.moveToNext()) {
            String str1 = cursor.getString(cursor.getColumnIndex("name"));
            Cursor cursor2 = db.rawQuery(" PRAGMA TABLE_INFO (" + str1 + ")", null);
            StringBuilder str2 = new StringBuilder();
            while (cursor2.moveToNext()) {
                str2.append(" ").append(cursor.getString(cursor.getColumnIndex("name")));
            }
            cursor2.close();
            str.append("[").append(str1).append("](").append(str2).append(")\n");
        }
        cursor.close();
        return str.toString();
    }

    /**
     * 获取分类信息
     *
     * @param UserId 用户id
     * @return 分类信息
     */
    @SuppressLint("Range")
    public JSONArray getCategory(String UserId, String bookId) {
        String type, level;
        String incomeId = "";
        if (!db.isOpen()) {
            openDb();
        }
        Cursor parentCursor = db.rawQuery("select * from parentcategory where userid = ?", new String[]{String.valueOf(UserId)});
        utils.log("父级分类信息数量: " + parentCursor.getCount() + "", false);
        JSONArray jsonArray = new JSONArray();
        while (parentCursor.moveToNext()) {
            String name = parentCursor.getString(parentCursor.getColumnIndex("categoryname"));
            if (name.equals("收入")) {
                type = "1";
                incomeId = parentCursor.getString(parentCursor.getColumnIndex("categoryid"));
            } else {
                type = "0";
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name); // 分类名称
            jsonObject.put("type", type); // 类型->0:支出 1:收入
            jsonObject.put("id", parentCursor.getString(parentCursor.getColumnIndex("categoryid"))); // 分类id
            jsonObject.put("book_id", bookId); // 账本id
            jsonObject.put("parent", "0"); // 父级id
            jsonObject.put("level", "1"); // 层级->1:父级 2:子级
            jsonObject.put("icon", parentCursor.getString(parentCursor.getColumnIndex("iconurl"))); // 图标
            jsonObject.put("sort", "0"); // 金额
            jsonArray.add(jsonObject);
        }
        parentCursor.close();
        Cursor childCursor = db.rawQuery("select * from childcategory where userid = ?", new String[]{String.valueOf(UserId)});
        utils.log("子级分类信息数量: " + childCursor.getCount() + "", false);
        while (childCursor.moveToNext()) {
            String parentId = childCursor.getString(childCursor.getColumnIndex("parentcategoryid"));
            if (parentId.equals(incomeId)) {
                type = "1";
                level = "1";
            } else {
                type = "0";
                level = "2";
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", childCursor.getString(childCursor.getColumnIndex("categoryname")));
            jsonObject.put("type", type); // 类型->0:支出 1:收入
            jsonObject.put("id", childCursor.getString(childCursor.getColumnIndex("categoryid")));
            jsonObject.put("book_id", bookId); // 账本id
            jsonObject.put("parent", parentId); // 父级id
            jsonObject.put("level", level); // 层级->1:父级 2:子级
            jsonObject.put("icon", childCursor.getString(childCursor.getColumnIndex("iconurl")));
            jsonObject.put("sort", "0"); // 金额
            jsonArray.add(jsonObject);
        }
        childCursor.close();
        //  db.close();
        return jsonArray;
    }

    /**
     * 获取资产信息
     *
     * @param UserId 用户id
     * @return 资产信息
     */
    @SuppressLint("Range")
    public JSONArray getAsset(String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from asset where userid = ?", new String[]{String.valueOf(UserId)});
        utils.log("资产信息数量: " + cursor.getCount(), false);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sort", cursor.getInt(cursor.getColumnIndex("assetnumber"))); // 金额
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("assetid"))); // 资产id
            jsonObject.put("name", cursor.getString(cursor.getColumnIndex("assetname"))); // 资产名称
            jsonObject.put("icon", cursor.getString(cursor.getColumnIndex("asseticon"))); // 图标
            jsonObject.put("type", "0"); // 未知
            jsonObject.put("info", "1"); // 未知
            jsonArray.add(jsonObject);
        }
        cursor.close();
        return jsonArray;
    }

    public String getUserId() {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from userdb where id='1'", null);
        String id = "";
        while (cursor.moveToNext()) {
            id = cursor.getString(cursor.getColumnIndex("userid"));
        }
        cursor.close();
        return id;
    }

    @SuppressLint("Range")
    public JSONArray getUserBook(String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from accountbook where userid = ?", new String[]{String.valueOf(UserId)});
        JSONArray jsonArray = new JSONArray();
        boolean has = false;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("accountbookid")));
            String name = cursor.getString(cursor.getColumnIndex("bookname"));
            if (name.equals("日常账本")) {
                has = true;
            }
            jsonObject.put("name", name);
            jsonObject.put("userId", cursor.getString(cursor.getColumnIndex("userid")));
            jsonObject.put("cover", "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
            jsonArray.add(jsonObject);
        }
        cursor.close();
        if (!has) {//非vip并且没有默认账本会自动附加
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "-1");
            jsonObject.put("userId", UserId);
            jsonObject.put("name", "日常账本");
            jsonObject.put("cover", "http://res.qianjiapp.com/headerimages2/maarten-van-den-heuvel-7RyfX2BHoXU-unsplash.jpg!headerimages2");
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @SuppressLint("Range")
    public JSONArray getBills(String type, String UserId) {
        if (!db.isOpen()) {
            openDb();
        }
        Cursor cursor = db.rawQuery("select * from user_bill where TYPE='" + type + "' and USERID='" + UserId + "' and EXTRA IS NULL order by createtime desc", null);
        JSONArray jsonArray = new JSONArray();
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cursor.getString(cursor.getColumnIndex("billid")));
            jsonObject.put("remark", cursor.getString(cursor.getColumnIndex("REMARK")));
            jsonObject.put("money", cursor.getString(cursor.getColumnIndex("MONEY")));
            jsonObject.put("descinfo", cursor.getString(cursor.getColumnIndex("DESCINFO")));
            jsonObject.put("createtime", cursor.getString(cursor.getColumnIndex("createtime")));
            jsonObject.put("type", cursor.getString(cursor.getColumnIndex("TYPE")));
            jsonArray.add(jsonObject);
        }
        cursor.close();
        return jsonArray;
    }


    @Override
    public void finalize() {
        try {
            super.finalize();
            if (db != null && db.isOpen() && !isObj) {
                db.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}