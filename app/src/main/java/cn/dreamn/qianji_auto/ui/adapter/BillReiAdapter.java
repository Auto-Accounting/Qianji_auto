package cn.dreamn.qianji_auto.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;


public class BillReiAdapter extends ArrayAdapter {

    private final JSONObject reiJsonHash;

    public BillReiAdapter(Context context, int resource, JSONObject reiJsonHash) {
        super(context, resource);
        this.reiJsonHash = reiJsonHash;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject autoBill = (JSONObject) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_money_list_rei, null, false);
        if (reiJsonHash != null && reiJsonHash.containsKey(autoBill.getString("id"))) {
            view.setBackgroundColor(getContext().getColor(R.color.colorBlueAlpha25));
        }

        TextView date = view.findViewById(R.id.date);
        TextView money = view.findViewById(R.id.money);
        TextView remark = view.findViewById(R.id.remark);
        TextView paytools = view.findViewById(R.id.paytools);

        BillInfo billInfo = new BillInfo();
        billInfo.setMoney(autoBill.getString("money"));
        billInfo.setRemark(autoBill.getString("remark"));
        billInfo.setAccountName(autoBill.getString("descinfo"));
        billInfo.setBillId(autoBill.getString("id"));
        billInfo.setTimeStamp(Long.parseLong(autoBill.getString("createtime")) * 1000);
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

