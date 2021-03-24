package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.niv.NiceImageView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;

public class CategoryAdapter extends BaseAdapter {
    private Context mContext;

    private final Boolean allow;

    private int index=-1;
    private int select=-1;
    private int lastPos=-1;

    public CategoryAdapter(Context context,Boolean allowChange) {
        super(R.layout.grid_item_iv);
        mContext=context;
        allow=allowChange;

    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        LinearLayout view_1=(LinearLayout)holder.findViewById(R.id.view_grid_1);
        LinearLayout view_2=(LinearLayout)holder.findViewById(R.id.view_grid_2);
        if(position==index){
            view_1.setVisibility(View.GONE);
            view_2.setVisibility(View.VISIBLE);
            doItem(item,holder);
            return;
        }
        view_1.setVisibility(View.VISIBLE);
        view_2.setVisibility(View.GONE);

        if(item.getString("name")==null){
            view_1.setVisibility(View.INVISIBLE);
            return;
        }
        NiceImageView item_image_icon = (NiceImageView) holder.findView(R.id.item_image_icon);
        ImageView iv_more=(ImageView)holder.findView(R.id.iv_more);
        TextView item_text = (TextView) holder.findView(R.id.item_text);
       // LinearLayout linearLayout = (LinearLayout) holder.findView(R.id.linear);

        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==2001){
                    if(msg.arg1==0){
                        iv_more.setVisibility(View.GONE);
                    }else{
                        iv_more.setVisibility(View.VISIBLE);
                    }
                }else{
                    MyBitmapUtils.setImage(mContext,item_image_icon,(Bitmap) msg.obj);
                }

            }
        };
        CategoryNames.getChildren(item.getString("self_id"), item.getString("book_id"), item.getString("type"),allow, categoryNames -> {

            Message message=new Message();
            message.what=2001;
            if(categoryNames.length<=0){
                message.arg1=0;
            }else{
                message.arg1=1;
            }
            mHandler.sendMessage(message);
        });
        item_text.setText(item.getString("name"));

        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        myBitmapUtils.disPlay(item_image_icon,item.getString("icon"));

        setColor(select == position,item_image_icon,iv_more,item_text,view_2);


    }

    private void setColor(boolean select,NiceImageView imageView,ImageView imageView1,TextView textView,LinearLayout view_2){

        if(!select){
            textView.setTextColor(mContext.getColor(R.color.deep_gray));

            imageView.setBackgroundResource(R.drawable.bg_round_gray2);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.front_color)));

            imageView1.setBackgroundResource(R.drawable.bg_round_gray2);
            imageView1.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.front_color)));
        }else{

            textView.setTextColor(mContext.getColor(R.color.button_go_setting_bg));
            imageView.setBackgroundResource(R.drawable.bg_round_blue2);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.background_white)));

            imageView1.setBackgroundResource(R.drawable.bg_round_blue2);
            imageView1.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.background_white)));
        }
    }

    public void setSelect(int index){
        this.select=index;
    }
    public void setIndex(int index){
        this.index=index;
    }

    private void doItem(Bundle item,SmartViewHolder holder){

        LinearLayout three_pos=holder.findViewById(R.id.three_pos);
        SwipeRecyclerView recycler_view2=holder.findViewById(R.id.recycler_view2);
        CategoryItemAdapter categoryItemAdapter=new CategoryItemAdapter(mContext);
        recycler_view2.setLayoutManager(new GridLayoutManager(mContext,5));
        recycler_view2.setAdapter(categoryItemAdapter);
        three_pos.setPadding(item.getInt("left"),0,0,0);

    }


}
