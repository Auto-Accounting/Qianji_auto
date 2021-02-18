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

import cn.dreamn.qianji_auto.core.db.Table.Other;

@Dao
public interface OtherDao {
    @Query("SELECT * FROM other WHERE use=1  order by sort,id")
    Other[] load();

    @Query("SELECT * FROM other order by sort,id")
    Other[] loadAll();

    @Query("DELETE FROM other WHERE id=:id")
    void delete(int id);

    @Query("UPDATE  other set sort=:sort WHERE id=:id")
    void setSort(int id, int sort);


    @Query("UPDATE  other SET regular=:regular,name=:name,num=:num WHERE id=:id")
    void update(int id, String regular, String name, String num);

    @Query("UPDATE  other SET use=1 WHERE id=:id")
    void enable(int id);

    @Query("UPDATE  other SET use=0 WHERE id=:id")
    void deny(int id);

    @Query("INSERT INTO other(regular,name,num,use) values(:regex,:name,:num,1)")
    void add(String regex, String name, String num);

    @Query("DELETE FROM other")
    void clean();
}

