package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;

public class MoneyAdapter extends BaseAdapter {
    private final Context mContext;

    private Item itemListen;

    public MoneyAdapter(Context context) {
        super(R.layout.adapter_money_list);
        mContext = context;


    }


    @Override
    public SmartRecyclerAdapter<Bundle> refresh(Collection<Bundle> collection) {
        List<Bundle> list = (List<Bundle>) collection;
        return super.refresh(collection);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        TextView date_title= holder.findViewById(R.id.date_title);
        SwipeRecyclerView recyclerView=holder.findViewById(R.id.recycler_view);

        String data=item.getString("date");
        String text=data;

        if (data.equals(DateUtils.getTime("MM.dd"))) {
            text += " 今天";
        } else if (data.equals(DateUtils.getTime("MM.dd", -1))) {
            text += " 昨天";
        } else if (data.equals(DateUtils.getTime("MM.dd", -2))) {
            text += " 前天";
        }
        date_title.setText(text);

        MoneyItemAdapter moneyItemAdapter=new MoneyItemAdapter(mContext);
        Bundle[] datas=(Bundle[]) item.getSerializable("data");
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(moneyItemAdapter);

        moneyItemAdapter.refresh(Arrays.asList(datas));

        moneyItemAdapter.setOnItemClickListener((itemView, position1) -> {
            if(itemListen != null && position1 < datas.length){
                //     Log.i("click "+position1);
                itemListen.onClick(datas[position1], position1);
            }
        });
        moneyItemAdapter.setOnItemLongClickListener((itemView, position1) -> {
            if(itemListen != null && position1 < datas.length){
                //      Log.i("long click "+position1);
                itemListen.onLongClick(datas[position1], position1);
            }
        });


    }





    public interface Item {
        void onClick(Bundle bundle, int pos);
        void onLongClick(Bundle bundle,int pos);
    }

    public void setOnItemListener(Item itemListen) {
        this.itemListen = itemListen;
    }

}
