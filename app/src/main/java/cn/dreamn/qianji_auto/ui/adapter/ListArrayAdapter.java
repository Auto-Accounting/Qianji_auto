package cn.dreamn.qianji_auto.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.components.IconView;


public class ListArrayAdapter extends ArrayAdapter {
    JSONArray jsonArray;

    public ListArrayAdapter(Context context, int resource, JSONArray js) {
        super(context, resource);
        jsonArray = js;
        //   Log.d(jsonArray.toString());
    }

    @Override
    public int getCount() {
        if (jsonArray == null) return 0;
        return jsonArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject jsonObject = jsonArray.getJSONObject(position);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_list_items2, null, false);

        IconView iconView = view.findViewById(R.id.icon_header);
        iconView.setFont(jsonObject.getString("icon"));

        TextView textView = view.findViewById(R.id.item_value);
        textView.setText(jsonObject.getString("name"));
        TextView textView2 = view.findViewById(R.id.item_sub);
        textView2.setText(jsonObject.getString("sub"));

        // Log.d(jsonObject.getString("name"));
        //  Log.d(jsonObject.getString("sub"));
        return view;


    }
}

