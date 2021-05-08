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

import android.widget.SeekBar;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.SettingUtils;
import cn.dreamn.qianji_auto.ui.views.SuperText;


@Page(name = "主页插件设置", anim = CoreAnim.slide)
public class MainSetFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;


    @BindView(R.id.pay_all)
    SuperText pay_all;
    @BindView(R.id.pay_half)
    SuperText pay_half;
    @BindView(R.id.income_all)

    SuperText income_all;

    @BindView(R.id.income_half)
    SuperText income_half;

    @BindView(R.id.default_bookName)

    SuperText default_bookName;

    @BindView(R.id.remark)

    SuperText remark;

    @BindView(R.id.auto_sort_open)

    SuperText auto_sort_open;

    @BindView(R.id.auto_sort_close)
    SuperText auto_sort_close;
    @BindView(R.id.count_down)

    SuperText count_down;
    @BindView(R.id.count_down_seekbar)
    SeekBar count_down_seekbar;

    @BindView(R.id.click_window_record)

    SuperText click_window_record;

    @BindView(R.id.click_window_edit)
    SuperText click_window_edit;

    @BindView(R.id.click_window_close)
    SuperText click_window_close;


    @BindView(R.id.long_click_window_record)

    SuperText long_click_window_record;


    @BindView(R.id.long_click_window_edit)
    SuperText long_click_window_edit;

    @BindView(R.id.long_click_window_close)
    SuperText long_click_window_close;

    @BindView(R.id.end_window_record)

    SuperText end_window_record;

    @BindView(R.id.end_window_edit)
    SuperText end_window_edit;

    @BindView(R.id.end_window_close)
    SuperText end_window_close;

    @BindView(R.id.notice_click_window_record)

    SuperText notice_click_window_record;

    @BindView(R.id.notice_click_window_edit)
    SuperText notice_click_window_edit;


    @BindView(R.id.notice_click_window_close)
    SuperText notice_click_window_close;

    @BindView(R.id.qianji_auto)

    SuperText qianji_auto;

    @BindView(R.id.qianji_ui)
    SuperText qianji_ui;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_set;
    }


    @Override
    protected void initViews() {
        SettingUtils settingUtils=new SettingUtils(getContext());
        settingUtils.init(pay_all,
                pay_half,

                income_all,
                income_half,

                default_bookName,

                remark,

                auto_sort_open,
                auto_sort_close,

                count_down,
                count_down_seekbar,

                click_window_record,
                click_window_edit,
                click_window_close,

                long_click_window_record,
                long_click_window_edit,
                long_click_window_close,

                end_window_record,
                end_window_edit,
                end_window_close,

                notice_click_window_record,
                notice_click_window_edit,
                notice_click_window_close,

                qianji_auto,
                qianji_ui);

    }

    @Override
    protected void initListeners() {
       // ankio_head.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://www.ankio.net"));
    }

    @Override
    public void onResume() {


        super.onResume();

    }

    @Override
    protected TitleBar initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        return null;
    }






}
