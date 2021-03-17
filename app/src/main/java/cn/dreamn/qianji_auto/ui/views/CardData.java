package cn.dreamn.qianji_auto.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;

public class CardData extends RelativeLayout {
    private final LinearLayout lib_bg;
    private final TextView lib_title;
    private final TextView lib_author;
    private final TextView lib_desc;
    private final TextView lib_license;
  //  private  String lib_url;
    public CardData(Context context, AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater.from(context).inflate(R.layout.card_data,this,true);
        lib_bg =  findViewById(R.id.lib_bg);
        lib_title = findViewById(R.id.lib_title);
        lib_author =  findViewById(R.id.lib_author);
        lib_desc =  findViewById(R.id.lib_desc);
        lib_license =  findViewById(R.id.lib_license);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CardData);
        if(typedArray!=null){
            int lib_title_color=typedArray.getResourceId(R.styleable.CardData_lib_title_color, R.color.button_go_setting_bg);
            int lib_author_color=typedArray.getResourceId(R.styleable.CardData_lib_author_color, R.color.main_title);
            int lib_desc_color=typedArray.getResourceId(R.styleable.CardData_lib_desc_color, R.color.background_white);
            int lib_license_color=typedArray.getResourceId(R.styleable.CardData_lib_license_color, R.color.background_white);
            int lib_bg_color=typedArray.getResourceId(R.styleable.CardData_lib_bg_color, R.color.background_white);
            String lib_title = typedArray.getString(R.styleable.CardData_lib_title);
            String lib_author = typedArray.getString(R.styleable.CardData_lib_author);
            String lib_desc = typedArray.getString(R.styleable.CardData_lib_desc);
            String lib_license = typedArray.getString(R.styleable.CardData_lib_license);
         //   this.lib_url= typedArray.getString(R.styleable.CardData_lib_url);
            setLibAuthor(lib_author);
            setLibAuthorColor(lib_author_color);
            setLibBgColor(lib_bg_color);
            setLibDesc(lib_desc);
            setLibDescColor(lib_desc_color);
            setLibLicense(lib_license);
            setLibLicenseColor(lib_license_color);
            setLibTitle(lib_title);
            setLibTitleColor(lib_title_color);
            typedArray.recycle();
        }


    }



    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            lib_bg.setOnClickListener(onClickListener);
        }
    }



    public void setLibTitle(String data){
        lib_title.setText(data);
    }

    public void setLibAuthor(String data){
        lib_author.setText(data);
    }
    public void setLibDesc(String data){
        lib_desc.setText(data);
    }
    public void setLibLicense(String data){
        lib_license.setText(data);
    }

    public void setLibTitleColor(int data){
        lib_title.setTextColor(getContext().getColor(data));
    }

    public void setLibAuthorColor(int data){
        lib_author.setTextColor(getContext().getColor(data));
    }
    public void setLibDescColor(int data){
        lib_desc.setTextColor(getContext().getColor(data));
    }
    public void setLibLicenseColor(int data){
        lib_license.setTextColor(getContext().getColor(data));
    }
    public void setLibBgColor(int data){
        lib_bg.setBackgroundColor(getContext().getColor(data));
    }


}

