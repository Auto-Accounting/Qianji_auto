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

package cn.dreamn.qianji_auto.ui.fragment.data.regulars;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.adapter.RemoteListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.AutoBillWeb;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


@Page(name = "APP通知云端规则页面", anim = CoreAnim.slide)
public class outFragment extends BaseFragment {


    private final String type;
    private final boolean isWeb;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    Handler mHandler;
    private RemoteListAdapter mAdapter;
    private List<Bundle> list;

    public outFragment(String type, Boolean isWeb) {
        this.type = type;
        this.isWeb = isWeb;
    }

    private String getName() {
        switch (type) {
            case "notice":
                return getString(R.string.notice);
            case "app":
                return getString(R.string.app);
            case "sms":
                return getString(R.string.sms);
        }
        return "";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data_remote;
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
            viewHolder.setText(R.id.empty_info, String.format(getString(R.string.no_regular), getName()));
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
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });

    }

    private void initLayout() {
        mAdapter = new RemoteListAdapter(getContext(), isWeb);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setEnableRefresh(true);
        mAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Bundle bundle = list.get(position);
                String pkg = bundle.getString("pkg", null);
                bundle.putString("type", type);
                bundle.putString("fromApp", pkg);
                if (!isWeb) {
                    openNewPage(localFragment.class, "data", bundle);
                }

            }
        });

    }


    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        if (isWeb) {
            AutoBillWeb.getDataList(type, new AutoBillWeb.WebCallback() {
                @Override
                public void onFailure() {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                }

                @Override
                public void onSuccessful(String data) {
                    //  Log.i("网页返回结果->  " + data);
                    List<Bundle> datas = new ArrayList<>();
                    try {
                        JSONArray jsonArray = JSONArray.parseArray(data);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String pkg = jsonObject.getString("name");
                            String count = String.valueOf(jsonObject.getInteger("count"));
                            String appName;

                            if (type.equals("sms")) {
                                appName = pkg;
                            } else {
                                appName = AppInfo.getName(getContext(), pkg);
                            }
                            if (appName.equals("unknown")) {
                                continue;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("pkg", pkg);
                            bundle.putString("name", appName);
                            bundle.putString("count", count);
                            bundle.putString("type", type);
                            datas.add(bundle);
                        }
                    } catch (Exception | Error e) {
                        Log.i("JSON解析错误！！" + e.toString());
                        e.printStackTrace();
                    }
                    list = datas;
                    //Log.i("数据" + list.toString());
                    HandlerUtil.send(mHandler, HANDLE_OK);
                }
            });
        } else {
            identifyRegulars.getAllApps(type, strings -> {
                list = strings;
                HandlerUtil.send(mHandler, HANDLE_OK);
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
