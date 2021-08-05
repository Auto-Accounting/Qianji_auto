package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class MoneyItemAdapter extends BaseAdapter {
    private final Context mContext;


    public MoneyItemAdapter(Context context) {
        super(R.layout.adapter_money_list_item_part);
        mContext = context;


    }



    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        TextView sort= holder.findViewById(R.id.sort);
        TextView date= holder.findViewById(R.id.date);
        TextView money= holder.findViewById(R.id.money);
        TextView remark= holder.findViewById(R.id.remark);
        TextView paytools= holder.findViewById(R.id.paytools);

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
