package com.thl.filechooser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class SdCardAdapter extends BaseAdapter {

    ArrayList<String> arrayList = new ArrayList<>();
    Context context;

    public SdCardAdapter(Context context, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int itemViewWidth;

    public int getItemViewWidth() {
        if (itemViewWidth == 0)
            itemViewWidth = 380;
        return itemViewWidth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.item_file_path, null);
        TextView textView = (TextView) convertView.findViewById(R.id.fileName);
        View line = convertView.findViewById(R.id.divider);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.fileIcon);
        imageView.getLayoutParams().width = dp2px(26);
        imageView.getLayoutParams().height = dp2px(30);

        if (position == 0) {
            imageView.setImageResource(R.drawable.phone);
        } else {
            imageView.setImageResource(R.drawable.sdcard);
        }
        if (arrayList != null && arrayList.size() > 0 && position != arrayList.size() - 1) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);
        }
        textView.setText(arrayList.get(position));

        convertView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemViewWidth = convertView.getMeasuredWidth();
        return convertView;
    }

    public int dp2px(final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
