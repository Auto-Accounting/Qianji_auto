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

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuexiang.xfloatview.XFloatView;
import com.xuexiang.xutil.display.ScreenUtils;

import cn.dreamn.qianji_auto.R;

/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class ListFloat extends XFloatView {

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private TextView list_title;
    private ListView list_view;


    private Callback callback;

    /**
     * 构造器
     *
     * @param context
     */
    public ListFloat(Context context) {
        super(context);
        initData();
    }

    private void initData() {
      //  setWindowManagerParams(0, 200, ScreenUtils.getScreenWidth(), WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void setData(String title, int type, Object[] item, Callback select) {
        list_title.setText(title);
        if (type == 1) {
            ListAdapter listAdapter = new ListAdapter(getContext(), R.layout.list_item, (String[]) item);//listdata和str均可
            list_view.setAdapter(listAdapter);
        } else if (type == 2) {
            ListAdapter2 listAdapter2 = new ListAdapter2(getContext(), R.layout.list_item2, (Bundle[]) item);//listdata和str均可
            list_view.setAdapter(listAdapter2);
        } else if (type == 3) {
            ListAdapter3 listAdapter3 = new ListAdapter3(getContext(), R.layout.list_item3, (Bundle[]) item);//listdata和str均可
            list_view.setAdapter(listAdapter3);
        }


        callback = select;

    }

    /**
     * @return 获取根布局的ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.float_list;
    }

    /**
     * @return 能否移动或者触摸响应
     */
    @Override
    protected boolean canMoveOrTouch() {
        return false;
    }

    @Override
    protected WindowManager.LayoutParams getFloatViewLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags =
                // LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        // LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        params.gravity = Gravity.LEFT | Gravity.TOP;
        return params;
    }

    /**
     * 初始化悬浮控件
     */
    @Override
    protected void initFloatView() {
        list_title = findViewById(R.id.list_title);
        list_view = findViewById(R.id.list_view);


    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {

        list_view.setOnItemClickListener((parent, view, position, id) -> {

            if (callback != null) {
                callback.onResponse(position);
            }
            this.clear();
        });

    }

    /**
     * @return 设置悬浮框是否吸附在屏幕边缘
     */
    @Override
    protected boolean isAdsorbView() {
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    public interface Callback {
        void onResponse(int data);
    }

}
