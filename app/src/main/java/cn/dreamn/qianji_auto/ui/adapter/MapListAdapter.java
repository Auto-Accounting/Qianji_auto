package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class MapListAdapter extends BaseAdapter {
    private final Context mContext;
    public MapListAdapter(Context context) {

        super(R.layout.map_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView icon_header = (ImageView) holder.findView(R.id.icon_header);
        TextView item_title2 = (TextView) holder.findView(R.id.item_title2);

        TextView item_title = (TextView) holder.findView(R.id.item_title);

        item_title2.setText(item.getString("name"));
        item_title.setText(item.getString("mapName"));
        Assets.getPic(item.getString("mapName"), asset2s -> {
            Glide.with(mContext)
                    .load(asset2s)
                    .into(icon_header);
        });
    }
}
