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
import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.utils.file.Storage;

import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_ACCOUNT;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_MONEY;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_REMARK;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_SORT;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_SUB;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_TIME;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_TYPE;
public class Bill {
    public static void put(String ACCOUNT,String MONEY,String REMARK,String SUB,String TYPE,String SORT){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(KEY_ACCOUNT,ACCOUNT);
        jsonObject.put(KEY_MONEY,MONEY);
        jsonObject.put(KEY_REMARK,REMARK);
        jsonObject.put(KEY_SORT,SORT);
        jsonObject.put(KEY_SUB,SUB);
        jsonObject.put(KEY_TIME,getTime());
        jsonObject.put(KEY_TYPE,TYPE);
        JSONArray jsonArray= Storage.type(Storage.Bill).getJSONArray("bill");
        jsonArray.add(jsonObject);
        Storage.type(Storage.Bill).set("bill",jsonArray);
    }
    public static void put(String ACCOUNT,String MONEY,String REMARK,String SUB,String TYPE){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(KEY_ACCOUNT,ACCOUNT);
        jsonObject.put(KEY_MONEY,MONEY);
        jsonObject.put(KEY_REMARK,REMARK);
        jsonObject.put(KEY_SUB,SUB);
        jsonObject.put(KEY_TIME,getTime());
        jsonObject.put(KEY_TYPE,TYPE);
        JSONArray jsonArray= Storage.type(Storage.Bill).getJSONArray("bill");
        jsonArray.add(jsonObject);
        Storage.type(Storage.Bill).set("bill",jsonArray);
    }

    @SuppressLint("DefaultLocale")
    private static String getTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }


}
