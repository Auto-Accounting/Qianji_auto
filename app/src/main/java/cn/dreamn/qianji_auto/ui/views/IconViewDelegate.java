package cn.dreamn.qianji_auto.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.zhengsr.skinlib.callback.ICusSkinDelegate;
import com.zhengsr.skinlib.entity.SkinAttr;

import java.util.Map;
import java.util.Set;

public class IconViewDelegate extends ICusSkinDelegate<TextView> {


    @Override
    public void onApply(TextView view, Map<String, SkinAttr> maps) {
        Set<Map.Entry<String, SkinAttr>> entrySet = maps.entrySet();
        for (Map.Entry<String, SkinAttr> entry : entrySet) {
            String key = entry.getKey();
            SkinAttr attr = entry.getValue();
            if (hasResource(attr)) {
                if ("textColor".equals(key)){
                    //替换皮肤的
                    view.setTextColor(getColor(attr));
                }
            }

        }
    }
}