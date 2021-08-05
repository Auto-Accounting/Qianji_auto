package cn.dreamn.qianji_auto.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cn.dreamn.qianji_auto.R;


public class AppAdapter extends ArrayAdapter {


    private final CallAdapter callAdapter;
    public interface CallAdapter{
        void tryRegex(String item, CardView cardView);
    }
    public AppAdapter(Context context, int resource, Bundle[] bundles,CallAdapter callAdapter) {
        super(context, resource, bundles);
        this.callAdapter=callAdapter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_grid_item, null, false);

        ImageView image = view.findViewById(R.id.item_image_icon);
        // image.setImageResource(bundle.getInt("appIcon"));
        image.setImageResource(bundle.getInt("appIcon"));
        TextView textView = view.findViewById(R.id.item_text);
        textView.setText(bundle.getString("appName"));
        CardView cardView = view.findViewById(R.id.card_shadow);
        //CornerLabelView cornerLabelView=(CornerLabelView)view.findViewById(R.id.icon_choose);
        String packageName = bundle.getString("appPackage");
        callAdapter.tryRegex(packageName, cardView);
        return view;


    }
}

