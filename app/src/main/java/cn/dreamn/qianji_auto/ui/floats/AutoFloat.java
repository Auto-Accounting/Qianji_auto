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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xfloatview.XFloatView;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;
import com.xuexiang.xutil.display.ScreenUtils;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.Assets;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.core.db.Helper.Category;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.fragment.asset.category.CateChoose;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.picture.MyBitmapUtils;
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

    private RelativeLayout parent_layout;
    private RelativeLayout account_book2_layout;
    private RelativeLayout category_layout;
    private RelativeLayout account_layout;
    private RelativeLayout account_book_layout;
    private RelativeLayout remark_layout;
    private RelativeLayout time_layout;
    private RelativeLayout type_layout;

    private RelativeLayout reimbursement_layout;
    private TextView auto_reimbursement;


    private ImageView iv_cate;
    private ImageView iv_account;
    private ImageView iv_account2;


    private TextView btn_cancel;
    private TextView btn_save;

    private BillInfo billInfo2;

    private String book_id = "-1";

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
        //  Caches.AddOrUpdate("float_lock", "true");


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


    @SuppressLint("RtlHardcoded")
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

        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

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
        parent_layout = findViewById(R.id.parent_layout);


        reimbursement_layout = findViewById(R.id.reimbursement_layout);

        auto_reimbursement = findViewById(R.id.auto_reimbursement);


        iv_cate = findViewById(R.id.iv_cate);

        iv_account = findViewById(R.id.iv_account);

        iv_account2 = findViewById(R.id.iv_account2);

    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {
        category_layout.setOnClickListener(v -> {
            MMKV mmkv = MMKV.defaultMMKV();
            if (mmkv.getBoolean("auto_cate_table", true)) {


                BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cate_choose, null, false);

                ExpandableListView expandableListView = view.findViewById(R.id.expandableListViewData);


                CateChoose cateChoose = new CateChoose(expandableListView, getContext(), BillInfo.getTypeName(billInfo2.getType()), false, book_id);


                cateChoose.refresh();

                if (cateChoose.isEmpty()) {
                    XToast.error(getContext(), "您尚未添加任何分类!").show();
                    return;
                }

                cateChoose.setOnClick(new CateChoose.CallBack() {
                    @Override
                    public void OnLongClickGroup(Bundle parent) {
                        billInfo2.setCateName(parent.getString("name"));
                        setData(billInfo2);
                        dialog.cancel();
                    }

                    @Override
                    public void OnLongClickChild(Bundle parent, Bundle child) {
                        billInfo2.setCateName(child.getString("name"));
                        setData(billInfo2);
                        dialog.cancel();
                    }


                    @Override
                    public void OnClickChild(Bundle parent, Bundle child) {
                        billInfo2.setCateName(child.getString("name"));
                        setData(billInfo2);
                        dialog.cancel();
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                } else {
                    dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                }

                dialog.setContentView(view);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(true);


                dialog.show();
            } else {
                billInfo2.setCateChoose(true);
                CallAutoActivity.goQianji(getContext(), billInfo2);
                this.clear();
            }

        });
        account_layout.setOnClickListener(v -> {
            Bundle[] bookNameList = BookNames.getAllIcon(true);


            showMenu("请选择账本", 3, bookNameList, data -> {

                billInfo2.setBookName(bookNameList[data].getString("name"));
                book_id = bookNameList[data].getString("book_id");
                this.setData(billInfo2);
            });
        });
        account_book_layout.setOnClickListener(v -> {
            Bundle[] assets = Assets.getAllIcon();
            if (assets == null || assets.length <= 0) {
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户", 2, assets, data -> {
                billInfo2.setAccountName(assets[data].getString("name"));
                this.setData(billInfo2);
            });
        });
        account_book2_layout.setOnClickListener(v -> {
            Bundle[] assets = Assets.getAllIcon();

            if (assets == null || assets.length <= 0) {
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户", 2, assets, data -> {
                billInfo2.setAccountName2(assets[data].getString("name"));
                this.setData(billInfo2);
            });
        });
        remark_layout.setOnClickListener(v -> {
            showInputDialog("请输入备注信息", billInfo2.getRemark(), data -> {
                billInfo2.setRemark(data);
                this.setData(billInfo2);
            });

        });
        time_layout.setOnClickListener(v -> {
            showInputDialog("请修改时间信息", billInfo2.getTime(), data -> {
                billInfo2.setTime(data);
                this.setData(billInfo2);
            });
        });
        type_layout.setOnClickListener(v -> {

            String[] strings = {"支出", "收入", "转账", "信用还款"};


            showMenu("请选择收支类型", 1, strings, data -> {
                billInfo2.setType(BillInfo.getTypeId(strings[data]));
                this.setData(billInfo2);
            });
        });
        btn_save.setOnClickListener(v -> {
            CallAutoActivity.goQianji(getContext(), billInfo2);
            this.clear();
        });
        btn_cancel.setOnClickListener(v -> this.clear());
        reimbursement_layout.setOnClickListener(v -> {
            if (auto_reimbursement.getText().toString().equals("不报销")) {
                auto_reimbursement.setText("报销");

                billInfo2.setRrimbursement(true);

            } else {
                auto_reimbursement.setText("不报销");
                billInfo2.setRrimbursement(false);
            }
            String cate = Category.getCategory(billInfo2);
            if (cate.equals("NotFind")) {
                billInfo2.setCateName("其它");//设置自动分类

            } else {
                billInfo2.setCateName(cate);//设置自动分类
            }
            //账单修改报销后重新分类

            this.setData(billInfo2);
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
        billInfo2 = billInfo;
        auto_remark.setText(billInfo.getRemark());
        auto_category.setText(billInfo.getCateName());
        auto_book.setText(billInfo.getBookName());
        auto_type.setText(BillInfo.getTypeName(billInfo.getType()));
        auto_account.setText(billInfo.getAccountName());
        auto_account2.setText(billInfo.getAccountName2());
        auto_time.setText(billInfo.getTime());
        if (billInfo.getType().equals(BillInfo.TYPE_INCOME) || billInfo.getType().equals(BillInfo.TYPE_PAY) || billInfo.getType().equals(BillInfo.TYPE_PAYMENT_REFUND)) {
            account_book2_layout.setVisibility(View.GONE);
            category_layout.setVisibility(View.VISIBLE);
        } else {
            account_book2_layout.setVisibility(View.VISIBLE);
            category_layout.setVisibility(View.GONE);
        }
        String type;
        if (!billInfo.getType().equals(BillInfo.TYPE_PAY)) {
            type = "1";
            reimbursement_layout.setVisibility(View.GONE);
        } else {
            type = "0";
            reimbursement_layout.setVisibility(View.VISIBLE);

        }
        if (billInfo.getType(true).equals(BillInfo.TYPE_PAYMENT_REFUND)) {
            auto_reimbursement.setText("报销");
        }
        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(getContext());


        if (BookNames.getAllLen() != 0) {
            Bundle bundle = BookNames.getOne(billInfo.getBookName());
            if (bundle.getString("book_id") != null) {
                book_id = bundle.getString("book_id");
            }
        }

        myBitmapUtils.disPlay(iv_cate, CategoryNames.getPic(billInfo.getCateName(), type, book_id));
        iv_cate.setColorFilter(getContext().getColor(R.color.darkGrey));
        myBitmapUtils.disPlay(iv_account, Assets.getPic(billInfo.getAccountName()));
        myBitmapUtils.disPlay(iv_account2, Assets.getPic(billInfo.getAccountName2()));

        auto_money.setText(BillTools.getCustomBill(billInfo));
        auto_money.setTextColor(BillTools.getColor(billInfo));


    }

    private void showMenu(String title, int type, Object[] list, ListFloat.Callback callBack) {

        ListFloat listFloat = new ListFloat(getContext());
        listFloat.setData(title, type, list, callBack);
        if (ScreenUtils.getScreenWidth() > ScreenUtils.getScreenHeight()) {
            listFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenHeight() - 100, ScreenUtils.getScreenWidth());
        } else {
            listFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() - 100);
        }
        listFloat.show();
    }


    public void showInputDialog(String title, String def, InputFloat.Callback callBack) {
        InputFloat inputFloat = new InputFloat(getContext());
        inputFloat.setData(title, def, getContext().getString(R.string.set_cancel), getContext().getString(R.string.input_ok), null, callBack);
        if (ScreenUtils.getScreenWidth() > ScreenUtils.getScreenHeight()) {
            inputFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenHeight() - 100, ScreenUtils.getScreenWidth());
        } else {
            inputFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() - 100);
        }
        inputFloat.show();


    }

    @Override
    public void dismiss() {

        String cate = Category.getCategory(billInfo2);
        if (cate.equals("NotFind")) {


            MMKV mmkv = MMKV.defaultMMKV();
            if (mmkv.getBoolean("auto_sort", false)) {
                Category.setCateJs(billInfo2, billInfo2.getCateName());
            }


        }
        super.dismiss();

    }
}
