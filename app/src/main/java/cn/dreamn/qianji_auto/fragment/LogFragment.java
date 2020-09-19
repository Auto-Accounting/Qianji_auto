package cn.dreamn.qianji_auto.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatImageView;
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

import cn.dreamn.qianji_auto.core.LogListAdapter;

import cn.dreamn.qianji_auto.utils.active.Fun;
import cn.dreamn.qianji_auto.utils.file.Storage;
import cn.dreamn.qianji_auto.utils.file.myFile;


import static cn.dreamn.qianji_auto.core.BillListAdapter.KEY_POS;
import static cn.dreamn.qianji_auto.core.LogListAdapter.KEY_SUB;
import static cn.dreamn.qianji_auto.core.LogListAdapter.KEY_TITLE;


@Page(name = "日志查询")
public class LogFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.map_error)
    SuperTextView map_error;
    private LogListAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;

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

        mAdapter = new LogListAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);

        mAdapter.setOnItemClickListener((LogListAdapter.OnItemClickListener) (item, pos)-> {
            new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_log)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        switch (position){
                            case 0:
                                Fun.clipboard(item.get(KEY_TITLE));
                                SnackbarUtils.Long(getView(), getString(R.string.bill_clip)).info().show();
                                break;
                            case 1:
                                Storage.type(Storage.Log).del("log",pos);
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
                myFile.del("Log.json");
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
            JSONArray logs = Storage.type(Storage.Log).getJSONArray("log");
            List<Map<String, String>> data = new ArrayList<>();
            for(int i = logs.size()-1; i >=0; i--){
                Map<String, String> item = new HashMap<>();
                JSONObject jsonObject=(JSONObject)logs.get(i);
                item.put(KEY_SUB, String.format("%s  %s",jsonObject.getString("tag"),jsonObject.getString("time")));
                item.put(KEY_TITLE, jsonObject.getString("log"));
                item.put(KEY_POS, String.valueOf(i));
                data.add(item);
            }
            if(data.size()!=0){
                map_error.setLeftString(String.format(getString(R.string.log_has_sth), data.size()));
            }else{
                map_error.setLeftString(getString(R.string.sort_nothing));
            }
            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
        }, 1000);
    }






}
