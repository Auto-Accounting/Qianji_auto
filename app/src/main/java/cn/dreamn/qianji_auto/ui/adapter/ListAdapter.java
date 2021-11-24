package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.components.IconView;


public class ListAdapter extends ArrayAdapter {
    JSONArray jsonArray;

    public ListAdapter(Context context, int resource, JSONArray js) {
        super(context, resource, js.toArray());
        jsonArray = js;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject jsonObject = jsonArray.getJSONObject(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_list_item_3, null, false);

        IconView iconView = view.findViewById(R.id.icon_header);
        iconView.setFont(jsonObject.getString("icon"));
        IconView iconView2 = view.findViewById(R.id.icon_isOk);
        PermissionUtils permissionUtils = new PermissionUtils(getContext());
        String isOk = permissionUtils.isGrant(jsonObject.getInteger("permission"));
        if(isOk.equals("1")){
            iconView2.setFont(getContext().getString(R.string.icon_gou));
            iconView2.setTextColor(getContext().getColor(R.color.succeed));
        }else if(isOk.equals("0")){
            iconView2.setFont(getContext().getString(R.string.icon_cha1));
            iconView2.setTextColor(getContext().getColor(R.color.error));
        }else{
            iconView2.setFont(getContext().getString(R.string.icon_weizhi));
            iconView2.setTextColor(getContext().getColor(R.color.warnning));
        }

        TextView textView = view.findViewById(R.id.item_value);
        textView.setText(jsonObject.getString("name"));
        TextView textView2 = view.findViewById(R.id.item_sub);
        textView2.setText(jsonObject.getString("sub"));

        return view;


    }
}

