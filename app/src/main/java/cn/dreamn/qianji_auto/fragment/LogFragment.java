package cn.dreamn.qianji_auto.fragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.core.SimpleRecyclerAdapterLog;
import cn.dreamn.qianji_auto.func.Storage;
import cn.dreamn.qianji_auto.func.myFile;
import cn.dreamn.qianji_auto.utils.XToastUtils;



@Page(name = "日志")
public class LogFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.map_error)
    SuperTextView map_error;
    private SimpleRecyclerAdapterLog mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_log;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        WidgetUtils.initRecyclerView(recyclerView);

        mAdapter = new SimpleRecyclerAdapterLog();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);

        mAdapter.setOnItemClickListener(position -> {
            JSONArray data = Storage.type(Storage.Log).getJSONArray("log");
            //获取剪贴板管理器：
            getContext();
            ClipboardManager cm = (ClipboardManager) Objects.requireNonNull(getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", data.getJSONObject(position).getString("log"));
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            XToastUtils.success("日志复制成功！");
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
                XToastUtils.success("清除成功！");
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
            JSONArray data = Storage.type(Storage.Log).getJSONArray("log");
            List<String> list = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                list.add(String.valueOf(i));
            }
            if(data.size()!=0){
                map_error.setLeftString(String.format("已有%d条日志", data.size()));

            }else{
                map_error.setLeftString("这里空空的...");
            }
            mAdapter.refresh(list);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
        }, 1000);
    }






}
