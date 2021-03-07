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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.czy1121.view.CornerLabelView;

import cn.dreamn.qianji_auto.R;


public class AppAdapter extends ArrayAdapter {


    private final CallAdapter callAdapter;
    public interface CallAdapter{
        void tryRegex(String item,CornerLabelView cornerLabelView);
    }
    public AppAdapter(Context context, int resource, Bundle[] bundles,CallAdapter callAdapter) {
        super(context, resource, bundles);
        this.callAdapter=callAdapter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.grid_items, null, false);

        ImageView image = (ImageView) view.findViewById(R.id.item_image_icon);
        image.setImageResource(bundle.getInt("appIcon"));
        TextView textView = (TextView) view.findViewById(R.id.item_text);
        textView.setText(bundle.getString("appName"));
        CornerLabelView cornerLabelView=(CornerLabelView)view.findViewById(R.id.icon_choose);
        String packageName=bundle.getString("appPackage");
        callAdapter.tryRegex(packageName,cornerLabelView);
        return view;


    }
}

