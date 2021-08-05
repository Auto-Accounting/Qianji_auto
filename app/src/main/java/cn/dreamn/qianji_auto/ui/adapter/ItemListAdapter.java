package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.AppUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class ItemListAdapter extends BaseAdapter {
    private final Context mContext;

    public ItemListAdapter(Context context) {

        super(R.layout.adapter_list_item);
        mContext = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView iv_appIcon = (ImageView) holder.findView(R.id.iv_appIcon);
        TextView tv_appName = (TextView) holder.findView(R.id.tv_appName);
        TextView tv_data = (TextView) holder.findView(R.id.tv_title);
        TextView tv_date = (TextView) holder.findView(R.id.tv_des);
/*        TextView tv_could = (TextView) holder.findView(R.id.tv_could);
        TextView tv_local = (TextView) holder.findView(R.id.tv_local);*/
        tv_data.setText(item.getString("rawData"));
        tv_date.setText(Tool.getShortTime(Long.parseLong(item.getString("time") + "000"), "yyyy-MM-dd HH:mm:ss"));
        iv_appIcon.setImageBitmap(AppUtils.getBitmap(mContext, item.getString("fromApp")));
        tv_appName.setText(AppUtils.getAppName(mContext, item.getString("fromApp")));
/*
        //已适配 未适配
        String bool = item.getString("cloud");
        if (bool != null) {
            tv_could.setBackground(mContext.getDrawable(R.drawable.btn_normal));
            tv_could.setText("云端已适配");
        }else{
            tv_could.setBackground(mContext.getDrawable(R.drawable.btn_normal_3));
            tv_could.setText("云端未适配");
        }
        String bool2=item.getString("local");
        if(bool2!=null){
            tv_local.setBackground(mContext.getDrawable(R.drawable.btn_normal));
            tv_local.setText("本地已适配");
        }else{
            tv_local.setBackground(mContext.getDrawable(R.drawable.btn_normal_3));
            tv_local.setText("本地未适配");
        }*/
    }
}
