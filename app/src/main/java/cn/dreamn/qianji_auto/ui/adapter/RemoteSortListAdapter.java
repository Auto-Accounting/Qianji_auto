package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class RemoteSortListAdapter extends BaseAdapter {
    private final Context mContext;

    public RemoteSortListAdapter(Context context) {
        super(R.layout.adapter_remote_sort_item);
        mContext = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {


        TextView app_name = (TextView) holder.findView(R.id.app_name);

        app_name.setText(item.getString("name"));
    }

}
