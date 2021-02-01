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

package cn.dreamn.qianji_auto.ui.fragment.category;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Regular;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.ui.adapter.CateAdapter;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;

import static cn.dreamn.qianji_auto.ui.adapter.CateAdapter.KEY_DATA;
import static cn.dreamn.qianji_auto.ui.adapter.CateAdapter.KEY_DENY;
import static cn.dreamn.qianji_auto.ui.adapter.CateAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.CateAdapter.KEY_TITLE;
import static cn.dreamn.qianji_auto.ui.adapter.CateAdapter.KEY_VALUE;


@Page(name = "自动分类")
public class CategoryFragment extends StateFragment {

    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;

    private CateAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;



    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        WidgetUtils.initRecyclerView(recyclerView);
        mAdapter = new CateAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(item-> new MaterialDialog.Builder(getContext())
                .title(R.string.tip_options)
                .items(R.array.menu_values_req)
                .itemsCallback((dialog, itemView, position, text) ->{
                    int id = Integer.parseInt(Objects.requireNonNull(item.get(KEY_ID)));
                    if(position==0){
                        Category.del(id);
                        SnackbarUtils.Long(getView(), getString(R.string.del_success)).info().show();
                        refresh();
                    }else if(position==1){
                        Bundle params = new Bundle();
                        params.putString("id",String.valueOf(id));
                        params.putString("data",item.get(KEY_DATA));
                        openPage(EditFragment.class,params);
                    }else if(position==2){
                        CookieBar.builder(getActivity())
                                .setTitle(R.string.helper_tip6)
                                .setMessage(R.string.helper_tip_qianji6)
                                .setDuration(-1)
                                .setActionColor(android.R.color.white)
                                .setTitleColor(android.R.color.white)
                                .setAction(R.string.helper_qianji_err, view1 -> {}).setBackgroundColor(R.color.colorPrimary).show();
                    }else if(position==3){
                        Category.deny(id);
                        SnackbarUtils.Long(getView(), getString(R.string.deny_success)).info().show();
                        refresh();
                    }else if(position==4){
                        Category.enable(id);
                        SnackbarUtils.Long(getView(), getString(R.string.enable_success)).info().show();
                        refresh();
                    }

                })
                .show());
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refresh();
    }


    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();

        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {

                openPage(EditFragment.class,true);

                /**
                 * // 设置需要传递的参数
                 * Bundle params = new Bundle();
                 * params.putBoolean(DateReceiveFragment.KEY_IS_NEED_BACK, false);
                 * int id = (int) (Math.random() * 100);
                 * params.putString(DateReceiveFragment.KEY_EVENT_NAME, "事件" + id);
                 * params.putString(DateReceiveFragment.KEY_EVENT_DATA, "事件" + id + "携带的数据");
                 */
            }
        });

        return titleBar;


    }




    private void loadData() {
        new Handler().postDelayed(() -> {
           // mStatefulLayout.showLoading("正在加载分类规则");
            Regular[] regulars= Category.getAll();
            List<Map<String, String>> data = new ArrayList<>();
            for (Regular regular : regulars) {
                Map<String, String> item = new HashMap<>();
                item.put(KEY_TITLE, regular.name);
                item.put(KEY_VALUE, regular.cate);
                item.put(KEY_DENY,regular.use==1?"false":"true40");
                item.put(KEY_ID, String.valueOf(regular.id));
                item.put(KEY_DATA, regular.tableList);
                data.add(item);
            }
            if(data.size()==0) {
                showEmpty("没有任何分类规则");
                return;
            }

            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
            showContent();
        }, 1000);
    }

    @Override
    protected void initListeners() {

    }
    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

}
