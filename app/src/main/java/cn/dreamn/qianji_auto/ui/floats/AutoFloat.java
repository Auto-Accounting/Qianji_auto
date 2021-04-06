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
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xfloatview.XFloatView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class AutoFloat extends XFloatView {

    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==0){
                setData(billInfo2);
            }
        }
    };



    private LinearLayout layout_money;
    private TextView tv_money;

    private LinearLayout ll_fee;
    private TextView tv_fee;

    private LinearLayout ll_book;
    private TextView tv_book;

    private LinearLayout ll_category;
    private ImageView iv_category;
    private TextView tv_category;


    private LinearLayout ll_type;
    private TextView tv_type;
    private TextView chip_bx;

    private LinearLayout ll_account1;
    private TextView tv_account1;
    private ImageView iv_account1;

    private LinearLayout ll_account2;
    private TextView tv_account2;
    private ImageView iv_account2;

    private LinearLayout ll_time;
    private TextView tv_time;

    private LinearLayout ll_remark;
    private TextView tv_remark;

    private Button button_fail;

    private Button button_next;


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
        Log.d("初始化窗口");
        //  Caches.AddOrUpdate("float_lock", "true");


    }

    /**
     * @return 获取根布局的ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.float_edit;
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
        bindView();
        setVisible();
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {
        ll_category.setOnClickListener(v -> {
            //分类选择
        });
        ll_book.setOnClickListener(v -> {
            BookNames.getAllIcon(true, new BookNames.getBookBundles() {
                @Override
                public void onGet(Bundle[] bundle) {
                    //
                }
            });

/*
            showMenu("请选择账本", 3, bookNameList, data -> {

                billInfo2.setBookName(bookNameList[data].getString("name"));
                book_id = bookNameList[data].getString("book_id");
                this.setData(billInfo2);
            });*/

        });
        ll_account1.setOnClickListener(v -> {
            Assets.getAllIcon(asset2s -> {

            });
           /* if (assets == null || assets.length <= 0) {
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户", 2, assets, data -> {
                billInfo2.setAccountName(assets[data].getString("name"));
                this.setData(billInfo2);
            });*/
        });
        ll_account2.setOnClickListener(v -> {
             Assets.getAllIcon(asset2s -> {

             });

            /*if (assets == null || assets.length <= 0) {
                XToastUtils.error("资产账户为空，请在资产账户设置中添加账本数据。");
                return;
            }
            showMenu("请选择资产账户", 2, assets, data -> {
                billInfo2.setAccountName2(assets[data].getString("name"));
                this.setData(billInfo2);
            });*/
        });
        ll_remark.setOnClickListener(v -> {
           /* showInputDialog("请输入备注信息", billInfo2.getRemark(), data -> {
                billInfo2.setRemark(data);
                this.setData(billInfo2);
            });*/

        });
        ll_time.setOnClickListener(v -> {
            /*showInputDialog("请修改时间信息", billInfo2.getTime(), data -> {
                billInfo2.setTime(data);
                this.setData(billInfo2);
            });*/
        });
        tv_type.setOnClickListener(v -> {

            String[] strings = {"支出", "收入", "转账", "信用还款"};


           /* showMenu("请选择收支类型", 1, strings, data -> {
                billInfo2.setType(BillInfo.getTypeId(strings[data]));
                if (!strings[data].equals("转账") && !strings[data].equals("信用还款"))
                    billInfo2.setAccountName2(null);//不等于就去掉第二个账户
                this.setData(billInfo2);
            });*/
        });
        button_next.setOnClickListener(v -> {
            SendDataToApp.goApp(getContext(), billInfo2);
            this.clear();
        });
        button_fail.setOnClickListener(v -> this.clear());
        chip_bx.setOnClickListener(v -> {
            billInfo2.setRrimbursement(chip_bx.getText().toString().equals("不报销"));
            Category.getCategory(billInfo2, cate -> {
                if (cate.equals("NotFind")) {
                    billInfo2.setCateName("其它");//设置自动分类

                } else {
                    billInfo2.setCateName(cate);//设置自动分类
                }
                //账单修改报销后重新分类

                mMainHandler.sendEmptyMessage(0);
            });

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

    @SuppressLint("SetTextI18n")
    public void setData(BillInfo billInfo) {

        billInfo2 = billInfo;


        tv_fee.setText("-￥" + billInfo.getFee());

        tv_book.setText(billInfo.getBookName());
        tv_category.setText(billInfo.getCateName());
        tv_type.setText(BillInfo.getTypeName(billInfo.getType()));
        setCheckable(billInfo.getReimbursement());
        tv_account1.setText(billInfo.getAccountName());
        tv_account2.setText(billInfo.getAccountName2());
        tv_time.setText(billInfo.getTime());
        String remark=billInfo.getRemark();
        if(remark.length()>20){
            remark=remark.substring(0,20)+"...";
        }
        tv_remark.setText(remark);


        tv_money.setText(BillTools.getCustomBill(billInfo));
        tv_money.setTextColor(BillTools.getColor(getContext(),billInfo));


        if (billInfo.getType().equals(BillInfo.TYPE_INCOME) || billInfo.getType().equals(BillInfo.TYPE_PAY) || billInfo.getType().equals(BillInfo.TYPE_PAYMENT_REFUND)) {
            ll_account2.setVisibility(View.GONE);
            ll_category.setVisibility(View.VISIBLE);
        } else {
            ll_account2.setVisibility(View.VISIBLE);
            ll_category.setVisibility(View.GONE);
        }
        String type;
        if (!billInfo.getType().equals(BillInfo.TYPE_PAY)) {
            type = "1";
            chip_bx.setVisibility(View.GONE);
        } else {
            type = "0";
            chip_bx.setVisibility(View.VISIBLE);
        }
        if(billInfo.getType().equals(BillInfo.TYPE_PAY)||billInfo.getType().equals(BillInfo.TYPE_INCOME)){
            ll_fee.setVisibility(View.GONE);
        }else{
            ll_fee.setVisibility(View.VISIBLE);
        }


        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Object[] objects=(Object[])msg.obj;
                MyBitmapUtils.setImage(getContext(),(View) objects[0],(Bitmap)objects[1]);
            }
        };

        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(getContext(),mHandler);

        BookNames.getAllLen(length -> {
            if(length!=0){
              BookNames.getOne(billInfo.getBookName(), bundle -> {
                  if (bundle.getString("book_id") != null) {
                      book_id = bundle.getString("book_id");
                  }
              });
            }
        });

        CategoryNames.getPic(billInfo.getCateName(), type, book_id,pic->{
            myBitmapUtils.disPlay(iv_category,pic);
        });

        iv_category.setColorFilter(getContext().getColor(R.color.darkGrey));

        Assets.getPic(billInfo.getAccountName(),asset2s -> {
            myBitmapUtils.disPlay(iv_account1,asset2s);
        });
        Assets.getPic(billInfo.getAccountName2(),asset2s -> {
            myBitmapUtils.disPlay(iv_account2,asset2s);
        });


        setVisible();


    }

   /* private void showMenu(String title, int type, Object[] list, ListFloat.Callback callBack) {

        ListFloat listFloat = new ListFloat(getContext());
        listFloat.setData(title, type, list, callBack);
        if (ScreenUtils.getScreenWidth() > ScreenUtils.getScreenHeight()) {
            listFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenHeight() - 100, ScreenUtils.getScreenWidth());
        } else {
            listFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() - 100);
        }
        listFloat.show();
    }*/


  /*  public void showInputDialog(String title, String def, InputFloat.Callback callBack) {
        InputFloat inputFloat = new InputFloat(getContext());
        inputFloat.setData(title, def, getContext().getString(R.string.set_cancel), getContext().getString(R.string.input_ok), null, callBack);
        if (ScreenUtils.getScreenWidth() > ScreenUtils.getScreenHeight()) {
            inputFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenHeight() - 100, ScreenUtils.getScreenWidth());
        } else {
            inputFloat.setWindowManagerParams(0, 0, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() - 100);
        }
        inputFloat.show();


    }*/

    @Override
    public void dismiss() {


        super.dismiss();

    }

    public void bindView(){
        layout_money=findViewById(R.id.layout_money);
        tv_money=findViewById(R.id.tv_money);
        ll_fee=findViewById(R.id.ll_fee);
        tv_fee=findViewById(R.id.tv_fee);
        ll_book=findViewById(R.id.ll_book);
        tv_book=findViewById(R.id.tv_book);
        ll_category=findViewById(R.id.ll_category);
        iv_category=findViewById(R.id.iv_category);
        tv_category=findViewById(R.id.tv_category);
        ll_type=findViewById(R.id.ll_type);
        tv_type=findViewById(R.id.tv_type);
        chip_bx=findViewById(R.id.chip_bx);
        ll_account1=findViewById(R.id.ll_account1);
        iv_account1=findViewById(R.id.iv_account1);
        tv_account1=findViewById(R.id.tv_account1);
        iv_account2=findViewById(R.id.iv_account2);
        ll_account2=findViewById(R.id.ll_account2);
        tv_account2=findViewById(R.id.tv_account2);
        ll_time=findViewById(R.id.ll_time);
        tv_time=findViewById(R.id.tv_time);
        ll_remark=findViewById(R.id.ll_remark);
        tv_remark=findViewById(R.id.tv_remark);
        button_fail=findViewById(R.id.button_fail);
        button_next=findViewById(R.id.button_next);

    }
    public void setVisible(){
        MMKV mmkv=MMKV.defaultMMKV();
        if(mmkv.getBoolean("layout_money",false)){
            layout_money.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_fee",false)){
            ll_fee.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_book",false)){
            ll_book.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_category",false)){
            ll_category.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_type",false)){
            ll_type.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_account1",false)){
            ll_account1.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_account2",false)){
            ll_account2.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_time",false)){
            ll_time.setVisibility(View.GONE);
        }else if(mmkv.getBoolean("ll_remark",false)){
            ll_remark.setVisibility(View.GONE);
        }
    }

    public void setCheckable(boolean check){


        if(check){
            chip_bx.setText("报销");
           chip_bx.setBackground(getContext().getDrawable(R.drawable.btn_normal_1));
           chip_bx.setTextColor(getContext().getColor(R.color.background_white));
        }else{
            chip_bx.setText("不报销");
            chip_bx.setBackground(getContext().getDrawable(R.drawable.btn_normal_2));
            chip_bx.setTextColor(getContext().getColor(R.color.button_go_setting_bg));
        }
    }
}
