package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class DataListAdapter extends BaseAdapter {
    private final Context mContext;
    public DataListAdapter(Context context) {

        super(R.layout.data_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView icon_header = (ImageView) holder.findView(R.id.icon_header);

        TextView item_title = (TextView) holder.findView(R.id.item_title);

        item_title.setText(item.getString("name"));

        Glide.with(mContext)
                .load(item.getString("icon"))
                .into(icon_header);


    }
}
