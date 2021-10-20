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

package cn.dreamn.qianji_auto.data.database.Helper;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.DbManger;
import cn.dreamn.qianji_auto.data.database.Table.AutoBill;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class AutoBills {
    public interface getAutoBill{
        void onGet(Bundle[] autoBills);
    }

    public interface Finish{
        void onEnd();
    }

    public static void getDates(getAutoBill getAuto) {
        TaskThread.onThread(() -> {
            AutoBill[] autoBills = DbManger.db.AutoBillDao().getAll();
            if (autoBills != null && autoBills.length != 0) {
                Map<String, List<Bundle>> haspMap = new HashMap<>();


                for (AutoBill autoBill : autoBills) {

                    List<Bundle> bundles;
                    if (haspMap.containsKey(autoBill.date)) {
                        bundles = haspMap.get(autoBill.date);
                   }else{
                       bundles = new ArrayList<>();
                       haspMap.put(autoBill.date,bundles);
                   }

                    Bundle bundle = new Bundle();
                    bundle.putString("date", autoBill.date);
                    bundle.putString("billinfo", autoBill.billInfo);
                    bundle.putInt("id",autoBill.id);
                    assert bundles != null;
                    bundles.add(bundle);
                    haspMap.replace(autoBill.date,bundles);
                }

                List<Map.Entry<String,List<Bundle>>> list = new ArrayList<>(haspMap.entrySet()); //转换为list
                list.sort((o1, o2) -> {
                    Double l=Double.parseDouble(o1.getKey());
                    Double r=Double.parseDouble(o2.getKey());
                    return r.compareTo(l);
                });


                List<Bundle> bundles=new ArrayList<>();

                for(int i=0;i<list.size();i++){
                    Bundle bundle=new Bundle();
                    bundle.putString("date",list.get(i).getKey());
                    List<Bundle> bundles1=list.get(i).getValue();
                    bundle.putSerializable("data", bundles1.toArray(new Bundle[0]));
                    bundles.add(bundle);
                }

                getAuto.onGet(bundles.toArray(new Bundle[0]));
                return;
            }
            getAuto.onGet(null);
        });

    }

    public static void del(int id,Finish end) {
        TaskThread.onThread(() -> {
            DbManger.db.AutoBillDao().del(id);
            end.onEnd();
        });
    }

    public static void add(BillInfo billInfo) {
        TaskThread.onThread(() -> {
            DbManger.db.AutoBillDao().add(billInfo.toString());
            AutoBill[] autoBills = DbManger.db.AutoBillDao().getLast();
            if (autoBills != null && autoBills.length != 0) {
                billInfo.setId(autoBills[0].id);
            }
        });
    }

    public static void update(int id, BillInfo billInfo) {
        TaskThread.onThread(() -> DbManger.db.AutoBillDao().update(id, billInfo.toString()));
    }

    public static void delAll(Finish end) {
        TaskThread.onThread(() -> {
            DbManger.db.AutoBillDao().delAll();
            end.onEnd();
        });
    }
}
