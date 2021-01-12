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

package cn.dreamn.qianji_auto.ui.fragment;

import android.annotation.SuppressLint;
import android.view.KeyEvent;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.App;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.ui.core.BaseContainerFragment;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.tools.Logs;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "自动记账", anim = CoreAnim.none)
public class MainFragment extends BaseFragment implements ClickUtils.OnClick2ExitListener{
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
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        setActive();
        initListen();

    }

    @Override
    public void onPause() {
        super.onPause();
        setActive();
    }

    @Override
    public void onResume() {
        super.onResume();
        setActive();
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle().setLeftClickListener(view -> ClickUtils.exitBy2Click(2000, this));
    }
    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    private void setActive(){
        if(Status.isActive(getContext())){
            Logs.d("已激活");
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_success));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_true));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_active), Status.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s(%d)", App.getAppVerName(),App.getAppVerCode()));

        }else{
            Logs.d("未激活");
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_err));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_false));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_noactive), Status.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s (%d)",App.getAppVerName(),App.getAppVerCode()));
        }


    }



    private void initListen(){
        menu_status.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(ModeFragment.class);
        });
        menu_set.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(SetFragment.class);
        });
        /*

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
        menu_About.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(AboutFragment.class);
        });*/
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
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }
}
