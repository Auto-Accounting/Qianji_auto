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
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Asset;
import cn.dreamn.qianji_auto.core.db.BookName;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.ui.adapter.AssetAdapter;
import cn.dreamn.qianji_auto.ui.adapter.MapAdapter;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;

import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_TITLE;
import static cn.dreamn.qianji_auto.ui.adapter.MapAdapter.KEY_VALUE;


@Page(name = "账本管理")
public class BookFragment extends StateFragment {

    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private AssetAdapter mAdapter;

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        initSet();
        mAdapter.setOnItemClickListener(item -> new MaterialDialog.Builder(getContext())
                .title(R.string.tip_options)
                .items(R.array.menu_values)
                .itemsCallback((dialog, itemView, position, text) -> {
                    int id = Integer.parseInt(Objects.requireNonNull(item.get(MapAdapter.KEY_ID)));
                    if (position == 0) {
                        BookNames.del(id);
                        refresh();
                    } else {

                        change(id, item.get(AssetAdapter.KEY_TITLE));
                    }

                })
                .show());
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {
                change(-1, "");
            }
        });

        return titleBar;


    }


    private void initSet() {

        WidgetUtils.initRecyclerView(recyclerView);
        mAdapter = new AssetAdapter();
        recyclerView.setAdapter(mAdapter);


    }

    private void loadData() {
        new Handler().postDelayed(() -> {

            BookName[] bookNames = BookNames.getAllWith();
            List<Map<String, String>> data = new ArrayList<>();
            for (BookName bookName : bookNames) {
                Map<String, String> item = new HashMap<>();
                item.put(KEY_TITLE, bookName.name);
                item.put(KEY_ID, String.valueOf(bookName.id));
                data.add(item);
            }
            if (data.size() == 0) {
                showEmpty("没有账本信息");
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

    private void change(int id, String def) {

        showInputDialog("添加账本", "钱迹里的账本名称", def, str -> {

            if (id != -1) {
                BookNames.upd(id, str);
            } else {
                BookNames.add(str);
            }

            SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
            refresh();
        });

    }

}
