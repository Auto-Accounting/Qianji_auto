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

package cn.dreamn.qianji_auto.ui.fragment.base.sorts;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.adapter.TabAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.cards.cardsFragment1;
import cn.dreamn.qianji_auto.ui.fragment.base.cards.cardsFragment2;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.views.IconView;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "主页分类管理", anim = CoreAnim.slide)
public class MainSortFragment extends BaseFragment {

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
        BookNames.showBookSelect(getContext(), "请选择账本", bundle -> {
            String book_id=bundle.getString("book_id");
            //等选择账本之后再显示
            List<Fragment> tabFragments = new ArrayList<>();  //实例化集合
            tabFragments.add(new sortsFragment1(book_id));
            tabFragments.add(new cardsFragment2());
            TabAdapter adapter=new TabAdapter(getChildFragmentManager(), tabFragments,new String[]{"支出","收入"}); //参数1为fragment管理器
            viewPager.setAdapter(adapter); //给viewPager设置适配器
            tabLayout.setupWithViewPager(viewPager); //将tabLayout与viewPager建立匹配
        });

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
        ThemeManager themeManager = new ThemeManager(getContext());
        themeManager.setStatusBar(getActivity(),title_count,R.color.background_white);
        iv_left_icon.setOnClickListener(v->popToBack());
        return null;
    }






}
