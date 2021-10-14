package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;

public class BookListAdapter extends BaseAdapter {
    private final Context mContext;
    public BookListAdapter(Context context) {

        super(R.layout.adapter_book_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        RelativeLayout rl_bg = (RelativeLayout) holder.findView(R.id.rl_bg);

        TextView item_title = (TextView) holder.findView(R.id.item_value);

        item_title.setText(item.getString("name"));
        GlideLoadUtils.getInstance().glideLoad(mContext, item.getString("cover"), rl_bg, R.drawable.bg_timepicker);



    }
}
