package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Helper.Assets;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;

public class MapListAdapter extends BaseAdapter {
    private final Context mContext;
    public MapListAdapter(Context context) {

        super(R.layout.adapter_map_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView icon_header = (ImageView) holder.findView(R.id.icon_header);
        TextView item_title2 = (TextView) holder.findView(R.id.item_title2);

        TextView item_title = (TextView) holder.findView(R.id.item_value);

        item_title2.setText(item.getString("name"));
        item_title.setText(item.getString("mapName"));
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                GlideLoadUtils.getInstance().glideLoad(mContext, (String) msg.obj, icon_header, R.drawable.bg_timepicker);
            }
        };
        Assets.getPic(item.getString("mapName"), asset2s -> {
            Message message = new Message();
            message.obj = asset2s;
            mHandler.sendMessage(message);
        });
    }
}
