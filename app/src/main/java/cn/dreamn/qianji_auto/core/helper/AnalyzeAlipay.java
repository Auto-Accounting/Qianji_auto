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

package cn.dreamn.qianji_auto.core.helper;

import android.content.Context;

import java.util.List;

import cn.dreamn.qianji_auto.core.db.Table.Cache;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;

class AnalyzeAlipay {
    public final static String TAG = "alipayment";

    public static boolean paymnet(List<String> list, Context context) {
        Cache cache = Caches.getOne(TAG, "0");
        if (cache != null) {
            BillInfo billInfo = BillInfo.parse(cache.cacheData);
            Caches.del(TAG);
            CallAutoActivity.call(context, billInfo);
            return true;
        } else return false;
    }


}
