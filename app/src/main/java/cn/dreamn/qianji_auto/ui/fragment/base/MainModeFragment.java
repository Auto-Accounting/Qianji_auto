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

package cn.dreamn.qianji_auto.ui.fragment.base;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.ModeUtils;


@Page(name = "主页模式选择", anim = CoreAnim.slide)
public class MainModeFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;

    @BindView(R.id.mode_list)
    LinearLayout mode_list;
    @BindView(R.id.mode_name)
    TextView mode_name;
    @BindView(R.id.lv_permission)
    ListView lv_permission;
    ModeUtils modeUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_mode;
    }


    @Override
    protected void initViews() {
        modeUtils = new ModeUtils(this, mode_list, mode_name, lv_permission);
        modeUtils.setMode();
    }

    @Override
    protected void initListeners() {
       // ankio_head.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://www.ankio.net"));
    }

    @Override
    public void onResume() {

        MMKV mmkv=MMKV.defaultMMKV();
        modeUtils.setPermission(mmkv.getString("helper_choose", "xposed"));
        super.onResume();

    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        // return null;
    }






}
