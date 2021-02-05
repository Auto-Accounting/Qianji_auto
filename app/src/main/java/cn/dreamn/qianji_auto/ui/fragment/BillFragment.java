package cn.dreamn.qianji_auto.ui.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.AutoBill;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.AutoBills;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.adapter.BillAdapter;
import cn.dreamn.qianji_auto.utils.tools.Logs;

import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_ACCOUNT;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_BILLINFO;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_ID;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_MONEY;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_REMARK;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_SORT;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_SUB;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_TIME;
import static cn.dreamn.qianji_auto.ui.adapter.BillAdapter.KEY_TYPE;


@Page(name = "账单列表")
public class BillFragment extends StateFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    
    private BillAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;


    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        WidgetUtils.initRecyclerView(recyclerView);

        mAdapter = new BillAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);



        mAdapter.setOnItemClickListener((BillAdapter.OnItemClickListener) (item, pos)-> {
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_bill)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        BillInfo billInfo2;
                        switch (position){
                            case 0:
                                //前往钱迹
                                 billInfo2=BillInfo.parse(item.get(KEY_BILLINFO));
                              //  billInfo2.setSilent(false);
                                CallAutoActivity.callNoAdd(getContext(),billInfo2);
                                break;
                            case 2:
                                Tools.clipboard(getContext(),item.get(KEY_BILLINFO));
                                SnackbarUtils.Long(getView(), getString(R.string.bill_clip)).info().show();
                                break;
                            case 1:
                                 billInfo2=BillInfo.parse(item.get(KEY_BILLINFO));
                                 billInfo2.setSilent(false);
                                CallAutoActivity.callNoAdd(getContext(),billInfo2);
                                break;
                            case 3:
                               // Storage.type(Storage.Bill).del("bill",pos);
                                AutoBills.del(Integer.parseInt(item.get(KEY_ID)));
                                refresh();
                                break;
                        }


                    })
                    .show();
        });
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }



    @Override
    protected void initListeners() {

    }
    @Override
    protected TitleBar initTitle() {

        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("清空") {
            @Override
            public void performAction(View view) {

                new MaterialDialog.Builder(requireContext())
                        .title("清空账单信息")
                        .content("您确定要清除账单信息吗？")
                        .positiveText("确定")
                        .onPositive((dialog, which) -> {
                            AutoBills.delAll();
                            refresh();
                            SnackbarUtils.Long(getView(), getString(R.string.del_success)).info().show();

                        })
                        .negativeText("取消")
                        .show();
            }
        });
        return titleBar;
    }

    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        new Handler().postDelayed(() -> {
           // showLoading("正在加载账单列表");
            AutoBill[] autoBills= AutoBills.getAll();
            List<Map<String, String>> data = new ArrayList<>();
            for (AutoBill autoBill : autoBills) {
                Map<String, String> item = new HashMap<>();

                item.put(KEY_ID, String.valueOf(autoBill.id));
                item.put(KEY_BILLINFO, autoBill.billInfo);
                item.put(KEY_SUB, autoBill.source);
                item.put(KEY_TIME, autoBill.time);
                item.put(KEY_TYPE, autoBill.type);
                item.put(KEY_MONEY, autoBill.money);
                item.put(KEY_ACCOUNT, autoBill.accountname);
                item.put(KEY_REMARK, autoBill.remark);
                item.put(KEY_SORT, autoBill.catename);


                data.add(item);
            }
            if(data.size()==0){
                showEmpty("没有账单信息");
                return;
            }

            showContent();

            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
        }, 1000);
    }


}
