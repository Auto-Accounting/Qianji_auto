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

import cn.dreamn.qianji_auto.R;

import static com.xuexiang.xui.utils.ResUtils.getResources;

public class BillTools {
    public static String getMoney(String s) {
        return getDoubleValue(s);
    }
    private static String getDoubleValue(String str)
    {
        double d = 0;

        if(str!=null && str.length()!=0)
        {
            StringBuilder bf = new StringBuilder();

            char[] chars = str.toCharArray();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                } else if (c == '.') {
                    if (bf.length() == 0) {
                        continue;
                    } else if (bf.indexOf(".") != -1) {
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
            try
            {
                d = Double.parseDouble(bf.toString());
            }
            catch(Exception e)
            {}
        }

        return String.valueOf(d);
    }

    public static int getColor(BillInfo billInfo){
        if(billInfo.getType().equals(BillInfo.TYPE_INCOME)){
            return getResources().getColor(R.color.colorSwipeTwo_winter);
        }else if(billInfo.getType().equals(BillInfo.TYPE_PAY)){
            return getResources().getColor(R.color.toast_error_color);
        }else if(billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)){
            return getResources().getColor(R.color.colorSwipeThree_autumn);
        }else{
            return getResources().getColor(R.color.darkGrey_winter);
        }
    }

    public static String getCustomBill(BillInfo billInfo){
        if(billInfo.getType().equals(BillInfo.TYPE_INCOME)){
            return "+ ￥"+billInfo.getMoney();
        }else if(billInfo.getType().equals(BillInfo.TYPE_PAY)){
            return "- ￥"+billInfo.getMoney();
        }else if(billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)){
            return "-> ￥"+billInfo.  getMoney();
        }else{
            return "- ￥"+billInfo.getMoney();
        }
    }

}
