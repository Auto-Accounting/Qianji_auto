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

package cn.dreamn.qianji_auto.database.Helper;

import android.os.Bundle;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.Asset;
import cn.dreamn.qianji_auto.database.Table.Asset2;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


public class Assets {
    public interface getAssets2{
        void onGet(Asset2[] asset2s);
    }
    public interface getAssets{
        void onGet(Asset[] asset2s);
    }
    public interface getAssets2Strings{
        void onGet(String[] asset2s);
    }
    public interface getAssets2String{
        void onGet(String asset2s);
    }
    public interface getAssets2Bundle{
        void onGet(Bundle[] asset2s);
    }
    public static void getAllAccount(getAssets2 getAsset) {
        Task.onThread(()-> getAsset.onGet(DbManger.db.Asset2Dao().getAll()));
    }

    public static void getAllAccountName(getAssets2Strings getAssets) {
        Task.onThread(()-> {
            Asset2[] assets = DbManger.db.Asset2Dao().getAll();
            if (assets.length <= 0) {
                getAssets.onGet(null);
                return;
            }
            String[] result = new String[assets.length];
            for (int i = 0; i < assets.length; i++) {
                result[i] = assets[i].name;
            }
            getAssets.onGet(result);
        });
    }

    public static void getAllIcon(getAssets2Bundle getAsset) {
        Task.onThread(()-> {
        Asset2[] assets =  DbManger.db.Asset2Dao().getAll();
        if (assets.length <= 0){
            getAsset.onGet(null);
            return;
        }

        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (Asset2 asset : assets) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", asset.id);
            bundle.putString("name", asset.name);
            bundle.putString("icon", asset.icon);
            bundleArrayList.add(bundle);
        }
        getAsset.onGet(bundleArrayList.toArray(new Bundle[0]));
        });
    }

    public static void getPic(String name,getAssets2String getAssets) {
        Task.onThread(()-> {
        Asset2[] asset2s =  DbManger.db.Asset2Dao().get(name);
        if (asset2s != null && asset2s.length != 0){
            getAssets.onGet(asset2s[0].icon);
            return;
        }
        getAssets.onGet("");
        });
    }

    public static void getMap(getAssets2Strings getAssets) {
        Task.onThread(()-> {
        Asset[] assets =  DbManger.db.AssetDao().getAll();
        if (assets.length <= 0) {
            getAssets.onGet(null);
            return;
        }
        String[] result = new String[assets.length];
        for (int i = 0; i < assets.length; i++) {
            result[i] = assets[i].mapName;
        }
            getAssets.onGet(result);
        });
    }

    public static void getAllMap(getAssets getAsset) {
        Task.onThread(()-> getAsset.onGet(DbManger.db.AssetDao().getAll()));
    }

    public static void delAsset(int id) {
        Task.onThread(()-> DbManger.db.Asset2Dao().del(id));
    }

    public static void addAsset(String assetName) {
        Task.onThread(()-> DbManger.db.Asset2Dao().add(assetName));
    }

    public static void addAsset(String assetName, String icon, int sort) {
        Task.onThread(()-> DbManger.db.Asset2Dao().add(assetName, icon, sort));
    }

    public static void cleanAsset() {
        Task.onThread(()-> DbManger.db.Asset2Dao().clean());
    }

    public static void updAsset(int id, String assetName) {
        Task.onThread(()-> DbManger.db.Asset2Dao().update(id, assetName));
    }

    public static void delMap(int id) {
        Task.onThread(()-> DbManger.db.AssetDao().del(id));
    }

    public static void addMap(String assetName, String mapName) {
        Task.onThread(()-> DbManger.db.AssetDao().add(assetName, mapName));
    }

    public static void updMap(int id, String assetName, String mapName) {
        Task.onThread(()-> DbManger.db.AssetDao().update(id, assetName, mapName));
    }

    public static void getMap(String assetName,getAssets2String getAssets) {
        Task.onThread(()-> {
            if (assetName == null) {
                getAssets.onGet(null);
                return;
            }
            Asset[] assets = DbManger.db.AssetDao().get(assetName);
            if (assetName.equals("")) {
                getAssets.onGet(null);
                return;
            }
            //没有资产创造资产
            if (assets.length <= 0) {
                DbManger.db.AssetDao().add(assetName, assetName);
                getAssets.onGet(assetName);
                return;
            }
            getAssets.onGet(assets[0].mapName);
        });

    }

    public static void setSort(int id, int fromPosition) {
        Task.onThread(()-> DbManger.db.Asset2Dao().setSort(id, fromPosition));
    }
}
