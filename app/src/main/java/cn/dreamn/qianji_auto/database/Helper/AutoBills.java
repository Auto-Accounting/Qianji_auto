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

package cn.dreamn.qianji_auto.database.Helper;


import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.AutoBill;
import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class AutoBills {
    public interface getAutoBill{
        void onGet(AutoBill[] autoBills);
    }
    public static void getAll(getAutoBill getAuto) {
        Task.onThread(()-> getAuto.onGet(DbManger.db.AutoBillDao().getAll()));

    }

    public static void del(int id) {
        Task.onThread(()->DbManger.db.AutoBillDao().del(id));
    }

    public static void add(BillInfo billInfo) {
        Task.onThread(()->DbManger.db.AutoBillDao().add(billInfo.toString()));
    }

    public static void update(int id, BillInfo billInfo) {
        Task.onThread(()->DbManger.db.AutoBillDao().update(id, billInfo.toString()));
    }

    public static void delAll() {
        Task.onThread(()->DbManger.db.AutoBillDao().delAll());
    }
}
