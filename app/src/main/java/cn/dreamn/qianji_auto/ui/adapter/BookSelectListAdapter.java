package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;


public class BookSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    public BookSelectListAdapter(Context context, Bundle[] bundles) {
        super(context, R.layout.adapter_book_item_select, bundles);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_book_item_select, null, false);
        RelativeLayout rl_bg = view.findViewById(R.id.rl_bg);

        TextView item_title = view.findViewById(R.id.item_value);

        item_title.setText(bundle.getString("name"));

        GlideLoadUtils.getInstance().glideLoad(mContext, bundle.getString("cover"), rl_bg, R.drawable.bg_timepicker);

        return view;


    }
}

