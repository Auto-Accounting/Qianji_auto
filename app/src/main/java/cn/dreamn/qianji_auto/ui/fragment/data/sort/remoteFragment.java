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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.ui.adapter.CateItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


@Page(name = "云端分类", anim = CoreAnim.slide)
public class remoteFragment extends BaseFragment {


    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;
    private static final int HANDLE_NO_REFRESH = 3;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private CateItemListAdapter mAdapter;
    private List<Bundle> list;
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
                    if ((d != null && !d.equals("")))
                        ToastUtils.show(d);
                    loadFromData(refreshLayout);
                    break;
                case HANDLE_NO_REFRESH:
                    String d2 = (String) msg.obj;
                    if ((d2 != null && !d2.equals("")))
                        ToastUtils.show(d2);
                    //   loadFromData(refreshLayout);
                    break;
            }

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
            viewHolder.setText(R.id.empty_info, "云端暂无任何自动分类规则");
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.load_info, "正在加载自动分类规则");
        });
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
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
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if (list == null || position >= list.size()) return;

        Bundle bundle = list.get(position);

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, "请选择操作(" + bundle.getString("name") + ")");
        DialogListExtKt.listItems(dialog, null, Arrays.asList("下载至本地"), null, true, (materialDialog, index, text) -> {
            if (index == 0) {
                Category.addCategory(bundle.getString("regular"), bundle.getString("name"), bundle.getString("tableList"), bundle.getString("des"), () -> {
                    Message message = new Message();
                    message.obj = "添加成功！";
                    message.what = HANDLE_NO_REFRESH;
                    mHandler.sendMessage(message);
                    //  ToastUtils.show("添加成功！");
                });
            }
            return null;
        });
        dialog.show();

    }


    public void loadFromData(RefreshLayout refreshLayout) {




        AutoBillWeb.getCategoryWeb(null, new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {
                //失败就不显示了
                mHandler.sendEmptyMessage(HANDLE_OK);
            }

            @Override
            public void onSuccessful(String data) {
                Log.m("网页返回结果->  " + data);
                List<Bundle> datas = new ArrayList<>();
                try {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    if (jsonObject.getInteger("code") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", jsonArray.getJSONObject(i).getString("name"));
                            bundle.putString("text", jsonArray.getJSONObject(i).getString("text"));
                            bundle.putString("regular", jsonArray.getJSONObject(i).getString("data"));
                            bundle.putString("tableList", jsonArray.getJSONObject(i).getString("tableList"));
                            bundle.putString("identify", jsonArray.getJSONObject(i).getString("identify"));
                            bundle.putString("fromApp", jsonArray.getJSONObject(i).getString("fromApp"));
                            bundle.putString("des", jsonArray.getJSONObject(i).getString("description"));
                            bundle.putInt("use", 2);
                            datas.add(bundle);
                            // bundle.putString("name");
                            //  datas.get(i).putBundle("cloud_data",bundle);
                        }
                    }
                } catch (Exception | Error e) {
                    Log.i("JSON解析错误！！" + e.toString());
                    e.printStackTrace();
                }
                list = datas;
                Log.m("数据" + list.toString());
                mHandler.sendEmptyMessage(HANDLE_OK);
            }
        });

    }


}
