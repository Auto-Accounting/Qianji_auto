package cn.dreamn.qianji_auto.ui.base;

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





import android.os.Bundle;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import java.util.Collection;
import java.util.Map;

import cn.dreamn.qianji_auto.R;


public class BaseAdapter extends SmartRecyclerAdapter<Bundle> {


    public BaseAdapter(int layoutId) {
        super(layoutId);
    }

    public BaseAdapter(Collection<Bundle> collection, int layoutId) {
        super(collection, layoutId);
    }

    public BaseAdapter(Collection<Bundle> collection, int layoutId, SmartViewHolder.OnItemClickListener listener) {
        super(collection, layoutId, listener);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {

    }


}

