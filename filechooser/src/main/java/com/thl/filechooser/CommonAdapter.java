package com.thl.filechooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public abstract class CommonAdapter<T> extends RecyclerView.Adapter {

    public Context context;
    protected ArrayList<T> dataList = new ArrayList<>();
    private int resId;
    private OnItemClickListener itemClickListener;

    public CommonAdapter(Context context, ArrayList<T> dataList, int resId) {
        this.context = context;
        this.resId = resId;
        this.dataList = dataList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        bindView(holder, dataList.get(position), position);
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    public abstract void bindView(RecyclerView.ViewHolder holder, T data, int position);

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(LayoutInflater.from(context).inflate(resId, parent, false));
    }


    public void add(T t) {
        dataList.add(t);
        notifyDataSetChanged();
    }

    public void add(T t, int index) {
        dataList.add(index, t);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        dataList.remove(t);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        dataList.remove(index);
        notifyDataSetChanged();
    }

    public void setData(List<T> dataList) {
        this.dataList = (ArrayList<T>) dataList;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
