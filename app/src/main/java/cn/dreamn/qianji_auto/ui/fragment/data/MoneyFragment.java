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
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.hjq.toast.ToastUtils;
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
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.ui.adapter.MoneyAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "账单列表", anim = CoreAnim.slide)
public class MoneyFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;

    @BindView(R.id.recycler_view)
    SwipeRecyclerView recycler_view;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private MoneyAdapter mAdapter;
    private List<Bundle> list;
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH=2;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_money;
    }


    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, "没有任何账单"));
     //   statusView.setOnLoadingViewConvertListener(viewHolder -> viewHolder.setText(R.id.load_info, "正在加载账单信息"));

        statusView.showLoadingView();
        initLayout();
    }
    private void initLayout() {
        mAdapter=new MoneyAdapter(getContext());

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnItemListener(new MoneyAdapter.Item() {
            @Override
            public void onClick(Bundle bundle, int pos) {
                OnItemClickListen(bundle, pos);
            }

            @Override
            public void onLongClick(Bundle bundle, int pos) {
                OnItemLongClickListen(bundle, pos);
            }
        });
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    if (statusView != null) statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(1000,()->statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d=(String)msg.obj;
                    if((d!=null&& !d.equals("")))
                        ToastUtils.show(d);
                    loadFromData(refreshLayout);
                    break;
            }
        }
    };
    private void loadFromData(RefreshLayout refreshLayout) {
        Task.onThread(() -> {
            AutoBills.getDates(datas -> {
                if (datas == null || datas.length == 0) {
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                } else {
                    list = Arrays.asList(datas);
                    // Log.d(list.toString());
                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }

    @SuppressLint("CheckResult")
    private void OnItemClickListen(Bundle bundle, int i) {
        BillInfo billInfo=BillInfo.parse(bundle.getString("billinfo"));

        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "请选择操作");
        DialogListExtKt.listItems(dialog, null, Arrays.asList("发起记账请求","删除记录"), null, true, (materialDialog, index, text) -> {
            switch (index){
                case 0:goBillApp(billInfo);break;
                case 1:del(bundle);break;
            }
            return null;
        });
        dialog.show();
    }
    @SuppressLint("CheckResult")
    private void OnItemLongClickListen(Bundle bundle, int i) {

        BillInfo billInfo=BillInfo.parse(bundle.getString("billinfo"));

        Context mContext=getContext();


        Tool.clipboard(mContext,billInfo.toString());

        ToastUtils.show("已复制到剪切板");

    }

    private void del(Bundle bundle) {
        int id=bundle.getInt("id");
        AutoBills.del(id, () -> {
            Message message=new Message();
            message.obj="删除成功！";
            message.what=HANDLE_REFRESH;
            mHandler.sendMessage(message);
        });

    }

    private void goBillApp(BillInfo billInfo) {
        SendDataToApp.callNoAdd(getContext(),billInfo);
    }


    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(0/*,false*/);//传入false表示刷新失败
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        title_bar.setRightIconOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null, "是否清空账单列表");
            dialog.message(null, "即将清空所有账单数据", null);
            dialog.positiveButton(null, "确定清空", materialDialog -> {
                AutoBills.delAll(() -> {
                  Message message=new Message();
                  message.obj="清除成功！";
                  message.what=HANDLE_REFRESH;
                  mHandler.sendMessage(message);
              });

              return null;
            });
            dialog.negativeButton(null, "取消清空", materialDialog -> {
                return null;
            });

            dialog.show();
        });
        //  return null;
    }






}
