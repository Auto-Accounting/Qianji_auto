package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.github.xiaofeidev.round.RoundImageView;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class CategoryAdapter extends BaseAdapter {
    private final Context mContext;

    private final Boolean allow;

    private int select = -1;
    private List<Bundle> list;

    private Item itemListen;

    public CategoryAdapter(Context context, Boolean allowChange) {
        super(R.layout.adapter_category_item);
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
        //Log.i("绑定数据"+item.toString());
        LinearLayout view_1 = holder.findViewById(R.id.view_grid_1);
        LinearLayout view_2 = holder.findViewById(R.id.view_grid_2);
        view_1.setVisibility(View.VISIBLE);
        view_2.setVisibility(View.GONE);
        if (item.getString("name") == null) {
            if (item.containsKey("change")) {
                view_1.setVisibility(View.GONE);
                if (item.getBoolean("change")) {
                    view_2.setVisibility(View.VISIBLE);
                    doItem(item, holder, position);
                }
            } else {
                view_1.setVisibility(View.INVISIBLE);
            }

            return;
        }


        RoundImageView item_image_icon = (RoundImageView) holder.findView(R.id.item_image_icon);
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

        Glide.with(mContext)
                .load(item.getString("icon"))
                .into(item_image_icon);


        setColor(select == position, item_image_icon, iv_more, item_text);

    }


    private void setColor(boolean select, RoundImageView imageView, ImageView imageView1, TextView textView) {

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


    private void doItem(Bundle item, SmartViewHolder holder, int position1) {
        final int[] last = {-1};
        LinearLayout three_pos = holder.findViewById(R.id.three_pos);
        SwipeRecyclerView recycler_view2 = holder.findViewById(R.id.recycler_view2);
        CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter(mContext);
        categoryItemAdapter.setOpenAnimationEnable(false);
        recycler_view2.setLayoutManager(new GridLayoutManager(mContext, 5));
        recycler_view2.setAdapter(categoryItemAdapter);
        int left = item.getInt("left");
        three_pos.setPadding(left, 0, 0, 0);

        Bundle[] bundles = (Bundle[]) item.getSerializable("data");
        List<Bundle> list = Arrays.asList(bundles);
        categoryItemAdapter.refresh(list);
        categoryItemAdapter.setOnItemClickListener((itemView, position) -> {
            if (position >= list.size()) return;
            Bundle item1 = list.get(position);
            Log.d("last", "[1]" + last[0]);
            Log.d("last", "[position]" + position);
            categoryItemAdapter.setSelect(position);
            // categoryItemAdapter.notifyItemChanged(position);
            categoryItemAdapter.notifyItemChanged(last[0]);
            categoryItemAdapter.notifyItemChanged(position);
            if (last[0] == position) {
                categoryItemAdapter.setSelect(-1);
                categoryItemAdapter.notifyItemChanged(position);
                last[0] = -1;
                return;
            }
            last[0] = position;
            if (itemListen != null)
                itemListen.onClick(item1, item.getBundle("item"), position1);
        });
    }

    public interface Item {
        void onClick(Bundle bundle, Bundle parent, int parentPos);
    }

    public void setOnItemListener(Item itemListen) {
        this.itemListen = itemListen;
    }

}
