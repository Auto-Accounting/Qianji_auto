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

import cn.dreamn.qianji_auto.data.database.DbManger;
import cn.dreamn.qianji_auto.data.database.Table.AppData;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class AppDatas {


    public static void add(String identify, String fromApp, String rawData) {
        TaskThread.onThread(() -> DbManger.db.AppDataDao().add(rawData, identify, fromApp));
    }

    public static void del(Integer pos) {
        TaskThread.onThread(() -> DbManger.db.AppDataDao().del(pos));
    }

    public static void delAll(String identify, onEnd end) {
        TaskThread.onThread(() -> {
            DbManger.db.AppDataDao().delAll(identify);
            end.finish();
        });
    }

    public static void getAll(String identify, onResult ret) {
        TaskThread.onThread(() -> {
            AppData[] data = DbManger.db.AppDataDao().loadAll(identify);

            ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            if (data != null && data.length != 0) {
                for (AppData appData : data) {
                    Bundle bundle = new Bundle();

                    bundle.putInt("id", appData.id);
                    bundle.putString("rawData", appData.rawData);
                    bundle.putString("time", appData.time);
                    bundle.putString("fromApp", appData.fromApp);
                    bundle.putString("identify", appData.identify);
                    bundleArrayList.add(bundle);
                }
            }

            ret.onGet(bundleArrayList);
        });
    }

    public interface onEnd {
        void finish();
    }

    public interface onResult {
        void onGet(ArrayList<Bundle> bundleArrayList);
    }
}

