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

package cn.dreamn.qianji_auto.core.db;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface Asset2Dao {
    @Query("SELECT * FROM asset2")
    Asset2[] getAll();
    @Query("SELECT * FROM asset2 WHERE id=:id")
    Asset2[] get(int id);
    @Query("SELECT * FROM asset2 WHERE name=:name")
    Asset2[] get(String name);
    @Query("DELETE FROM asset2 WHERE id=:id")
    void del(int id);
    @Query("INSERT INTO asset2(name) values(:name)")
    void add(String name);
    @Query("UPDATE  asset2 SET name=:name WHERE id=:id")
    void update(int id, String name);
}

