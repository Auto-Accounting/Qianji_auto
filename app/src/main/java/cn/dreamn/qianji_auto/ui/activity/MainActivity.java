package cn.dreamn.qianji_auto.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;


public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openMainPage();
    }


    public void openMainPage() {


        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Bundle bundle = new Bundle();
            Uri uri = intent.getData();
            String str = "";
            if (uri != null)
                str = Uri.decode(uri.getEncodedPath());
            bundle.putString("url", str);
            openLastPage(bundle);
            return;
        }
        //MMKV检查
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getInt("version", 2) != 3) {//不是3.0版本

            String[] fragments = {
                    "引导设置",
                    "记账软件",
                    "工作模式",
                    "数据同步",
                    "使用习惯",
                    "完成设置"
            };
            openPage(fragments[mmkv.getInt("helper_page", 0)]);
        } else {
            openLastPage(null);
        }
    }

    private void openLastPage(Bundle data) {
        openPage(MainFragment.class, data);
    }


}