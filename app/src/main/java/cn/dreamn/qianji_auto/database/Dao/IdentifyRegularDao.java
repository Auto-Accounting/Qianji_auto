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

import cn.dreamn.qianji_auto.database.Table.IdentifyRegular;


@Dao
public interface IdentifyRegularDao {
    @Query("SELECT * FROM IdentifyRegular WHERE use=1 and identify=:identify and fromApp=:App  order by sort")
    IdentifyRegular[] load(String identify,String App);

    @Query("SELECT * FROM IdentifyRegular WHERE  identify=:identify and fromApp=:App  order by sort,use")
    IdentifyRegular[] loadAll(String identify, String App);


    @Query("SELECT * FROM IdentifyRegular WHERE  identify=:identify order by sort,use")
    IdentifyRegular[] loadAll(String identify);

    @Query("DELETE FROM IdentifyRegular WHERE id=:id")
    void delete(int id);

    @Query("UPDATE  IdentifyRegular set sort=:sort WHERE id=:id")
    void setSort(int id, int sort);

    @Query("UPDATE  IdentifyRegular SET regular=:regular,name=:name,text=:text,tableList=:tableList,identify=:identify,fromApp=:fromApp,des=:des WHERE id=:id")
    void update(int id, String regular, String name, String text, String tableList, String identify, String fromApp, String des);

    @Query("UPDATE  IdentifyRegular SET use=1 WHERE id=:id")
    void enable(int id);

    @Query("UPDATE  IdentifyRegular SET use=0 WHERE id=:id")
    void deny(int id);

    @Query("INSERT INTO IdentifyRegular(regular,name,text,use,sort,tableList,identify,fromApp,des) values(:regex,:name,:text,1,0,:tableList,:identify,:fromApp,:des)")
    void add(String regex, String name, String text, String tableList, String identify, String fromApp, String des);

    @Query("DELETE FROM IdentifyRegular WHERE identify=:identify and fromApp=:App")
    void clean(String identify, String App);
}

