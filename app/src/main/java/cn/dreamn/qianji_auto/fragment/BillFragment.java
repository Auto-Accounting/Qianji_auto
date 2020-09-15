package cn.dreamn.qianji_auto.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.core.BillListAdapter;
import cn.dreamn.qianji_auto.fragment.test.TestBillFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.active.Api;

import cn.dreamn.qianji_auto.utils.active.Fun;
import cn.dreamn.qianji_auto.utils.file.Storage;
import cn.dreamn.qianji_auto.utils.file.myFile;

import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_ACCOUNT;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_MONEY;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_REMARK;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_SORT;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_SUB;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_TIME;
import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_TYPE;



@Page(name = "账单列表")
public class BillFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.map_error)
    SuperTextView map_error;
    private BillListAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    //彩蛋
    private int COUNTS = 5;// 点击次数
    private long[] mHits = new long[COUNTS];//记录点击次数
    private long DURATION = 2000;//有效时间

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort_alipay;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        WidgetUtils.initRecyclerView(recyclerView);

        mAdapter = new BillListAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);

        map_error.setOnClickListener(view -> {
            //将mHints数组内的所有元素左移一个位置
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//获得当前系统已经启动的时间
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)){
                openNewPage(TestBillFragment.class);
                mHits = new long[COUNTS];
            }

        });

        mAdapter.setOnItemClickListener((BillListAdapter.OnItemClickListener) (item, pos)-> {
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_bill)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        switch (position){
                            case 0:
                                Fun.browser(getContext(),Api.getQianji(item.get(KEY_TYPE),item.get(KEY_MONEY),item.get(KEY_REMARK),item.get(KEY_ACCOUNT),item.get(KEY_SORT)));
                                break;
                            case 1:
                                Fun.clipboard(Api.getQianji(item.get(KEY_TYPE),item.get(KEY_MONEY),item.get(KEY_REMARK),item.get(KEY_ACCOUNT),item.get(KEY_SORT)));
                                SnackbarUtils.Long(getView(), getString(R.string.bill_clip)).info().show();
                                break;
                            case 2:
                                showInputDialog(getString(R.string.bill_tip),item.get(KEY_REMARK),item.get(KEY_MONEY),item.get(KEY_ACCOUNT),item.get(KEY_SORT),( remark,  money,  from,  sort)->{
                                    JSONObject jsonObject=new JSONObject();
                                    jsonObject.put("sub",item.get(KEY_SUB));
                                    jsonObject.put("time",item.get(KEY_TIME));
                                    jsonObject.put("type",item.get(KEY_TYPE));
                                    jsonObject.put("money",money);
                                    jsonObject.put("account",from);
                                    jsonObject.put("remark",remark);
                                    jsonObject.put("sort",sort);
                                    Storage.type(Storage.Bill).set("bill",pos,jsonObject);
                                    SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                                    refresh();
                                });
                                break;
                            case 3:
                                Storage.type(Storage.Bill).del("bill",pos);
                                refresh();
                                break;
                        }


                    })
                    .show();
        });
    }



    @Override
    protected void initListeners() {
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }
    @Override
    protected TitleBar initTitle() {

        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("清空") {
            @Override
            public void performAction(View view) {
                myFile.del("Bill.json");
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
            JSONArray bill = Storage.type(Storage.Bill).getJSONArray("bill");
            List<Map<String, String>> data = new ArrayList<>();
            for(int i = 0; i < bill.size(); i++){
                Map<String, String> item = new HashMap<>();
                JSONObject jsonObject=(JSONObject)bill.get(i);
                item.put(KEY_SUB, jsonObject.getString(KEY_SUB));
                item.put(KEY_TIME, jsonObject.getString(KEY_TIME));
                item.put(KEY_TYPE, jsonObject.getString(KEY_TYPE));
                item.put(KEY_MONEY, jsonObject.getString(KEY_MONEY));
                item.put(KEY_ACCOUNT, jsonObject.getString(KEY_ACCOUNT));
                item.put(KEY_REMARK, jsonObject.getString(KEY_REMARK));
                item.put(KEY_SORT, jsonObject.getString(KEY_SORT));
                data.add(item);
            }
            if(data.size()!=0){
                map_error.setLeftString(String.format(getString(R.string.bill_has_sth), data.size()));
            }else{
                map_error.setLeftString(getString(R.string.sort_nothing));
            }
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
