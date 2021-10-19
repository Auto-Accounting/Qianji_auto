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

package cn.dreamn.qianji_auto.database.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import cn.dreamn.qianji_auto.database.Table.Log;

@Dao
public interface LogDao {
    @Query("SELECT * FROM Log order by pos DESC")
    Log[] loadAll();

    @Query("SELECT * FROM Log order by pos DESC limit :limit")
    Log[] loadLimit(int limit);

    @Query("DELETE FROM Log WHERE (strftime('%s','now'))- time > :timeout")
    void deleteTimeout(int timeout);

    @Query("INSERT INTO Log(time,time2,title,sub) values(strftime('%s','now'),:time2,:title,:sub)")
    void add(String title, String sub, String time2);

    @Query("DELETE FROM Log WHERE pos not in (SELECT pos FROM Log order by pos DESC limit :limit)")
    void del(int limit);

    @Query("DELETE FROM Log")
    void delAll();
}

