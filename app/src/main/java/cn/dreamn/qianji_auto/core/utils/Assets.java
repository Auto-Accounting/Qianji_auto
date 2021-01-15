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

import cn.dreamn.qianji_auto.core.db.Asset;
import cn.dreamn.qianji_auto.core.db.Asset2;
import cn.dreamn.qianji_auto.core.db.DbManger;

public class Assets {
    public static Asset2[] getAllAccount(){
        return DbManger.db.Asset2Dao().getAll();
    }
    public static String[] getAllAccountName(){
        Asset2[] assets= DbManger.db.Asset2Dao().getAll();
        if(assets.length<=0)return null;
        String[] result = new String[assets.length];
        for(int i=0;i<assets.length;i++){
            result[i]=assets[i].name;
        }
        return result;
    }
    public static String[] getMap(){
        Asset[] assets= DbManger.db.AssetDao().getAll();
        if(assets.length<=0)return null;
        String[] result = new String[assets.length];
        for(int i=0;i<assets.length;i++){
            result[i]=assets[i].mapName;
        }
        return result;
    }
    public static Asset[] getAllMap(){
        return  DbManger.db.AssetDao().getAll();
    }
    public  static void delAsset(int id){
        DbManger.db.Asset2Dao().del(id);
    }
    public static void addAsset(String assetName){
        DbManger.db.Asset2Dao().add(assetName);
    }
    public static void updAsset(int id,String assetName){
        DbManger.db.Asset2Dao().update(id,assetName);
    }
    public  static void delMap(int id){
        DbManger.db.AssetDao().del(id);
    }
    public static void addMap(String assetName,String mapName){
        DbManger.db.AssetDao().add(assetName,mapName);
    }
    public static void updMap(int id,String assetName,String mapName){
        DbManger.db.AssetDao().update(id,assetName,mapName);
    }
    public static String getMap(String assetName){
        Asset[] assets=DbManger.db.AssetDao().get(assetName);
        //没有资产创造资产
        if(assets.length<=0){
            DbManger.db.AssetDao().add(assetName,assetName);
            return assetName;
        }
        return assets[0].mapName;

    }
}
