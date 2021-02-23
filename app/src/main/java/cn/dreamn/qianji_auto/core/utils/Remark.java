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

import com.tencent.mmkv.MMKV;

public class Remark {
    public static String getRemark(BillInfo billInfo) {
        String shopName = billInfo.getShopAccount();
        String shopRemark = billInfo.getShopRemark();
        String app = billInfo.getSource().contains("微信") ? "微信" : (billInfo.getSource().contains("支付宝") ? "支付宝" : "短信");

        if (shopName == null) shopName = "";
        if (shopRemark == null) shopRemark = "";
        return getRemarkTpl().replace("[商户名]", shopName)
                .replace("[商户备注]", shopRemark)
                .replace("[APP]", app)
                .replace("[支出资产]", billInfo.getAccountName())
                .replace("[变动资产]", billInfo.getAccountName2())
                ;
    }

    public static String getRemarkTpl() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.getString("remark_tpl", "[商户名] - [商户备注]");
    }

    public static void setTpl(String tpl) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("remark_tpl", tpl);
    }
}
