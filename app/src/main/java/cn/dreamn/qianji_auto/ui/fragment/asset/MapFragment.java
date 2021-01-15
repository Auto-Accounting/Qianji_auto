/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.ui.fragment.asset;

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
import cn.dreamn.qianji_auto.core.db.Asset;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.ui.adapter.MapAdapter;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;

import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_TITLE;
import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_VALUE;


@Page(name = "资产映射")
public class MapFragment extends BaseFragment {
    @BindView(R.id.ll_stateful)
    StatefulLayout mStatefulLayout;
    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;

    private MapAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_asset_map;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        initSet();
        mAdapter.setOnItemClickListener(item-> new MaterialDialog.Builder(getContext())
                .title(R.string.tip_options)
                .items(R.array.menu_values)
                .itemsCallback((dialog, itemView, position, text) ->{
                    int id = Integer.parseInt(Objects.requireNonNull(item.get(MapAdapter.KEY_ID)));
                    if(position==0){
                        Assets.delMap(id);
                        refresh();
                    }else{

                        changeMap(id,item.get(MapAdapter.KEY_TITLE));
                    }

                })
                .show());
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {
                changeMap(-1,"");
            }
        });

        return titleBar;


    }



    private void initSet(){

        WidgetUtils.initRecyclerView(recyclerView);
        mAdapter = new MapAdapter();
        recyclerView.setAdapter(mAdapter);


    }
    private void loadData() {
        new Handler().postDelayed(() -> {
            mStatefulLayout.showLoading("正在加载资产");
            Asset[] asset=Assets.getAllMap();
            List<Map<String, String>> data = new ArrayList<>();
            for (Asset assets : asset) {
                Map<String, String> item = new HashMap<>();
                item.put(KEY_TITLE, assets.name);
                item.put(KEY_VALUE, assets.mapName);
                item.put(KEY_ID, String.valueOf(assets.id));
                data.add(item);
            }
            if(data.size()==0) {
                mStatefulLayout.showEmpty("没有资产映射信息");
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
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }
    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

    private void changeMap(int id,String def){
        String[] assets=Assets.getAllAccountName();
        if(assets==null){
            SnackbarUtils.Long(getView(), "请先添加钱迹资产").info().show();
            return;
        }
        showInputDialog("添加资产映射","识别出来的资产名称",def, str->{

            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(assets)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        if(id!=-1){
                            Assets.updMap(id,str,text.toString());
                        }else{
                            Assets.addMap(str,text.toString());
                        }

                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        refresh();
                    })
                    .show();
        });

    }

}
