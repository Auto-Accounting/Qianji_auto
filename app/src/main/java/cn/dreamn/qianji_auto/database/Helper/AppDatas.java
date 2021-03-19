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
import cn.dreamn.qianji_auto.database.Table.AppData;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class AppDatas {


    public static void add(String identify,String fromApp,String rawData){
        Task.onThread(()-> DbManger.db.AppDataDao().add(rawData,identify,fromApp));
    }

    public static void del(Integer pos) {
        Task.onThread(()-> DbManger.db.AppDataDao().del(pos));
    }


    public static void delAll(String identify,String fromApp) {
        Task.onThread(()-> DbManger.db.AppDataDao().delAll(identify,fromApp));
    }

    interface onResult{
        void onGet(AppData[] datas);
    }

    public static void getAll(String identify,String fromApp,onResult ret) {
        Task.onThread(()-> ret.onGet(DbManger.db.AppDataDao().loadAll(identify,fromApp)));
    }
}

