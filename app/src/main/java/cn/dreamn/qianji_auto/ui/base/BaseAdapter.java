package cn.dreamn.qianji_auto.ui.base;


import android.os.Bundle;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import java.util.Collection;


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

