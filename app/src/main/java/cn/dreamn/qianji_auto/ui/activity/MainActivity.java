package cn.dreamn.qianji_auto.ui.activity;

import android.os.Bundle;


import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.ui.utils.StatusBarUtil;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForWindow(this);
        StatusBarUtil.setDarkMode(this);
        openMainPage();


    }

    private void openMainPage(){
        //MMKV检查
        MMKV mmkv=MMKV.defaultMMKV();
        if(mmkv.getBoolean("version_3_0",true)){//不是3.0版本

            String[] fragments={
                    "引导设置",
                    "记账软件",
                    "工作模式",
                    "数据同步",
                    "使用习惯",
                    "完成设置"
            };

            //开启设置
            openPage(fragments[mmkv.getInt("helper_page",0)]);
        }else{
            openPage(MainFragment.class);
        }
    }

}