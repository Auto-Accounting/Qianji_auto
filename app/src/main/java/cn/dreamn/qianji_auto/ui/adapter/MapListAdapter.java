package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;

public class MapListAdapter extends BaseAdapter {
    private Context mContext;
    public MapListAdapter(Context context) {

        super(R.layout.map_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        ImageView icon_header = (ImageView) holder.findView(R.id.icon_header);
        TextView item_title2 = (TextView) holder.findView(R.id.item_title2);

        TextView item_title = (TextView) holder.findView(R.id.item_title);

        item_title2.setText(item.getString("name"));
        item_title.setText(item.getString("mapName"));
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Object[] objects=(Object[])msg.obj;
                MyBitmapUtils.setImage(mContext,(View) objects[0],(Bitmap)objects[1]);
            }
        };
        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        Assets.getPic(item.getString("mapName"), asset2s -> myBitmapUtils.disPlay(icon_header,asset2s));
    }
}
