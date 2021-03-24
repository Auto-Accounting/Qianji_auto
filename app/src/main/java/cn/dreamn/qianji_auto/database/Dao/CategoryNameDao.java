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

import cn.dreamn.qianji_auto.database.Table.CategoryName;


@Dao
public interface CategoryNameDao {
    @Query("SELECT * FROM categoryname WHERE type=:type AND level='1' AND book_id=:book_id order by CAST(sort as int)")
    CategoryName[] get(String type, String book_id);

    @Query("SELECT * FROM categoryname WHERE type=:type AND level='2' AND parent_id=:parent  AND book_id=:book_id  order by CAST(sort as int)")
    CategoryName[] get(String type, String parent, String book_id);
    @Query("SELECT * FROM categoryname WHERE type=:type AND level='2' AND parent_id=:parent  AND book_id=:book_id limit 1")
    CategoryName[] getOne(String type, String parent, String book_id);
    @Query("DELETE FROM categoryname WHERE id=:id")
    void del(int id);

    @Query("SELECT * FROM categoryname WHERE id=:id limit 1")
    CategoryName[] get(int id);

    @Query("INSERT INTO categoryname(name,icon,level,type,self_id,parent_id,book_id,sort) values(:name,:icon,:level,:type,:self_id,:parent_id,:book_id,:sort)")
    void add(String name, String icon, String level, String type, String self_id, String parent_id, String book_id, String sort);

    @Query("UPDATE  categoryname SET name=:name WHERE id=:id")
    void update(int id, String name);

    @Query("Select * from categoryname where name=:name and type=:type AND book_id=:book_id")
    CategoryName[] getByName(String name, String type, String book_id);

    @Query("DELETE  FROM categoryname")
    void clean();
}

