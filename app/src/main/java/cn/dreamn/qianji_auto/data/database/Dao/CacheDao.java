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

import cn.dreamn.qianji_auto.data.database.Table.Cache;


@Dao
public interface CacheDao {
    @Query("SELECT * FROM Cache WHERE (strftime('%s','now')) - time < 300  ORDER BY id desc limit 1")
    Cache[] getWithoutName();//获取最新的一条缓存数据

    @Query("SELECT * FROM Cache WHERE (strftime('%s','now')) - time < 300 AND cacheName=:name AND cacheType=:type ORDER BY id desc limit 1")
    Cache[] getOne(String name, String type);//获取最新的一条缓存数据

    @Query("DELETE FROM Cache WHERE (strftime('%s','now')) - time > 300")
    void deleteTimeout();

    @Query("INSERT INTO Cache(cacheName,cacheData,time,cacheType) values(:name,:data,strftime('%s','now'),:type)")
    long add(String name, String data, String type);

    @Query("UPDATE Cache SET cacheData=:data,time=strftime('%s','now') WHERE cacheName=:name")
    void update(String name, String data);

    @Query("DELETE FROM Cache WHERE cacheName=:name")
    void del(String name);

    @Query("DELETE FROM Cache")
    void deleteAll();

    @Query("SELECT * FROM Cache WHERE  cacheType = :type order by id")
    Cache[] getType(String type);

    @Query("DELETE FROM Cache WHERE cacheData=:body")
    void delbody(String body);
}

