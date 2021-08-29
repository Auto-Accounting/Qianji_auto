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

package cn.dreamn.qianji_auto.ui.fragment.base.cards;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.TabAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;


@Page(name = "主页资产管理", anim = CoreAnim.slide)
public class MainCardFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    RelativeLayout title_bar;
    @BindView(R.id.title_count)
    RelativeLayout title_count;

    @BindView(R.id.tablayout)
   TabLayout tabLayout;

    @BindView(R.id.iv_left_icon)
    IconView iv_left_icon;
    @BindView(R.id.viewpage)
    ViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_cards;
    }


    @Override
    protected void initViews() {
        List<Fragment> tabFragments = new ArrayList<>();  //实例化集合
        tabFragments.add(new assertFragment());
        tabFragments.add(new bookFragment());
        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), tabFragments, new String[]{getString(R.string.assert_title), getString(R.string.assert_book)}); //参数1为fragment管理器
        viewPager.setAdapter(adapter); //给viewPager设置适配器
        tabLayout.setupWithViewPager(viewPager); //将tabLayout与viewPager建立匹配
}


    @Override
    protected View getBarView() {
        return title_count;
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        iv_left_icon.setOnClickListener(v -> popToBack());
    }


}
