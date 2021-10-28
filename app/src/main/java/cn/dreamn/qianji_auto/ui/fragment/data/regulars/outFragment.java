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

import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Regular;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.adapter.RemoteListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "APP规则页面", anim = CoreAnim.slide)
public class outFragment extends BaseFragment {
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.multiple_actions_down)
    FloatingActionsMenu floatingActionButton;
    @BindView(R.id.action_cate)
    FloatingActionButton action_cate;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.action_import)
    FloatingActionButton action_import;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.action_export)
    FloatingActionButton action_export;
    @BindView(R.id.action_delAll)
    FloatingActionButton action_delAll;
    LoadingDialog loadingDialog;
    private String type;
    private String app;
    Handler mHandler;
    private boolean isWeb;
    private RemoteListAdapter rAdapter;
    private CateItemListAdapter cAdapter;
    private List<Bundle> list;

    public outFragment() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.type = bundle.getString("type");
            this.isWeb = bundle.getBoolean("isWeb");
            this.app = bundle.getString("app");

        }
    }

    public outFragment(String type, Boolean isWeb) {
        this.type = type;
        this.isWeb = isWeb;
        this.app = "";

    }

    private String getType() {
        return this.type;
    }

    private String getName() {
        switch (type) {
            case "notice":
            case "notice_detail":
                return getString(R.string.notice);
            case "app":
            case "app_detail":
                return getString(R.string.app);
            case "sms":
                return getString(R.string.sms);
            case "category":
                return getString(R.string.category);
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
                        if (isDataList()) {
                            cAdapter.refresh(list);
                        } else {
                            rAdapter.refresh(list);
                        }

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

    private boolean isDataList() {
        return (type.equals("category") || (type.equals("sms") && !isWeb) || type.equals("notice_detail") || type.equals("app_detail"));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        BaseFragment baseFragment = this;
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadFromData();
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });

        if (!isWeb) {
            action_cate.setOnClickListener(v -> {
                if (type.equals("category")) {
                    BottomArea.list(getContext(), getString(R.string.select_edit), Arrays.asList(getString(R.string.edit_default), getString(R.string.edit_js)), index -> {
                        switch (index) {
                            case 0:
                                WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/index.html");
                                break;
                            case 1:
                                WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/js.html");
                                break;

                        }
                    });
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", this.type);
                    WebViewFragment.openUrl(this, "file:///android_asset/html/reg/index.html", jsonObject.toJSONString());
                }


            });
            action_import.setOnClickListener(v -> {

                RegularManager.importReg(getContext(), getName(), getType(), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {
                    }
                });

            });
            action_export.setOnClickListener(v -> {
                RegularManager.output(getContext(), getName(), getType());
            });
            action_delAll.setOnClickListener(v -> {
                BottomArea.msg(getContext(), getString(R.string.log_clean_title), String.format(getString(R.string.reg_clean_body), getName()), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void sure() {
                        TaskThread.onThread(() -> {
                            Db.db.RegularDao().clean(getType());
                            HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                        });
                    }
                });

            });
        }

    }

    private void initLayout() {
        BaseFragment baseFragment = this;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (isDataList()) {
            titleBar.setTitle(getName());
            titleBar.setLeftIconOnClickListener(v -> {
                popToBack();
            });

            cAdapter = new CateItemListAdapter(getContext());
            recyclerView.setAdapter(cAdapter);
            if (isWeb) {
                cAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        loadingDialog = new LoadingDialog(getContext(), getString(R.string.main_loading));
                        loadingDialog.show();
                        Bundle bundle = list.get(position);
                        String pkg = bundle.getString("app");
                       /* Handler handler = new Handler(Looper.getMainLooper()) {
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
                                                            HandlerUtil.send(handler1, -1);
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

                        AutoBillWeb.getDataListByApp(type, pkg, new AutoBillWeb.WebCallback() {
                            @Override
                            public void onFailure() {
                                ToastUtils.show(R.string.remote_error);
                                loadingDialog.close();
                            }

                            @Override
                            public void onSuccessful(String data) {
                                HandlerUtil.send(handler, data, 1);

                            }
                        });*/
                    }
                });
            } else {
                cAdapter.setOnItemClickListener((itemView, position) -> {
                    if (list == null || position >= list.size()) return;
                    Bundle cate = list.get(position);
                    String disable = getString(R.string.disable);
                    if (cate.getInt("use") != 1) {
                        disable = getString(R.string.enable);
                    }

                    String finalDisable = disable;
                    BottomArea.list(getContext(), String.format(getString(R.string.assert_change), cate.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.edit_default), getString(R.string.upload_cloud), getString(R.string.share_share), disable), new BottomArea.ListCallback() {
                        @Override
                        public void onSelect(int position) {
                            switch (position) {
                                case 0://删除
                                    TaskThread.onThread(() -> {
                                        Db.db.RegularDao().delete(cate.getInt("id"));
                                        HandlerUtil.send(mHandler, getString(R.string.del_success), HANDLE_REFRESH);
                                    });
                                    break;
                                case 1://编辑

                                    String data = cate.getString("data");
                                    if (type.equals("category")) {
                                        BottomArea.list(getContext(), getString(R.string.select_edit), Arrays.asList(getString(R.string.edit_default), getString(R.string.edit_js)), index -> {
                                            switch (index) {
                                                case 0:
                                                    WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/index.html", data);
                                                    break;
                                                case 1:
                                                    WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/js.html", data);
                                                    break;

                                            }
                                        });

                                    } else {
                                        WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/reg/index.html", data);
                                    }
                                    break;

                                case 2://上传到云端

                                    BottomArea.msg(getContext(), getString(R.string.could_title), getString(R.string.could_body), getString(R.string.regular_upload), getString(R.string.regular_know), new BottomArea.MsgCallback() {
                                        @Override
                                        public void cancel() {

                                        }

                                        @Override
                                        public void sure() {
                                            RegularManager.outputRegOne(getContext(), getName(), getType(), cate.getString("dataId"), cate.getString("version"), JSONObject.parseObject(cate.getString("data")));
                                            Tool.goUrl(getContext(), getString(R.string.submit_regular));

                                        }
                                    });


                                    break;
                                case 3:
                                    RegularManager.outputRegOne(getContext(), getName(), getType(), cate.getString("dataId"), cate.getString("version"), JSONObject.parseObject(cate.getString("data")), true);

                                    break;
                                case 4:
                                    if (finalDisable.equals(getString(R.string.disable))) {
                                        TaskThread.onThread(() -> {
                                            Db.db.RegularDao().deny(cate.getInt("id"));
                                            HandlerUtil.send(mHandler, getString(R.string.deny_success), HANDLE_REFRESH);
                                        });
                                    } else {
                                        TaskThread.onThread(() -> {
                                            Db.db.RegularDao().enable(cate.getInt("id"));
                                            HandlerUtil.send(mHandler, getString(R.string.deny_success), HANDLE_REFRESH);
                                        });
                                    }
                            }
                        }
                    });
                });
            }
        } else {//不是数据部分
            titleBar.setVisibility(View.GONE);
            rAdapter = new RemoteListAdapter(getContext(), isWeb);
            recyclerView.setAdapter(rAdapter);
            rAdapter.setOnItemClickListener((itemView, position) -> {
                Bundle bundle = list.get(position);
                String pkg = bundle.getString("app", null);
                bundle.putString("type", getNextType());
                bundle.putString("app", pkg);
                bundle.putBoolean("isWeb", isWeb);
                PageOption.to(outFragment.class)
                        .setNewActivity(true)
                        .putString("type", getNextType())
                        .putString("app", pkg)
                        .putBoolean("isWeb", isWeb)
                        .open(this);
            });
        }
        refreshLayout.setEnableRefresh(true);
        if (isWeb) {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    private String getNextType() {
        if (type.equals("notice")) return "notice_detail";
        if (type.equals("app")) return "app_detail";
        return type;
    }

    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        if (isWeb) {
            //TODO 网络部分也区分，category不分类
           /* AutoBillWeb.getDataList(type, new AutoBillWeb.WebCallback() {
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
            });*/
        } else {
            TaskThread.onThread(() -> {
                Regular[] regulars;
                if (type.equals("notice") || type.equals("app")) {
                    regulars = Db.db.RegularDao().loadApps(this.type);
                } else if (type.equals("sms") || type.equals("category")) {
                    regulars = Db.db.RegularDao().load(this.type, null, 0, 500);
                } else {
                    //notice_detail,app_detail
                    String t = type;
                    if (t.equals("notice_detail")) t = "notice";
                    if (t.equals("app_detail")) t = "app";
                    regulars = Db.db.RegularDao().load(t, this.app, 0, 500);
                }
                if (regulars.length == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                    return;
                }
                List<Bundle> bundleList = new ArrayList<>();
                for (Regular regular : regulars) {
                    Bundle bundle = Tool.class2Bundle(regular);
                    bundleList.add(bundle);
                }
                list = bundleList;
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
