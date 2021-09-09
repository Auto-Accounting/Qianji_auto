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

package cn.dreamn.qianji_auto.ui.fragment.data.sort;

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
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
import cn.dreamn.qianji_auto.ui.adapter.RemoteSortListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

@Page(name = "云端分类", anim = CoreAnim.slide)
public class remoteFragment extends BaseFragment {


    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private RemoteSortListAdapter mAdapter;
    private List<Bundle> list;
    LoadingDialog loadingDialog;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    statusView.showContentView();
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_data_remote;
    }

    @Override
    protected void initViews() {

        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, String.format(getString(R.string.could_empty), getString(R.string.auto)));
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0/*,false*/);//传入false表示刷新失败
        });

    }

    private void initLayout() {
        mAdapter = new RemoteSortListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                loadingDialog = new LoadingDialog(getContext(), getString(R.string.main_loading));
                loadingDialog.show();
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        String data = (String) msg.obj;
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(data);
                            String des = jsonObject.getString("des");
                            BottomArea.msg(getContext(), list.get(position).getString("name"), des, getString(R.string.remote_download), getString(R.string.remote_cancle), new BottomArea.MsgCallback() {
                                @Override
                                public void cancel() {

                                }

                                @Override
                                public void sure() {
                                    RegularManager.restoreFromData(getContext(), getString(R.string.auto), "category", data, new RegularManager.End() {
                                        @Override
                                        public void onFinish(int code) {

                                        }
                                    });
                                }
                            });
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Log.i("解析错误：" + e.toString() + "\n" + data);
                        }
                        loadingDialog.close();
                    }
                };
                AutoBillWeb.getCategory(list.get(position).getString("name"), new AutoBillWeb.WebCallback() {
                    @Override
                    public void onFailure() {
                        ToastUtils.show(R.string.remote_error);
                    }

                    @Override
                    public void onSuccessful(String data) {
                        HandlerUtil.send(handler, data, 0);

                    }
                });
            }
        });
        refreshLayout.setEnableRefresh(true);
        loadFromData();
    }


    public void loadFromData() {
        AutoBillWeb.getCategoryList(new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
                HandlerUtil.send(mHandler, HANDLE_ERR);
            }

            @Override
            public void onSuccessful(String data) {
                // Log.m("网页返回结果->  " + data);
                List<Bundle> datas = new ArrayList<>();
                try {
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String str = jsonArray.getString(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", str);
                        datas.add(bundle);
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    Log.i("JSON解析错误！！" + e.toString());
                    e.printStackTrace();
                }
                list = datas;
                //  Log.m("数据" + list.toString());
                HandlerUtil.send(mHandler, HANDLE_OK);
            }
        });
    }


}
