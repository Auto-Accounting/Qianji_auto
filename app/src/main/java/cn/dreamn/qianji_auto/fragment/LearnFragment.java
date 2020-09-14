package cn.dreamn.qianji_auto.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.core.SortListAdapter;
import cn.dreamn.qianji_auto.utils.file.Storage;

import static cn.dreamn.qianji_auto.core.SortListAdapter.KEY_TITLE;
import static cn.dreamn.qianji_auto.core.SortListAdapter.KEY_VALUE;


@Page(name = "语义识别映射")
public class LearnFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.map_error)
    SuperTextView map_error;
    private SortListAdapter mAdapter;
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

        mAdapter = new SortListAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);

        mAdapter.setOnItemClickListener(item-> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        if(position==0){
                            Storage.type(Storage.Learn).del(item.get(KEY_TITLE));
                            refresh();
                        }else{
                            showInputDialog(getString(R.string.learn_change),item.get(KEY_TITLE),item.get(KEY_VALUE),(from,to)->{
                                Storage.type(Storage.Learn).set(from,to);
                                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                                refresh();
                            });
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
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.map_add)) {
            @Override
            public void performAction(View view) {
                showInputDialog(getString(R.string.sort_add_sub),"","",(from,to)->{
                    Storage.type(Storage.Learn).set(from,to);
                    SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                    refresh();
                });
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
            JSONObject alipay = Storage.type(Storage.Learn).getAll();
            List<Map<String, String>> data = new ArrayList<>();
            for(Map.Entry<String, Object> entry : alipay.entrySet()){
                Map<String, String> item = new HashMap<>();
                item.put(KEY_TITLE, entry.getKey());
                item.put(KEY_VALUE, (String) entry.getValue());
                data.add(item);
            }
            if(data.size()!=0){
                map_error.setLeftString(String.format(getString(R.string.sort_has_sth), data.size()));
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
        void onResponse(String item, String value);
    }


    public void showInputDialog(String title, String from, String to, CallBack callBack) {

        LayoutInflater factory = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.input_map, null);
        final EditText map_from = textEntryView.findViewById(R.id.map_from);
        final EditText map_to = textEntryView.findViewById(R.id.map_to);

        final TextView input_map_title = textEntryView.findViewById(R.id.input_map_title);
        final TextView input_map_sub = textEntryView.findViewById(R.id.input_map_sub);

        input_map_title.setText(getString(R.string.learn_top));
        input_map_sub.setText(getString(R.string.input_map_sub));

        map_from.setText(from);
        map_to.setText(to);
        new MaterialDialog.Builder(getContext())
                .customView(textEntryView, true)
                .title(title)
                .positiveText(getString(R.string.set_save))
                .onPositive((dialog, which) -> {
                    callBack.onResponse(map_from.getText().toString(),map_to.getText().toString());
                })
                .negativeText(getString(R.string.set_cancel))
                .show();


    }





}
