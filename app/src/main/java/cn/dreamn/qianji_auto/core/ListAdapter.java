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

package cn.dreamn.qianji_auto.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import cn.dreamn.qianji_auto.R;
import com.xuexiang.xui.adapter.listview.BaseListAdapter;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;


import java.util.List;
import java.util.Map;

/**
 * 列表显示效果适配器
 *
 * @author xuexiang
 * @since 2018/12/19 上午12:19
 */
public class ListAdapter extends BaseListAdapter<Map<String, String>, ListAdapter.ViewHolder> {

    public static final String KEY_TITLE = "title";
    public static final String KEY_SUB_TITLE = "sub_title";
    public static final String KEY_PIC = "pic";
    public static final String KEY_BG = "bg";
    public static final String KEY_TCOLOR = "tc";
    public static final String KEY_SCOLOR = "sc";

    public ListAdapter(Context context, List<Map<String, String>> data) {
        super(context, data);
    }

    @Override
    protected ViewHolder newViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.superTextView = convertView.findViewById(R.id.super_text);
        return holder;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.adapter_item_list;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(ViewHolder holder, Map<String, String> item, int position) {

        holder.superTextView.setLeftTopString(item.get(KEY_TITLE));
        holder.superTextView.setLeftBottomString(item.get(KEY_SUB_TITLE));
        holder.superTextView.setLeftIcon(Integer.parseInt(item.get(KEY_PIC)));
        holder.superTextView.setLeftTopTextColor(getColor(Integer.parseInt(item.get(KEY_TCOLOR))));
        holder.superTextView.setLeftBottomTextColor(getColor(Integer.parseInt(item.get(KEY_SCOLOR))));
        holder.superTextView.setBackgroundColor(getColor(Integer.parseInt(item.get(KEY_BG))));

    }

    public static class ViewHolder {

        public SuperTextView superTextView;
    }
}
