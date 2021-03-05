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

package cn.dreamn.qianji_auto.core.db.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import cn.dreamn.qianji_auto.core.db.Table.Asset;

@Dao
public interface AssetDao {
    @Query("SELECT * FROM asset")
    Asset[] getAll();

    @Query("SELECT * FROM asset WHERE name LIKE 'regex:%'")
    Asset[] getAllFromRegex();

    @Query("SELECT * FROM asset WHERE id=:id")
    Asset[] get(int id);

    @Query("SELECT * FROM asset WHERE name=:name")
    Asset[] get(String name);

    @Query("DELETE FROM asset WHERE id=:id")
    void del(int id);

    @Query("INSERT INTO asset(name,mapName) values(:name,:mapName)")
    void add(String name, String mapName);

    @Query("UPDATE  asset SET name=:name,mapName=:mapName WHERE id=:id")
    void update(int id, String name, String mapName);
}

