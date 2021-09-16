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
import cn.dreamn.qianji_auto.ui.adapter.RemoteListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.AppUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


@Page(name = "云端识别规则", anim = CoreAnim.slide)
public class remoteFragment extends BaseFragment {


    private final String type;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private RemoteListAdapter mAdapter;
    private List<Bundle> list;
    LoadingDialog loadingDialog;

    Handler mHandler;

    public remoteFragment(String type) {
        this.type = type;
    }

    private String getName() {
        switch (type) {
            case "sms":
                return getString(R.string.sms);
            case "notice":
                return getString(R.string.notice);
            case "app":
                return getString(R.string.app);
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
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, String.format(getString(R.string.could_empty), getName()));
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
        mAdapter = new RemoteListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setEnableRefresh(true);
        String mType = this.type;
        mAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                loadingDialog = new LoadingDialog(getContext(), getString(R.string.main_loading));
                loadingDialog.show();
                Bundle bundle = list.get(position);
                String pkg = bundle.getString("pkg");


                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (loadingDialog != null)
                            loadingDialog.close();
                        Handler handler1 = this;
                        String data = (String) msg.obj;
                        switch (msg.what) {
                            case 1:
                                if (bundle.getString("count").equals("0")) {
                                    ToastUtils.show(R.string.remote_zero);
                                    return;
                                }

                                try {
                                    JSONArray jsonArray = JSONArray.parseArray(data);
                                    List<String> regular = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        String name = jsonArray.getString(i);
                                        regular.add(name);
                                    }
                                    BottomArea.listLong(getContext(), getString(R.string.remote_tip), regular, new BottomArea.ListCallback() {
                                        @Override
                                        public void onSelect(int position) {
                                            String title = regular.get(position);

                                            loadingDialog = new LoadingDialog(getContext(), getString(R.string.main_loading));
                                            loadingDialog.show();
                                            AutoBillWeb.getData(mType, pkg, title, new AutoBillWeb.WebCallback() {
                                                @Override
                                                public void onFailure() {
                                                    ToastUtils.show(R.string.remote_error);
                                                }

                                                @Override
                                                public void onSuccessful(String data) {
                                                    HandlerUtil.send(handler1, data, 2);


                                                }
                                            });

                                        }
                                    });
                                } catch (Throwable e) {
                                    ToastUtils.show(R.string.reg_error);
                                    Log.i("解析错误：" + e.toString() + "\n" + data);
                                    loadingDialog.close();
                                }

                                break;
                            case 2:

                                try {
                                    JSONObject jsonObject = JSONObject.parseObject(data);
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    if (jsonArray.size() == 0) {
                                        ToastUtils.show(R.string.reg_error);
                                        return;
                                    }
                                    String des = jsonObject.getJSONArray("data").getJSONObject(0).getString("des");
                                    String name = jsonObject.getJSONArray("data").getJSONObject(0).getString("name");
                                    BottomArea.msg(getContext(), name, des, getString(R.string.remote_download), getString(R.string.remote_cancle), new BottomArea.MsgCallback() {
                                        @Override
                                        public void cancel() {

                                        }

                                        @Override
                                        public void sure() {
                                            RegularManager.restoreFromData(getContext(), getName(), mType, data, new RegularManager.End() {
                                                @Override
                                                public void onFinish(int code) {

                                                }
                                            });
                                        }
                                    });
                                } catch (Throwable e) {
                                    ToastUtils.show(R.string.reg_error);
                                    Log.i("解析错误：" + e.toString() + "\n" + data);
                                }

                                break;
                        }
                    }
                };

                AutoBillWeb.getDataListByApp(mType, pkg, new AutoBillWeb.WebCallback() {
                    @Override
                    public void onFailure() {
                        ToastUtils.show(R.string.remote_error);
                        loadingDialog.close();
                    }

                    @Override
                    public void onSuccessful(String data) {
                        HandlerUtil.send(handler, data, 1);

                    }
                });
            }
        });

    }


    public void loadFromData() {
        statusView.showLoadingView();
        String mType = this.type;
        AutoBillWeb.getDataList(mType, new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
                HandlerUtil.send(mHandler, HANDLE_ERR);
            }

            @Override
            public void onSuccessful(String data) {
              //  Log.m("网页返回结果->  " + data);
                List<Bundle> datas = new ArrayList<>();
                try {
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String pkg = jsonObject.getString("name");
                        String count = String.valueOf(jsonObject.getInteger("count"));
                        String appName = "";
                        Log.m(mType);
                        if (mType.equals("sms")) {
                            appName = pkg;
                        } else {
                            appName = AppUtils.getAppName(getContext(), pkg);
                        }
                        if (appName.equals("unknown")) {
                            continue;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("pkg", pkg);
                        bundle.putString("name", appName);
                        bundle.putString("count", count);
                        bundle.putString("type", mType);
                        datas.add(bundle);
                    }
                } catch (Exception | Error e) {
                    Log.i("JSON解析错误！！" + e.toString());
                    e.printStackTrace();
                }
                list = datas;
                //Log.m("数据" + list.toString());
                HandlerUtil.send(mHandler, HANDLE_OK);
            }
        });

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
