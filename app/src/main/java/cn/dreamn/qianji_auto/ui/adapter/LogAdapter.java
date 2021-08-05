package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class LogAdapter extends BaseAdapter {
    private final Context mContext;

    public LogAdapter(Context context) {

        super(R.layout.adapter_item_log);
        mContext = context;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        TextView item_top = (TextView) holder.findView(R.id.item_top);
        TextView item_bottom = (TextView) holder.findView(R.id.item_bottom);
        item_top.setText(item.getString("title"));
        item_bottom.setText(item.getString("sub") + " " + item.getString("time"));
    }
}
