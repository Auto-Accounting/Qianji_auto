package cn.dreamn.qianji_auto.ui.fragment;


import android.os.Handler;
import android.view.View;

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
import cn.dreamn.qianji_auto.core.db.Table.Log;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.adapter.LogAdapter;
import cn.dreamn.qianji_auto.utils.tools.Logs;

import static cn.dreamn.qianji_auto.ui.adapter.LogAdapter.KEY_POS;
import static cn.dreamn.qianji_auto.ui.adapter.LogAdapter.KEY_SUB;
import static cn.dreamn.qianji_auto.ui.adapter.LogAdapter.KEY_TITLE;


@Page(name = "日志查询")
public class LogFragment extends StateFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    private LogAdapter mAdapter;

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        showLoading("加载中...");

        WidgetUtils.initRecyclerView(recyclerView);

        mAdapter = new LogAdapter();
        recyclerView.setAdapter(mAdapter);

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);

        mAdapter.setOnItemClickListener((LogAdapter.OnItemClickListener) (item, pos) -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_log)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        switch (position) {
                            case 0:
                                Tools.clipboard(getContext(), item.get(KEY_TITLE));
                                SnackbarUtils.Long(getView(), getString(R.string.choose_copy_succeed)).info().show();
                                break;
                            case 1:
                                Logs.del(pos);
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
        new MaterialDialog.Builder(getContext())
                .title("日志缓存")
                .content("日志缓存有效期为24小时，如果有无法正常记账的情况请在24小时内反馈，24小时后日志会自动删除。")
                .negativeText("我知道了")
                .show();
    }

    @Override
    protected TitleBar initTitle() {

        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("清空") {
            @Override
            public void performAction(View view) {
                new MaterialDialog.Builder(getContext())
                        .title("清空日志")
                        .content("您确定要清除日志信息吗？")
                        .positiveText("确定")
                        .onPositive((dialog, which) -> {
                            Logs.delAll();
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
            //showLoading("正在加载日志");
            Log[] logs = Logs.getAll();
            List<Map<String, String>> data = new ArrayList<>();
            for (int i = 0; i < logs.length; i++) {
                Map<String, String> item = new HashMap<>();
                item.put(KEY_SUB, String.format("%s  %s", logs[i].sub, logs[i].time2));
                item.put(KEY_TITLE, logs[i].title);
                item.put(KEY_POS, String.valueOf(i));
                data.add(item);
            }
            if (data.size() == 0) {
                showEmpty("没有日志信息");
                return;
            }
            mAdapter.refresh(data);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
            showContent();
        }, 1000);
    }


}
