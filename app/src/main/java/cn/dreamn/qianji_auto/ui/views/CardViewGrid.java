package cn.dreamn.qianji_auto.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.CardAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.ListManager;

public class CardViewGrid extends RelativeLayout {
    private final RelativeLayout title_bar_bg;
    private final View leftView;
    private final TextView title_name;
    private final GridView title_body;

    public CardViewGrid(Context context, AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater.from(context).inflate(R.layout.cardview_list,this,true);
        title_bar_bg =  findViewById(R.id.title_bar_bg);
        leftView = findViewById(R.id.view);
        title_name =  findViewById(R.id.title_name);
        title_body =  findViewById(R.id.title_body);


        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CardViewGrid);
        if(typedArray!=null){
            int left_color=typedArray.getResourceId(R.styleable.CardViewGrid_left_color, R.color.button_go_setting_bg);
            int titleColor=typedArray.getResourceId(R.styleable.CardViewGrid_title_color, R.color.main_title);
            int titleBgColor=typedArray.getResourceId(R.styleable.CardViewGrid_title_bg, R.color.background_white);
            int bgColor=typedArray.getResourceId(R.styleable.CardViewGrid_grid_bg, R.color.background_white);

            String title = typedArray.getString(R.styleable.CardViewGrid_title_text);

           setLeftColor(context.getColor(left_color));
            setTitleColor(context.getColor(titleColor));
            setGridBgColor(context.getColor(bgColor));
            setTitleBgColor(context.getColor(titleBgColor));
            setTitle(title);
            }
            typedArray.recycle();

    }



    public void setOnGridClickListener(AdapterView.OnItemClickListener onClickListener) {
        if (onClickListener != null) {
            title_body.setOnItemClickListener(onClickListener);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener){
        if (onClickListener != null) {
            title_bar_bg.setOnClickListener(onClickListener);
        }
    }

    public void setLeftColor(int Color){
        leftView.setBackgroundColor(Color);
    }

    public void setTitleColor(int Color){
        title_name.setTextColor(Color);
    }
    public void setTitleBgColor(int Color){
        title_bar_bg.setBackgroundColor(Color);
    }
    public void setGridBgColor(int Color){
        title_body.setBackgroundColor(Color);
    }
    public void setTitle(String title){title_name.setText(title);}
    public void setData(Bundle[] bundles, BaseFragment baseFragment,int fromData){
        //设置基础列表
        CardAdapter cardAdapter=new CardAdapter(getContext(),R.layout.grid_items_icon, bundles,(item, cardView) -> {
            ListManager.onListClick(item,baseFragment,fromData);
        });
       // title_body.set(lines*80);
        title_body.setAdapter(cardAdapter);
    }
}

