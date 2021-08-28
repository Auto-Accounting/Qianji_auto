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

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.ui.adapter.AppAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;



@Page(name = "记账软件", anim = CoreAnim.slide)
public class AppFragment extends BaseFragment {

    @BindView(R.id.button_next)
    Button button_next;
    @BindView(R.id.app_list)
    GridView app_list;
    @BindView(R.id.help_skip_last)
    TextView help_skip_last;
    @BindView(R.id.help_skip)
    TextView help_skip;
    @BindView(R.id.help_2_choose)
    TextView help_2_choose;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_2;
    }

    @Override
    protected void initViews() {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode("helper_page", 1);
        //  ButtonUtils.disable(button_next,getContext());
        setAppList();
        Listeners();
    }

    private void setAppList() {
        Bundle[] bundles = AppManager.getAllApps();
        int width = ScreenUtils.getScreenWidth(getActivity());
        width = ScreenUtils.px2dip(getContext(), width);
        width = width - 40;//真实可分配大小
        int s = width / 80;//分80

        assert bundles != null;
        int all = bundles.length;
        if (all <= s) {
            app_list.setColumnWidth(ScreenUtils.dip2px(getContext(), (float) width / all));
            app_list.setNumColumns(all);
        }
        AppAdapter appAdapter = new AppAdapter(getContext(), R.layout.adapter_grid_item, bundles);
        app_list.setAdapter(appAdapter);
        setSelectName(AppManager.getAppInfo(getContext()).getString("appName"));
        app_list.setOnItemClickListener((parent, view, position, id) -> {
            String packageName = bundles[position].getString("appPackage");
            if (packageName == null) {
                ToastUtils.show(R.string.helper_2_tip);
                return;
            }
            setSelectName(bundles[position].getString("appName"));
            AppManager.setApp(packageName);
        });
    }



    protected void Listeners() {
        help_skip_last.setOnClickListener(v->{
            openNewPage(HelperFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("version_3_0", false);
            openNewPage(MainFragment.class);
        });
        button_next.setOnClickListener(v -> {
            openNewPage(ModeFragment.class);
        });
    }


    private void setSelectName(String name) {
        help_2_choose.setText(String.format(getResources().getString(R.string.helper_2_choose), name));
    }


}
