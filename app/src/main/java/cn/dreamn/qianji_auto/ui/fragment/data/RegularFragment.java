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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.B64;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.DataUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


@Page(name = "本地识别规则", anim = CoreAnim.slide)
public class RegularFragment extends BaseFragment {

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;
    private static final int HANDLE_OUT = 3;
    @BindView(R.id.title_count)
    RelativeLayout title_count;
    private final String type;
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
    @BindView(R.id.iv_left_icon)
    IconView iv_left_icon;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    if (statusView != null) statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(() -> statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d = (String) msg.obj;
                    if (loadDialog != null)
                        loadDialog.close();
                    if ((d != null && !d.equals("")))
                        ToastUtils.show(d);
                    loadFromData(refreshLayout);
                    break;
                case HANDLE_OUT:
                    if (loadDialog != null)
                        loadDialog.close();
                    String d2 = (String) msg.obj;
                    if ((d2 != null && !d2.equals("")))
                        ToastUtils.show(d2);

                    break;
            }
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    };
    LoadingDialog loadDialog;
    private CateItemListAdapter mAdapter;
    private List<Bundle> list;

    public RegularFragment(String type) {
        this.type = type;
    }

    public static void openWithType(BaseFragment baseFragment, String type) {
        //sms notice app
        PageOption.to(RegularFragment.class)
                .setNewActivity(true)
                .putString(KEY_DATA, type)
                .open(baseFragment);
    }

    private String getName() {
        switch (type) {
            case "sms":
                return "短信";
            case "notice":
                return "通知";
            case "app":
                return "app";
        }
        return "";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_sort_manager;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(HANDLE_REFRESH);
    }

    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, "你还没有任何" + getName() + "规则哦！\n");
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.load_info, "正在加载" + getName() + "规则...");
        });
        floatingActionButton.setVisibility(View.GONE);
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });
        action_cate.setOnClickListener(v -> {


            WebViewFragment.openUrl(this, "file:///android_asset/html/Regulars/index.html?type=" + this.type);


        });
        action_import.setOnClickListener(v -> {
            RegularManager.importReg(getContext(), getName(), getType());
        });
        action_export.setOnClickListener(v -> {
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog1 = new MaterialDialog(getContext(), bottomSheet);
            dialog1.cornerRadius(15f, null);
            dialog1.title(null, "请选择导出方案");
            DialogListExtKt.listItems(dialog1, null, Arrays.asList("导出至下载文件夹", "分享"), null, true, (materialDialog, index, text) -> {
                RegularManager.outputReg(getContext(), getName(), getType(), index);
                return null;
            });
            dialog1.show();


        });
        action_delAll.setOnClickListener(v -> {
            BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog2 = new MaterialDialog(getContext(), bottomSheet2);
            dialog2.cornerRadius(15f, null);
            dialog2.title(null, "删除提醒");
            dialog2.message(null, "是否清空所有" + getName() + "规则数据？", null);
            dialog2.negativeButton(null, "不清空", (a) -> null);
            dialog2.positiveButton(null, "清空", (a) -> {
                identifyRegulars.clear(getType(), str -> {
                    Message message = new Message();
                    message.what = HANDLE_REFRESH;
                    message.obj = "清除成功";
                    mHandler.sendMessage(message);
                });

                return null;
            });
            dialog2.show();
        });
    }

    private String getType() {
        return this.type;
    }

    private void initLayout() {
        mAdapter = new CateItemListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOnMoreClick(item -> {
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.cornerRadius(15f, null);
            dialog.title(null, item.getString("name"));
            dialog.message(null, item.getString("des"), null);
            dialog.show();
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
                identifyRegulars.setSort(list.get(fromPosition).getInt("id"), fromPosition);
                identifyRegulars.setSort(list.get(toPosition).getInt("id"), toPosition);

                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {

            }

        });// 监听拖拽，更新UI。
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if (list == null || position >= list.size()) return;

        Bundle cate = list.get(position);

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.cornerRadius(15f, null);
        String disable = "禁用";
        if (cate.getInt("use") != 1) {
            disable = "启用";
        }
        dialog.title(null, "请选择操作(" + cate.getString("name") + ")");
        DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "可视化编辑", "上传到云端", disable), null, true, (materialDialog, index, text) -> {
            switch (index) {
                case 0:
                    identifyRegulars.del(cate.getInt("id"), () -> {
                        Message message = new Message();
                        message.obj = "删除成功";
                        message.what = HANDLE_REFRESH;
                        mHandler.sendMessage(message);
                    });
                    break;
                case 1:

                    DataUtils dataUtils = new DataUtils();
                    dataUtils.put("name", cate.getString("name"));
                    dataUtils.put("text", cate.getString("text"));
                    dataUtils.put("regular", cate.getString("regular"));
                    dataUtils.put("fromApp", cate.getString("fromApp"));

                    dataUtils.put("des", cate.getString("des"));
                    dataUtils.put("identify", cate.getString("identify"));
                    dataUtils.put("tableList", cate.getString("tableList"));

                    WebViewFragment.openUrl(this, "file:///android_asset/html/Regulars/index.html?id=" + cate.getInt("id") + "&type=" + this.type + "&data=" + B64.encode(dataUtils.toString()));
                    break;
                case 2:
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", cate.getString("name"));
                    jsonObject.put("text", cate.getString("text"));
                    jsonObject.put("data", cate.getString("regular"));
                    jsonObject.put("tableList", cate.getString("tableList"));
                    jsonObject.put("identify", cate.getString("identify"));
                    jsonObject.put("fromApp", cate.getString("fromApp"));
                    jsonObject.put("isCate", "0");
                    jsonObject.put("description", cate.getString("des"));
                    String result = B64.encode(jsonObject.toString());
                    AutoBillWeb.httpSend(getContext(), this, "send", result);
                    break;
                case 3:
                    if (text == "禁用") {
                        identifyRegulars.deny(cate.getInt("id"), () -> {
                            Message message = new Message();
                            message.obj = "禁用成功";
                            message.what = HANDLE_REFRESH;
                            mHandler.sendMessage(message);
                        });

                    } else {
                        identifyRegulars.enable(cate.getInt("id"), () -> {
                            Message message = new Message();
                            message.obj = "启用成功";
                            message.what = HANDLE_REFRESH;
                            mHandler.sendMessage(message);
                        });
                    }
            }
            return null;
        });
        dialog.show();

    }


    public void loadFromData(RefreshLayout refreshLayout) {
        loadDialog = new LoadingDialog(getContext(), "数据加载中...");
        loadDialog.show();
        Task.onThread(() -> {
            identifyRegulars.getAll(type, null, regulars -> {
                loadDialog.close();
                if (regulars == null || regulars.length == 0) {
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                } else {
                    list = Arrays.asList(regulars);
                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }


}
