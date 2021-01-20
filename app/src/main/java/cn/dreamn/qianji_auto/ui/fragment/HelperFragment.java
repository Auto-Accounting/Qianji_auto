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
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.AppUtils;
import com.xuexiang.xutil.display.Colors;

import java.util.Objects;

import butterknife.BindViews;
import butterknife.OnClick;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "引导", anim = CoreAnim.none)
public class HelperFragment extends BaseFragment {

    @BindViews({
            R.id.page_1,
            R.id.page_4,
            R.id.page_2,
            R.id.page_3,
            R.id.page_3_1,
            R.id.page_3_2,
            R.id.page_5
    })
    ScrollView[] scrollViews;

    private static final int page_1 = 0;
    private static final int page_4 = 1;
    private static final int page_2 = 2;
    private static final int page_3 = 3;
    private static final int page_3_1 = 4;
    private static final int page_3_2 = 5;
    private static final int page_5 = 6;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helper;
    }


    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews(){
        StatusBarUtils.initStatusBarStyle(Objects.requireNonNull(getActivity()), false, Colors.TRANSPARENT);
        //没有同意过隐私协议则弹出
        MMKV mmkv=MMKV.defaultMMKV();
        if(!mmkv.getBoolean("protocol",false))
            showMsg();
        int i=mmkv.getInt("step",page_1);
        step(i);

    }
    @SuppressLint("NonConstantResourceId")
    @OnClick({
            R.id.helper_start,
            R.id.helper_bill,
            R.id.helper_bill_next,
            R.id.helper_async,
            R.id.helper_async_next,
            R.id.helper_choose_help,
            R.id.helper_choose_xp,

            R.id.helper_permission_next,
            R.id.helper_permission_next2,

            R.id.set_ok,

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

            R.id.set_check,
            R.id.set_delay,
            R.id.set_default_book,



    })
    public void onViewClicked(View view) {
        MMKV mmkv=MMKV.defaultMMKV();
        switch (view.getId()) {
            case R.id.helper_start:step(page_4);break;
            case R.id.helper_bill:
                showTip(false,R.string.helper_tip2,R.string.helper_tip_qianji2,R.string.helper_qianji_ok,view1 -> {
                  //  showTip(false,R.string.helper_tip2,R.string.helper_tip_qianji2,R.string.helper_qianji_ok,view3 ->{});

                  // intent = new Intent(Intent.ACTION_MAIN);
                    //
                    //        ComponentName componentName = new ComponentName("com.mutangtech.qianji", "com.mutangtech.qianji.bill.add.AddBillActivity");
                    //        intent.setComponent(componentName);
                    try{

                        AppUtils.launchApp("com.mutangtech.qianji");
                        Logs.d("启动钱迹成功。");
                    }catch (Exception e){
                        Logs.d("启动钱迹失败。");
                        showTip(true,R.string.helper_tip,R.string.helper_tip_qianji,R.string.helper_qianji_install,view2 -> {
                            Uri uri = Uri.parse("market://details?id=com.mutangtech.qianji");
                            Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);
                        },-1);
                    }
                },-1);
                break;
            case R.id.helper_bill_next:
                step(page_2);

                break;
            case R.id.helper_async:
                showTip(false,R.string.helper_tip3,R.string.helper_tip_qianji3,R.string.helper_qianji_err,view1 -> {},-1);
                break;
            case R.id.helper_async_next:
                step(page_3);
                break;
            case R.id.helper_choose_help:
                //辅助功能
                mmkv.encode("helper_choose","helper");
                step(page_3_1);
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
           /* case R.id.permission_security:
                Permission.getInstance().grant(this.getContext(),Permission.Security);
                break;*/

            case R.id.helper_choose_xp:
                mmkv.encode("helper_choose","xposed");
                step(page_3_2);
                break;

            case R.id.helper_permission_next:

            case R.id.helper_permission_next2:
                step(page_5);
                break;
            case R.id.set_check:
                if(mmkv.getBoolean("auto_check",true)){
                    mmkv.encode("auto_check",false);
                    showTip(false,R.string.helper_tip_qianji4,R.string.helper_tip4,R.string.helper_qianji_err,view1 -> {},5000);
                }else{
                    mmkv.encode("auto_check",true);
                    showTip(false,R.string.helper_tip_qianji5,R.string.helper_tip5,R.string.helper_qianji_err,view1 -> {},5000);
                }
                break;
            case R.id.set_delay:
                showInputDialog("请输入延时时间","默认延时10秒，设置为0不延时。",mmkv.getString("auto_timeout","10"), (CallBack) data -> {
                    try{
                        Integer.parseInt(data);
                        mmkv.encode("auto_timeout",data);
                    }catch (Exception e){
                        new MaterialDialog.Builder(getContext())
                                .title("类型错误")
                                .content("只允许输入整数！")
                                .positiveText(getString(R.string.input_ok))
                                .show();
                    }
                });
                break;
            case R.id.set_default_book:
                showInputDialog("请输入默认账本","钱迹非会员请保持使用默认账本",mmkv.getString("defaultBookName","默认账本"), (CallBack) data -> {
                    mmkv.encode("defaultBookName",data);
                });
                break;


            case R.id.set_ok:
                mmkv.encode("first",false);
                openNewPage(MainFragment.class);
            default:
                break;
        }
    }


    @SuppressLint("MissingPermission")
    public void showMsg() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.fragment_helper_service, null);
        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
               .customView(textEntryView,true)
                .buttonsGravity(GravityEnum.CENTER)
                .positiveText(getString(R.string.helper_service_ok))
                .negativeText(getString(R.string.helper_service_err))
                .onPositive((dialog, which) -> {
                    //下一步
                    MMKV mmkv=MMKV.defaultMMKV();
                    mmkv.encode("protocol",true);//同意协议
                    Logs.d("已同意服务协议与隐私政策。");
                })
                .onNegative((dialog, which) -> {
                    Logs.d("不同意服务协议与隐私政策,APP退出。");
                    XUtil.exitApp();
                })
                .show();
    }

    private void step(int i){
        Logs.d("页面展示 "+i);
        if(i<scrollViews.length){
            for (ScrollView scrollView : scrollViews) {
                scrollView.setVisibility(View.GONE);
            }
                scrollViews[i].setVisibility(View.VISIBLE);
            MMKV mmkv=MMKV.defaultMMKV();
            mmkv.encode("step",i);//同意协议
        }
    }
    // 回调接口
    private interface Callback {
        void onResponse(View view);
    }
    private void showTip(boolean err, int title, int content, int tipAction, Callback callback,int timeout){
       CookieBar.Builder cookieBar= CookieBar.builder(getActivity())
                .setTitle(title)
                .setMessage(content)
                .setDuration(timeout)
                .setActionColor(android.R.color.white)
                .setTitleColor(android.R.color.white)
                .setAction(tipAction, callback::onResponse);
        if(!err){
            cookieBar.setBackgroundColor(R.color.colorPrimary);
        }else{
            cookieBar.setBackgroundColor(R.color.toast_warning_color);
        }
        cookieBar.show();

    }
    // 回调接口
    private interface Callback2 {
        void onResponse(String data);
    }
    public void showInputDialog(String title,String tip,String def,Callback2 callBack) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(tip)
                .input(
                        getString(R.string.input_tip),
                        def,
                        false,
                        ((dialog, input) -> {}))
                .positiveText(getString(R.string.input_ok))
                .negativeText(getString(R.string.set_cancel))
                .onPositive((dialog, which) -> callBack.onResponse(dialog.getInputEditText().getText().toString()))
                .show();
    }

}
