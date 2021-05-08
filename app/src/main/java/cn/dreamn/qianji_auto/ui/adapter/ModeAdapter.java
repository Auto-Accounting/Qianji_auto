package cn.dreamn.qianji_auto.ui.adapter;

/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


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

