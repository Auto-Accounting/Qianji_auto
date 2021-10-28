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

import cn.dreamn.qianji_auto.data.database.Table.AssetMap;

@Dao
public interface AssetMapDao {
    @Query("SELECT * FROM AssetMap limit :from,:to")
    AssetMap[] getAll(int from, int to);

    @Query("SELECT * FROM AssetMap WHERE id=:id")
    AssetMap[] get(int id);

    @Query("SELECT * FROM AssetMap WHERE name=:name")
    AssetMap[] get(String name);

    @Query("DELETE FROM AssetMap WHERE id=:id")
    void del(int id);

    @Query("DELETE FROM AssetMap WHERE name=:name")
    void del(String name);

    @Query("INSERT INTO AssetMap(name,mapName) values(:name,:mapName)")
    void add(String name, String mapName);

    @Query("UPDATE  AssetMap SET name=:name,mapName=:mapName WHERE id=:id")
    void update(int id, String name, String mapName);

    @Query("SELECT * FROM AssetMap WHERE name like 'reg:%'")
    AssetMap[] getAllFromRegex();
}

