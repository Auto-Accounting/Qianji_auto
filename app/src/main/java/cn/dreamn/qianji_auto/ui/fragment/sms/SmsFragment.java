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

package cn.dreamn.qianji_auto.ui.fragment.sms;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Sms;
import cn.dreamn.qianji_auto.core.utils.Smses;
import cn.dreamn.qianji_auto.ui.adapter.SmsAdapter;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;

import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_DENY;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_NUM;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_REGEX;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_TITLE;


@Page(name = "短信识别")
public class SmsFragment extends BaseFragment {
    @BindView(R.id.ll_stateful)
    StatefulLayout mStatefulLayout;
    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;

    private SmsAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auto_catgory;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        WidgetUtils.initRecyclerView(recyclerView);
        mAdapter = new SmsAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(item-> new MaterialDialog.Builder(getContext())
                .title(item.get(KEY_TITLE))
                .items(R.array.menu_values_req2)
                .itemsCallback((dialog, itemView, position, text) ->{
                    int id = Integer.parseInt(Objects.requireNonNull(item.get(KEY_ID)));
                    if(position==0){
                        Smses.del(id);
                        SnackbarUtils.Long(getView(), getString(R.string.del_success)).info().show();
                        refresh();
                    }else if(position==1){
                        Bundle params = new Bundle();
                        params.putString("id",String.valueOf(id));
                        params.putString("num",item.get(KEY_NUM));
                        params.putString("regex",item.get(KEY_REGEX));
                        params.putString("title",item.get(KEY_TITLE));
                        openPage(EditFragment.class,params);
                    }else if(position==2){
                        Smses.deny(id);
                        SnackbarUtils.Long(getView(), getString(R.string.deny_success)).info().show();
                        refresh();
                    }else if(position==3){
                        Smses.enable(id);
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

            }
        });

        return titleBar;


    }




    private void loadData() {
        new Handler().postDelayed(() -> {
            mStatefulLayout.showLoading("正在加载短信识别规则");
            Sms[] sms= Smses.getAll();
            List<Map<String, String>> data = new ArrayList<>();
            for (Sms value : sms) {
                Map<String, String> item = new HashMap<>();
                /**
                 *   params.putString("id",String.valueOf(id));
                 *                         params.putString("num",item.get(KEY_NUM));
                 *                         params.putString("regex",item.get(KEY_REGEX));
                 *                         params.putString("title",item.get(KEY_TITLE));
                 */
                item.put(KEY_TITLE, value.name);
                item.put(KEY_REGEX, value.regular);
                item.put(KEY_DENY,value.use==1?"false":"true");
                item.put(KEY_ID, String.valueOf(value.id));
                item.put(KEY_NUM, value.smsNum);
                data.add(item);
            }
            if(data.size()==0) {
                mStatefulLayout.showEmpty("没有任何短信识别规则");
                return;
            }

            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
            mStatefulLayout.showContent();
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
