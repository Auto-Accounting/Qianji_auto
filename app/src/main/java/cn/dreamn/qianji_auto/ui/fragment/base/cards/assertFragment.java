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

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Asset;
import cn.dreamn.qianji_auto.ui.adapter.DataListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "资产", anim = CoreAnim.slide)
public class assertFragment extends BaseFragment {


    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.floatingActionButton)
    IconView floatingActionButton;
    private DataListAdapter mAdapter;
    private List<Bundle> list;


    Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_cards_page;
    }


    @Override
    protected void initViews() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        if (statusView != null) statusView.showEmptyView();
                        break;
                    case HANDLE_OK:
                        mAdapter.refresh(list);
                        if (statusView != null) statusView.showContentView();
                        break;
                    case HANDLE_REFRESH:
                        loadFromData();
                        break;
                }
                String d = (String) msg.obj;
                if ((d != null && !d.equals("")))
                    ToastUtils.show(d);
            }
        };
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);
        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, getString(R.string.assert_empty_info));
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));

        });
        initLayout();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0);
        });

        floatingActionButton.setOnClickListener(v -> {
            BottomArea.input(getContext(), getString(R.string.assert_input), "", getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                @Override
                public void input(String data) {
                    TaskThread.onThread(() -> {
                        Db.db.AssetDao().add(data, "https://pic.dreamn.cn/uPic/2021032022075916162492791616249279427UY2ok6支付.png", 0, "-1");
                        HandlerUtil.send(mHandler, getString(R.string.add_success), HANDLE_REFRESH);
                    });
                }

                @Override
                public void cancel() {
                }
            });


        });

    }


    private void initLayout() {
        mAdapter = new DataListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongPressDragEnabled(true);
        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 此方法在Item拖拽交换位置时被调用。
                // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(list, fromPosition, toPosition);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                //   Logs.d(mDataList.get(toPosition).get(KEY_TITLE)+"key id"+mDataList.get(toPosition).get(KEY_ID)+" to"+toPosition);
                list.get(fromPosition).getInt("id");

                TaskThread.onThread(() -> {
                    Db.db.AssetDao().setSort(list.get(fromPosition).getInt("id"), fromPosition);
                    Db.db.AssetDao().setSort(list.get(toPosition).getInt("id"), toPosition);
                });
                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {

            }

        });// 监听拖拽，更新UI。
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        refreshLayout.setEnableRefresh(true);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if (list == null || position >= list.size()) return;

        Bundle assest = list.get(position);

        BottomArea.list(getContext(), String.format(getString(R.string.assert_change), assest.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.change)), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int index) {
                switch (index) {
                    case 0:
                        del(assest);
                        break;
                    case 1:
                        change(assest);
                        break;
                }
            }
        });


    }

    @SuppressLint("CheckResult")
    private void change(Bundle assest) {

        BottomArea.input(getContext(), getString(R.string.assert_change_name), assest.getString("name"), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
            @Override
            public void input(String data) {
                TaskThread.onThread(() -> {
                    Db.db.AssetDao().update(assest.getInt("id"), data);
                    HandlerUtil.send(mHandler, getString(R.string.assert_change_success), HANDLE_REFRESH);
                });
            }

            @Override
            public void cancel() {

            }
        });
    }

    private void del(Bundle assest) {
        BottomArea.msg(getContext(), getString(R.string.del_title), String.format(getString(R.string.assert_del_msg), assest.getString("name")), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
            @Override
            public void cancel() {

            }

            @Override
            public void sure() {
                TaskThread.onThread(() -> {
                    Db.db.AssetDao().del(assest.getInt("id"));
                    HandlerUtil.send(mHandler, getString(R.string.del_success), HANDLE_REFRESH);
                });
            }
        });
    }


    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        TaskThread.onThread(() -> {
            TaskThread.onThread(() -> {
                Asset[] assets = Db.db.AssetDao().getAll(0, 200);
                if (assets == null || assets.length == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                    return;
                }

                if (list != null) {
                    list.clear();
                } else {
                    list = new ArrayList<>();
                }
                for (Asset asset : assets) {
                    Bundle bundle = Tool.class2Bundle(asset);
                    list.add(bundle);
                    HandlerUtil.send(mHandler, HANDLE_OK);
                }

            });

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }


}
