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

package cn.dreamn.qianji_auto.core.base.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Map;

import cn.dreamn.qianji_auto.core.db.Helper.Assets;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.db.Table.Asset;
import cn.dreamn.qianji_auto.core.db.Table.Asset2;
import cn.dreamn.qianji_auto.core.hook.app.qianji.Data;
import cn.dreamn.qianji_auto.core.utils.ServerManger;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.ui.activity.MainActivity;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class QianjiBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        /**
         *
         Bundle bundle=new Bundle();
         bundle.putParcelableArrayList("asset",asset);
         bundle.putParcelableArrayList("category",asset);
         bundle.putParcelableArrayList("userBook",asset);
         */

        Bundle extData = intent.getExtras();
        if (extData == null) return;

        ArrayList<Data> asset = extData.getParcelableArrayList("asset");

        ArrayList<Data> category = extData.getParcelableArrayList("category");

        ArrayList<Data> userBook = extData.getParcelableArrayList("userBook");
        if (asset != null && category != null && userBook != null) {
            Logs.i("钱迹资产信息有效");
            Assets.cleanAsset();
            for (int i = 0; i < asset.size(); i++) {
                Data d = asset.get(i);
                Bundle m = d.get();
                Assets.addAsset(m.getString("name"), m.getString("icon"));
            }
            CategoryNames.clean();
            for (int i = 0; i < category.size(); i++) {
                Data d = category.get(i);
                Bundle m = d.get();
                CategoryNames.insert(m.getString("name"), m.getString("icon"), m.getString("level"), m.getString("type"), m.getString("id"), m.getString("parent"));
            }
            BookNames.clean();
            for (int i = 0; i < userBook.size(); i++) {
                Data d = userBook.get(i);
                Bundle m = d.get();
                BookNames.add(m.getString("name"), m.getString("cover"));
            }
            Intent intent1 = new Intent();
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setClass(context, MainActivity.class);
            context.startActivity(intent1);
        }

    }
}
