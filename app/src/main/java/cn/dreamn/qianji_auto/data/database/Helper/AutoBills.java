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


import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.AutoBill;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class AutoBills {

    public static void add(BillInfo billInfo) {
        TaskThread.onThread(() -> {
            Db.db.AutoBillDao().add(billInfo.toString());
            AutoBill[] autoBills = Db.db.AutoBillDao().getLast();
            if (autoBills != null && autoBills.length != 0) {
                billInfo.setId(autoBills[0].id);
            }
        });
    }


}
