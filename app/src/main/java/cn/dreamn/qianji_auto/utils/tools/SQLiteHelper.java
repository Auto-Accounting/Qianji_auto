/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.utils.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "Library";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "msg";

    //创建数据库，里面添加了3个参数，分别是：Msgone VARCHAR类型，30长度当然这了可以自定义
    //Msgtwo VARCHAR(20)   Msgthree VARCHAR(30))  NOT NULL不能为空
    String sql = "CREATE TABLE " + TABLE_NAME
            + "(_id INTEGER PRIMARY KEY,"
            + " Msgone VARCHAR(30)  NOT NULL,"
            + " Msgtwo VARCHAR(20),"
            + " Msgthree VARCHAR(30))";
    //构造函数，创建数据库
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    //获取游标
    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    //插入一条记录
    public long insert(String msg1,String msg2,String msg3 ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Msgone", msg1);
        cv.put("Msgtwo", msg2);
        cv.put("Msgthree", msg3);
        return db.insert(TABLE_NAME, null, cv);
    }

    //根据条件查询
    public Cursor query(String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE BookName LIKE ?", args);
    }

    //删除记录
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where ="_id = ?";
        String[] whereValue = { Integer.toString(id) };
        db.delete(TABLE_NAME, where, whereValue);
    }

    //更新记录
    public void update(int id, String msg1,String msg2,String msg3 ) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "_id = ?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put("Msgone", msg1);
        cv.put("Msgtwo", msg2);
        cv.put("Msgthree", msg3);
        db.update(TABLE_NAME, cv, where, whereValue);
    }

}
