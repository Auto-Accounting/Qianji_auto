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


import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import java.util.Map;
import java.util.Objects;

import cn.dreamn.qianji_auto.R;

import static com.xuexiang.xui.utils.ResUtils.getString;

/**
 * 主副标题显示适配器
 *
 * @author xuexiang
 * @since 2018/12/19 上午12:19
 */
public class LogListAdapter extends SmartRecyclerAdapter<Map<String, String>> {

    public static final String KEY_SUB = "sub";
    public static final String KEY_TITLE = "title";
    public static final String KEY_POS = "pos";
    public LogListAdapter() {
        super(R.layout.map_list_2);
    }


    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Map<String, String> item, int position) {
        TextView  item_top = (TextView)holder.findView(R.id.item_top);
        item_top.setText(item.get(KEY_TITLE));
        TextView  item_bottom = (TextView)holder.findView(R.id.item_bottom);
        item_bottom.setText(item.get(KEY_SUB));
        TwoLineListItem item_click = (TwoLineListItem)holder.findView(R.id.item);
        item_click.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item,Integer.parseInt(Objects.requireNonNull(item.get(KEY_POS))));
            }
        });
    }



    //第一步 定义接口
    public interface OnItemClickListener {
                    void onClick(Map<String, String> item, Integer pos);
    }

    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
