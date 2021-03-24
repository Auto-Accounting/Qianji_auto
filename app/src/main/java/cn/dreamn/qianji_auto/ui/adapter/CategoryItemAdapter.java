package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.niv.NiceImageView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;

public class CategoryItemAdapter extends BaseAdapter {
    private Context mContext;
    public CategoryItemAdapter(Context context) {
        super(R.layout.grid_item_iv2);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        NiceImageView item_image_icon = (NiceImageView) holder.findView(R.id.item_image_icon);

        TextView item_text = (TextView) holder.findView(R.id.item_text);

        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                MyBitmapUtils.setImage(mContext,item_image_icon,(Bitmap) msg.obj);

            }
        };
        item_text.setText(item.getString("name"));

        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        myBitmapUtils.disPlay(item_image_icon,item.getString("icon"));
    }
}
