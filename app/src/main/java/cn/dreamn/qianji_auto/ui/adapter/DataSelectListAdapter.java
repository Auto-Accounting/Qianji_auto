package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Table.Asset;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;


public class DataSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    private final Asset[] assets;

    public DataSelectListAdapter(Context context, Asset[] assets) {
        super(context, R.layout.adapter_data_item);
        mContext = context;
        this.assets = assets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Asset asset = assets[position];

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_data_item, null, false);
        ImageView icon_header = view.findViewById(R.id.icon_header);
        TextView item_title = view.findViewById(R.id.item_value);

        item_title.setText(asset.name);


        GlideLoadUtils.getInstance().glideLoad(mContext, asset.icon, icon_header, R.drawable.bg_timepicker);

        return view;


    }
}

