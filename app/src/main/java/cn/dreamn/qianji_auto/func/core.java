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

package cn.dreamn.qianji_auto.func;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class core {
    /**
     * 获取资产映射
     * @param payTools 资产
     * @return 资产
     */
    public static String getPayTools(String payTools){
        return Storage.type(Storage.Map).get(payTools,payTools);
    }

    /**
     * 获取分类
     * @param shopName 商户
     * @param detail 商品详情
     * @return 取得的分类
     */
    public static String getSort(String shopName, String detail) {
        JSONObject jsonObject = Storage.type(Storage.Learn).getAll();
        for (Map.Entry entry : jsonObject.entrySet()) {
            if (detail.contains(entry.getKey().toString())) {
                return entry.getValue().toString();
            }
            if (shopName.contains(entry.getKey().toString())) {
                return entry.getValue().toString();
            }
        }
        return "其它";
    }
}
