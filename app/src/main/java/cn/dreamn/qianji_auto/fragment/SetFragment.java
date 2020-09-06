/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.fragment;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.file.Storage;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import static com.xuexiang.xui.utils.ResUtils.getColor;

@Page(name = "工作模式")
public class SetFragment extends BaseFragment {


    @BindView(R.id.mode_xp)
    SuperTextView mode_xp;

    @BindView(R.id.mode_default)
    SuperTextView mode_default;

    @BindView(R.id.mode_au_autostart)
    SuperTextView mode_au_autostart;

    @BindView(R.id.mode_au_storage)
    SuperTextView mode_au_storage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mode;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        initSet();
        initListen();
    }
    private void initSet(){
        String mode= Storage.type(Storage.Set).get("mode","default");
        if(mode.equals("default")){
            mode_default.setBackgroundColor(getColor(R.color.list_bg_success));
            mode_default.setLeftTopTextColor(getColor(R.color.list_bg_noraml));
            mode_default.setLeftBottomTextColor(getColor(R.color.list_bg_noraml));
            mode_xp.setBackgroundColor(getColor(R.color.list_bg_noraml));
            mode_xp.setLeftTopTextColor(getColor(R.color.list_text_color_normal));
            mode_xp.setLeftBottomTextColor(getColor(R.color.list_text_color_normal_sub));
        }else{
            mode_xp.setBackgroundColor(getColor(R.color.list_bg_success));
            mode_xp.setLeftTopTextColor(getColor(R.color.list_bg_noraml));
            mode_xp.setLeftBottomTextColor(getColor(R.color.list_bg_noraml));
            mode_default.setBackgroundColor(getColor(R.color.list_bg_noraml));
            mode_default.setLeftTopTextColor(getColor(R.color.list_text_color_normal));
            mode_default.setLeftBottomTextColor(getColor(R.color.list_text_color_normal_sub));
        }


    }
    private void initListen(){
        mode_default.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("mode","default");
            initSet();
        });
        mode_xp.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("mode","xposed");
            initSet();
        });
        mode_au_autostart.setOnSuperTextViewClickListener(superTextView -> {

        });
        mode_au_storage.setOnSuperTextViewClickListener(superTextView -> {

        });
    }
}
