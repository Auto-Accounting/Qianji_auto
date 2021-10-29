package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.ui.components.IconView;

public class CateItemListAdapter extends BaseAdapter {
    private final Context mContext;

    private MoreClick moreClick;
    private UpdateClick updateClick;

    private JSONObject jsonObject;

    public CateItemListAdapter(Context context) {
        super(R.layout.adapter_cate_list_item);
        mContext = context;
    }

    public void setUpdateJSON(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        View view_status = holder.findView(R.id.view_status);
        View view_auto = holder.findView(R.id.view_auto);
        TextView tv_title = (TextView) holder.findView(R.id.tv_title);
        TextView tv_des = (TextView) holder.findView(R.id.tv_des);
        IconView icon_info = (IconView) holder.findView(R.id.icon_info);
        TextView tv_update = (TextView) holder.findView(R.id.update);
        tv_title.setText(item.getString("name"));
        String des = item.getString("remark");
        boolean isAuto = false;
        if (des != null) {
            if (des.startsWith("[自动生成]")) {
                isAuto = true;
            }
            tv_des.setText(des);
        } else {
            icon_info.setVisibility(View.GONE);
            tv_des.setVisibility(View.GONE);
        }
        Drawable drawable_top = mContext.getDrawable(R.drawable.bg_cate_green);
        Drawable drawable_bottom = mContext.getDrawable(R.drawable.bg_cate_green_bottom);

        if (item.getInt("use") != 1) {
            drawable_top = mContext.getDrawable(R.drawable.bg_cate_gray);
            drawable_bottom = mContext.getDrawable(R.drawable.bg_cate_gray_bottom);
        }
        view_status.setBackground(drawable_top);
        if (isAuto) {
            view_auto.setBackground(mContext.getDrawable(R.drawable.bg_cate_auto));
        } else {
            view_auto.setBackground(drawable_bottom);
        }
        icon_info.setOnClickListener(v -> {
            if (moreClick != null)
                moreClick.onClick(item);
        });

        //获取云端Id
        tv_update.setVisibility(View.INVISIBLE);
        if (jsonObject == null) {

            return;
        }
        String dataId = item.getString("dataId");
        if (dataId == null || dataId.equals("")) {

            return;
        }
        String version = item.getString("version");
        if (version == null || version.equals("")) {

            return;
        }
        String name = item.getString("name");

        JSONObject jsonObject1 = jsonObject.getJSONObject(dataId);
        if (jsonObject1 == null) {

            return;
        }

        int serverVer = Integer.parseInt(jsonObject1.getString("version"));
        int localVer = Integer.parseInt(version);

        if (serverVer > localVer) {
            tv_update.setVisibility(View.VISIBLE);
            String title = name + "[" + localVer + " → " + serverVer + "]";
            String updateLog = jsonObject1.getString("log");
            if (updateLog == null || updateLog.equals("")) updateLog = "无更新日志";
            JSONObject js = new JSONObject();
            js.put("title", title);
            js.put("log", updateLog);
            js.put("name", jsonObject1.getString("name"));
            tv_update.setOnClickListener(v -> {
                if (updateClick != null)
                    updateClick.onClick(js);
            });


        }


    }


    public void setOnMoreClick(MoreClick moreClick) {
        if (moreClick != null)
            this.moreClick = moreClick;
    }

    public void setOnUpdateClick(UpdateClick updateClick) {
        if (updateClick != null)
            this.updateClick = updateClick;
    }

    public interface MoreClick {
        void onClick(Bundle item);
    }

    public interface UpdateClick {
        void onClick(JSONObject data);
    }
}
