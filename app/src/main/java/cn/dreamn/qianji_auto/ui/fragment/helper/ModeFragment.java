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

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.ui.utils.ModeUtils;


@Page(name = "工作模式", anim = CoreAnim.slide)
public class ModeFragment extends BaseFragment {

    @BindView(R.id.button_next)
    Button button_next;
    @BindView(R.id.mode_list)
    LinearLayout mode_list;
    @BindView(R.id.mode_name)
    TextView mode_name;
    @BindView(R.id.help_skip_last)
    TextView help_skip_last;
    @BindView(R.id.help_skip)
    TextView help_skip;
    @BindView(R.id.lv_permission)
    ListView lv_permission;

    ModeUtils modeUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_3;
    }

    @Override
    protected void initViews() {
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("helper_page",2);
        setMode();
    }





    
    @Override
    protected void initListeners() {
        help_skip_last.setOnClickListener(v->{
            openNewPage(AppFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("version_3_0",false);

            openNewPage(MainFragment.class);
        });
        button_next.setOnClickListener(v->{
            openNewPage(AsyncFragment.class);
        });
    }
    private void setMode() {
        modeUtils = new ModeUtils(this, mode_list, mode_name, lv_permission);
        modeUtils.setMode();
    }



    @Override
    public void onResume() {
        super.onResume();
        MMKV mmkv=MMKV.defaultMMKV();
        modeUtils.setPermission(mmkv.getString("helper_choose", "xposed"));
    }
}
