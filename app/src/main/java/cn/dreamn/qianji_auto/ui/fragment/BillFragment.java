package cn.dreamn.qianji_auto.ui.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.classic.common.MultipleStatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.AutoBill;
import cn.dreamn.qianji_auto.core.utils.AutoBills;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.Auto.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.adapter.BillAdapter;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;

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
public class BillFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;

    @BindView(R.id.ll_stateful)
    MultipleStatusView mStatefulLayout;
    private BillAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_asset_map;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        WidgetUtils.initRecyclerView(recyclerView);

        mAdapter = new BillAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);



        mAdapter.setOnItemClickListener((BillAdapter.OnItemClickListener) (item, pos)-> {
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_bill)
                    .itemsCallback((dialog, itemView, position, text) ->{

                        switch (position){
                            case 0:
                                //前往钱迹
                                BillInfo billInfo2=BillInfo.parse(item.get(KEY_BILLINFO));
                                CallAutoActivity.goQianji(getContext(),billInfo2);
                                break;
                            case 1:
                                Tools.clipboard(getContext(),item.get(KEY_BILLINFO));
                                SnackbarUtils.Long(getView(), getString(R.string.bill_clip)).info().show();
                                break;
                            case 2:
                                showInputDialog(getString(R.string.bill_tip),item.get(KEY_REMARK),item.get(KEY_MONEY),item.get(KEY_ACCOUNT),item.get(KEY_SORT),(remark, money, from, sort)->{
                                    BillInfo billInfo=BillInfo.parse(item.get(KEY_BILLINFO));
                                    billInfo.setMoney(money);
                                    billInfo.setAccountName(from);
                                    billInfo.setRemark(remark);
                                    billInfo.setCateName(sort);

                                    AutoBills.update(Integer.parseInt(item.get(KEY_ID)),billInfo);
                                    SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                                    refresh();
                                });
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
                AutoBills.delAll();
                refresh();
                SnackbarUtils.Long(getView(), getString(R.string.del_success)).info().show();
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
           // mStatefulLayout.showLoading("正在加载账单列表");
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
                mStatefulLayout.showEmpty("没有账单信息");
                return;
            }

            mStatefulLayout.showContent();

            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
        }, 1000);
    }

    public interface CallBack {
        void onResponse( String remark, String money, String from, String sort);
    }


    public void showInputDialog(String title, String remark, String money, String from, String sort, CallBack callBack) {

        LayoutInflater factory = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.input_map_2, null);
        final EditText input_map_remark = textEntryView.findViewById(R.id.input_map_remark);
        final EditText input_map_money = textEntryView.findViewById(R.id.input_map_money);
        final EditText input_map_from = textEntryView.findViewById(R.id.input_map_from);
        final EditText input_map_sort = textEntryView.findViewById(R.id.input_map_sort);

        input_map_remark.setText(remark);
        input_map_money.setText(money);
        input_map_from.setText(from);
        input_map_sort.setText(sort);
        new MaterialDialog.Builder(getContext())
                .customView(textEntryView, true)
                .title(title)
                .positiveText(getString(R.string.bill_send))
                .onPositive((dialog, which) -> {
                    callBack.onResponse(input_map_remark.getText().toString(),input_map_money.getText().toString(),input_map_from.getText().toString(),input_map_sort.getText().toString());
                })
                .negativeText(getString(R.string.set_cancel))
                .show();


    }





}
