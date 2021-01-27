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

package cn.dreamn.qianji_auto.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.Permission;

@Page(name = "工作模式")
public class ModeFragment extends BaseFragment {


    @BindView(R.id.mode_xp)
    SuperTextView mode_xp;

    @BindView(R.id.mode_default)
    SuperTextView mode_default;

    @BindView(R.id.set_helper_list)
    LinearLayout set_helper_list;

    @BindView(R.id.set_xp_list)
    LinearLayout set_xp_list;



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
        MMKV mmkv = MMKV.defaultMMKV();
        String mode=mmkv.getString("helper_choose","xposed");
        if(mode.equals("helper")){
            setSelectedModel(mode_default,true);
            setSelectedModel(mode_xp,false);
            set_helper_list.setVisibility(View.VISIBLE);
            set_xp_list.setVisibility(View.GONE);
        }else{
            setSelectedModel(mode_default,false);
            setSelectedModel(mode_xp,true);
            set_helper_list.setVisibility(View.GONE);
            set_xp_list.setVisibility(View.VISIBLE);


        }


    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({
            R.id.mode_default,
            R.id.mode_xp,

            R.id.permission_assist,
            R.id.permission_sms,
            R.id.permission_float,
            R.id.permission_start,
            R.id.permission_battery,
            R.id.permission_lock,
            R.id.permission_battery_ingore,
            // R.id.permission_security,
            R.id.permission_notification,
            R.id.permission_float2,
            R.id.permission_start2,

    })
    public void onViewClicked(View view){
        MMKV mmkv = MMKV.defaultMMKV();
        switch (view.getId()){
            case R.id.mode_default:
                SnackbarUtils.Long(getView(), getString(R.string.mode_msg_default)).info().show();
                mmkv.encode("helper_choose","helper");
                initSet();
                break;
            case R.id.mode_xp:
                SnackbarUtils.Long(getView(), getString(R.string.mode_msg_xp)).info().show();
                mmkv.encode("helper_choose","xposed");
                initSet();
                break;
            case R.id.permission_assist:
                Permission.getInstance().grant(this.getContext(),Permission.Assist);
                break;
            case R.id.permission_sms:
                Permission.getInstance().grant(this.getContext(),Permission.Sms);
                break;
            case R.id.permission_float: case R.id.permission_float2:
                Permission.getInstance().grant(this.getContext(),Permission.Float);
                break;
            case R.id.permission_start: case R.id.permission_start2:
                Permission.getInstance().grant(this.getContext(),Permission.Start);
                break;
            case R.id.permission_battery:
                Permission.getInstance().grant(this.getContext(),Permission.Battery);
                break;
            case R.id.permission_lock:
                Permission.getInstance().grant(this.getContext(),Permission.Lock);
                break;
            case R.id.permission_battery_ingore:
                Permission.getInstance().grant(this.getContext(),Permission.BatteryIngore);
                break;
            case R.id.permission_notification:
                Permission.getInstance().grant(this.getContext(),Permission.Notification);
                break;
            default:break;
        }
    }

    private void initListen(){

    }
}
