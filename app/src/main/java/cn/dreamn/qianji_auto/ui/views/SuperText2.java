package cn.dreamn.qianji_auto.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cn.dreamn.qianji_auto.R;

public class SuperText2 extends RelativeLayout {
    private final IconView iv_left_icon;
    private final IconView iv_right_icon;
    private final TextView tv_tilte;
    private final CardView card_view;

    public SuperText2(Context context, AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater.from(context).inflate(R.layout.list_items3,this,true);
        iv_left_icon = (IconView) findViewById(R.id.iv_left_icon);
        iv_right_icon = (IconView) findViewById(R.id.iv_right_icon);
        tv_tilte = (TextView) findViewById(R.id.tv_tilte);
        card_view = (CardView) findViewById(R.id.card_view);


        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SuperText2);
        if(typedArray!=null){
            int leftColor=typedArray.getResourceId(R.styleable.SuperText2_left_color, R.color.button_go_setting_bg);
            int rightColor=typedArray.getResourceId(R.styleable.SuperText2_right_color, R.color.button_go_setting_bg);
            int titleColor=typedArray.getResourceId(R.styleable.SuperText2_title_color, R.color.main_title);

            String leftIcon = typedArray.getString(R.styleable.SuperText2_left_icon);
            String rightIcon = typedArray.getString(R.styleable.SuperText2_right_icon);
            String titleText = typedArray.getString(R.styleable.SuperText2_title_text);


            setLeftIcon(leftIcon);
            setLeftIconColor(context.getColor(leftColor));
            setRightIcon(rightIcon);
            setRightIconColor(context.getColor(rightColor));
            setTitle(titleText);
            setTitleColor(context.getColor(titleColor));
            }
            typedArray.recycle();

    }



    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            card_view.setOnClickListener(onClickListener);
        }
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

