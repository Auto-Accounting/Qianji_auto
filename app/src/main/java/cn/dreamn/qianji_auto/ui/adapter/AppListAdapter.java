package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class AppListAdapter extends BaseAdapter {

    private final Context mContext;

    public AppListAdapter(Context context) {
        super(R.layout.adapter_data_item);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        ImageView imageView = holder.findViewById(R.id.icon_header);
        TextView textView = holder.findViewById(R.id.item_value);
        RelativeLayout rl = holder.findViewById(R.id.rl);
        imageView.setImageDrawable(AppInfo.getIcon(mContext, item.getString("pkg")));
        textView.setText(item.getString("name"));
        if (item.getBoolean("checked")) {
            rl.setBackgroundColor(mContext.getColor(R.color.float_2));
        } else {
            rl.setBackgroundColor(mContext.getColor(R.color.background_white));
        }

    }
}
