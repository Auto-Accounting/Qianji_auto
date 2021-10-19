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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "本地分类", anim = CoreAnim.slide)
public class localFragment extends BaseFragment {

    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.multiple_actions_down)
    FloatingActionsMenu floatingActionButton;

    @BindView(R.id.action_cate)
    FloatingActionButton action_cate;
    @BindView(R.id.action_import)
    FloatingActionButton action_import;
    @BindView(R.id.action_export)
    FloatingActionButton action_export;
    @BindView(R.id.action_delAll)
    FloatingActionButton action_delAll;
    private CateItemListAdapter mAdapter;
    private List<Bundle> list;
    private JSONObject jsonObject;
    Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_sort_manager;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUpdate();
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
                        mAdapter.setUpdateJSON(jsonObject);

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
            viewHolder.setText(R.id.empty_info, String.format(getString(R.string.no_regular), getString(R.string.auto)));
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        BaseFragment baseFragment = this;
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            getUpdate();
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });
        action_cate.setOnClickListener(v -> {

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


        });
        action_import.setOnClickListener(v -> {

            RegularManager.importReg(getContext(), getString(R.string.auto), "category", new RegularManager.End() {
                @Override
                public void onFinish(int code) {
                    HandlerUtil.send(mHandler, HANDLE_REFRESH);
                }
            });


        });
        action_export.setOnClickListener(v -> {
            RegularManager.output(getContext(), getString(R.string.auto), "category");
        });
        action_delAll.setOnClickListener(v -> {

            BottomArea.msg(getContext(), getString(R.string.log_clean_title), getString(R.string.auto_clean_body), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    Category.clear(() -> {
                        HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                    });
                }
            });
        });
    }

    private void initLayout() {
        mAdapter = new CateItemListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOnMoreClick(item -> {
            BottomArea.msg(getContext(), item.getString("name"), item.getString("des"));
        });
        mAdapter.setOnUpdateClick(json -> {
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    RegularManager.restoreFromData(getContext(), "", "category", (String) msg.obj, code -> {
                    });
                }
            };
            BottomArea.msg(getContext(), json.getString("title"), json.getString("log"), getContext().getString(R.string.update_go), getContext().getString(R.string.update_cancel), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    AutoBillWeb.getCategory(json.getString("name"), new AutoBillWeb.WebCallback() {
                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onSuccessful(String data) {
                            HandlerUtil.send(handler, data, -1);

                        }
                    });
                }
            });
        });
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
                Category.setSort(list.get(fromPosition).getInt("id"), fromPosition);
                Category.setSort(list.get(toPosition).getInt("id"), toPosition);

                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {

            }

        });// 监听拖拽，更新UI。
        refreshLayout.setEnableRefresh(true);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {

        if (list == null || position >= list.size()) return;

        Bundle cate = list.get(position);

        String disable = getString(R.string.disable);
        if (cate.getInt("use") != 1) {
            disable = getString(R.string.enable);
        }
        BaseFragment baseFragment = this;
        String finalDisable = disable;
        BottomArea.list(getContext(), String.format(getString(R.string.assert_change), cate.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.edit_default), getString(R.string.edit_js), getString(R.string.upload_cloud), getString(R.string.share_share), disable), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int position) {
                switch (position) {
                    case 0:
                        Category.del(cate.getInt("id"), () -> {
                            HandlerUtil.send(mHandler, getString(R.string.del_success), HANDLE_REFRESH);
                        });
                        break;
                    case 1:
                        if (cate.getString("tableList") == null || cate.getString("tableList").equals("")) {
                            ToastUtils.show(getString(R.string.edit_error));
                            break;
                            //兼容beta6以前。
                        } else {
                            JSONObject jsonObject = JSONObject.parseObject(cate.getString("tableList"));
                            if (jsonObject.containsKey("code")) {
                                ToastUtils.show(getString(R.string.edit_error));
                                break;
                            }
                        }
                        JSONObject js = JSONObject.parseObject(cate.getString("tableList"));
                        js.put("id", String.valueOf(cate.getInt("id")));
                        WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/index.html", Uri.encode(js.toJSONString()));
                        break;
                    case 2:
                        JSONObject js1 = JSONObject.parseObject(cate.getString("tableList"));
                        if ("".equals(cate.getString("tableList")) || js1 == null) {
                            //兼容beta6以前
                            js1 = new JSONObject();
                            js1.put("code", cate.getString("regular"));
                            js1.put("regular_name", cate.getString("name"));
                            js1.put("regular_remark", cate.getString("des"));
                            js1.put("version", "1");
                        }
                        js1.put("id", String.valueOf(cate.getInt("id")));
                        WebViewFragment.openUrl(baseFragment, "file:///android_asset/html/cate/js.html", Uri.encode(js1.toJSONString()));
                        break;
                    case 3:

                        BottomArea.msg(getContext(), getString(R.string.could_title), getString(R.string.could_body), getString(R.string.regular_upload), getString(R.string.regular_know), new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void sure() {
                                JSONObject js1 = JSONObject.parseObject(cate.getString("tableList"));
                                if (js1 == null || "".equals(js1.getString("data_id"))) {
                                    Log.i(cate.getString("tableList"));
                                    ToastUtils.show(R.string.beta6Tip);
                                    return;
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", cate.getString("name"));
                                jsonObject.put("regular", cate.getString("regular"));
                                jsonObject.put("tableList", cate.getString("tableList"));
                                jsonObject.put("des", cate.getString("des"));

                                RegularManager.outputRegOne(getContext(), getString(R.string.auto), "category", js1.getString("data_id"), js1.getString("version"), jsonObject);

                                Tool.goUrl(getContext(), getString(R.string.github_issue_category));

                            }
                        });


                        break;
                    case 4:
                        JSONObject js2 = JSONObject.parseObject(cate.getString("tableList"));
                        if (js2 == null || "".equals(js2.getString("data_id"))) {
                            ToastUtils.show(R.string.beta6Tip);
                            return;
                        }
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", cate.getString("name"));
                        jsonObject.put("regular", cate.getString("regular"));
                        jsonObject.put("tableList", cate.getString("tableList"));
                        jsonObject.put("des", cate.getString("des"));
                        RegularManager.outputRegOne(getContext(), "自动分类_" + cate.getString("name"), "category", js2.getString("data_id"), js2.getString("version"), jsonObject, true);
                        break;
                    case 5:
                        if (finalDisable.equals(getString(R.string.disable))) {
                            Category.deny(cate.getInt("id"), () -> {
                                HandlerUtil.send(mHandler, getString(R.string.deny_success), HANDLE_REFRESH);
                            });
                        } else {
                            Category.enable(cate.getInt("id"), () -> {
                                HandlerUtil.send(mHandler, getString(R.string.enable_success), HANDLE_REFRESH);
                            });
                        }
                }
            }
        });


    }

    private void getUpdate() {
        if (statusView != null) statusView.showLoadingView();
        AutoBillWeb.getCategoryList(new AutoBillWeb.WebCallback() {

            @Override
            public void onFailure() {
                HandlerUtil.send(mHandler, HANDLE_REFRESH);
            }

            @Override
            public void onSuccessful(String data) {
                jsonObject = JSONObject.parseObject(data);
                HandlerUtil.send(mHandler, HANDLE_REFRESH);
            }
        });
    }

    public void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        Task.onThread(() -> {
            Category.getAll(regulars -> {
                if (regulars == null || regulars.length == 0) {
                    HandlerUtil.send(mHandler, HANDLE_ERR);
                } else {
                    //  Log.d("regular", regulars.toString());
                    list = Arrays.asList(regulars);
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
