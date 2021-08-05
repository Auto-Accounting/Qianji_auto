package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import cn.dreamn.qianji_auto.R;


public class BookSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    public BookSelectListAdapter(Context context, Bundle[] bundles) {
        super(context, R.layout.adapter_book_item_select, bundles);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_book_item_select, null, false);
        RelativeLayout rl_bg = view.findViewById(R.id.rl_bg);

        TextView item_title = view.findViewById(R.id.item_title);

        item_title.setText(bundle.getString("name"));

        Glide.with(mContext)
                .load(bundle.getString("cover"))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        rl_bg.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        return view;


    }
}

