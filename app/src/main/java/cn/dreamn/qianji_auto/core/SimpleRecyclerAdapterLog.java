/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.func.Storage;

/**
 * 基于simple_list_item_2简单的适配器
 *
 * @author XUE
 * @since 2019/4/1 11:04
 */
public class SimpleRecyclerAdapterLog extends SmartRecyclerAdapter<String> {

    public SimpleRecyclerAdapterLog() {

        super(android.R.layout.simple_list_item_2);

    }

    /**
     * 绑定布局控件
     *
     * @param holder
     * @param model
     * @param position
     */
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, String model, int position) {
        JSONArray jsonArray = Storage.type(Storage.Log).getJSONArray("log");
        holder.text(android.R.id.text1, jsonArray.getJSONObject(position).getString("log"));
        holder.text(android.R.id.text2, jsonArray.getJSONObject(position).getString("time")+" "+jsonArray.getJSONObject(position).getString("tag"));
        holder.textColorId(android.R.id.text2, R.color.xui_config_color_light_blue_gray);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });


    }



    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
