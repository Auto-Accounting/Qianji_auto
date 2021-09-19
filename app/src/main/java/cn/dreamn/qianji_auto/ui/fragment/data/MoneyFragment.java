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

package cn.dreamn.qianji_auto.ui.fragment.data;

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.ui.adapter.MoneyAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "账单列表", anim = CoreAnim.slide)
public class MoneyFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;

    @BindView(R.id.recycler_view)
    SwipeRecyclerView recycler_view;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private MoneyAdapter mAdapter;
    private List<Bundle> list;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_money;
    }

    Handler mHandler;

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

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, R.string.no_money));
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });
        initLayout();
    }

    private void initLayout() {
        mAdapter=new MoneyAdapter(getContext());

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnItemListener(new MoneyAdapter.Item() {
            @Override
            public void onClick(Bundle bundle, int pos) {
                OnItemClickListen(bundle, pos);
            }

            @Override
            public void onLongClick(Bundle bundle, int pos) {

            }
        });

        refreshLayout.setEnableRefresh(true);
    }

    private void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        Task.onThread(() -> {
            AutoBills.getDates(datas -> {
                if (datas == null || datas.length == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                } else {
                    list = Arrays.asList(datas);
                    HandlerUtil.send(mHandler, HANDLE_OK);
                }
            });
        });
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(Bundle bundle, int i) {
        BillInfo billInfo = BillInfo.parse(bundle.getString("billinfo"));
        BottomArea.list(getContext(), getString(R.string.select_function), Arrays.asList(getString(R.string.money_send), getString(R.string.money_del), getString(R.string.money_copy)), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int position) {
                switch (position) {
                    case 0:
                        goBillApp(billInfo, bundle.getInt("id"));
                        /* int id = bundle.getInt("id");
                         */
                        break;
                    case 1:
                        del(bundle);
                        break;
                    case 2:
                        copy(bundle);
                        break;
                }
            }
        });

    }

    private void copy(Bundle bundle) {

        BillInfo billInfo = BillInfo.parse(bundle.getString("billinfo"));

        Context mContext = getContext();


        Tool.clipboard(mContext, billInfo.toString());

        ToastUtils.show(R.string.copied);

    }

    private void del(Bundle bundle) {
        int id = bundle.getInt("id");
        AutoBills.del(id, () -> {
            ToastUtils.show(R.string.del_success);
            HandlerUtil.send(mHandler, HANDLE_REFRESH);
        });

    }

    private void goBillApp(BillInfo billInfo, int i) {
        billInfo.setId(i);
        SendDataToApp.callNoAdd(getContext(), billInfo, i);
    }


    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        title_bar.setRightIconOnClickListener(v -> {

            BottomArea.msg(getContext(), getString(R.string.money_clean_title), getString(R.string.money_clean_body), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    AutoBills.delAll(() -> {
                        HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                    });
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
