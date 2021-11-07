package cn.dreamn.qianji_auto.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.data.database.Table.AutoBill;


public class BillListAdapter extends ArrayAdapter {
    List<AutoBill> autoBills;

    public BillListAdapter(Context context, int resource, List<AutoBill> autoBills) {
        super(context, resource);
        this.autoBills = autoBills;
        //   Log.d(jsonArray.toString());
    }

    public void setAutoBills(List<AutoBill> autoBills) {
        this.autoBills = autoBills;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (autoBills == null) return 0;
        return autoBills.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutoBill autoBill = autoBills.get(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_money_list_item_part, null, false);

        TextView sort = view.findViewById(R.id.sort);
        TextView date = view.findViewById(R.id.date);
        TextView money = view.findViewById(R.id.money);
        TextView remark = view.findViewById(R.id.remark);
        TextView paytools = view.findViewById(R.id.paytools);

        BillInfo billInfo = BillInfo.parse(autoBill.billInfo);
        sort.setText(billInfo.getCateName());
        date.setText(billInfo.getTime());
        money.setText(BillTools.getCustomBill(billInfo));
        String remarkStr = billInfo.getRemark();
        remark.setText(remarkStr);
        paytools.setText(billInfo.getAccountName());

        money.setTextColor(BillTools.getColor(getContext(), billInfo));

        // Log.d(jsonObject.getString("name"));
        //  Log.d(jsonObject.getString("sub"));
        return view;


    }
}

