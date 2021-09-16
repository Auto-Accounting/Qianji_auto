package cn.dreamn.qianji_auto.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // StatusBarUtil.setTransparentForWindow(this);
        Log.i("Qianji-Auto", "onCreate");
        Log.i("Qianji-Auto", Intent.ACTION_VIEW);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Log.i("Qianji-Action", action);
            Bundle bundle = new Bundle();
            Uri uri = intent.getData();
            String str = "";
            if (uri != null)
                str = Uri.decode(uri.getEncodedPath());
            bundle.putString("url", str);
            openNewPage(MainFragment.class, bundle);
        } else {
            openMainPage();
        }

    }


    private void openMainPage() {
        //MMKV检查
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getBoolean("version_3_0", true)) {//不是3.0版本

            String[] fragments = {
                    "引导设置",
                    "记账软件",
                    "工作模式",
                    "数据同步",
                    "使用习惯",
                    "完成设置"
            };
            //开启设置
            //        ThemeManager themeManager = new ThemeManager(this);
            //         themeManager.setStatusBar(this, null, R.color.background_white);

            openPage(fragments[mmkv.getInt("helper_page", 0)]);
        }else{
            openNewPage(MainFragment.class);
        }
    }

}