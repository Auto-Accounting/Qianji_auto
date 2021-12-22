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

import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.LineLay;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.utils.SettingUtils;


@Page(name = "主页插件设置", anim = CoreAnim.slide)
public class MainSetFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    TitleBar title_bar;

    @BindView(R.id.set_lazy_mode)
    LineLay set_lazy_mode;
    @BindView(R.id.set_front)
    LineLay set_front;
    @BindView(R.id.set_back)
    LineLay set_back;
    @BindView(R.id.set_default)
    LineLay set_default;
    @BindView(R.id.set_remark)
    LineLay set_remark;
    @BindView(R.id.set_sort)
    LineLay set_sort;
    @BindView(R.id.set_float_click)
    LineLay set_float_click;
    @BindView(R.id.set_float_time)
    LineLay set_float_time;
    @BindView(R.id.set_float_long_click)
    LineLay set_float_long_click;
    @BindView(R.id.set_float_time_end)
    LineLay set_float_time_end;
    @BindView(R.id.set_notice_click)
    LineLay set_notice_click;
    @BindView(R.id.set_float_style)
    LineLay set_float_style;

    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.set_app)
    LineLay set_app;
    @BindView(R.id.set_need_cate)
    LineLay set_need_cate;

    @BindView(R.id.set_lock)
    LineLay set_lock;
    @BindView(R.id.set_lock_qianji)
    LineLay set_lock_qianji;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_set;
    }


    @Override
    protected void initViews() {
        SettingUtils settingUtils = new SettingUtils(getContext());
        settingUtils.init(set_app, set_need_cate, set_lazy_mode,
                set_front,
                set_back,
                set_default,
                set_remark,
                set_sort,
                set_float_time,
                set_float_click,
                set_float_long_click,
                set_float_time_end,
                set_notice_click,
                set_float_style, textView6, textView7, textView8, set_lock, set_lock_qianji);

    }


    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
    }






}
