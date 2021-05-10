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
import cn.dreamn.qianji_auto.ui.views.IconView;


public class ModeAdapter extends ArrayAdapter {

    private final CallAdapter callAdapter;
    public interface CallAdapter{
        void tryRegex(String item, CardView cardView);
    }
    public ModeAdapter(Context context, int resource, Bundle[] bundles, CallAdapter callAdapter) {
        super(context, resource, bundles);
        this.callAdapter=callAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.grid_items_icon_2, null, false);

        IconView iconView = (IconView) view.findViewById(R.id.item_image_icon);
        iconView.setFont(bundle.getString("appIcon"));
        TextView textView = (TextView) view.findViewById(R.id.item_text);
        textView.setText(bundle.getString("appName"));
        CardView cardView= view.findViewById(R.id.card_shadow);
        String appValue=bundle.getString("appValue");
        callAdapter.tryRegex(appValue,cardView);

        return view;


    }
}

