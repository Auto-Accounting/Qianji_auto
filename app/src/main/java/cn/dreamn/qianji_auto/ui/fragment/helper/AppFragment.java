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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.github.czy1121.view.CornerLabelView;
import com.liuguangqiang.cookie.CookieBar;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.ui.adapter.AppAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.ButtonUtils;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "记账软件", anim = CoreAnim.slide)
public class AppFragment extends BaseFragment {

    @BindView(R.id.button_next)
    Button button_next;
    @BindView(R.id.app_list)
    GridView app_list;
    @BindView(R.id.help_skip_last)
    TextView help_skip_last;
    @BindView(R.id.help_skip)
    TextView help_skip;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_2;
    }

    @Override
    protected void initViews() {
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("helper_page",1);
        ButtonUtils.disable(button_next,getContext());
        setAppList();
    }

    private void setAppList(){
        Bundle[] bundles=AppManager.getAllApps();
        int width= ScreenUtils.getScreenWidth(getActivity());
        width=ScreenUtils.px2dip(getContext(),width);
        width=width-40;//真实可分配大小
        int s=width/80;//分80

        int all=bundles.length;
        if(all<=s){
            app_list.setColumnWidth(ScreenUtils.dip2px(getContext(),(float) width/all));
            app_list.setNumColumns(all);
        }

        final CornerLabelView[] lastCorner = {null};
        AppAdapter appAdapter=new AppAdapter(getContext(), R.layout.grid_items, bundles, (item, cornerLabelView) -> {
            if(item!=null&&item.equals(AppManager.getApp())){
                cornerLabelView.setVisibility(View.VISIBLE);
                lastCorner[0]=cornerLabelView;
                ButtonUtils.enable(button_next,getContext());
            }
        });
        app_list.setAdapter(appAdapter);


        app_list.setOnItemClickListener((parent, view, position, id) -> {

            String packageName=bundles[position].getString("appPackage");
            if(packageName==null){
                new CookieBar.Builder(getActivity())
                        .setTitle("无法设置该记账APP")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(String.format("需要《%s》作者先行适配自动记账才可使用哦。",bundles[position].getString("appName")))
                        .setAction("知道了", () -> {
                        }).setDuration(10000)
                        .show();
                return;
            }
            AppManager.setApp(packageName);
            CornerLabelView cornerLabelView=(CornerLabelView)view.findViewById(R.id.icon_choose);

            if(lastCorner[0] !=null){
                lastCorner[0].setVisibility(View.GONE);
            }
            lastCorner[0] =cornerLabelView;
            cornerLabelView.setVisibility(View.VISIBLE);
            ButtonUtils.enable(button_next,getContext());
        });
    }


    @Override
    protected void initListeners() {
        help_skip_last.setOnClickListener(v->{
            openPage(HelperFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("first",false);
            //TODO 跳转主页面
            openPage(AppFragment.class);
        });
        button_next.setOnClickListener(v->{
            openPage(ModeFragment.class);
        });
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return true;
    }


}
