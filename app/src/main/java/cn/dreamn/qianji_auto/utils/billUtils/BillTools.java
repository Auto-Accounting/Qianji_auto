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

package cn.dreamn.qianji_auto.utils.billUtils;

import android.content.Context;

import cn.dreamn.qianji_auto.R;


public class BillTools {
    public static String getMoney(String s) {
        return getDoubleValue(s);
    }

    private static String getDoubleValue(String str) {


        double d = 0;

        if (str != null && str.length() != 0) {
            str = str.replace(",", "");
            StringBuilder bf = new StringBuilder();

            char[] chars = str.toCharArray();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                } else if (c == '.') {
                    if (bf.indexOf(".") != -1) {
                        break;
                    } else {
                        bf.append(c);
                    }
                } else {
                    if (bf.length() != 0) {
                        break;
                    }
                }
            }
            try {
                d = Double.parseDouble(bf.toString());
            } catch (Exception ignored) {
            }
        }

        return String.valueOf(d);
    }

    public static int getColor(Context context, BillInfo billInfo) {
        if (billInfo.getType().equals(BillInfo.TYPE_INCOME)) {
            return context.getColor(R.color.float_3);
        } else if (billInfo.getType().equals(BillInfo.TYPE_PAY)) {
            return context.getColor(R.color.float_4);
        } else if (billInfo.getType().equals(BillInfo.TYPE_PAYMENT_REFUND)) {
            return context.getColor(R.color.float_3);
        } else if (billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            return context.getColor(R.color.float_2);
        } else {
            return context.getColor(R.color.float_1);
        }
    }

    public static String getCustomBill(BillInfo billInfo) {
        if (billInfo.getType().equals(BillInfo.TYPE_INCOME)) {
            return "+ ￥" + billInfo.getMoney();
        } else if (billInfo.getType().equals(BillInfo.TYPE_PAY)) {
            return "- ￥" + billInfo.getMoney();
        } else if (billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            return "-> ￥" + billInfo.getMoney();
        } else if (billInfo.getType().equals(BillInfo.TYPE_PAYMENT_REFUND)) {
            return "+ ￥" + billInfo.getMoney();
        } else {
            return "- ￥" + billInfo.getMoney();
        }
    }


}
