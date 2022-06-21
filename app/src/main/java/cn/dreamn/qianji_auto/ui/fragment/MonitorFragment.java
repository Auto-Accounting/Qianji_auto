package cn.dreamn.qianji_auto.ui.fragment;


import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.adapter.AppListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

@Page(name = "记账监控", anim = CoreAnim.slide)
public class MonitorFragment extends BaseFragment implements TextWatcher {

    @BindView(R.id.search_et_input)
    TextView search_et_input;
    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recycler_view;
    Handler mHandler;
    private AppListAdapter mAdapter;
    private List<Bundle> list;
    private String keyWord = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_monitor;
    }

    @Override
    protected void initViews() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        if (statusView != null) statusView.showEmptyView();
                        break;
                    case HANDLE_OK:
                        mAdapter.refresh(list, keyWord);
                        if (statusView != null) statusView.showContentView();
                        break;
                    case HANDLE_REFRESH:
                        loadFromData();
                        break;
                }
                String d = (String) msg.obj;
                if ((d != null && !d.equals("")))
                    ToastUtils.show(d);
            }


        };
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, getString(R.string.notice_empty)));
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });

        mAdapter = new AppListAdapter(getContext());

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Bundle bundle = list.get(position);

                SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getContext(), "apps", Context.MODE_PRIVATE);
                String[] apps = sharedPreferences.getString("apps", "").split(",");
                List<String> list2 = Arrays.asList(apps);
                List<String> arrList = new ArrayList(list2);
                if (bundle.getBoolean("checked")) {
                    bundle.putBoolean("checked", false);
                    arrList.remove(bundle.getString("pkg"));
                } else {
                    bundle.putBoolean("checked", true);
                    arrList.add(bundle.getString("pkg"));
                }
                StringBuilder ss = new StringBuilder();
                for (String s : arrList) {
                    ss.append(s).append(",");
                }
                sharedPreferences.edit().putString("apps", ss.toString()).apply();
                mAdapter.replace(position, bundle);
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    private void loadFromData() {
        if (statusView != null) statusView.showLoadingView();
        TaskThread.onThread(() -> getPkgList(getActivity()));

    }

    private void getPkgList(Context context) {
        SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getContext(), "apps", Context.MODE_PRIVATE);
        String[] apps = sharedPreferences.getString("apps", "").split(",");
        List<Bundle> checked = new ArrayList<Bundle>();
        List<Bundle> packages = new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec("pm list packages");
            InputStreamReader isr = new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                Log.d("pm list packages: " + line);
                line = line.trim();
                if (line.length() > 8) {
                    String prefix = line.substring(0, 8);
                    if (prefix.equalsIgnoreCase("package:")) {
                        line = line.substring(8).trim();
                        if (!TextUtils.isEmpty(line)) {
                            String name = AppInfo.getName(getActivity(), line);
                            if (!keyWord.equals("") && !line.contains(keyWord) && !name.contains(keyWord))
                                continue;
                            Bundle bundle = new Bundle();
                            bundle.putString("name", name);
                            bundle.putString("pkg", line);
                            boolean check = isIn(apps, line);
                            bundle.putBoolean("checked", check);
                            if (check) {
                                checked.add(bundle);
                            } else packages.add(bundle);
                        }
                    }
                }
                line = br.readLine();
            }

            br.close();
            p.destroy();
            checked.addAll(packages);
            list = checked;
            HandlerUtil.send(mHandler, HANDLE_OK);
        } catch (Throwable ignored) {

        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        search_et_input.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        keyWord = editable.toString();
        loadFromData();
    }

    private boolean isIn(String[] packages, String pack) {
        boolean flag = false;
        for (String package1 : packages) {
            if (pack.equals(package1)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
