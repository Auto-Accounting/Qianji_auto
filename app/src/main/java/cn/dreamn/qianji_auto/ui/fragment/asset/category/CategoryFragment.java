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

package cn.dreamn.qianji_auto.ui.fragment.asset.category;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.WidgetUtils;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;


@Page(name = "分类管理")
public class CategoryFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.tab1)
    TabLayout mTabLayout1;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initViews() {

        // 固定数量的Tab,关联ViewPager
        FragmentAdapter<TabFragmentBase> adapter = new FragmentAdapter<>(getChildFragmentManager());
        for (String page : ContentPage.getPageNames()) {
            adapter.addFragment(TabFragmentBase.newInstance(page), page);
        }
        mTabLayout1.addOnTabSelectedListener(this);
        mViewPager.setAdapter(adapter);
        mTabLayout1.setupWithViewPager(mViewPager);


        WidgetUtils.setTabLayoutTextFont(mTabLayout1);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
