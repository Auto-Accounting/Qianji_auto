/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.ui.fragment.sms;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.thl.filechooser.FileChooser;
import com.thl.filechooser.FileInfo;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.Smses;
import cn.dreamn.qianji_auto.core.db.Table.Sms;
import cn.dreamn.qianji_auto.core.utils.App;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.adapter.SmsAdapter;
import cn.dreamn.qianji_auto.ui.fragment.StateFragment;
import cn.dreamn.qianji_auto.utils.tools.FileUtils;

import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_DENY;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_NUM;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_REGEX;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_TEXT;
import static cn.dreamn.qianji_auto.ui.adapter.SmsAdapter.KEY_TITLE;


@Page(name = "短信识别")
public class SmsFragment extends StateFragment {

    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private SmsAdapter mAdapter;

    private List<Map<String, String>> mDataList;

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        WidgetUtils.initRecyclerView(recyclerView);
        mAdapter = new SmsAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLongPressDragEnabled(true);
        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                // 此方法在Item拖拽交换位置时被调用。
                // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

                // 交换数据，并更新adapter。
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                Collections.swap(mDataList, fromPosition, toPosition);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                //   Logs.d(mDataList.get(toPosition).get(KEY_TITLE)+"key id"+mDataList.get(toPosition).get(KEY_ID)+" to"+toPosition);
                Smses.setSort(Integer.parseInt(mDataList.get(fromPosition).get(KEY_ID)), fromPosition);
                Smses.setSort(Integer.parseInt(mDataList.get(toPosition).get(KEY_ID)), toPosition);

                // 返回true，表示数据交换成功，ItemView可以交换位置。
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {

            }

        });// 监听拖拽，更新UI。


        mAdapter.setOnItemClickListener(item -> new MaterialDialog.Builder(getContext())
                .title(item.get(KEY_TITLE))
                .items(R.array.menu_values_req2)
                .itemsCallback((dialog, itemView, position, text) -> {
                    int id = Integer.parseInt(Objects.requireNonNull(item.get(KEY_ID)));
                    if (position == 0) {
                        Smses.del(id);
                        SnackbarUtils.Long(getView(), getString(R.string.del_success)).info().show();
                        refresh();
                    } else if (position == 1) {
                        Bundle params = new Bundle();
                        params.putString("id", String.valueOf(id));
                        params.putString("num", item.get(KEY_NUM));
                        params.putString("regex", item.get(KEY_REGEX));
                        params.putString("text", item.get(KEY_TEXT));
                        params.putString("title", item.get(KEY_TITLE));
                        openPage(EditFragment.class, params);
                    } else if (position == 2) {
                        Smses.deny(id);
                        SnackbarUtils.Long(getView(), getString(R.string.deny_success)).info().show();
                        refresh();
                    } else if (position == 3) {
                        Smses.enable(id);
                        SnackbarUtils.Long(getView(), getString(R.string.enable_success)).info().show();
                        refresh();
                    }

                })
                .show());
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refresh();
    }


    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();

        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {

                openPage(EditFragment.class, true);

            }
        });
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_import) {
            @Override
            public void performAction(View view) {

                new MaterialDialog.Builder(getContext())
                        .title("请选择")
                        .items(R.array.menu_values_import)
                        .itemsCallback((dialog, itemView, position, text) -> {
                            if (position == 0) {
                                try {
                                    int allowVersion = 49;
                                    FileChooser fileChooser = new FileChooser(getActivity(), filePath -> {
                                        //filePath.get(0).getFilePath()
                                        String data = FileUtils.get(filePath.get(0).getFilePath());
                                        JSONObject jsonObject = JSONObject.parseObject(data);
                                        int version = jsonObject.getIntValue("version");
                                        String from = jsonObject.getString("from");
                                        if (version < allowVersion) {
                                            SnackbarUtils.Long(getView(), "不支持该版本的配置恢复").info().show();
                                            return;
                                        }
                                        if (!from.equals("SMS")) {
                                            SnackbarUtils.Long(getView(), "该文件不是有效的短信配置数据文件").info().show();
                                            return;
                                        }
                                        new MaterialDialog.Builder(getContext())
                                                .title("恢复提醒")
                                                .content("是否覆盖旧数据（清空所有数据不做保留）？")
                                                .positiveText("确定")
                                                .onPositive((dialog2, which) -> {
                                                    Smses.clear();

                                                })
                                                .negativeText("取消")
                                                .onAny((dialog3, which) -> {
                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                        Smses.add(jsonObject1.getString("regular"), jsonObject1.getString("name"), jsonObject1.getString("smsNum"), jsonObject1.getString("text"));
                                                    }
                                                    SnackbarUtils.Long(getView(), "恢复成功").info().show();
                                                    refresh();
                                                })
                                                .show();


                                    });

                                    fileChooser.setTitle("请选择短信配置数据文件");
                                    fileChooser.setDoneText("确定");
                                    fileChooser.setChooseType(FileInfo.FILE_TYPE_AUTOJSON);
                                    fileChooser.open();
                                } catch (Exception e) {
                                    SnackbarUtils.Long(getView(), "不是自动记账所支持的恢复文件，请重新选择。").info().show();
                                }
                                //导入
                            } else if (position == 1) {
                                //导出
                                Sms[] sms = Smses.getAll();
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("version", App.getAppVerCode());
                                    jsonObject.put("from", "SMS");
                                    JSONArray jsonArray = new JSONArray();
                                    for (Sms sm : sms) {
                                        JSONObject jsonObject1 = new JSONObject();
                                        jsonObject1.put("name", sm.name);
                                        jsonObject1.put("regular", sm.regular);
                                        jsonObject1.put("text", sm.text);
                                        jsonObject1.put("smsNum", sm.smsNum);
                                        jsonArray.add(jsonObject1);
                                    }
                                    jsonObject.put("data", jsonArray);
                                    Tools.writeToCache(getContext(), "sms.autoJson", jsonObject.toJSONString());
                                    Tools.shareFile(getContext(), getContext().getExternalCacheDir().getPath() + "/sms.autoJson");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();

            }
        });


        return titleBar;


    }


    private void loadData() {
        new Handler().postDelayed(() -> {
            // showLoading("正在加载短信识别规则");
            mDataList = new ArrayList<>();
            Sms[] sms = Smses.getAll();
            for (Sms value : sms) {
                Map<String, String> item = new HashMap<>();

                item.put(KEY_TITLE, value.name);
                item.put(KEY_REGEX, value.regular);
                item.put(KEY_TEXT, value.text);
                item.put(KEY_DENY, value.use == 1 ? "false" : "true");
                item.put(KEY_ID, String.valueOf(value.id));
                item.put(KEY_NUM, value.smsNum);
                mDataList.add(item);
            }
            if (mDataList.size() == 0) {
                showEmpty("没有任何短信识别规则");
                return;
            }

            mAdapter.refresh(mDataList);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
            showContent();
        }, 1000);
    }

    @Override
    protected void initListeners() {

    }

    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

}
