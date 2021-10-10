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

import static cn.dreamn.qianji_auto.ui.fragment.data.NoticeFragment.KEY_DATA;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.database.Helper.AppDatas;
import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.ui.adapter.ItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "通知列表", params = {KEY_DATA}, anim = CoreAnim.slide)

public class NoticeFragment extends BaseFragment {

    public static final String KEY_DATA = "KEY_DATA";

    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recycler_view;
    @BindView(R.id.status)
    StatusView statusView;
    private ItemListAdapter mAdapter;
    private List<Bundle> list;
    Handler mHandler;

    public static void openWithType(BaseFragment baseFragment, String type) {
        //sms notice app
        PageOption.to(NoticeFragment.class)
                .setNewActivity(true)
                .putString(KEY_DATA, type)
                .open(baseFragment);
    }

    private String getType() {
        String target = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            target = bundle.getString(KEY_DATA);
        }
        return target;
    }



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice;
    }

    @SuppressLint("SetTextI18n")
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
        switch (getType()) {
            case "sms":
                title_bar.setTitle(getString(R.string.list_sms));
                tv_tip.setText(getString(R.string.list_sms_tip));
                break;
            case "notice":
                title_bar.setTitle(getString(R.string.list_notice));
                tv_tip.setText(getString(R.string.list_notice_tip));
                break;
            case "app":
                title_bar.setTitle(getString(R.string.list_app));
                tv_tip.setText(getString(R.string.list_app_tip));
                break;
        }


        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, getString(R.string.notice_empty)));
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });

        initLayout();
    }

    private void initLayout() {
        mAdapter = new ItemListAdapter(getContext());
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::itemClick);
        refreshLayout.setEnableRefresh(true);
    }

    @SuppressLint("CheckResult")
    private void itemClick(View view, int i) {
        //点击click
        if (list != null && list.size() > i) {
            Bundle item = list.get(i);
            //Log.d("item", item.toString());
            String[] strings;
            strings = new String[]{getString(R.string.notice_del), getString(R.string.notice_create), getString(R.string.regular_test), getString(R.string.regular_copy)};
            BaseFragment baseFragment = this;
            BottomArea.list(getContext(), getString(R.string.notice_choose), Arrays.asList(strings), new BottomArea.ListCallback() {
                @Override
                public void onSelect(int position) {
                    if (position == 0) {
                        AppDatas.del(item.getInt("id"));
                        HandlerUtil.send(mHandler, getString(R.string.del_success), HANDLE_REFRESH);
                    } else if (position == 1) {

                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("account1", "");
                        jsonObject.put("account2", "");
                        jsonObject.put("type", "0");
                        jsonObject.put("money", "");
                        jsonObject.put("fee", "");
                        jsonObject.put("shopName", "");
                        jsonObject.put("shopRemark", "");

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("name", getString(R.string.reg_create));
                        jsonObject2.put("text", item.getString("rawData"));
                        jsonObject2.put("regular", item.getString("rawData"));
                        jsonObject2.put("fromApp", item.getString("fromApp"));
                        jsonObject2.put("des", "");
                        jsonObject2.put("identify", getType());
                        jsonObject2.put("tableList", jsonObject);
                        WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/Regulars/index.min.html?data=" + Uri.encode(jsonObject2.toJSONString()) + "&type=" + getType());
                    } else if (position == 2) {
                        Handler mHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                BillInfo billInfo = (BillInfo) msg.obj;
                                billInfo.setFromApp(item.getString("fromApp"));
                                SendDataToApp.callNoAdd(getContext(), billInfo);
                            }
                        };
                        identifyRegulars.run(getType(), item.getString("fromApp"), item.getString("rawData"), billInfo -> {
                            if (billInfo != null) {
                                HandlerUtil.send(mHandler, billInfo, HANDLE_OK);
                            } else {
                                ToastUtils.show(R.string.regular_error);
                            }

                        });
                    } else if (position == 3) {
                        Tool.clipboard(getContext(), item.getString("rawData"));
                        ToastUtils.show(R.string.copied);
                    }
                }
            });
        }
    }

    private void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        Task.onThread(() -> {
            AppDatas.getAll(getType(), datas -> {
                if (datas == null || datas.size() == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                } else {
                    list = datas;
                    HandlerUtil.send(mHandler, HANDLE_OK);

                }
            });
        });
    }

    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });
        title_bar.setRightIconOnClickListener(v -> {
            BottomArea.msg(getContext(), getString(R.string.log_clean_title), getString(R.string.notice_clean_body), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    AppDatas.delAll(getType(), () -> {
                        HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                    });
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @Override
    protected View getBarView() {
        return title_bar;
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
