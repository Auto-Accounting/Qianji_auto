package cn.dreamn.qianji_auto.fragment;


import android.graphics.Color;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.core.SimpleRecyclerAdapter;

import cn.dreamn.qianji_auto.func.SimpleData;
import cn.dreamn.qianji_auto.func.Storage;
import cn.dreamn.qianji_auto.utils.XToastUtils;


@Page(name = "资产映射")
public class MapFragment extends BaseFragment {


    @BindView(R.id.map_layout)
    SwipeRefreshLayout map_layout;
    @BindView(R.id.map_error)
    SuperTextView map_error;
    private SimpleRecyclerAdapter mAdapter;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }
    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("添加") {
            @Override
            public void performAction(View view) {
                showAddDialog("添加资产映射","","",(from,to)->{
                    Storage.type(Storage.Map).set(from,to);
                    XToastUtils.success("添加成功！");
                    refresh();
                });
            }
        });
        return titleBar;
    }
    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        SimpleData.initMap();
        WidgetUtils.initRecyclerView(recyclerView);

        //必须在setAdapter之前调用
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        //必须在setAdapter之前调用
        recyclerView.setOnItemMenuClickListener(mMenuItemClickListener);
        recyclerView.setAdapter(mAdapter = new SimpleRecyclerAdapter());

        map_layout.setColorSchemeColors(0xff0099cc, 0xffff4444, 0xff669900, 0xffaa66cc, 0xffff8800);


    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, position) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 添加左侧的，如果不添加，则左侧不会出现菜单。
        {

        }

        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext()).setBackground(R.color.app_color_theme_1)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

            SwipeMenuItem addItem = new SwipeMenuItem(getContext()).setBackground(R.color.toast_success_color)
                    .setText("修改")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = (menuBridge, position) -> {
        menuBridge.closeMenu();

        int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
        int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

        if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
            JSONObject data = Storage.type(Storage.Map).getAll();
            List list=new ArrayList<>(data.keySet());
            String key= list.get(position).toString();
            String value=data.getString(key);
            if(menuPosition==0){
                Storage.type(Storage.Map).del(key);
                refresh();
            }else{
                showAddDialog("修改资产映射",key,value,(from,to)->{
                    Storage.type(Storage.Map).set(from,to);
                    XToastUtils.success("修改成功！");
                    refresh();
                });
            }
        }
    };

    @Override
    protected void initListeners() {
        //下拉刷新
        map_layout.setOnRefreshListener(this::loadData);
        refresh(); //第一次进入触发自动刷新，演示效果
    }

    private void refresh() {
        map_layout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        new Handler().postDelayed(() -> {
            JSONObject data = Storage.type(Storage.Map).getAll();
            List<String> list ;
            if(data!=null&&data.size()!=0){
                map_error.setLeftString(String.format("已有%d条资产映射", data.size()));
                list=new ArrayList<>(data.keySet());
            }else{
                map_error.setLeftString("这里空空的...");
                list = new ArrayList<>();
            }
            mAdapter.refresh(list);
            if (map_layout != null) {
                map_layout.setRefreshing(false);
            }
        }, 1000);
    }
    // 回调接口
    interface CallBack {
        void onResponse(String from,String to);

    }
    protected void showAddDialog(String title,String from,String to,CallBack callBack) {

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.input_map, null);
        final EditText map_from = (EditText) textEntryView.findViewById(R.id.map_from);
        final EditText map_to = (EditText)textEntryView.findViewById(R.id.map_to);
        map_from.setText(from);
        map_to.setText(to);
        new MaterialDialog.Builder(getContext())
                .customView(textEntryView, true)
                .title(title)
                .positiveText("保存")
                .onPositive((dialog, which) -> {
                    callBack.onResponse(map_from.getText().toString(),map_to.getText().toString());
                })
                .negativeText("取消")
                .show();


    }





}
