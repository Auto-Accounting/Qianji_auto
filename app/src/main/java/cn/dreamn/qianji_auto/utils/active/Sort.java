/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.utils.active;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import cn.dreamn.qianji_auto.utils.file.Storage;

public class Sort {
    /**
     * 获取资产映射
     * @param payTools 资产
     * @return 资产
     */
    public static String getPayTools(String payTools){
        String sort=Storage.type(Storage.Map).get(payTools,null);
        if(sort==null){
            Storage.type(Storage.Map).set(payTools,payTools);
            sort=payTools;
        }
        return sort;
    }
    public static String getAliTools(String payTools){
        String sort=Storage.type(Storage.AliMap).get(payTools,null);
        if(sort==null){
            Storage.type(Storage.AliMap).set(payTools,payTools);
            sort=payTools;
        }
        return sort;
    }
    public static String getWeTools(String payTools){
        String sort=Storage.type(Storage.WeMap).get(payTools,null);
        if(sort==null){
            Storage.type(Storage.WeMap).set(payTools,payTools);
            sort=payTools;
        }
        return sort;

    }
    /**
     * 获取分类
     * @param shopName 商户
     * @param detail 商品详情
     * @return 取得的分类
     */
    public static String getLearnSort(String shopName, String detail) {
        JSONObject jsonObject = Storage.type(Storage.Learn).getAll();
        for (Map.Entry entry : jsonObject.entrySet()) {
            if (detail.contains(entry.getKey().toString())) {
                return entry.getValue().toString();
            }
            if (shopName.contains(entry.getKey().toString())) {
                return entry.getValue().toString();
            }
        }
        Storage.type(Storage.Learn).set(shopName,"其它");
        return "其它";
    }



}
