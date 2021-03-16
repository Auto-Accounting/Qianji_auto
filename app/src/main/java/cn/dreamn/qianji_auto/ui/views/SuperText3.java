package cn.dreamn.qianji_auto.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cn.dreamn.qianji_auto.R;

public class SuperText3 extends RelativeLayout {
    private final IconView iv_left_icon;
    private final IconView iv_right_icon;
    private final TextView tv_tilte;
    private final RelativeLayout relativeLayout;

    public SuperText3(Context context, AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater.from(context).inflate(R.layout.list_items4,this,true);
        iv_left_icon = (IconView) findViewById(R.id.iv_left_icon);
        iv_right_icon = (IconView) findViewById(R.id.iv_right_icon);
        tv_tilte = (TextView) findViewById(R.id.tv_tilte);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);


        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SuperText3);
        if(typedArray!=null){
            int leftColor=typedArray.getResourceId(R.styleable.SuperText3_left_color, R.color.button_go_setting_bg);
            int rightColor=typedArray.getResourceId(R.styleable.SuperText3_right_color, R.color.button_go_setting_bg);
            int titleColor=typedArray.getResourceId(R.styleable.SuperText3_title_color, R.color.main_title);
            int bgColor=typedArray.getResourceId(R.styleable.SuperText3_backgroundColor, R.color.background_white);

            String leftIcon = typedArray.getString(R.styleable.SuperText3_left_icon);
            String rightIcon = typedArray.getString(R.styleable.SuperText3_right_icon);
            String titleText = typedArray.getString(R.styleable.SuperText3_title_text);

            setBackGroundColor(context.getColor(bgColor));
            setLeftIcon(leftIcon);
            setLeftIconColor(context.getColor(leftColor));
            setRightIcon(rightIcon);
            setRightIconColor(context.getColor(rightColor));
            setTitle(titleText);
            setTitleColor(context.getColor(titleColor));
            }
            typedArray.recycle();

    }



    public void setLeftIconOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            iv_left_icon.setOnClickListener(onClickListener);
        }
    }
    public void setRightIconOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            iv_right_icon.setOnClickListener(onClickListener);
        }
    }
    public void setTitleOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            tv_tilte.setOnClickListener(onClickListener);
        }
    }

    public void setBackGroundColor(int color){
      //  relativeLayout.setBackgroundColor(color);
        relativeLayout.setBackgroundTintList(ColorStateList.valueOf(color));
        /*iv_left_icon.setBackgroundColor(color);
        iv_right_icon.setBackgroundColor(color);
        tv_tilte.setBackgroundColor(color);*/
    }


    public void setLeftIcon(String icon){
        iv_left_icon.setFont(icon);
    }
    public void setLeftIconColor(int Color){
        iv_left_icon.setTextColor(Color);
    }
    public void setRightIcon(String icon){
        iv_right_icon.setFont(icon);
    }
    public void setRightIconColor(int Color){
        iv_right_icon.setTextColor(Color);
    }
    public void setTitle(String title){
        tv_tilte.setText(title);
    }
    public void setTitleColor(int Color){
        tv_tilte.setTextColor(Color);
    }
}

