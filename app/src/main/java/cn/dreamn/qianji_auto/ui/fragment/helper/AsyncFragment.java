/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.ui.fragment.helper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


@Page(name = "数据同步", anim =  CoreAnim.slide)
public class AsyncFragment extends BaseFragment {

    @BindView(R.id.button_next)
    Button button_next;

    @BindView(R.id.help_skip_last)
    TextView help_skip_last;
    @BindView(R.id.help_skip)
    TextView help_skip;
    @BindView(R.id.button_async)
    Button button_async;
    @BindView(R.id.app_help_3_desc)
    TextView app_help_3_desc;
    @BindView(R.id.app_help_3_tip)
    TextView app_help_3_tip;

    @BindView(R.id.iv_icon)
    ImageView iv_icon;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_4;
    }

    @Override
    protected void initViews() {
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("helper_page",3);
       setAppDesc();
    }

    private void setAppDesc() {
        Bundle bundle=AppManager.getAppInfo();
        app_help_3_desc.setText(bundle.getString("appAsync"));
        app_help_3_tip.setText(String.format(getString(R.string.app_help_3_tip),bundle.getString("appName")));
        Task.onThread(()->{ iv_icon.setImageResource(bundle.getInt("appIcon"));});
    }




    
    @Override
    protected void initListeners() {
        help_skip_last.setOnClickListener(v->{
            openNewPage(ModeFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("version_3_0",false);

            openNewPage(MainFragment.class);
        });
        button_next.setOnClickListener(v->{
            openNewPage(SetFragment.class);
        });
        button_async.setOnClickListener(v->{
            AppManager.Async(getContext());
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        MMKV mmkv=MMKV.defaultMMKV();
        if(mmkv.getBoolean("isAsync",false)){
            app_help_3_tip.setText(getString(R.string.app_help_3_tip2));
            button_async.setText(getString(R.string.app_help_3_button_async));
        }

    }
}
