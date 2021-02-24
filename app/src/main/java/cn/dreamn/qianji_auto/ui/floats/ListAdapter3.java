/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.ui.floats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.picture.MyBitmapUtils;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * Created by Administrator-Liu on 2018/8/27.
 */
public class ListAdapter3 extends ArrayAdapter {


    public ListAdapter3(Context context, int resource, Bundle[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item3, null, false);


        TextView tv_title = view.findViewById(R.id.tv_title);
        FrameLayout fl_title = view.findViewById(R.id.fl_title);
        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(getContext());


        String name = bundle.getString("name");
        tv_title.setText(name);
        String cover = bundle.getString("cover");

        myBitmapUtils.disPlay(fl_title, cover);
        //  fl_title.setBackground(new BitmapDrawable(getContext().getResources(), myBitmapUtils.getBitmap(cover)));

        return view;


    }
}
