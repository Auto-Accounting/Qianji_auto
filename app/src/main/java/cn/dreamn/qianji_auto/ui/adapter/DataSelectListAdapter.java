package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;


public class DataSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    public DataSelectListAdapter(Context context, Bundle[] bundles) {
        super(context, R.layout.adapter_data_item, bundles);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_data_item, null, false);
        ImageView icon_header = view.findViewById(R.id.icon_header);
        TextView item_title = view.findViewById(R.id.item_value);

        item_title.setText(bundle.getString("name"));


        GlideLoadUtils.getInstance().glideLoad(mContext, bundle.getString("icon"), icon_header, R.drawable.bg);

        return view;


    }
}

