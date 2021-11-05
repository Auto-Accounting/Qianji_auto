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
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Regular;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.adapter.RemoteListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.AutoBillWeb;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import cn.dreamn.qianji_auto.utils.task.ConsumptionTask;
import cn.dreamn.qianji_auto.utils.task.RunBody;


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
    private String type;
    private String app;
    Handler mHandler;
    private boolean isWeb;
    private RemoteListAdapter rAdapter;
    private CateItemListAdapter cAdapter;
    private List<Bundle> list;

    public outFragment() {


    }

    public outFragment(String type, Boolean isWeb) {
        this.type = type;
        this.isWeb = isWeb;
        this.app = "";

    }

    private String getType() {
        return this.type;
    }

    private String getLastType() {
        if (type.equals("notice_detail")) return "notice";
        if (type.equals("app_detail")) return "app";
        if (type.equals("sms_detail")) return "sms";

        return this.type;
    }


    private String getName() {
        switch (type) {
            case "notice_detail":
                if (isWeb) return "云 · 通知规则 · " + AppInfo.getName(getContext(), app);
                return "通知规则 · " + AppInfo.getName(getContext(), app);
            case "notice":

                return getString(R.string.notice);
            case "app":
                return getString(R.string.app);
            case "app_detail":
                if (isWeb) return "云 · APP规则 · " + AppInfo.getName(getContext(), app);
                return "APP规则 · " + AppInfo.getName(getContext(), app);


            case "sms":
                return getString(R.string.sms);
            case "sms_detail":
                return "云 · 短信规则 · " + getString(R.string.sms);
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.type = bundle.getString("type");
            this.isWeb = bundle.getBoolean("isWeb");
            this.app = bundle.getString("app");

        }
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
                            //  Log.d("刷新数据：");
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
        return (type.equals("category") || (type.equals("sms") && !isWeb) || type.equals("notice_detail") || type.equals("app_detail") || type.equals("sms_detail"));
    }

    private boolean isHasBar() {
        return (type.equals("category") || (type.equals("sms")) || type.equals("notice") || type.equals("app"));
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
                    jsonObject.put("identify", getLastType());
                    if (app != null) {
                        jsonObject.put("regular_app", app);
                    }
                    WebViewFragment.openUrl(this, "file:///android_asset/html/reg/index.html", jsonObject.toJSONString());
                }


            });
            action_import.setOnClickListener(v -> {

                RegularManager.importReg(getContext(), getName(), getLastType(), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {
                    }
                });

            });
            action_export.setOnClickListener(v -> {
                RegularManager.output(getContext(), getName(), getLastType(), app);
            });
            action_delAll.setOnClickListener(v -> {
                BottomArea.msg(getContext(), getString(R.string.log_clean_title), String.format(getString(R.string.reg_clean_body), getName()), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void sure() {
                        TaskThread.onThread(() -> {
                            if (app != null) {
                                Db.db.RegularDao().clean(getLastType(), app);
                            } else {
                                Db.db.RegularDao().clean(getLastType());
                            }
                            HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                        });
                    }
                });

            });


        }

    }


    private void intoLocal(String dataId) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                RegularManager.restoreFromData(getContext(), getName(), getLastType(), (String) msg.obj, code -> {
                });
            }
        };
        AutoBillWeb.getById(dataId, getContext(), new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccessful(String data) {
                HandlerUtil.send(handler, data, 0);
            }
        });
    }

    private void initLayout() {
        BaseFragment baseFragment = this;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (isHasBar()) {
            titleBar.setVisibility(View.GONE);
        } else {
            titleBar.setTitle(getName());
            titleBar.setLeftIconOnClickListener(v -> {
                popToBack();
            });
        }
        if (isDataList()) {
            cAdapter = new CateItemListAdapter(getContext());
            recyclerView.setAdapter(cAdapter);
            if (isWeb) {
                cAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        Bundle bundle = list.get(position);
                        String dataId = bundle.getString("dataId");
                        BottomArea.msg(getContext(), bundle.getString("name"), bundle.getString("remark"), "导入", "关闭", new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void sure() {
                                intoLocal(dataId);
                            }
                        });
                    }
                });
                cAdapter.setOnImportClick(new CateItemListAdapter.ImportClick() {
                    @Override
                    public void onClick(String dataId) {
                        intoLocal(dataId);
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
                    BottomArea.list(getContext(), String.format(getString(R.string.assert_change), cate.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.edit), getString(R.string.upload_cloud), getString(R.string.share_share), disable), new BottomArea.ListCallback() {
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

                                    JSONObject jsonObject = JSONObject.parseObject(cate.getString("data"));
                                    jsonObject.put("id", cate.getInt("id"));
                                    String data = jsonObject.toJSONString();
                                    if (type.equals("category")) {
                                        if (data.contains("regular_sort")) {
                                            WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/index.html", data);

                                        } else {
                                            WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/js.html", data);

                                        }
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
                                            RegularManager.outputRegOne(getContext(), getName(), getLastType(), cate.getString("dataId"), cate.getString("version"), Tool.bundle2JSONObject(cate));
                                            Tool.goUrl(getContext(), getString(R.string.submit_regular));

                                        }
                                    });


                                    break;
                                case 3:
                                    RegularManager.outputRegOne(getContext(), getName(), getLastType(), cate.getString("dataId"), cate.getString("version"), Tool.bundle2JSONObject(cate), true);

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
                cAdapter.setOnUpdateClick(new CateItemListAdapter.UpdateClick() {
                    @Override
                    public void onClick(JSONObject data) {
                        BottomArea.msg(getContext(), data.getString("title"), data.getString("log"), "更新", "关闭", new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void sure() {
                                intoLocal(data.getString("dataId"));
                            }
                        });
                    }
                });
            }
            cAdapter.setOnMoreClick(item -> {
                BottomArea.msg(getContext(), item.getString("name"), item.getString("remark"));
            });


        } else {//不是数据部分
            rAdapter = new RemoteListAdapter(getContext(), isWeb);
            recyclerView.setAdapter(rAdapter);
            rAdapter.setOnItemClickListener((itemView, position) -> {
                Bundle bundle = list.get(position);
                String pkg = bundle.getString("app", null);
                bundle.putString("type", getNextType());
                bundle.putString("app", pkg);
                bundle.putBoolean("isWeb", isWeb);
                openPage(outFragment.class, bundle);
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
        if (type.equals("sms")) return "sms_detail";
        return type;
    }

    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        if (isWeb) {
            TaskThread.onThread(() -> {
                AutoBillWeb.getList(getContext(), new AutoBillWeb.WebCallback() {
                    @Override
                    public void onFailure() {
                        Log.d("Web访问失败");
                        HandlerUtil.send(mHandler, HANDLE_ERR);
                    }

                    @Override
                    public void onSuccessful(String data) {
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = JSON.parseObject(data);
                        } catch (Exception e) {
                            Log.d("JSON解析失败！" + data);
                            HandlerUtil.send(mHandler, HANDLE_ERR);
                            return;
                        }

                        if (!jsonObject.containsKey(getLastType())) {
                            Log.d("缺失环境");
                            HandlerUtil.send(mHandler, HANDLE_ERR);
                            return;
                        }
                        JSONObject jsonObject1 = jsonObject.getJSONObject(getLastType());

                        List<Bundle> bundleList = new ArrayList<>();
                        if (type.equals("app") || type.equals("notice") || type.equals("sms")) {
                            for (Map.Entry<String, Object> stringObjectEntry : jsonObject1.entrySet()) {
                                String key = stringObjectEntry.getKey();
                                JSONObject value = (JSONObject) stringObjectEntry.getValue();
                                Bundle bundle = new Bundle();
                                bundle.putString("identify", getLastType());
                                bundle.putString("app", key);
                                bundle.putString("name", type.equals("sms") ? key : AppInfo.getName(getContext(), key));
                                bundle.putString("count", String.valueOf(value.size()));
                                //Db.db.RegularDao().loadByDataId()
                                //bundle.putBoolean("install",false);//判断是否安装
                                bundleList.add(bundle);
                            }
                        } else if (type.equals("category") || type.equals("app_detail") || type.equals("notice_detail") || type.equals("sms_detail")) {
                            if (app != null) {
                                jsonObject1 = jsonObject1.getJSONObject(app);
                            }
                            //分类
                            for (Map.Entry<String, Object> stringObjectEntry : jsonObject1.entrySet()) {
                                String key = stringObjectEntry.getKey();
                                JSONObject value = (JSONObject) stringObjectEntry.getValue();
                                Bundle bundle = new Bundle();
                                bundle.putString("log", value.getString("version"));
                                bundle.putString("date", value.getString("date"));
                                bundle.putString("dataId", key);
                                bundle.putString("version", value.getString("version"));
                                bundle.putString("name", value.getString("name"));
                                bundle.putString("remark", value.getString("remark"));
                                bundleList.add(bundle);
                            }

                        }
                        if (bundleList.size() == 0) {
                            Log.d("数据为空");
                            HandlerUtil.send(mHandler, HANDLE_ERR);
                            return;
                        }
                        list = bundleList;
                        HandlerUtil.send(mHandler, HANDLE_OK);
                    }
                });

            });
        } else {

            RunBody runBody = new RunBody() {
                @Override
                public void run(Context context, ConsumptionTask task) {
                    TaskThread.onThread(() -> {
                        Regular[] regulars;
                        if (type.equals("notice") || type.equals("app")) {
                            regulars = Db.db.RegularDao().loadApps(getLastType());
                        } else if (app == null) {
                            regulars = Db.db.RegularDao().load(getLastType(), "", 0, 200);
                        } else {
                            regulars = Db.db.RegularDao().load(getLastType(), app, 0, 200);
                        }

                        if (regulars.length == 0) {
                            Log.d("规则长度为0");
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
            };


            if (isDataList()) {
                AutoBillWeb.getList(getContext(), new AutoBillWeb.WebCallback() {
                    @Override
                    public void onFailure() {
                        runBody.run(null, null);
                    }

                    @Override
                    public void onSuccessful(String data) {
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = JSON.parseObject(data);
                        } catch (Exception e) {
                            Log.d("解析JSON失败：" + data);
                            runBody.run(null, null);
                            //   HandlerUtil.send(mHandler, HANDLE_ERR);
                            return;
                        }


                        if (!jsonObject.containsKey(getLastType())) {
                            Log.d("JSON不包含数据：" + data);
                            runBody.run(null, null);
                            //    HandlerUtil.send(mHandler, HANDLE_ERR);
                            return;
                        }
                        JSONObject jsonObject1 = jsonObject.getJSONObject(getLastType());
                        //Log.i(jsonObject1.toJSONString());
                        if (app != null) {
                            jsonObject1 = jsonObject1.getJSONObject(app);
                        }
                        //  Log.i(app);
                        //更新文件加上
                        cAdapter.setUpdateJSON(jsonObject1, isWeb);
                        runBody.run(null, null);
                    }
                });
            } else {
                runBody.run(null, null);
            }


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
