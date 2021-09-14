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

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.LogAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

@Page(name = "日志", anim = CoreAnim.slide)
public class LogFragment extends BaseFragment {
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private LogAdapter mAdapter;
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
            if (loadingDialog != null) loadingDialog.close();
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_log;
    }

    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, getString(R.string.log_empty));
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            loadingDialog = new LoadingDialog(getAttachContext(), getString(R.string.main_loading));
            loadingDialog.show();
        });
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getBoolean("show_log_tip", true)) {
            BottomArea.msg(getContext(), getString(R.string.log_cache_title), getString(R.string.log_cache_body), getString(R.string.log_cache_know), getString(R.string.log_cache_no_show), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    mmkv.encode("show_log_tip", false);
                }
            });
        }


        refreshLayout.setOnRefreshListener(refreshlayout -> {
           loadFromData();
            refreshlayout.finishRefresh(0);//传入false表示刷新失败
        });


    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> popToBack());
        title_bar.setRightIconOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.log, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.sendLog:
                        String fileName = "temp.log";
                        Handler mHandler2 = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                Tool.shareFile(getContext(), Tool.getCacheFileName(getContext(), fileName));
                            }
                        };
                        //写出日志到临时文件
                        Task.onThread(() -> {
                            StringBuilder l = new StringBuilder();
                            if (list == null) {
                                ToastUtils.show(R.string.log_null);
                                return;
                            }
                            for (Bundle bundle : list) {
                                l.append("[").append(bundle.getString("time")).append("]").append("[").append(bundle.getString("sub")).append("]").append(bundle.getString("title")).append("\n");
                            }
                            Tool.writeToCache(getContext(), fileName, l.toString());
                            HandlerUtil.send(mHandler2, HANDLE_OK);
                        });
                        break;
                    case R.id.cleanLog:
                        BottomArea.msg(getContext(), getString(R.string.log_clean_title), getString(R.string.log_clean_body), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void sure() {
                                Log.delAll(() -> {
                                    HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);
                                });
                            }
                        });
                        break;
                    case R.id.logMode:
                        MMKV mmkv = MMKV.defaultMMKV();
                        int mode = mmkv.getInt("log_mode", 1);
                        String modeName = getString(R.string.log_no_log);
                        switch (mode) {
                            case 0:
                                modeName = getString(R.string.log_no_log);
                                break;
                            case 1:
                                modeName = getString(R.string.log_simple);
                                break;
                            case 2:
                                modeName = getString(R.string.log_more);
                                break;
                        }
                        BottomArea.list(getContext(), String.format(getString(R.string.log_mode), modeName), Arrays.asList(getString(R.string.log_no_log), getString(R.string.log_simple), getString(R.string.log_more)), new BottomArea.ListCallback() {
                            @Override
                            public void onSelect(int position) {
                                mmkv.encode("log_mode", position);
                                ToastUtils.show(getString(R.string.log_set_success));
                            }
                        });

                        break;
                }
                return false;
            });
            //显示(这一行代码不要忘记了)
            popup.show();
        });
        //  return null;
    }

    private void initLayout() {
        mAdapter = new LogAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setEnableRefresh(true);
        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                Bundle bundle = list.get(adapterPosition);
                String str = bundle.getString("title");
                Tool.clipboard(getContext(), str);
                ToastUtils.show(R.string.copied);
            }
        });
        loadFromData();
    }

    public void loadFromData() {
        statusView.showLoadingView();
        Task.onThread(() -> Log.getAll(logs -> {
            if (logs == null || logs.length == 0) {
                HandlerUtil.send(mHandler, HANDLE_ERR);
            } else {
                list = Arrays.asList(logs);
                HandlerUtil.send(mHandler, HANDLE_OK);
            }
        }));
    }

}
