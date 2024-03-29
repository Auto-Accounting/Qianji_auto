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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xfloatview.XFloatView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class AutoFloatTip extends XFloatView {

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private RelativeLayout tip;
    private TextView money;
    private TextView time;

    private BillInfo billInfo2;

    private CountDownTimer countDownTimer;

    /**
     * 构造器
     *
     * @param context
     */
    public AutoFloatTip(Context context) {
        super(context);
        initData();
    }

    private void initData() {

    }

    /**
     * @return 获取根布局的ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.float_tip;
    }

    /**
     * @return 能否移动或者触摸响应
     */
    @Override
    protected boolean canMoveOrTouch() {
        return true;
    }


    /**
     * 初始化悬浮控件
     */
    @Override
    protected void initFloatView() {
        Bundle appinfo = AppManager.getAppInfo(getContext());
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(appinfo.getInt("appIcon"));
        tip = findViewById(R.id.tip);
        money = findViewById(R.id.money);
        time = findViewById(R.id.time);
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {
        MMKV mmkv = MMKV.defaultMMKV();
        tip.setOnClickListener(v -> {
            Log.i("you click me");
            Log.i(mmkv.getString("click_window", "edit"));
            switch (mmkv.getString("click_window","edit")){
                case "edit":
                    SendDataToApp.showFloatByAlert(getContext(), billInfo2);
                    break;
                case "record":
                    SendDataToApp.goApp(getContext(),billInfo2);
                    break;
                case "close":
                    break;
            }
            this.clear();
        });
        tip.setOnLongClickListener(v->{
            Log.i("you  long click me");
            Log.i(mmkv.getString("long_click_window", "edit"));
            switch (mmkv.getString("long_click_window","edit")){
                case "edit":
                    SendDataToApp.showFloatByAlert(getContext(), billInfo2);
                    break;
                case "record":
                    SendDataToApp.goApp(getContext(),billInfo2);
                    break;
                case "close":
                    break;
            }
            this.clear();
            return false;
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

    public void setData(BillInfo billInfo) {
        //TODO 4.0新增：多币种记账支持，此处预留修改位。
        billInfo2 = billInfo;
        money.setText(BillTools.getCustomBill(billInfo));
        money.setTextColor(BillTools.getColor(getContext(), billInfo));
        int timeCount = SendDataToApp.getTimeout();
        if (timeCount == 0) {
            SendDataToApp.end(getContext(), billInfo2);
            clear();
            return;
        }
        String times = timeCount + "s";

        time.setText(times);

        countDownTimer = new CountDownTimer(timeCount * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("倒计时：" + millisUntilFinished);
                String times1 = millisUntilFinished / 1000 + "s";
                time.setText(times1);
            }

            @Override
            public void onFinish() {
                SendDataToApp.end(getContext(), billInfo2);
                //取消倒计时
                cancel();
                clear();
            }
        };
        countDownTimer.start();

    }



    @Override
    public void dismiss() {
        super.dismiss();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        //   Caches.update("float_lock", "false");
    }
}
