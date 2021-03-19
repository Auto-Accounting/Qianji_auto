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

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.views.IconView;


public class ListAdapter extends ArrayAdapter {

    public ListAdapter(Context context, int resource, Bundle[] bundles) {
        super(context, resource, bundles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.list_items2, null, false);

        IconView iconView = (IconView) view.findViewById(R.id.icon_header);
        iconView.setFont(bundle.getString("appIcon"));
        IconView iconView2 = (IconView) view.findViewById(R.id.icon_isOk);
        PermissionUtils permissionUtils=new PermissionUtils(getContext());
        String isOk=permissionUtils.isGrant(bundle.getInt("appId"));
        if(isOk.equals("1")){
            iconView2.setFont("&#xe701;");
            iconView2.setTextColor(getContext().getColor(R.color.succeed));
        }else if(isOk.equals("0")){
            iconView2.setFont("&#xe702;");
            iconView2.setTextColor(getContext().getColor(R.color.error));
        }else{
            iconView2.setFont("&#xe69d;");
            iconView2.setTextColor(getContext().getColor(R.color.warnning));
        }

        TextView textView = (TextView) view.findViewById(R.id.item_title);
        textView.setText(bundle.getString("appName"));
        TextView textView2 = (TextView) view.findViewById(R.id.item_sub);
        textView2.setText(bundle.getString("appSubName"));

        return view;


    }
}

