package cn.dreamn.qianji_auto.fragment.sort;


import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONObject;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
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


@Page(name = "微信收入分类映射")
public class WechatFragment extends BaseFragment {


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

        mAdapter.setOnItemClickListener(item-> showInputDialog(getString(R.string.set_sort_msg),item.get(KEY_TITLE),item.get(KEY_VALUE),(str)->{
            Storage.type(Storage.WeMap).set(item.get(KEY_TITLE),str);
            SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
            refresh();
        }));
    }



    @Override
    protected void initListeners() {
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }
    @Override
    protected TitleBar initTitle() {
        return super.initTitle();
    }

    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        new Handler().postDelayed(() -> {
            JSONObject alipay = Storage.type(Storage.WeMap).getAll();

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






}
