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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.files.DialogFileChooserExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.files.FileUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;


@Page(name = "本地分类", anim = CoreAnim.slide)
public class localFragment extends BaseFragment {

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;
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

    private CateItemListAdapter mAdapter;
    private List<Bundle> list;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(1000, () -> statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d = (String) msg.obj;
                    if ((d != null && !d.equals("")))
                        Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                    loadFromData(refreshLayout);
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
    protected void initViews() {
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

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
            WebViewFragment.openUrl(this, "file:///android_asset/html/category/index.html");
        });
        action_import.setOnClickListener(v -> {
            // 导入
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.title(null, "请选择自动记账分类配置文件");

            DialogFileChooserExtKt.fileChooser(dialog, getContext(), Environment.getExternalStorageDirectory(), file -> file.isDirectory() || (file.isFile() && file.getName().endsWith("ankio.category.backup")),
                    true, R.string.files_default_empty_text, false, null,
                    (materialDialog, file) -> {
                        //Log.d(file.getAbsolutePath());
                        String data = FileUtils.get(file.getAbsolutePath());
                        JSONObject jsonObject = JSONObject.parseObject(data);
                        String from = jsonObject.getString("from");

                        if (!from.equals("Category")) {
                            Toasty.error(getContext(), "该文件不是有效的自动分类配置数据文件").show();
                            return null;
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
                        dialog2.setOnCancelListener(dialog1 -> {
                            Task.onThread(() -> {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Category.addCategory(new String(Base64.decode(jsonObject1.getString("regular"), Base64.NO_WRAP)), jsonObject1.getString("name"), jsonObject1.getString("tableList"), jsonObject1.getString("des"));
                                }
                                Message message = new Message();
                                message.what = HANDLE_REFRESH;
                                message.obj = "恢复成功！";
                                mHandler.sendMessage(message);
                            });


                        });
                        dialog2.show();
                        return null;
                    });

            dialog.cornerRadius(15f, null);
            dialog.show();
        });
        action_export.setOnClickListener(v -> {

        });
    }

    private void initLayout() {
        mAdapter = new CateItemListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOnMoreClick(new CateItemListAdapter.MoreClick() {
            @Override
            public void onClick(Bundle item) {
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
                dialog.cornerRadius(15f, null);
                dialog.title(null, item.getString("title"));
                dialog.message(null, item.getString("des"), null);
                dialog.show();
            }
        });
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if (list == null || position >= list.size()) return;

        Bundle bookName = list.get(position);

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, "请选择操作(" + bookName.getString("name") + ")");
        DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "可视化编辑", "js编辑", "上传到云端"), null, true, (materialDialog, index, text) -> {
            //TODO 可视化操作
            return null;
        });
        dialog.show();

    }



    private void del(Bundle bookName) {
        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "删除确认");
        dialog.message(null, "确定要删除（" + bookName.getString("name") + "）吗？", null);
        dialog.positiveButton(null, "确定", materialDialog -> {
            BookNames.del(bookName.getInt("id"), () -> {
                Message message = new Message();
                message.obj = "删除成功!";
                message.what = HANDLE_REFRESH;
                mHandler.sendMessage(message);
            });
            return null;
        });
        dialog.negativeButton(null, "取消", materialDialog -> null);
        dialog.show();
    }

    public void loadFromData(RefreshLayout refreshLayout) {

        Task.onMain(1000, () -> {
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
