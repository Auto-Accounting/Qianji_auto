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

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.KeyEvent;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;

import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.actionbar.TitleUtils;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import cn.dreamn.qianji_auto.utils.active.core;
import cn.dreamn.qianji_auto.utils.tools.app;

import static cn.dreamn.qianji_auto.utils.file.SimpleData.init;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(anim = CoreAnim.none)
public class MainFragment extends BaseFragment implements ClickUtils.OnClick2ExitListener {
    @BindView(R.id.title)
    SuperTextView title;
    @BindView(R.id.status)
    SuperTextView menu_status;
    @BindView(R.id.set)
    SuperTextView menu_set;
    @BindView(R.id.sortMap)
    SuperTextView menu_sortMap;
    @BindView(R.id.Map)
    SuperTextView menu_Map;
    @BindView(R.id.Learn)
    SuperTextView menu_Learn;
    @BindView(R.id.Bill)
    SuperTextView menu_Bill;
    @BindView(R.id.Log)
    SuperTextView menu_Log;
    @BindView(R.id.Backup)
    SuperTextView menu_Backup;
    @BindView(R.id.About)
    SuperTextView menu_About;

    @Override
    protected TitleBar initTitle() {


        return null;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        init();
        setActive();
        initListen();

    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast(getString(R.string.app_exit));
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }

    private void setActive(){
        title.setBackgroundColor(getResources().getColor(R.color.parent_bg));
        if(core.isActive(getContext())){
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_success));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_true));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_active), core.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s(%d)",app.getAppVerName(),app.getAppVerCode()));
        }else{
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_err));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_false));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_noactive), core.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s (%d)",app.getAppVerName(),app.getAppVerCode()));
        }


    }

    private void initListen(){
        menu_status.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(ModeFragment.class);
        });
        menu_set.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(SetFragment.class);
        });
        menu_sortMap.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(SortFragment.class);
        });
        menu_Map.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(MapFragment.class);
        });
        menu_Learn.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(LearnFragment.class);
        });
        menu_Bill.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(BillFragment.class);
        });
        menu_Log.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(LogFragment.class);
        });
        menu_Backup.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(BackupFragment.class);
        });
    }
}
