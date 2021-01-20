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

package cn.dreamn.qianji_auto.core.utils;

import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.db.DbManger;

public class Caches {

    public static Cache getOne(String name,String type){
        DbManger.db.CacheDao().deleteTimeout();
        Cache[] caches = DbManger.db.CacheDao().getOne(name,type);
        if(caches.length<=0)return null;

        return caches[0];
    }
    public static Cache getWithoutAnyThing(){
        Cache[] caches = DbManger.db.CacheDao().getWithoutName();
        if(caches.length<=0)return null;
        return caches[0];
    }

    public static void del(String name){
        DbManger.db.CacheDao().del(name);
    }

    public static long add(String name, String data,String type){
        DbManger.db.CacheDao().deleteTimeout();
        return DbManger.db.CacheDao().add(name,data,type);
    }

    public static void update(String name, String data){
        DbManger.db.CacheDao().update(name,data);
    }

    public static void Clean() {
        DbManger.db.CacheDao().deleteAll();
    }

    public static void AddOrUpdate(String  name,String data){
        if(getOne(name,"0")!=null){
            update(name,data);
        }else{
            add(name,data,"0");
        }
    }

    public static String getOneString(String  name, String def){
        DbManger.db.CacheDao().deleteTimeout();
        Cache[] caches = DbManger.db.CacheDao().getOne(name,"0");
        if(caches.length<=0)return def;

        return caches[0].cacheData;
    }
}
