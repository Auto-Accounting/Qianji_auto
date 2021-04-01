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
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;

public class BookListAdapter extends BaseAdapter {
    private Context mContext;
    public BookListAdapter(Context context) {

        super(R.layout.book_item);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

        RelativeLayout rl_bg = (RelativeLayout) holder.findView(R.id.rl_bg);

        TextView item_title = (TextView) holder.findView(R.id.item_title);

        item_title.setText(item.getString("name"));
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Object[] objects=(Object[])msg.obj;
                MyBitmapUtils.setImage(mContext,(View) objects[0],(Bitmap)objects[1]);
            }
        };
        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        myBitmapUtils.disPlay(rl_bg,item.getString("cover"));

    }
}
