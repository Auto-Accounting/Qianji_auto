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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.B64;
import cn.dreamn.qianji_auto.utils.files.FileUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "本地分类", anim = CoreAnim.slide)
public class SortFragment extends BaseFragment {

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;
    private static final int HANDLE_OUT = 3;
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
    @BindView(R.id.title_count)
    RelativeLayout title_count;
    @BindView(R.id.iv_left_icon)
    IconView iv_left_icon;
    private CateItemListAdapter mAdapter;
    private List<Bundle> list;
    LoadingDialog loadDialog;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    if (statusView != null) statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(1000, () -> statusView.showContentView());
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
    protected TitleBar initTitle() {
        ThemeManager themeManager = new ThemeManager(getContext());
        themeManager.setStatusBar(getActivity(), title_count, R.color.background_white);
        iv_left_icon.setOnClickListener(v -> popToBack());
        return null;
    }

    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, "你还没有任何自动分类规则哦！\n");
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.load_info, "正在加载自动分类规则...");
        });
        floatingActionButton.setVisibility(View.GONE);
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });
        action_cate.setOnClickListener(v -> {


            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog1 = new MaterialDialog(getContext(), bottomSheet);
            dialog1.cornerRadius(15f, null);
            dialog1.title(null, "请选择编辑方式");
            DialogListExtKt.listItems(dialog1, null, Arrays.asList("可视化编辑", "JS编辑"), null, true, (materialDialog, index, text) -> {
                switch (index) {
                    case 0:
                        WebViewFragment.openUrl(this, "file:///android_asset/html/Category/index.html");
                        break;
                    case 1:
                        WebViewFragment.openUrl(this, "file:///android_asset/html/Category/js.html");
                        break;

                }

                return null;
            });
            dialog1.show();


        });
        action_import.setOnClickListener(v -> {
            PermissionUtils permissionUtils = new PermissionUtils(getContext());
            permissionUtils.grant(PermissionUtils.Storage);
            try {


                final DialogProperties properties = new DialogProperties();

                FilePickerDialog dialog = new FilePickerDialog(getContext(), properties);
                dialog.setTitle("请选择自动记账分类配置文件");
                dialog.setPositiveBtnName("选中");
                dialog.setNegativeBtnName("关闭");
                properties.extensions = new String[]{".auto.category.backup"};
                properties.root = Environment.getExternalStorageDirectory();
                properties.offset = Environment.getExternalStorageDirectory();
                properties.show_hidden_files = false;
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.error_dir = Environment.getExternalStorageDirectory();
                dialog.setProperties(properties);
                dialog.show();
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        dialog.dismiss();
                        String file = files[0];
                        String data = FileUtils.get(file);
                        JSONObject jsonObject = JSONObject.parseObject(data);
                        String from = jsonObject.getString("from");

                        if (!from.equals("Category")) {
                            ToastUtils.show("该文件不是有效的自动分类配置数据文件");
                            return;
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
                        MaterialDialog dialog2 = new MaterialDialog(getContext(), bottomSheet2);
                        dialog2.cornerRadius(15f, null);
                        dialog2.title(null, "恢复提醒");
                        dialog2.message(null, "是否覆盖原有数据（清空不保留）？", null);
                        dialog2.negativeButton(null, "不清空", (a) -> null);
                        dialog2.positiveButton(null, "清空", (a) -> {
                            Category.clear();

                            return null;
                        });

                        dialog2.setOnDismissListener(dialog1 -> {
                            loadDialog = new LoadingDialog(getContext(), "数据导入中...");
                            loadDialog.show();
                            Task.onThread(() -> {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    Category.addCategory(B64.decode(jsonObject1.getString("regular")), jsonObject1.getString("name"), jsonObject1.getString("tableList"), jsonObject1.getString("des"), new Category.Finish() {
                                        @Override
                                        public void onFinish() {
                                            Log.d("finish data" + jsonObject1.toString());
                                        }
                                    });
                                }
                                Message message = new Message();
                                message.what = HANDLE_REFRESH;
                                message.obj = "恢复成功！";

                                mHandler.sendMessage(message);
                            });


                        });
                        dialog2.show();
                    }
                });


            } catch (Exception | Error e) {
                e.printStackTrace();
                Log.i("出错了，可能是权限未给全！" + e.toString());
            }
        });
        action_export.setOnClickListener(v -> {
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog1 = new MaterialDialog(getContext(), bottomSheet);
            dialog1.cornerRadius(15f, null);
            dialog1.title(null, "请选择导出方案");
            DialogListExtKt.listItems(dialog1, null, Arrays.asList("导出至下载文件夹", "分享"), null, true, (materialDialog, index, text) -> {
                loadDialog = new LoadingDialog(getContext(), "数据导出中...");
                loadDialog.show();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("from", "Category");
                Category.getAll(bundle -> {
                    JSONArray jsonArray = new JSONArray();
                    for (Bundle regular : bundle) {
                        //        Category.addCategory(new String(Base64.decode(jsonObject1.getString("regular"), Base64.NO_WRAP)), jsonObject1.getString("name"), jsonObject1.getString("tableList"), jsonObject1.getString("des")
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("name", regular.getString("name"));
                        jsonObject1.put("regular", B64.encode(regular.getString("regular")));
                        jsonObject1.put("tableList", regular.getString("tableList"));
                        jsonObject1.put("des", regular.getString("des"));
                        jsonArray.add(jsonObject1);
                    }
                    jsonObject.put("data", jsonArray);
                    String fileName = Tool.getTime("yyyyMMddHHmmss") + ".auto.category.backup";
                    Tool.writeToCache(getContext(), fileName, jsonObject.toJSONString());
                    switch (index) {
                        case 0:
                            String newFileName = Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/" + fileName;
                            FileUtils.makeRootDirectory(Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/");
                            FileUtils.copyFile(getContext().getExternalCacheDir().getPath() + "/" + fileName, newFileName);
                            Log.m(fileName);
                            FileUtils.del(fileName);
                            break;
                        case 1:

                            Tool.shareFile(getContext(), getContext().getExternalCacheDir().getPath() + "/" + fileName);
                            FileUtils.del(fileName);
                            break;

                    }
                    Message message = new Message();
                    message.what = HANDLE_OUT;
                    message.obj = "数据导出成功";
                    mHandler.sendMessage(message);


                });

                return null;
            });
            dialog1.show();


        });
        action_delAll.setOnClickListener(v -> {
            BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog2 = new MaterialDialog(getContext(), bottomSheet2);
            dialog2.cornerRadius(15f, null);
            dialog2.title(null, "删除提醒");
            dialog2.message(null, "是否清空所有分类数据？", null);
            dialog2.negativeButton(null, "不清空", (a) -> null);
            dialog2.positiveButton(null, "清空", (a) -> {
                Category.clear(() -> {
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
                Category.setSort(list.get(fromPosition).getInt("id"), fromPosition);
                Category.setSort(list.get(toPosition).getInt("id"), toPosition);

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
        DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "可视化编辑", "js编辑", "上传到云端", disable), null, true, (materialDialog, index, text) -> {
            switch (index) {
                case 0:
                    Category.del(cate.getInt("id"), () -> {
                        Message message = new Message();
                        message.obj = "删除成功";
                        message.what = HANDLE_REFRESH;
                        mHandler.sendMessage(message);
                    });
                    break;
                case 1:
                    if (cate.getString("tableList") == null || cate.getString("tableList").equals("")) {
                        ToastUtils.show("该规则使用js编辑，无法进行可视化编辑。");
                        break;
                    }
                    WebViewFragment.openUrl(this, "file:///android_asset/html/Category/index.html?id=" + cate.getInt("id") + "&data=" + B64.encode(cate.getString("tableList")));
                    break;
                case 2:

                    WebViewFragment.openUrl(this, "file:///android_asset/html/Category/js.html?id=" + cate.getInt("id") + "&data=" + B64.encode(cate.getString("regular")) + "&name=" + cate.getString("name") + "&des=" + cate.getString("des"));
                    break;
                case 3:
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", cate.getString("name"));
                    jsonObject.put("text", "");
                    jsonObject.put("data", cate.getString("regular"));
                    jsonObject.put("tableList", cate.getString("tableList"));
                    jsonObject.put("identify", "");
                    jsonObject.put("fromApp", "");
                    jsonObject.put("isCate", "1");
                    jsonObject.put("description", cate.getString("des"));
                    String result = B64.encode(jsonObject.toString());
                    AutoBillWeb.httpSend(getContext(), this, "send", result);
                    break;
                case 4:
                    if (text == "禁用") {
                        Category.deny(cate.getInt("id"), () -> {
                            Message message = new Message();
                            message.obj = "禁用成功";
                            message.what = HANDLE_REFRESH;
                            mHandler.sendMessage(message);
                        });
                    } else {
                        Category.enable(cate.getInt("id"), () -> {
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

        Task.onThread(() -> {
            Category.getAll(regulars -> {
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
