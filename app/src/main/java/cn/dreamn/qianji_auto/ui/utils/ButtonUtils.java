package cn.dreamn.qianji_auto.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;

import cn.dreamn.qianji_auto.R;

public class ButtonUtils {
    //按钮启用禁用设置颜色

    @SuppressLint("UseCompatLoadingForDrawables")
    public static  void disable(Button button, Context context){
        //   Log.m("APP","color2"+color);
        button.setBackground(context.getDrawable(R.drawable.btn_normal_disable));
        button.setEnabled(false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static  void enable(Button button, Context context){
        button.setBackground(context.getDrawable(R.drawable.btn_normal_1));
        button.setEnabled(true);
    }
}
