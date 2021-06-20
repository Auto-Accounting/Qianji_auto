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

package cn.dreamn.qianji_auto.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mmkv.MMKV;


public class AppBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        MMKV mmkv = MMKV.defaultMMKV();
        if (!mmkv.getBoolean("needAsync", false)) return;
        mmkv.encode("needAsync", false);


       /* Bundle extData = intent.getExtras();
        if (extData == null) return;

        ArrayList<Data> asset = extData.getParcelableArrayList("asset");

        ArrayList<Data> category = extData.getParcelableArrayList("category");

        ArrayList<Data> userBook = extData.getParcelableArrayList("userBook");
        if (asset != null && category != null && userBook != null) {
            Logs.i("钱迹数据信息有效");
            Assets.cleanAsset();
            for (int i = 0; i < asset.size(); i++) {
                Data d = asset.get(i);
                Bundle m = d.get();
                Assets.addAsset(m.getString("name"), m.getString("icon"), m.getInt("sort"));
            }
            CategoryNames.clean();
            for (int i = 0; i < category.size(); i++) {
                Data d = category.get(i);
                Bundle m = d.get();
                CategoryNames.insert(m.getString("name"), m.getString("icon"), m.getString("level"), m.getString("type"), m.getString("id"), m.getString("parent"), m.getString("book_id"), m.getString("sort"));
            }
            BookNames.clean();
            for (int i = 0; i < userBook.size(); i++) {
                Data d = userBook.get(i);
                Bundle m = d.get();
                BookNames.add(m.getString("name"), m.getString("cover"), m.getString("id"));
            }
            Intent intent1 = new Intent();
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setClass(context, MainActivity.class);
            context.startActivity(intent1);
            Logs.i("钱迹数据同步完毕");
        }*/

    }
}
