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
import android.widget.GridView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.ui.adapter.AppAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
import cn.dreamn.qianji_auto.ui.utils.ButtonUtils;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;



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
        Listeners();
    }

    private void setAppList(){
        Bundle[] bundles=AppManager.getAllApps();
        int width= ScreenUtils.getScreenWidth(getActivity());
        width=ScreenUtils.px2dip(getContext(),width);
        width=width-40;//真实可分配大小
        int s=width/80;//分80

        assert bundles != null;
        int all=bundles.length;
        if(all<=s){
            app_list.setColumnWidth(ScreenUtils.dip2px(getContext(),(float) width/all));
            app_list.setNumColumns(all);
        }

        List<CardView> cardViews=new ArrayList<>();
        AppAdapter appAdapter=new AppAdapter(getContext(), R.layout.adapter_grid_item, bundles, (item, cardView) -> {
            if(item!=null&&item.equals(AppManager.getApp())){
                // cardView.setCardElevation(15);
                if(!cardViews.contains(cardView))
                    cardViews.add(cardView);
                ButtonUtils.enable(button_next,getContext());
            }
        });
        app_list.setAdapter(appAdapter);


        app_list.setOnItemClickListener((parent, view, position, id) -> {
            String packageName=bundles[position].getString("appPackage");
            if(packageName==null){
                ToastUtils.show(String.format("需要《%s》作者先行适配自动记账才可使用哦。", bundles[position].getString("appName")));
                return;
            }
            AppManager.setApp(packageName);
            CardView cardView = view.findViewById(R.id.card_shadow);

            for(int i=0;i<cardViews.size();i++){
                cardViews.get(i).setCardElevation(0);
            }
            if(!cardViews.contains(cardView))
                cardViews.add(cardView);
            //cardView.setCardElevation(15);
            ButtonUtils.enable(button_next,getContext());
        });
    }



    protected void Listeners() {
        help_skip_last.setOnClickListener(v->{
            openNewPage(HelperFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("version_3_0",false);
            openNewPage(MainFragment.class);
        });
        button_next.setOnClickListener(v->{
            openNewPage(ModeFragment.class);
        });
    }



    


}
