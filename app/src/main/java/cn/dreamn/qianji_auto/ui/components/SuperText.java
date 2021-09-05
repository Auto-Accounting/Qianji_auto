package cn.dreamn.qianji_auto.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;

public class SuperText extends RelativeLayout {
    private final IconView icon_header;
    private final IconView icon_isOk;
    private final TextView item_title;
    private final TextView item_sub;
    private final RelativeLayout layout_supertext;

    public SuperText(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.components_supertext, this, true);
        icon_header = findViewById(R.id.icon_header);
        icon_isOk = findViewById(R.id.icon_isOk);
        item_title = findViewById(R.id.item_value);
        item_sub = findViewById(R.id.item_sub);
        layout_supertext = findViewById(R.id.layout_supertext);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SuperText);
        if (typedArray != null) {
            int leftColor = typedArray.getResourceId(R.styleable.SuperText_left_color, R.color.blue);
            int rightColor = typedArray.getResourceId(R.styleable.SuperText_right_color, R.color.blue);
            int titleColor = typedArray.getResourceId(R.styleable.SuperText_title_color, R.color.black);
            int subColor = typedArray.getResourceId(R.styleable.SuperText_sub_color, R.color.skip_title);
            String leftIcon = typedArray.getString(R.styleable.SuperText_left_icon);
            String rightIcon = typedArray.getString(R.styleable.SuperText_right_icon);
            String titleText = typedArray.getString(R.styleable.SuperText_title_text);
            String subText = typedArray.getString(R.styleable.SuperText_sub_text);

            boolean select = typedArray.getBoolean(R.styleable.SuperText_select, false);
            setSelect(select);
            setLeftIcon(leftIcon);
            setLeftIconColor(context.getColor(leftColor));
            setRightIcon(rightIcon);
            setRightIconColor(context.getColor(rightColor));
            setTitle(titleText);
            setTitleColor(context.getColor(titleColor));
            setSub(subText);
            setSubColor(context.getColor(subColor));
            }
            typedArray.recycle();

    }



    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            layout_supertext.setOnClickListener(onClickListener);
        }
    }

    public void setSelect(boolean isSelected){
        if(isSelected){
            icon_isOk.setTextColor(getResources().getColor(R.color.succeed));
            icon_isOk.setFont("&#xe626;");
        }else{
            icon_isOk.setFont("");
        }
    }

    public void setLeftIcon(String icon){
        icon_header.setFont(icon);
    }
    public void setLeftIconColor(int Color){
        icon_header.setTextColor(Color);
    }
    public void setRightIcon(String icon){
        icon_isOk.setFont(icon);
    }
    public void setRightIconColor(int Color){
        icon_isOk.setTextColor(Color);
    }
    public void setTitle(String title){
        item_title.setText(title);
    }
    public void setTitleColor(int Color){
        item_title.setTextColor(Color);
    }
    public void setSub(String sub){
        item_sub.setText(sub);
    }
    public void setSubColor(int Color){
        item_sub.setTextColor(Color);
    }
}

