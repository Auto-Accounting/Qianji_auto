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
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.about.AboutFragment;
import cn.dreamn.qianji_auto.ui.fragment.about.BackUpFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainMapFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainModeFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainSetFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.cards.MainCardFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.sorts.MainSortFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.LogFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.MoneyFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.NoticeFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.regulars.MainAutoLearnFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.sort.MainAutoSortFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.views.IconView;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import es.dmoral.toasty.Toasty;


@Page(name = "自动记账", anim = CoreAnim.slide)
public class MainFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    RelativeLayout title_bar;

    @BindView(R.id.linelayout)
    RelativeLayout linearLayout;
    @BindView(R.id.title_count)
    RelativeLayout title_count;
/*
    @BindView(R.id.cv_list)
    CardViewGrid cv_list;
    @BindView(R.id.cv_log)
    CardViewGrid cv_log;
    @BindView(R.id.cv_complie)
    CardViewGrid cv_complie;
    @BindView(R.id.cv_other)
    CardViewGrid cv_other;
    @BindView(R.id.cv_custom)
    CardViewGrid cv_custom;*/


    @BindView(R.id.rl_set)
    RelativeLayout rl_set;

    @BindView(R.id.rl_asset)
    RelativeLayout rl_asset;

    @BindView(R.id.rl_map)
    RelativeLayout rl_map;

    @BindView(R.id.rl_sort)
    RelativeLayout rl_sort;

    @BindView(R.id.rl_async)
    RelativeLayout rl_async;

    @BindView(R.id.rl_bill)
    RelativeLayout rl_bill;

    @BindView(R.id.rl_bill_check)
    RelativeLayout rl_bill_check;

    @BindView(R.id.rl_year)
    RelativeLayout rl_year;

    @BindView(R.id.rl_app_log)
    RelativeLayout rl_app_log;

    @BindView(R.id.rl_sms_log)
    RelativeLayout rl_sms_log;

    @BindView(R.id.rl_notice_log)
    RelativeLayout rl_notice_log;

    @BindView(R.id.rl_log)
    RelativeLayout rl_log;

    @BindView(R.id.rl_auto_sort)
    RelativeLayout rl_auto_sort;
    @BindView(R.id.rl_app)
    RelativeLayout rl_app;
    @BindView(R.id.rl_sms)
    RelativeLayout rl_sms;
    @BindView(R.id.rl_notice)
    RelativeLayout rl_notice;
    @BindView(R.id.rl_float)
    RelativeLayout rl_float;
    @BindView(R.id.rl_skin)
    RelativeLayout rl_skin;
    @BindView(R.id.rl_wait)
    RelativeLayout rl_wait;
    @BindView(R.id.rl_check)
    RelativeLayout rl_check;
    @BindView(R.id.rl_backup)
    RelativeLayout rl_backup;
    @BindView(R.id.rl_text_teach)
    RelativeLayout rl_text_teach;
    @BindView(R.id.rl_video_teach)
    RelativeLayout rl_video_teach;
    @BindView(R.id.rl_github)
    RelativeLayout rl_github;
    @BindView(R.id.rl_about)
    RelativeLayout rl_about;

    @BindView(R.id.mode_select1)
    IconView mode_select1;
    @BindView(R.id.mode_select2)
    RelativeLayout mode_select2;
    @BindView(R.id.active_status)
    TextView active_status;
    @BindView(R.id.app_log)
    TextView app_log;

    @BindView(R.id.book_img)
    RelativeLayout book_img;
    @BindView(R.id.default_book)
    TextView default_book;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_2;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initViews() {
        ThemeManager themeManager = new ThemeManager(getContext());
        themeManager.setStatusBar(getActivity(), title_count, R.color.background_white);

        app_log.setText(BuildConfig.VERSION_NAME);
        setActive();
        AutoBillWeb.update(getContext());
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        setActive();
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {

        super.onResume();
      //  homeBarUtils.setClick(home);
        setActive();

    }

    @Override
    protected void initListeners() {
        mode_select1.setOnClickListener(v-> openNewPage(MainModeFragment.class));
        mode_select2.setOnClickListener(v-> openNewPage(MainModeFragment.class));
        initGridLayout();
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }


    private void setActive() {

        if (AppStatus.isActive(getContext())) {
            Log.d("已激活");
            mode_select2.setBackgroundColor(ThemeManager.getColor(getActivity(),R.color.button_go_setting_bg));
            mode_select2.setBackgroundTintList(ColorStateList.valueOf(ThemeManager.getColor(getActivity(),R.color.button_go_setting_bg)));
            active_status.setText(String.format("已激活（%s）", AppStatus.getFrameWork(getContext())));
           active_status.setTextColor(ThemeManager.getColor(getActivity(),R.color.background_white));

        } else {
            Log.d("未激活");
            mode_select2.setBackgroundColor(ThemeManager.getColor(getActivity(),R.color.disable_bg));
            mode_select2.setBackgroundTintList(ColorStateList.valueOf(ThemeManager.getColor(getActivity(),R.color.disable_bg)));
            active_status.setText(String.format("未激活（%s）", AppStatus.getFrameWork(getContext())));
            active_status.setTextColor(ThemeManager.getColor(getActivity(),R.color.background_white));

        }
        //SetImg
        String def_book=BookNames.getDefault();
        default_book.setText(def_book);
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Object[] objects=(Object[])msg.obj;
                MyBitmapUtils.setImage(getContext(),(View) objects[0],(Bitmap)objects[1]);

            }
        };

        BookNames.getIcon(def_book,icon->{
            MyBitmapUtils myBitmapUtils=new MyBitmapUtils(getContext(),mHandler);
            myBitmapUtils.disPlay(book_img,icon);

        });
    }


    public void initGridLayout(){
        rl_set.setOnClickListener(v->{
            openNewPage(MainSetFragment.class);
        });
        rl_map.setOnClickListener(v->{
            openNewPage(MainMapFragment.class);
        });
        rl_asset.setOnClickListener(v->{
            openNewPage(MainCardFragment.class);
        });
        rl_sort.setOnClickListener(v->{
            openNewPage(MainSortFragment.class);
        });

        rl_async.setOnClickListener(v->{
            //TODO 数据同步
        });
        rl_bill.setOnClickListener(v->{
            openNewPage(MoneyFragment.class);
        });
        rl_bill_check.setOnClickListener(v->{
            //账单核对
        });
        rl_year.setOnClickListener(v->{
           //年度账单
        });
        rl_app_log.setOnClickListener(v->{
            NoticeFragment.openWithType(this, "app");
        });
        rl_sms_log.setOnClickListener(v->{
            NoticeFragment.openWithType(this, "sms");
        });
        rl_notice_log.setOnClickListener(v->{
            NoticeFragment.openWithType(this, "notice");
        });
        rl_log.setOnClickListener(v->{
            openNewPage(LogFragment.class);
        });
        rl_auto_sort.setOnClickListener(v->{
            openNewPage(MainAutoSortFragment.class);
        });
        rl_app.setOnClickListener(v->{
            MainAutoLearnFragment.openWithType(this, "app");
        });
        rl_sms.setOnClickListener(v->{
            MainAutoLearnFragment.openWithType(this, "sms");
        });
        rl_notice.setOnClickListener(v->{
            MainAutoLearnFragment.openWithType(this, "notice");
        });
        rl_float.setOnClickListener(v->{

        });
        rl_skin.setOnClickListener(v->{

        });
        rl_wait.setOnClickListener(v->{

        });
        rl_check.setOnClickListener(v->{

        });
        rl_backup.setOnClickListener(v->{
            openNewPage(BackUpFragment.class);
        });
        rl_text_teach.setOnClickListener(v->{
            WebViewFragment.openUrl(this,getContext().getString(R.string.learnUrl));
        });
        rl_video_teach.setOnClickListener(v->{
            WebViewFragment.openUrl(this,getContext().getString(R.string.biliUrl));
        });
        rl_github.setOnClickListener(v->{
            WebViewFragment.openUrl(this,getContext().getString(R.string.githubUrl));
        });
        rl_about.setOnClickListener(v->{
            openNewPage(AboutFragment.class);
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toasty.normal(getContext(), "再按一次退出程序!", Toast.LENGTH_LONG).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        }
        return true;
    }


}
