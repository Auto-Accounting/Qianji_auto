package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.AutoBills;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.utils.billUtils.BillInfo;
import cn.dreamn.qianji_auto.utils.billUtils.BillTools;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class MoneyItemAdapter extends BaseAdapter {
    private Context mContext;


    public MoneyItemAdapter(Context context) {
        super(R.layout.money_list_items);
        mContext = context;


    }



    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        TextView sort=(TextView)holder.findViewById(R.id.sort);
        TextView date=(TextView)holder.findViewById(R.id.date);
        TextView money=(TextView)holder.findViewById(R.id.money);
        TextView remark=(TextView)holder.findViewById(R.id.remark);
        TextView paytools=(TextView)holder.findViewById(R.id.paytools);

        BillInfo billInfo=BillInfo.parse(item.getString("billinfo"));
        sort.setText(billInfo.getCateName());
        date.setText(billInfo.getTime());
        money.setText(BillTools.getCustomBill(billInfo));
        String remarkStr=billInfo.getRemark();
        if(remarkStr.length()>20){
            remarkStr=remarkStr.substring(0,20)+"...";
        }
        remark.setText(remarkStr);
        paytools.setText(billInfo.getAccountName());

        money.setTextColor(BillTools.getColor(mContext,billInfo));
    }

}
