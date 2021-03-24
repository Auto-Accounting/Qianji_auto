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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;
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
import cn.dreamn.qianji_auto.ui.fragment.base.MainModeFragment;
import cn.dreamn.qianji_auto.ui.listData.ListManager;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.views.CardViewGrid;
import cn.dreamn.qianji_auto.ui.views.IconView;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import es.dmoral.toasty.Toasty;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "自动记账", anim = CoreAnim.slide)
public class MainFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    RelativeLayout title_bar;

    @BindView(R.id.linelayout)
    RelativeLayout linearLayout;
    @BindView(R.id.title_count)
    RelativeLayout title_count;

    @BindView(R.id.cv_list)
    CardViewGrid cv_list;
    @BindView(R.id.cv_log)
    CardViewGrid cv_log;
    @BindView(R.id.cv_complie)
    CardViewGrid cv_complie;
    @BindView(R.id.cv_other)
    CardViewGrid cv_other;


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
        return R.layout.fragment_main_1;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initViews() {
        ThemeManager themeManager = new ThemeManager(getContext());
        themeManager.setStatusBar(getActivity(), title_count, R.color.background_white);
        cv_list.setData(ListManager.getBaseLists(),this,1);
        cv_log.setData(ListManager.getLogLists(),this,2);
        cv_complie.setData(ListManager.getComplieLists(),this,3);
        cv_other.setData(ListManager.getOtherLists(),this,4);
        app_log.setText(BuildConfig.VERSION_NAME);
        setActive();
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


    }

    @Override
    protected void initListeners() {
        mode_select1.setOnClickListener(v-> openNewPage(MainModeFragment.class));
        mode_select2.setOnClickListener(v-> openNewPage(MainModeFragment.class));
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
            // TODO 未激活指引激活
        }
        //SetImg
        String def_book=BookNames.getDefault();
        default_book.setText(def_book);
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                MyBitmapUtils.setImage(getContext(),book_img,(Bitmap) msg.obj);

            }
        };

        BookNames.getIcon(def_book,icon->{
            MyBitmapUtils myBitmapUtils=new MyBitmapUtils(getContext(),mHandler);
            myBitmapUtils.disPlay(book_img,icon);

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
                System.exit(0);
            }
        }
        return true;
    }


}
