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
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
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
public class InputFloat extends XFloatView {

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private TextView input_title;
    private TextView input_text;
    private TextView button_cancel;
    private TextView button_save;

    private Callback cancel;
    private Callback save;

    /**
     * 构造器
     *
     * @param context
     */
    public InputFloat(Context context) {
        super(context);
        initData();
    }

    private void initData() {
     //   setWindowManagerParams(0, 200, ScreenUtils.getScreenWidth(), WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void setData(String title, String text, String text_cancel, String text_save, Callback cancel, Callback save) {
        input_title.setText(title);
        input_text.setText(text);
        button_cancel.setText(text_cancel);
        button_save.setText(text_save);
        this.cancel = cancel;
        this.save = save;
    }

    /**
     * @return 获取根布局的ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.float_input;
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
        input_title = findViewById(R.id.input_title);
        input_text = findViewById(R.id.input_text);
        button_cancel = findViewById(R.id.button_cancel);
        button_save = findViewById(R.id.button_save);

    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {
        button_cancel.setOnClickListener(v -> {
            if (this.cancel != null) {
                cancel.onResponse(input_text.getText().toString());
            }
            this.clear();
        });
        button_save.setOnClickListener(v -> {
            if (this.save != null) {
                save.onResponse(input_text.getText().toString());
            }
            this.clear();
        });

    }

    /**
     * @return 设置悬浮框是否吸附在屏幕边缘
     */
    @Override
    protected boolean isAdsorbView() {
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    public interface Callback {
        void onResponse(String data);
    }

}
