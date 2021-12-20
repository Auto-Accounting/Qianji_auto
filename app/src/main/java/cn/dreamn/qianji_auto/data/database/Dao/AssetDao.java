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

import cn.dreamn.qianji_auto.data.database.Table.Asset;


@Dao
public interface AssetDao {
    @Query("SELECT * FROM Asset order by sort,id limit :from,:to")
    Asset[] getAll(int from, int to);

    @Query("SELECT * FROM Asset WHERE id=:id")
    Asset[] get(int id);

    @Query("SELECT * FROM Asset WHERE name=:name limit 1")
    Asset[] get(String name);

    @Query("DELETE FROM Asset WHERE id=:id")
    void del(int id);


    @Query("INSERT INTO Asset(name,icon,sort,qid) values(:name,:icon,:sort,:qid)")
    void add(String name, String icon, int sort, String qid);

    @Query("INSERT INTO Asset(name,icon,sort) values(:name,'',0)")
    void add(String name);

    @Query("UPDATE  Asset SET name=:name WHERE id=:id")
    void update(int id, String name);

    @Query("DELETE FROM Asset")
    void clean();

    @Query("UPDATE  Asset set sort=:sort WHERE id=:id")
    void setSort(int id, int sort);
}

