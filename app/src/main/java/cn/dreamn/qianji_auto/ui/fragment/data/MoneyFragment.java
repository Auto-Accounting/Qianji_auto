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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.ui.adapter.MapListAdapter;
import cn.dreamn.qianji_auto.ui.adapter.MoneyAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "账单列表", anim = CoreAnim.slide)
public class MoneyFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_money;
    }


    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, "没有任何账单"));
        statusView.setOnLoadingViewConvertListener(viewHolder -> viewHolder.setText(R.id.load_info, "正在加载账单信息"));

        statusView.showLoadingView();
        initLayout();
    }
    private void initLayout() {
        mAdapter=new MoneyAdapter(getContext());

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnItemListener(this::OnItemClickListen);
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(1000,()->statusView.showContentView());
                    break;
            }
        }
    };
    private void loadFromData(RefreshLayout refreshLayout) {
        Task.onMain(1000,()->{
            AutoBills.getDates(datas -> {
                if(datas==null||datas.length==0){
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                }else{
                    list= Arrays.asList(datas);
                   // Log.d(list.toString());
                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }

    private void OnItemClickListen(Bundle bundle, int i) {
        Log.d(bundle.toString());
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
    protected TitleBar initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        return null;
    }






}
