package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;


public class BookSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    private final ArrayList<Bundle> books;

    public BookSelectListAdapter(Context context, ArrayList<Bundle> bundles) {
        super(context, R.layout.adapter_book_item_select);
        mContext = context;
        books = bundles;
    }

    @Override
    public int getCount() {
        if (books == null) return 0;
        else {
            return books.size();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = books.get(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_book_item_select, null, false);
        ImageView rl_bg = view.findViewById(R.id.rl_bg);

        TextView item_title = view.findViewById(R.id.item_value);

        item_title.setText(bundle.getString("name"));

        GlideLoadUtils.getInstance().glideLoad(mContext, bundle.getString("cover"), rl_bg, R.drawable.bg_timepicker);

        return view;


    }
}

