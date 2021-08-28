package cn.dreamn.qianji_auto.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;

public class LineLay extends LinearLayout {

    private final IconView leftIcon;
    private final TextView item_title;
    private final TextView item_value;
    private final IconView rightIcon;

    private final LinearLayout linelayout;

    public LineLay(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.components_linelay, this, true);
        leftIcon = findViewById(R.id.leftIcon);
        item_title = findViewById(R.id.item_title);
        item_value = findViewById(R.id.item_value);
        rightIcon = findViewById(R.id.rightIcon);
        linelayout = findViewById(R.id.linelayout);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineLay);
        if (typedArray != null) {
            int leftColor = typedArray.getResourceId(R.styleable.LineLay_line_left_color, R.color.blue);
            setLeftIconColor(leftColor);
            String leftIcon = typedArray.getString(R.styleable.LineLay_line_left_icon);
            setLeftIcon(leftIcon);
            int leftMargin = typedArray.getInt(R.styleable.LineLay_line_left_margin_left, 0);
            setLeftIconMargin(leftMargin);
            int rightColor = typedArray.getResourceId(R.styleable.LineLay_line_right_color, R.color.gray);
            setRightIconColor(rightColor);
            String rightIcon = typedArray.getString(R.styleable.LineLay_line_right_icon);
            setRightIcon(rightIcon);

            int title = typedArray.getResourceId(R.styleable.LineLay_line_title, R.string.lineLay_none);
            int value = typedArray.getResourceId(R.styleable.LineLay_line_value, R.string.lineLay_none);
            int titleColor = typedArray.getResourceId(R.styleable.LineLay_line_title_color, R.color.black);
            int valueColor = typedArray.getResourceId(R.styleable.LineLay_line_value_color, R.color.gray);
            setTitle(title);
            setValue(value);
            setTitleColor(titleColor);
            setValueColor(valueColor);
            int bg = typedArray.getResourceId(R.styleable.LineLay_line_bg, R.drawable.btn_normal_4);
            setBg(bg);
            int left = typedArray.getInt(R.styleable.LineLay_line_margin_left, 10);
            int right = typedArray.getInt(R.styleable.LineLay_line_margin_right, 10);
            int top = typedArray.getInt(R.styleable.LineLay_line_margin_top, 20);
            int bottom = typedArray.getInt(R.styleable.LineLay_line_margin_bottom, 0);
            setMargins(left, right, top, bottom);
        }
        typedArray.recycle();

    }


    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            linelayout.setOnClickListener(onClickListener);
        }
    }

    public void setTitle(String title) {
        item_title.setText(title);
    }

    public void setTitle(int title) {
        item_title.setText(title);
    }

    public void setTitleColor(int color) {
        item_title.setTextColor(getContext().getColor(color));
    }

    public void setValue(String value) {
        item_value.setText(value);
    }

    public void setValue(int value) {
        item_value.setText(value);
    }

    public void setValueColor(int color) {
        item_value.setTextColor(getContext().getColor(color));
    }

    public void setLeftIcon(String icon) {
        leftIcon.setFont(icon);
    }

    public void setLeftIconMargin(int margin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftIcon.getLayoutParams();
        layoutParams.setMargins(margin, 0, 0, 0);
        leftIcon.setLayoutParams(layoutParams);
    }

    public void setLeftIconColor(int color) {
        leftIcon.setTextColor(getContext().getColor(color));
    }

    public void setRightIcon(String icon) {
        rightIcon.setFont(icon);
    }

    public void setRightIconColor(int color) {
        rightIcon.setTextColor(getContext().getColor(color));
    }

    public void setBg(int id) {
        linelayout.setBackgroundResource(id);
    }

    public void setMargins(int left, int right, int top, int bottom) {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) linelayout.getLayoutParams();
        layoutParams.setMargins(left, top, right, bottom);
        linelayout.setLayoutParams(layoutParams);
    }

}

