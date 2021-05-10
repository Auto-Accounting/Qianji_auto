package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;


public class DataSelectListAdapter extends ArrayAdapter {

    private final Context mContext;
    public DataSelectListAdapter(Context context, Bundle[] bundles) {
        super(context, R.layout.data_item, bundles);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.data_item, null, false);
        ImageView icon_header = (ImageView) view.findViewById(R.id.icon_header);
        TextView item_title = (TextView) view.findViewById(R.id.item_title);

        item_title.setText(bundle.getString("name"));
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Object[] objects=(Object[])msg.obj;
                MyBitmapUtils.setImage(mContext,(View) objects[0],(Bitmap)objects[1]);
            }
        };
        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        myBitmapUtils.disPlay(icon_header,bundle.getString("icon"));
        return view;


    }
}

