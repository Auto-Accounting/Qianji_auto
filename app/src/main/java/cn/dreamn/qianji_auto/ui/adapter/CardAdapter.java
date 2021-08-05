package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.components.IconView;


public class CardAdapter extends ArrayAdapter {


    private final CallAdapter callAdapter;
    public interface CallAdapter{
        void tryRegex(int item, CardView cardView);
    }
    public CardAdapter(Context context, int resource, Bundle[] bundles, CallAdapter callAdapter) {
        super(context, resource, bundles);
        this.callAdapter=callAdapter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint({"ViewHolder", "InflateParams"}) View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_grid_item_with_icon, null, false);


        IconView iconView = view.findViewById(R.id.item_image_icon);
        iconView.setFont(bundle.getString("appIcon"));
        iconView.setTextSize(bundle.getInt("appSize"));
        iconView.setTextColor(getContext().getColor(bundle.getInt("appColor")));
        TextView textView = view.findViewById(R.id.item_text);
        textView.setText(bundle.getString("appName"));
        CardView cardView= view.findViewById(R.id.card_shadow);
        callAdapter.tryRegex(position,cardView);
        return view;


    }
}

