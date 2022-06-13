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

package cn.dreamn.qianji_auto.ui.fragment.base;

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

import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Helper.Assets;
import cn.dreamn.qianji_auto.data.database.Table.Asset;
import cn.dreamn.qianji_auto.ui.adapter.MapListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


@Page(name = "主页资产映射", anim = CoreAnim.slide)
public class MainMapFragment extends BaseFragment {
    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.floatingActionButton)
    IconView floatingActionButton;
    private MapListAdapter mAdapter;
    private List<Bundle> list;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_map;
    }

    Handler mHandler;

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
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
            viewHolder.setText(R.id.empty_info, getString(R.string.no_map));
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });

        initLayout();

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0);
        });
        floatingActionButton.setOnClickListener(v -> {

            BottomArea.inputWithCheckBox(getContext(), getString(R.string.assert_change_name),"", getString(R.string.set_sure), getString(R.string.set_cancle), "正则模式", false, new BottomArea.InputWithCheckBoxCallback() {
                @Override
                public void inputWithCheck(String data, boolean checked) {
                    Assets.showAssetSelect(getContext(), getString(R.string.assert_choose), false, obj -> {
                        Asset asset2s = (Asset) obj;
                        TaskThread.onThread(() -> {
                            Db.db.AssetMapDao().add((checked ? "reg:" + data : data), asset2s.name);
                            HandlerUtil.send(mHandler, getString(R.string.add_success), HANDLE_REFRESH);
                        });
                    });
                }
                @Override
                public void cancel() {

                }
            });

        });

    }


    private void initLayout() {
        mAdapter = new MapListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        refreshLayout.setEnableRefresh(true);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if (list == null || position >= list.size()) return;

        Bundle assets = list.get(position);
        BottomArea.list(getContext(), String.format(getString(R.string.assert_change), assets.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.change)), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int index) {
                switch (index) {
                    case 0:
                        del(assets);
                        break;
                    case 1:
                        change(assets);
                        break;
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void change(Bundle assets) {
        String name = assets.getString("name");
        BottomArea.inputWithCheckBox(getContext(), getString(R.string.assert_change_name), name.replaceAll("reg:", ""), getString(R.string.set_sure), getString(R.string.set_cancle), "正则模式", name.startsWith("reg:"), new BottomArea.InputWithCheckBoxCallback() {
            @Override
            public void inputWithCheck(String data, boolean checked) {
                Assets.showAssetSelect(getContext(), getString(R.string.assert_choose), false, obj -> {
                    Asset asset2s = (Asset) obj;
                    TaskThread.onThread(() -> {
                        Db.db.AssetMapDao().update(assets.getInt("id"), checked ? "reg:" + data : data, asset2s.name);
                        HandlerUtil.send(mHandler, getString(R.string.assert_change_success), HANDLE_REFRESH);
                    });
                });
            }
            @Override
            public void cancel() {

            }
        });


        }


    private void del(Bundle assets) {
        BottomArea.msg(getContext(), getString(R.string.del_title), String.format(getString(R.string.assert_del_msg), assets.getString("name")), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
            @Override
            public void cancel() {

            }

            @Override
            public void sure() {
                TaskThread.onThread(() -> {
                    Db.db.AssetMapDao().del(assets.getInt("id"));
                    HandlerUtil.send(mHandler, getString(R.string.del_success), HANDLE_REFRESH);
                });
            }
        });
    }


    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        TaskThread.onThread(() -> {
            Assets.getAllMap(obj -> {
                List<Bundle> list1 = (List<Bundle>) obj;
                if (list1 == null || list1.size() == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                } else {
                    list = list1;
                    HandlerUtil.send(mHandler, HANDLE_OK);
                }
            });
        });
    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        //  return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
