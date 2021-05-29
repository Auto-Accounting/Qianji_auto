package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.ui.views.IconView;

public class CateItemListAdapter extends BaseAdapter {
    private final Context mContext;

    private MoreClick moreClick;
    public CateItemListAdapter(Context context) {

        super(R.layout.cate_list_item);
        mContext = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        View view_status = holder.findView(R.id.view_status);
        View view_auto = holder.findView(R.id.view_auto);
        TextView tv_title = (TextView) holder.findView(R.id.tv_title);
        TextView tv_des = (TextView) holder.findView(R.id.tv_des);
        IconView icon_info = (IconView) holder.findView(R.id.icon_info);

        tv_title.setText(item.getString("name"));
        String des = item.getString("des");
        boolean isAuto = false;
        if (des != null) {
            if (des.startsWith("[自动生成]")) {
                isAuto = true;
            }
            if (des.length() > 15)
                des = des.substring(0, 15) + "...";
            tv_des.setText(des);
        } else {
            icon_info.setVisibility(View.GONE);
            tv_des.setVisibility(View.GONE);
        }
        int color = mContext.getColor(R.color.button_go_setting_bg);
        if (item.getInt("use") != 1) {
            color = mContext.getColor(R.color.skip_title);
        }
        view_status.setBackgroundColor(color);
        if (isAuto) {
            view_auto.setBackgroundColor(mContext.getColor(R.color.float_3));
        } else {
            view_auto.setBackgroundColor(color);
        }
        icon_info.setOnClickListener(v -> {
            if (moreClick != null)
                moreClick.onClick(item);
        });

    }

    public void setOnMoreClick(MoreClick moreClick) {
        if (moreClick != null)
            this.moreClick = moreClick;
    }

    public interface MoreClick {
        void onClick(Bundle item);
    }
}
