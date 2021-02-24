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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;


@Page(name = "分类管理")
public class CategoryFragment extends StateFragment implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.tab1)
    TabLayout mTabLayout1;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    FragmentAdapter<TabFragmentBase> adapter;

    private String book_id = "-1";
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_state2;
    }

    @Override
    protected void initViews() {
        if (BookNames.getAllLen() == 0) {
            refresh();
        } else {
            select();
        }


    }

    private void select() {
        BookNames.showBookSelect(getContext(), "请选择账本", bundle -> {
            book_id = bundle.getString("book_id");
            if (book_id == null || book_id.equals("")) book_id = "-1";
            refresh();
        });
    }

    private void refresh() {
        showLoading("分类数据加载中...");
        new Handler().postDelayed(() -> {
            // 固定数量的Tab,关联ViewPager
            adapter = new FragmentAdapter<>(getChildFragmentManager());
            for (String page : ContentPage.getPageNames()) {
                adapter.addFragment(TabFragmentBase.newInstance(page, book_id), page);
            }
            mTabLayout1.addOnTabSelectedListener(this);
            mViewPager.setAdapter(adapter);
            mTabLayout1.setupWithViewPager(mViewPager);


            WidgetUtils.setTabLayoutTextFont(mTabLayout1);
            showContent();
        }, 1000);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("添加") {
            @Override
            public void performAction(View view) {

                //Logs.d("添加分类");
                change(-1, "-1", "1", "-1", "");

            }
        });
        titleBar.addAction(new TitleBar.TextAction("切换") {
            @Override
            public void performAction(View view) {

                select();

            }
        });
        return titleBar;
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

    public void change(int id, String parent_id, String level, String type, String def) {

        if (type.equals("-1")) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_cate)
                    .itemsCallback((dialog, itemView, position, text) -> {

                        change(id, parent_id, level, String.valueOf(position), def);

                    })
                    .show();
        } else {
            showInputDialog("添加分类", "请输入分类名称", def, str -> {

                if (id != -1) {
                    if (!CategoryNames.update(id, str, type, book_id)) {
                        SnackbarUtils.Long(getView(), getString(R.string.set_failed)).info().show();
                    } else {
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        //  adapterData.refresh(expandableListView,0);
                        refresh();
                    }
                } else {
                    if (!CategoryNames.insert(str, "", level, type, "", parent_id, book_id)) {
                        SnackbarUtils.Long(getView(), getString(R.string.set_failed)).info().show();
                    } else {
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        // adapterData.refresh(expandableListView,0);
                        refresh();
                    }
                }
            });
        }


    }

    public void showInputDialog(String title, String tip, String def, CallBack callBack) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(tip)
                .input(
                        getString(R.string.input_tip),
                        def,
                        false,
                        ((dialog, input) -> {
                        })
                )
                .positiveText(getString(R.string.input_ok))
                .negativeText(getString(R.string.set_cancel))
                .onPositive((dialog, which) -> callBack.onResponse(dialog.getInputEditText().getText().toString()))
                .show();
    }

    // 回调接口
    public interface CallBack {
        void onResponse(String data);
    }
}
