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

package cn.dreamn.qianji_auto.core.base;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Analyze {

    private static String TAG = "Qianji-Xp-Analyze";
    protected JSONObject jsonObject;

    /*
     * 设置需要分析的内容
     * */
    protected void setContent(String content) {
        try {
            jsonObject = JSONObject.parseObject(content);
        } catch (Exception e) {
            jsonObject = null;
            Logs.i(TAG, e.toString());
        }
    }

    /**
     * 从json中获取分析结果
     *
     * @param billInfo billInfo
     * @return billInfo
     */
    public BillInfo getResult(BillInfo billInfo) {
        return billInfo;
    }


    public BillInfo tryAnalyze(String content, String source) {
        setContent(content);
        if (jsonObject == null) return null;
        BillInfo billInfo = new BillInfo();
        billInfo = getResult(billInfo);
        if (billInfo == null) return null;
        billInfo.setSource(source);
        billInfo = setAvalibleData(billInfo);
        return billInfo;
    }

    public BillInfo setAvalibleData(BillInfo billInfo) {
        if (billInfo.getIsSilent()) return billInfo;
        if (jsonObject.containsKey("alipay_cache_shopremark") && jsonObject.containsKey("alipay_cache_money") && jsonObject.containsKey("alipay_cache_payTool")) {
            if (billInfo.getMoney().equals(jsonObject.getString("alipay_cache_money"))) {
                if (billInfo.getShopRemark() == null || billInfo.getShopRemark().equals("")) {
                    billInfo.setShopRemark(jsonObject.getString("alipay_cache_shopremark"));
                }

                if (!jsonObject.getString("alipay_cache_payTool").equals("")) {
                    billInfo.setAccountName(jsonObject.getString("alipay_cache_payTool"));
                }
            }
        }
        return billInfo;
    }
}
