package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionList;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.adapter.ListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class ModeUtils {
    private final BaseFragment baseFragment;
    private final ListView lv_permission;
    private final LinearLayout mode_list;
    private final TextView mode_name;

    public ModeUtils(BaseFragment baseFragment1, LinearLayout mode_list1, TextView mode_name, ListView lv_permission1) {
        baseFragment = baseFragment1;
        mode_list = mode_list1;
        lv_permission = lv_permission1;
        this.mode_name = mode_name;
    }

    private void setUI() {
        MMKV mmkv = MMKV.defaultMMKV();
        String choose = mmkv.getString("helper_choose", "xposed");
        assert choose != null;
        if (choose.contains("xposed")) {
            mode_name.setText(R.string.mode_xp);
        } else {
            mode_name.setText(R.string.mode_helper);
        }
    }

    public void setMode() {
        Context context = baseFragment.getContext();
        setUI();
        JSONArray jsonArray = JSONArray.parseArray(Tool.getJson(context, "workMode"));
        mode_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(context,
                        context.getString(R.string.mode_select),
                        jsonArray,
                        new BottomArea.ListCallback() {
                            @Override
                            public void onSelect(int position) {
                                String choose = jsonArray.getJSONObject(position).getString("value");
                                MMKV mmkv = MMKV.defaultMMKV();
                                mmkv.encode("helper_choose", choose);
                                setUI();
                                setPermission(choose);
                            }
                        });
            }
        });

    }

    public void setPermission(String mode) {
        JSONObject permission = PermissionList.get(baseFragment.getContext());
        JSONArray list = permission.getJSONArray(mode.toLowerCase());
        ListAdapter listAdapter = new ListAdapter(baseFragment.getContext(), R.layout.components_supertext, list);
        lv_permission.setAdapter(listAdapter);
        lv_permission.setOnItemClickListener((parent, view, position, id) -> {
            PermissionUtils permissionUtils = new PermissionUtils(baseFragment.getContext());
            permissionUtils.grant(list.getJSONObject(position).getInteger("permission"));
        });

    }

}
