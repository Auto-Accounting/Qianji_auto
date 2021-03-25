package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.shehuan.niv.NiceImageView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class CategoryAdapter extends BaseAdapter {
    private Context mContext;

    private final Boolean allow;

    private int index = -1;
    private int select = -1;
    private List<Bundle> list;

    public CategoryAdapter(Context context, Boolean allowChange) {
        super(R.layout.grid_item_iv);
        mContext = context;
        allow = allowChange;

    }

    @Override
    public SmartRecyclerAdapter<Bundle> refresh(Collection<Bundle> collection) {
        this.list = (List<Bundle>) collection;
        return super.refresh(collection);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
       // Log.d("刷新当前位置 "+position+"  刷新当前"+item.toString()+"");
       // RelativeLayout relativeLayout = (RelativeLayout) holder.findView(R.id.layout);
       // relativeLayout.setBackgroundColor(mContext.getColor(R.color.background_white));
        LinearLayout view_1 = (LinearLayout) holder.findViewById(R.id.view_grid_1);
        LinearLayout view_2 = (LinearLayout) holder.findViewById(R.id.view_grid_2);
        if (item.getString("name") == null) {
            if(item.containsKey("change")){
                if(item.getBoolean("change")){
                    view_1.setVisibility(View.GONE);
                    view_2.setVisibility(View.VISIBLE);
                    doItem(item, holder);
                }else{
                    view_1.setVisibility(View.GONE);
                    view_2.setVisibility(View.GONE);
                }
            }else{
                view_1.setVisibility(View.INVISIBLE);
            }

            return;
        }

        view_1.setVisibility(View.VISIBLE);
        view_2.setVisibility(View.GONE);

        NiceImageView item_image_icon = (NiceImageView) holder.findView(R.id.item_image_icon);
        ImageView iv_more = (ImageView) holder.findView(R.id.iv_more);
        TextView item_text = (TextView) holder.findView(R.id.item_text);




        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 2001) {
                    if (msg.arg1 == 0) {
                        iv_more.setVisibility(View.GONE);
                    } else {
                        iv_more.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(item_image_icon.getTag()==null){
                        MyBitmapUtils.setImage(mContext, item_image_icon, (Bitmap) msg.obj);
                        item_image_icon.setTag(item.getString("icon"));
                    }

                }

            }
        };
        CategoryNames.getChildren(item.getString("self_id"), item.getString("book_id"), item.getString("type"), allow, categoryNames -> {

            Message message = new Message();
            message.what = 2001;
            if (categoryNames.length <= 0) {
                message.arg1 = 0;
            } else {
                message.arg1 = 1;
            }
            mHandler.sendMessage(message);
        });
        item_text.setText(item.getString("name"));

        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(mContext, mHandler);
        myBitmapUtils.disPlay(item_image_icon, item.getString("icon"));

        setColor(select == position, item_image_icon, iv_more, item_text);


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setColor(boolean select, NiceImageView imageView, ImageView imageView1, TextView textView) {

        if (!select) {
            textView.setTextColor(mContext.getColor(R.color.deep_gray));
            imageView.setBackgroundResource(R.drawable.bg_round_gray2);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.front_color)));
            imageView1.setBackgroundResource(R.drawable.bg_round_gray2);
            imageView1.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.front_color)));
        } else {
            textView.setTextColor(mContext.getColor(R.color.button_go_setting_bg));
            imageView.setBackgroundResource(R.drawable.bg_round_blue2);
            imageView.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.background_white)));
            imageView1.setBackgroundResource(R.drawable.bg_round_blue2);
            imageView1.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.background_white)));
        }
    }

    public void setSelect(int index) {
        this.select = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void doItem(Bundle item, SmartViewHolder holder) {

        LinearLayout three_pos = holder.findViewById(R.id.three_pos);
        SwipeRecyclerView recycler_view2 = holder.findViewById(R.id.recycler_view2);
        CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter(mContext);
        categoryItemAdapter.setOpenAnimationEnable(false);
        recycler_view2.setLayoutManager(new GridLayoutManager(mContext, 5));
        recycler_view2.setAdapter(categoryItemAdapter);
        three_pos.setPadding(item.getInt("left"), 0, 0, 0);
        Bundle[] bundles = (Bundle[]) item.getSerializable("data");
        categoryItemAdapter.refresh(Arrays.asList(bundles));
    }


}
