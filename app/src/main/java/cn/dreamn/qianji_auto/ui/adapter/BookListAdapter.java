package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class BookListAdapter extends BaseAdapter {
    private final Context mContext;
    public BookListAdapter(Context context) {

        super(R.layout.adapter_book_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        RelativeLayout rl_bg = (RelativeLayout) holder.findView(R.id.rl_bg);

        TextView item_title = (TextView) holder.findView(R.id.item_title);

        item_title.setText(item.getString("name"));

        Glide.with(mContext)
                .load(item.getString("cover"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        rl_bg.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


    }
}
