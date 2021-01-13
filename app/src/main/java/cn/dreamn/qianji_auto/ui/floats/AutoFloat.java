/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.ui.floats;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.xuexiang.xfloatview.XFloatView;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class AutoFloat extends XFloatView {

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private TextView auto_remark;
    private TextView auto_money;
    private TextView auto_category;
    private TextView auto_book;
    private TextView auto_type;
    private TextView auto_time;
    private TextView auto_account;
    private TextView auto_account2;


    private RelativeLayout account_book2_layout;
    private RelativeLayout category_layout;
    private RelativeLayout account_layout;
    private RelativeLayout account_book_layout;
    private RelativeLayout remark_layout;
    private RelativeLayout time_layout;
    private RelativeLayout type_layout;

    private TextView btn_cancel;
    private TextView btn_save;

    private BillInfo billInfo2;
    /**
     * 构造器
     *
     * @param context
     */
    public AutoFloat(Context context) {
        super(context);
        initData();
    }

    private void initData() {
        Logs.d("初始化窗口");
    }

    /**
     * @return 获取根布局的ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.float_auto;
    }

    /**
     * @return 能否移动或者触摸响应
     */
    @Override
    protected boolean canMoveOrTouch() {
        return true;
    }


    @Override
    protected WindowManager.LayoutParams getFloatViewLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags =
                // LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        params.gravity = Gravity.LEFT | Gravity.TOP;
        return params;
    }
    /**
     * 初始化悬浮控件
     */
    @Override
    protected void initFloatView() {
        auto_remark = findViewById(R.id.auto_remark);
        auto_money = findViewById(R.id.auto_money);
        auto_time = findViewById(R.id.auto_time);
        auto_category = findViewById(R.id.auto_category);
        auto_book = findViewById(R.id.auto_book);
        auto_type = findViewById(R.id.auto_type);
        auto_account = findViewById(R.id.auto_account);
        auto_account2 = findViewById(R.id.auto_account2);

        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);

        category_layout = findViewById(R.id.category_layout);
        account_layout = findViewById(R.id.account_layout);
        account_book_layout = findViewById(R.id.account_book_layout);
        account_book2_layout = findViewById(R.id.account_book2_layout);
        remark_layout = findViewById(R.id.remark_layout);
        time_layout = findViewById(R.id.time_layout);
        type_layout = findViewById(R.id.type_layout);
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {
        category_layout.setOnClickListener(v->{
            billInfo2.setCateChoose(true);
            CallAutoActivity.goQianji(getContext(),billInfo2);
            this.clear();
        });
        account_layout.setOnClickListener(v->{
            String[] bookNameList = BookNames.getAll();
            if(bookNameList==null||bookNameList.length<=0){
                XToastUtils.error("账本为空，请在账本设置中添加账本数据。");
                return;
            }
            showMenu("请选择账本",bookNameList,data -> {

                billInfo2.setBookName(bookNameList[data]);
                this.setData(billInfo2);
            });
        });
        account_book_layout.setOnClickListener(v->{
            String[] assets = Assets.getAllAccountName();
            if(assets==null||assets.length<=0){
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户",assets,data -> {
                billInfo2.setAccountName(assets[data]);
                this.setData(billInfo2);
            });
        });
        account_book2_layout.setOnClickListener(v->{
            String[] assets = Assets.getAllAccountName();
            if(assets==null||assets.length<=0){
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户",assets,data -> {
                billInfo2.setAccountName2(assets[data]);
                this.setData(billInfo2);
            });
        });
        remark_layout.setOnClickListener(v->{
            showInputDialog("请输入备注信息",billInfo2.getRemark(),data -> {
                billInfo2.setRemark(data);
                this.setData(billInfo2);
            });

        });
        time_layout.setOnClickListener(v->{
            showInputDialog("请修改时间信息",billInfo2.getTime(),data -> {
                billInfo2.setTime(data);
                this.setData(billInfo2);
            });
        });
        type_layout.setOnClickListener(v->{
            String[] strings={"支出","收入","转账","信用还款"};
            showMenu("请选择资产账户",strings,data -> {
                billInfo2.setType(String.valueOf(data));
                this.setData(billInfo2);
            });
        });
        btn_save.setOnClickListener(v->{
            CallAutoActivity.goQianji(getContext(),billInfo2);
            this.clear();
        });
        btn_cancel.setOnClickListener(v->{
            this.clear();
        });

    }


    /**
     * @return 设置悬浮框是否吸附在屏幕边缘
     */
    @Override
    protected boolean isAdsorbView() {
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    public void setData(BillInfo billInfo) {
        billInfo2=billInfo;
        auto_remark.setText(billInfo.getRemark());
        auto_category.setText(billInfo.getCateName());
        auto_book.setText(billInfo.getBookName());
        auto_type.setText(billInfo.getTypeName(billInfo.getType()));
        auto_account.setText(billInfo.getAccountName());
        auto_account2.setText(billInfo.getAccountName2());
        auto_time.setText(billInfo.getTime());
        if(billInfo.getType().equals(BillInfo.TYPE_INCOME) || billInfo.getType().equals(BillInfo.TYPE_PAY)){
            account_book2_layout.setVisibility(View.GONE);
        }else{
            account_book2_layout.setVisibility(View.VISIBLE);
        }
        auto_money.setText(BillTools.getCustomBill(billInfo));
        auto_money.setTextColor(BillTools.getColor(billInfo));
    }
    // 回调接口

    private void showMenu(String title,String[] list,ListFloat.Callback callBack){

        ListFloat listFloat=new ListFloat(getContext());
        listFloat.setData(title,list,callBack);
        listFloat.show();
    }


    public void showInputDialog(String title, String def, InputFloat.Callback callBack) {
        InputFloat inputFloat=new InputFloat(getContext());
        inputFloat.setData(title,def,getContext().getString(R.string.set_cancel),getContext().getString(R.string.input_ok),null,callBack);
        inputFloat.show();


    }
}
