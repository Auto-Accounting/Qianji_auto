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
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.ui.adapter.ModeAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.ButtonUtils;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "工作模式", anim =  CoreAnim.slide)
public class ModeFragment extends BaseFragment {

    @BindView(R.id.button_next)
    Button button_next;
    @BindView(R.id.mode_list)
    GridView mode_list;
    @BindView(R.id.help_skip_last)
    TextView help_skip_last;
    @BindView(R.id.help_skip)
    TextView help_skip;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper_3;
    }

    @Override
    protected void initViews() {
        MMKV mmkv=MMKV.defaultMMKV();
        mmkv.encode("helper_page",2);
        ButtonUtils.disable(button_next,getContext());
        setMode();
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
    @Override
    protected void initListeners() {
        help_skip_last.setOnClickListener(v->{
            openPage(AppFragment.class);
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
    public void setMode(){
        Bundle[] bundles= new Bundle[2];
        Bundle bundle=new Bundle();
        bundle.putString("appIcon","&#xe65c;");
        bundle.putString("appName","Xposed模式");
        bundle.putString("appValue","xposed");
        bundles[0]=bundle;
        Bundle bundle2=new Bundle();
        bundle2.putString("appIcon","&#xe668;");
        bundle2.putString("appName","无障碍模式");
        bundle2.putString("appValue","helper");
        bundles[1]=bundle2;

        int width= ScreenUtils.getScreenWidth(getActivity());
        width=ScreenUtils.px2dip(getContext(),width);
        width=width-40;//真实可分配大小
        mode_list.setColumnWidth(ScreenUtils.dip2px(getContext(),(float) width/2));
        mode_list.setNumColumns(2);

        MMKV mmkv=MMKV.defaultMMKV();
        final CornerLabelView[] lastCorner = {null};
        ModeAdapter modeAdapter = new ModeAdapter(getContext(),R.layout.grid_items_icon, bundles,(item, cornerLabelView) -> {
            if(item!=null&&item.equals(mmkv.getString("helper_choose","xposed"))){
                cornerLabelView.setVisibility(View.VISIBLE);
                lastCorner[0]=cornerLabelView;
                ButtonUtils.enable(button_next,getContext());
            }
        });
        mode_list.setAdapter(modeAdapter);



        mode_list.setOnItemClickListener((parent, view, position, id) -> {
            String appName=bundles[position].getString("appName");
             mmkv.encode("helper_choose",(appName.equals("无障碍模式") ?"helper":"xposed"));
            CornerLabelView cornerLabelView=(CornerLabelView)view.findViewById(R.id.icon_choose);

            if(lastCorner[0] !=null){
                lastCorner[0].setVisibility(View.GONE);
            }
            lastCorner[0] =cornerLabelView;
            cornerLabelView.setVisibility(View.VISIBLE);
            ButtonUtils.enable(button_next,getContext());
        });
    }

}
