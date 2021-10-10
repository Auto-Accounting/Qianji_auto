package cn.dreamn.qianji_auto.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class LogScreen extends LinearLayout {

    ArrayList<String> mLogList = new ArrayList<>();
    @SuppressLint("DrawAllocation")
    private RecyclerView mRecyclerView;

    public LogScreen(Context context) {
        super(context);
        init(null, 0);
    }

    public LogScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LogScreen(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mRecyclerView = new RecyclerView(getContext());
        LogsAdapter logsAdapter = new LogsAdapter();
        mRecyclerView.setAdapter(logsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addView(mRecyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void printLog(ArrayList<String> log) {
        mLogList.clear();
        mLogList.addAll(log);
        Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private class LogsAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(new TextView(getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView;
            view.setText(mLogList.get(position));
            view.measure(0, 0);
            int desiredWidth = view.getMeasuredWidth();
            LayoutParams mParams = new LayoutParams(mRecyclerView.getLayoutParams());
            mParams.width = desiredWidth;
            view.setLayoutParams(mParams);
            if (mRecyclerView.getWidth() < desiredWidth) {
                mRecyclerView.requestLayout();
            }

        }

        @Override
        public int getItemCount() {
            return mLogList.size();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}