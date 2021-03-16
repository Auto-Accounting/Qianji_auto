package cn.dreamn.qianji_auto.ui.listData;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;

public class BaseList implements IList {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public void onClick(BaseFragment baseFragment) {

    }

    @Override
    public int getColor() {
        return R.color.button_go_setting_bg;
    }

    @Override
    public int getFontSize() {
        return 30;
    }
}
