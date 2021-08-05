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

import com.bumptech.glide.Glide;

import cn.dreamn.qianji_auto.R;


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
        TextView item_title = view.findViewById(R.id.item_title);

        item_title.setText(bundle.getString("name"));


        Glide.with(mContext)
                .load(bundle.getString("icon"))
                .into(icon_header);

        return view;


    }
}

