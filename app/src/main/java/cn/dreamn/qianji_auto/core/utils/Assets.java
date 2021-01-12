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
import cn.dreamn.qianji_auto.core.db.DbManger;

public class Assets {
    public static String[] getAll(){
        Asset[] assets= DbManger.db.AssetDao().getAll();
        if(assets.length<=0)return null;
        String[] result = new String[assets.length];
        for(int i=0;i<assets.length;i++){
            result[i]=assets[i].mapName;
        }
        return result;
    }

    public  static void del(int id){
        DbManger.db.AssetDao().del(id);
    }
    public static void add(String assetName,String mapName){
        DbManger.db.AssetDao().add(assetName,mapName);
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
