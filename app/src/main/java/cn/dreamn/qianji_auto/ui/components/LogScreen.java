package cn.dreamn.qianji_auto.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(new TextView(getContext()));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView tv = (TextView) holder.itemView;
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(10);
                tv.setHorizontallyScrolling(true);
                tv.setMovementMethod(ScrollingMovementMethod.getInstance());
                tv.setText(mLogList.get(position));
            }

            @Override
            public int getItemCount() {
                return mLogList.size();
            }
        });
        addView(mRecyclerView);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void printLog(String log) {
        mLogList.clear();
        mLogList.add(log);
        Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}