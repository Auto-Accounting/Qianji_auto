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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.adapter.ListAdapter;
import cn.dreamn.qianji_auto.ui.adapter.ModeAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.MainFragment;
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
    @BindView(R.id.lv_permission)
    ListView lv_permission;
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
    protected void initListeners() {
        help_skip_last.setOnClickListener(v->{
            openPage(AppFragment.class);
        });
        help_skip.setOnClickListener(v -> {

            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("first",false);

            openPage(MainFragment.class);
        });
        button_next.setOnClickListener(v->{
            openPage(AsyncFragment.class);
        });
    }
    private void setMode(){
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

        List<CardView> cardViews=new ArrayList<>();
        ModeAdapter modeAdapter=new ModeAdapter(getContext(), R.layout.grid_items, bundles, (item, cardView) -> {
            if(item!=null&&item.equals(mmkv.getString("helper_choose","xposed"))){
                cardView.setCardElevation(15);
                if(!cardViews.contains(cardView))
                    cardViews.add(cardView);
                ButtonUtils.enable(button_next,getContext());
            }
        });
        mode_list.setAdapter(modeAdapter);


        mode_list.setOnItemClickListener((parent, view, position, id) -> {
            String appName=bundles[position].getString("appName");
             mmkv.encode("helper_choose",(appName.equals("无障碍模式") ?"helper":"xposed"));
            CardView cardView=(CardView)view.findViewById(R.id.card_shadow);

            for(int i=0;i<cardViews.size();i++){
                cardViews.get(i).setCardElevation(0);
            }
            if(!cardViews.contains(cardView))
                cardViews.add(cardView);
            cardView.setCardElevation(15);
            ButtonUtils.enable(button_next,getContext());
            setPermission(mmkv.getString("helper_choose","xposed"));
        });
    }

    //设置权限
    private void setPermission(String helper_choose){
        List<Bundle> list=new ArrayList<>();
        Bundle bundle=new Bundle();
        bundle.putInt("appId", PermissionUtils.Float);
        bundle.putString("appIcon", "&#xe603;");
        bundle.putString("appName", "悬浮窗权限");
        bundle.putString("appNameSub", "用于弹出悬浮窗记账");
        list.add(bundle);
        Bundle bundle2=new Bundle();
        bundle2.putInt("appId", PermissionUtils.Start);
        bundle2.putString("appIcon", "&#xe6e2;");
        bundle2.putString("appName", "自启动权限");
        bundle2.putString("appNameSub", "启动自动记账进行记账");
        list.add(bundle2);
        Bundle bundle3=new Bundle();
        bundle3.putInt("appId", PermissionUtils.Sms);
        bundle3.putString("appIcon", "&#xe61c;");
        bundle3.putString("appName", "短信权限");
        bundle3.putString("appNameSub", "读取分析短信账单");
        list.add(bundle3);
        if(!helper_choose.equals("xposed")){
            Bundle bundle4=new Bundle();
            bundle4.putInt("appId", PermissionUtils.Assist);
            bundle4.putString("appIcon", "&#xe61b;");
            bundle4.putString("appName", "辅助服务");
            bundle4.putString("appNameSub", "用于无障碍记账");
            list.add(bundle4);
            Bundle bundle5=new Bundle();
            bundle5.putInt("appId", PermissionUtils.Battery);
            bundle5.putString("appIcon", "&#xe6b1;");
            bundle5.putString("appName", "后台运行无限制");
            bundle5.putString("appNameSub", "用于后台保活");
            list.add(bundle5);
            Bundle bundle6=new Bundle();
            bundle6.putInt("appId", PermissionUtils.BatteryIngore);
            bundle6.putString("appIcon", "&#xe604;");
            bundle6.putString("appName", "忽略电池优化");
            bundle6.putString("appNameSub", "用于后台保活");
            list.add(bundle6);
            Bundle bundle7=new Bundle();
            bundle7.putInt("appId", PermissionUtils.Hide);
            bundle7.putString("appIcon", "&#xe61f;");
            bundle7.putString("appName", "多任务隐藏");
            bundle7.putString("appNameSub", "用于后台保活");
            list.add(bundle7);
            Bundle bundle8=new Bundle();
            bundle8.putInt("appId", PermissionUtils.Lock);
            bundle8.putString("appIcon", "&#xe654;");
            bundle8.putString("appName", "多任务锁定");
            bundle8.putString("appNameSub", "用于后台保活");
            list.add(bundle8);
        }
        Bundle[] bundles=list.toArray(new Bundle[0]);
        ListAdapter listAdapter=new ListAdapter(getContext(),R.layout.list_items,bundles);
        lv_permission.setAdapter(listAdapter);
        lv_permission.setOnItemClickListener((parent, view, position, id) -> {
            PermissionUtils permissionUtils=new PermissionUtils(getContext());
            permissionUtils.grant(bundles[position].getInt("appId"));
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MMKV mmkv=MMKV.defaultMMKV();
        setPermission(mmkv.getString("helper_choose","xposed"));
    }
}
