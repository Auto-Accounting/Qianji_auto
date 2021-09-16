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

import cn.dreamn.qianji_auto.database.Table.Asset2;


@Dao
public interface Asset2Dao {
    @Query("SELECT * FROM asset2 order by sort,id")
    Asset2[] getAll();

    @Query("SELECT * FROM asset2 WHERE id=:id")
    Asset2[] get(int id);

    @Query("SELECT * FROM asset2 WHERE name=:name")
    Asset2[] get(String name);

    @Query("DELETE FROM asset2 WHERE id=:id")
    void del(int id);


    @Query("INSERT INTO asset2(name,icon,sort) values(:name,:icon,:sort)")
    void add(String name, String icon, int sort);

    @Query("INSERT INTO asset2(name,icon,sort) values(:name,'',0)")
    void add(String name);

    @Query("UPDATE  asset2 SET name=:name WHERE id=:id")
    void update(int id, String name);

    @Query("DELETE FROM asset2")
    void clean();

    @Query("UPDATE  asset2 set sort=:sort WHERE id=:id")
    void setSort(int id, int sort);
}

