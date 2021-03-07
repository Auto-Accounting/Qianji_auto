package cn.dreamn.qianji_auto.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openMainPage();


    }

    private void openMainPage(){
        //MMKV检查
        MMKV mmkv=MMKV.defaultMMKV();
        if(mmkv.getBoolean("first",true)){//不是3.0版本

            String[] fagments={
                    "引导设置",
                    "记账软件",
                    "工作模式"
            };

            //开启设置
            openPage(fagments[mmkv.getInt("helper_page",0)]);
        }else{
            openPage(MainFragment.class);
        }
    }

}