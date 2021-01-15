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
public interface RegularDao {
    @Query("SELECT * FROM regular")
    Regular[] loadAll();
    @Query("SELECT * FROM regular WHERE use=1")
    Regular[] load();
    @Query("DELETE FROM regular WHERE id=:id")
    void delete(int id);
    @Query("INSERT INTO regular(regular,cate,name,tableList,use) values(:regular,:cate,:name,:tableList,1)")
    void add(String regular,String name,String cate,String tableList);
    @Query("UPDATE  regular SET regular=:regular,name=:name,cate=:cate,tableList=:tableList WHERE id=:id")
    void update(int id, String regular,String name,String cate,String tableList);
    @Query("UPDATE  regular SET use=1 WHERE id=:id")
    void enable(int id);
    @Query("UPDATE  regular SET use=0 WHERE id=:id")
    void deny(int id);
    @Query("SELECT * FROM regular WHERE id=:id")
    Regular[] getOne(int id);
}

