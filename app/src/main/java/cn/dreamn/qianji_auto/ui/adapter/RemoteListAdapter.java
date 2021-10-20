package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class RemoteListAdapter extends BaseAdapter {
    private final Context mContext;

    private final boolean isWeb;

    public RemoteListAdapter(Context context, boolean isWeb) {
        super(R.layout.adapter_remote_item);
        this.isWeb = isWeb;
        mContext = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView icon_header = (ImageView) holder.findView(R.id.icon_header);
        TextView app_name = (TextView) holder.findView(R.id.app_name);
        TextView app_count = (TextView) holder.findView(R.id.app_count);
        if (item.getString("type").equals("sms")) {
            icon_header.setImageDrawable(AppInfo.getIcon(mContext, "com.android.providers.telephony"));
        } else {

            icon_header.setImageDrawable(AppInfo.getIcon(mContext, item.getString("pkg")));
        }

        if (isWeb) {
            app_name.setText(item.getString("name"));
            app_count.setText(item.getString("count"));
        } else {
            app_name.setText(AppInfo.getName(mContext, item.getString("pkg")));
            app_count.setVisibility(View.GONE);
        }


    }


}
