package cn.dreamn.qianji_auto.ui.views;

import com.zhengsr.skinlib.callback.ICusSkinDelegate;
import com.zhengsr.skinlib.entity.SkinAttr;

import java.util.Map;
import java.util.Set;

public class SuperTextDelegate extends ICusSkinDelegate<SuperText> {


    @Override
    public void onApply(SuperText view, Map<String, SkinAttr> maps) {
        Set<Map.Entry<String, SkinAttr>> entrySet = maps.entrySet();
        for (Map.Entry<String, SkinAttr> entry : entrySet) {
            String key = entry.getKey();
            SkinAttr attr = entry.getValue();
            if (hasResource(attr)) {
                switch (key){
                    case "leftColor":
                        view.setLeftIconColor(getColor(attr));
                        break;
                    case "rightColor":
                        view.setRightIconColor(getColor(attr));
                        break;
                    case "titleColor":
                        view.setTitleColor(getColor(attr));
                        break;
                    case "subColor":
                        view.setSubColor(getColor(attr));
                        break;
                }
            }

        }
    }
}