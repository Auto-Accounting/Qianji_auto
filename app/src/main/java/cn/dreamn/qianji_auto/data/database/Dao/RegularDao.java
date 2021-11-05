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

import cn.dreamn.qianji_auto.data.database.Table.Regular;


@Dao
public interface RegularDao {
    @Query("SELECT * FROM regular WHERE identify=:type AND app=:app order by sort,id limit :from,:to")
    Regular[] load(String type, String app, int from, int to);

    @Query("SELECT * FROM regular WHERE identify=:type  order by sort,id limit :from,:to")
    Regular[] load(String type, int from, int to);


    @Query("SELECT * FROM regular WHERE identify=:type AND (app=:app OR app='') and use=1  order by sort,id limit :from,:to")
    Regular[] loadUse(String type, String app, int from, int to);

    @Query("SELECT * FROM regular WHERE dataId=:dataId limit 1")
    Regular[] loadByDataId(String dataId);

    @Query("select * from  Regular  where  id  in (select  max(id)  from  Regular  WHERE  identify=:type   group by app having COUNT(*)>0  order by sort)")
    Regular[] loadApps(String type);

    @Query("DELETE FROM regular WHERE id=:id")
    void delete(int id);

    @Query("UPDATE regular set sort=:sort WHERE id=:id")
    void setSort(int id, int sort);

    @Query("INSERT INTO regular(regular,name,data,use,sort,remark,dataId,version,app,identify) values(:regular,:name,:data,1,0,:remark,:dataId,:version,:app,:type)")
    void add(String regular, String name, String data, String remark, String dataId, String version, String app, String type);

    @Query("UPDATE  regular SET regular=:regular,name=:name,data=:data,remark=:remark,dataId=:dataId,version=:version,app=:app,identify=:type WHERE id=:id")
    void update(int id, String regular, String name, String data, String remark, String dataId, String version, String app, String type);

    @Query("UPDATE regular SET use=1 WHERE id=:id")
    void enable(int id);

    @Query("UPDATE  regular SET use=0 WHERE id=:id")
    void deny(int id);

    @Query("SELECT * FROM regular WHERE id=:id")
    Regular[] getOne(int id);

    @Query("DELETE FROM regular WHERE identify=:type")
    void clean(String type);

    @Query("DELETE FROM regular WHERE identify=:type  and app=:app")
    void clean(String type, String app);

    @Query("SELECT * FROM regular  WHERE identify=:type")
    Regular[] getDataId(String type);

    @Query("SELECT * FROM regular  WHERE identify=:type and app=:app")
    Regular[] getDataId(String type, String app);

    @Query("SELECT * FROM regular  WHERE dataId=:dataId")
    Regular[] getByDataId(String dataId);
}

