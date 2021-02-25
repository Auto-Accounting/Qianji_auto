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

package cn.dreamn.qianji_auto.ui.activity;

import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.ui.core.BaseActivity;
import cn.dreamn.qianji_auto.ui.fragment.HelperFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 程序入口，空壳容器
 *
 * @author xuexiang
 * @since 2019-07-07 23:53
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MMKV kv = MMKV.defaultMMKV();

        if (kv.getBoolean("first", true)) {
            Logs.d("第一次使用，加载引导页面。");
            openPage(HelperFragment.class);
        } else {

            openPage(MainFragment.class);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
