package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.AppUtils;

public class ItemListAdapter extends BaseAdapter {
    private final Context mContext;

    public ItemListAdapter(Context context) {

        super(R.layout.list_item);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView iv_appIcon = (ImageView) holder.findView(R.id.iv_appIcon);
        TextView tv_appName = (TextView) holder.findView(R.id.tv_appName);
        TextView tv_data = (TextView) holder.findView(R.id.tv_data);
        TextView tv_date = (TextView) holder.findView(R.id.tv_date);

        tv_data.setText(item.getString("rawData"));
        tv_date.setText(item.getString("time"));
        iv_appIcon.setImageBitmap(AppUtils.getBitmap(mContext, item.getString("fromApp")));
        tv_appName.setText(AppUtils.getAppName(mContext, item.getString("fromApp")));

    }
}
