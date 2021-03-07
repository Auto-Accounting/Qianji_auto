package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.widget.Button;

import com.zhengsr.skinlib.ZSkin;

import cn.dreamn.qianji_auto.R;

public class ButtonUtils {
    //按钮启用禁用设置颜色
    public static  void disable(Button button, Context context){
        int color=R.color.disable_bg;
      //  Log.d("APP","color"+color);
        if (ZSkin.isLoadSkin()){
            color = ZSkin.getColor(color);
        }
     //   Log.d("APP","color2"+color);
        button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(color)));
        button.setEnabled(false);
    }
    public static  void enable(Button button, Context context){
        int color=R.color.button_go_setting_bg;
        if (ZSkin.isLoadSkin()){
            color = ZSkin.getColor(color);
        }
        button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(color)));
        button.setEnabled(true);
    }
}
