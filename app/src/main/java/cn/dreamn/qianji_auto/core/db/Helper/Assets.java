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

package cn.dreamn.qianji_auto.core.db.Helper;

import android.os.Bundle;

import com.xuexiang.xutil.common.StringUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Table.Asset;
import cn.dreamn.qianji_auto.core.db.Table.Asset2;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Assets {
    public static Asset2[] getAllAccount() {
        return DbManger.db.Asset2Dao().getAll();
    }

    public static String[] getAllAccountName() {
        Asset2[] assets = DbManger.db.Asset2Dao().getAll();
        if (assets.length <= 0) return null;
        String[] result = new String[assets.length];
        for (int i = 0; i < assets.length; i++) {
            result[i] = assets[i].name;
        }
        return result;
    }

    public static Bundle[] getAllIcon() {
        Asset2[] assets = DbManger.db.Asset2Dao().getAll();
        if (assets.length <= 0) return null;
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (Asset2 asset : assets) {
            Bundle bundle = new Bundle();
            bundle.putInt("id", asset.id);
            bundle.putString("name", asset.name);
            bundle.putString("icon", asset.icon);
            bundleArrayList.add(bundle);
        }
        return bundleArrayList.toArray(new Bundle[0]);
    }

    public static String getPic(String name) {
        Asset2[] asset2s = DbManger.db.Asset2Dao().get(name);
        if (asset2s != null && asset2s.length != 0)
            return asset2s[0].icon;
        return "";
    }

    public static String[] getMap() {
        Asset[] assets = DbManger.db.AssetDao().getAll();
        if (assets.length <= 0) return null;
        String[] result = new String[assets.length];
        for (int i = 0; i < assets.length; i++) {
            result[i] = assets[i].mapName;
        }
        return result;
    }

    public static Asset[] getAllMap() {
        return DbManger.db.AssetDao().getAll();
    }

    public static void delAsset(int id) {
        DbManger.db.Asset2Dao().del(id);
    }

    public static void addAsset(String assetName) {
        DbManger.db.Asset2Dao().add(assetName);
    }

    public static void addAsset(String assetName, String icon, int sort) {
        DbManger.db.Asset2Dao().add(assetName, icon, sort);
    }

    public static void cleanAsset() {
        DbManger.db.Asset2Dao().clean();
    }

    public static void updAsset(int id, String assetName) {
        DbManger.db.Asset2Dao().update(id, assetName);
    }

    public static void delMap(int id) {
        DbManger.db.AssetDao().del(id);
    }

    public static void addMap(String assetName, String mapName) {
        DbManger.db.AssetDao().add(assetName, mapName);
    }

    public static void updMap(int id, String assetName, String mapName) {
        DbManger.db.AssetDao().update(id, assetName, mapName);
    }

    public static String getMap(String assetName) {
        if (StringUtils.isEmptyTrim(assetName)) return "";
        Asset[] assets = DbManger.db.AssetDao().get(assetName);
        // 正则匹配内容  需要在内容中以regex:开头
        Asset[] assetsArr = DbManger.db.AssetDao().getAllFromRegex();
        if (assetsArr.length > 0) {
            for (Asset item : assetsArr) {
                // 替换掉 'regex:'
                String pattern = item.name.replaceFirst("regex:", "");
                if (StringUtils.isEmptyTrim(pattern)) {
                    continue;
                }
                // 匹配
                boolean isMatch = Pattern.matches(pattern, assetName);
                if (isMatch) {
                    Logs.i("资产匹配", String.format("源内容：[%s]，被正则表达式：[%s]，成功匹配。", assetName, pattern));
                    return item.mapName;
                }
            }
        }

        //没有资产创造资产
        if (assets.length <= 0) {
            // DbManger.db.AssetDao().add(assetName, assetName);
            return assetName;
        }
        return assets[0].mapName;

    }

    public static void setSort(int id, int fromPosition) {
        DbManger.db.Asset2Dao().setSort(id, fromPosition);
    }
}
