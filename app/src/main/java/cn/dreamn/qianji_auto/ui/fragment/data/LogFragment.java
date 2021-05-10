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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.LogAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;

@Page(name = "日志", anim = CoreAnim.slide)
public class LogFragment extends BaseFragment {

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private LogAdapter mAdapter;
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

        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_log;
    }

    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.empty_info, "没有日志！");
        });
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            viewHolder.setText(R.id.load_info, "正在加载日志信息");
        });

        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getBoolean("show_log_tip", true)) {
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.title(null, "日志缓存");
            dialog.message(null, "日志缓存有效期为24小时，如果有无法正常记账的情况请在24小时内反馈，24小时后日志会自动删除。\n\n 点击右上角可以进行日志反馈并切换日志记录模式。", null);
            dialog.positiveButton(null, "我知道了", materialDialog -> null);
            dialog.negativeButton(null, "不再显示", materialDialog -> {
                mmkv.encode("show_log_tip", false);
                return null;
            });
            dialog.show();
        }


        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });


    }

    @Override
    protected TitleBar initTitle() {
        title_bar.setInner(getActivity());


        title_bar.setLeftIconOnClickListener(v -> popToBack());
        title_bar.setRightIcon("&#xe63d;", 16);
        title_bar.setRightIconOnClickListener(v -> {
            //创建弹出式菜单对象（最低版本11）
            PopupMenu popup = new PopupMenu(getContext(), v);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.log, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.sendLog:
                        //写出日志到临时文件
                        // TODO 写到临时文件
                        break;
                    case R.id.cleanLog:
                        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
                        dialog.title(null, "清空确认");
                        dialog.message(null, "即将清空所有日志信息，请确认。", null);
                        dialog.positiveButton(null, "不清空", materialDialog -> null);
                        dialog.negativeButton(null, "清空", materialDialog -> {
                            Log.delAll(() -> {
                                Message message = new Message();
                                message.obj = "清除成功！";
                                message.what = HANDLE_REFRESH;
                                mHandler.sendMessage(message);
                            });

                            return null;
                        });
                        dialog.show();
                        break;
                    case R.id.logMode:
                        BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
                        MaterialDialog dialog2 = new MaterialDialog(getContext(), bottomSheet2);
                        MMKV mmkv = MMKV.defaultMMKV();
                        int mode = mmkv.getInt("log_mode", 1);
                        String modeName = "不记录";
                        switch (mode) {
                            case 0:
                                modeName = "不记录";
                                break;
                            case 1:
                                modeName = "简单日志";
                                break;
                            case 2:
                                modeName = "详细日志";
                                break;
                        }
                        dialog2.title(null, "日志模式切换(" + modeName + ")");
                        DialogListExtKt.listItems(dialog2, null, Arrays.asList("不记录", "简单日志", "详细日志"), null, true, (materialDialog, index, text) -> {
                            mmkv.encode("log_mode", index);
                            Toasty.success(getContext(), "设置成功！").show();
                            return null;
                        });
                        dialog2.show();

                        break;
                }
                return false;
            });
            //显示(这一行代码不要忘记了)
            popup.show();
        });
        return null;
    }

    private void initLayout() {
        mAdapter = new LogAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }

    public void loadFromData(RefreshLayout refreshLayout) {

        Task.onMain(1000, () -> Log.getAll(logs -> {
            if (logs == null || logs.length == 0) {
                mHandler.sendEmptyMessage(HANDLE_ERR);
            } else {
                list = Arrays.asList(logs);
                mHandler.sendEmptyMessage(HANDLE_OK);
            }
        }));
    }


}
