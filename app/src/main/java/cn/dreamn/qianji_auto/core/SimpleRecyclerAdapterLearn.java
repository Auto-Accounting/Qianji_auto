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

import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.func.Storage;

/**
 * 基于simple_list_item_2简单的适配器
 *
 * @author XUE
 * @since 2019/4/1 11:04
 */
public class SimpleRecyclerAdapterLearn extends SmartRecyclerAdapter<String> {

    public SimpleRecyclerAdapterLearn() {

        super(R.layout.map_list);

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
        JSONObject jsonObject = Storage.type(Storage.Learn).getAll();
        SuperTextView superTextView=holder.findViewById(R.id.map_text);
        superTextView.setLeftString(model);
        superTextView.setRightString(jsonObject.getString(model));

    }
}
