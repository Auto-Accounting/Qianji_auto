/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.ui.fragment.helper;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;


@Page(name = "引导设置", anim = CoreAnim.slide)
public class HelperFragment extends BaseFragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.help_skip)
    TextView help_skip;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.button_go_setting)
    TextView button_go_setting;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_1;
    }

    @Override
    protected void initViews() {
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("helper_page",0);
        Listeners();
    }


    protected void Listeners() {
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("version_3_0",false);

            openNewPage(MainFragment.class);
        });
        button_go_setting.setOnClickListener(v-> openNewPage(AppFragment.class));
    }




    


}
