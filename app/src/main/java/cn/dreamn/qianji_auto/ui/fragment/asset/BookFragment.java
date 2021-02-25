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

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.adapter.ListAdapter3;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;


@Page(name = "账本管理")
public class BookFragment extends StateFragment {

    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private ListAdapter3 mAdapter;

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        initSet();
        mAdapter.setOnItemClickListener((ListAdapter3.OnItemClickListener) (item, pos) -> new MaterialDialog.Builder(getContext())
                .title(item.getString("name"))
                .items(R.array.menu_values)
                .itemsCallback((dialog, itemView, position, text) -> {
                    int id = item.getInt("id");
                    Logs.d(item.toString());
                    if (position == 0) {
                        BookNames.del(id);
                        refresh();
                    } else {

                        change(id, item.getString("name"));
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
        mAdapter = new ListAdapter3(getContext());
        recyclerView.setAdapter(mAdapter);


    }

    private void loadData() {
        new Handler().postDelayed(() -> {

            Bundle[] bundles = BookNames.getAllIcon(false);
            if (bundles.length <= 0) {
                showEmpty("没有账本信息");
                return;
            }

            mAdapter.refresh(Arrays.asList(bundles));
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
        String title;
        if (id != -1) {
            title = "修改账本";
        } else {
            title = "添加账本";
        }

        showInputDialog(title, "钱迹里的账本名称", def, str -> {

            if (id != -1) {
                BookNames.upd(id, str);
            } else {
                BookNames.add(str);
            }

            SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
            refresh();
        });

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
