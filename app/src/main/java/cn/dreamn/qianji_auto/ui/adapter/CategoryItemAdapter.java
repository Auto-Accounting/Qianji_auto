package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.niv.NiceImageView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class CategoryItemAdapter extends BaseAdapter {
    private final Context mContext;

    private int select = -1;
    public CategoryItemAdapter(Context context) {
        super(R.layout.grid_item_iv2);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        NiceImageView item_image_icon = (NiceImageView) holder.findView(R.id.item_image_icon);

        TextView item_text = (TextView) holder.findView(R.id.item_text);

        setColor(select == position, item_image_icon, item_text);

        item_text.setText(item.getString("name"));


        Glide.with(mContext)
                .load(item.getString("icon"))
                .into(item_image_icon);

    }
    public void setSelect(int index) {
        this.select = index;
    }
    private void setColor(boolean select, NiceImageView imageView,TextView textView) {

        if (!select) {
            textView.setTextColor(mContext.getColor(R.color.deep_gray));
            imageView.setBackgroundResource(R.drawable.bg_round_gray);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.front_color)));

        } else {
            textView.setTextColor(mContext.getColor(R.color.button_go_setting_bg));
            imageView.setBackgroundResource(R.drawable.bg_round_blue);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.background_white)));
        }
    }

}
