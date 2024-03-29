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

package cn.dreamn.qianji_auto.data.database.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import cn.dreamn.qianji_auto.data.database.Table.AutoBill;


@Dao
public interface AutoBillDao {
    @Query("SELECT * FROM autobill order by CAST(date as double),id desc limit :from,:to")
    AutoBill[] getAll(int from, int to);

    @Query("SELECT * FROM autobill where billInfo=:bill order by id desc limit 1")
    AutoBill[] getLast(String bill);

    @Query("SELECT * FROM autobill WHERE id=:id")
    AutoBill[] get(int id);

    @Query("DELETE FROM autobill WHERE id=:id")
    void del(int id);

    @Query("INSERT INTO autobill(billInfo,date,isRecord) values(:billInfo,strftime('%m.%d','now','localtime'),0)")
    void add(String billInfo);

    @Query("SELECT * from autobill WHERE date=:date order by id desc")
    AutoBill[] getByDate(String date);

    @Query("UPDATE autobill SET billInfo=:billInfo WHERE id=:id")
    void update(int id, String billInfo);

    @Query("UPDATE autobill SET isRecord=1 WHERE id=:id AND isRecord=0")
    void update(int id);

    @Query("SELECT * FROM autobill WHERE isRecord=0 order by CAST(date as double),id desc")
    AutoBill[] getNoRecord();


    @Query("DELETE FROM autobill")
    void delAll();

    @Query("DELETE FROM autobill WHERE id not in (SELECT id FROM autobill order by id DESC limit :limit)")
    void delLimit(int limit);
}

