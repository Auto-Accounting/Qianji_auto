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

import cn.dreamn.qianji_auto.core.db.Table.BookName;

@Dao
public interface BookNameDao {
    @Query("SELECT * FROM bookname")
    BookName[] getAll();

    @Query("SELECT * FROM bookname WHERE id=:id")
    BookName[] getAll(int id);

    @Query("DELETE FROM bookname WHERE id=:id")
    void del(int id);

    @Query("INSERT INTO bookname(name) values(:name)")
    void add(String name);

    @Query("INSERT INTO bookname(name,icon) values(:name,:icon)")
    void add(String name, String icon);

    @Query("UPDATE  bookname SET name=:name WHERE id=:id")
    void update(int id, String name);

    @Query("DELETE FROM bookname")
    void clean();

    @Query("SELECT * FROM bookname WHERE name=:name")
    BookName[] get(String name);
}

