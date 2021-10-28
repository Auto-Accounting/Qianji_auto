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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;

import net.ankio.timepicker.listener.OnTimeSelectListener;

import java.util.Arrays;
import java.util.Date;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.BillTools;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Helper.Assets;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.data.database.Helper.Categorys;
import cn.dreamn.qianji_auto.data.database.Table.BookName;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class AutoFloat {

    private Context context;
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
    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                setData(billInfo2);
            }
        }
    };
    private View mView;
    private MaterialDialog dialog;

    /**
     * 构造器
     *
     * @param context
     */
    public AutoFloat(Context context) {

        // super(context);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {

        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_NoActionBar);
        context = ctx;
        LayoutInflater factory = LayoutInflater.from(ctx);
        mView = factory.inflate(R.layout.float_edit, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        dialog = new MaterialDialog(context, bottomSheet);
        dialog.cancelable(false);
    }

    private Context getContext() {
        return context;
    }

    private void initData() {
        Log.i("初始化窗口");
        //  Caches.AddOrUpdate("float_lock", "true");
        bindView();
        setVisible();
        initListener();
    }


    @SuppressLint("CheckResult")
    protected void initListener() {
        ll_category.setOnClickListener(v -> {
            //分类选择
            Categorys.showCategorySelect(getContext(), context.getString(R.string.select_sort), book_id, billInfo2.getType(), true, obj -> {
                Bundle categoryNames = (Bundle) obj;
                if (categoryNames != null) {
                    billInfo2.setCateName(categoryNames.getString("name"));
                    mMainHandler.sendEmptyMessage(0);
                }
            });
        });
        ll_book.setOnClickListener(v -> {
            Log.i("账本选择");
            BookNames.showBookSelect(getContext(), context.getString(R.string.select_book), true, obj -> {
                Bundle bundle = (Bundle) obj;
                billInfo2.setBookName(bundle.getString("name"));
                book_id = bundle.getString("book_id");
                mMainHandler.sendEmptyMessage(0);
            });

        });
        ll_account1.setOnClickListener(v -> {
            Log.i("账户1选择");
            Assets.showAssetSelect(getContext(), context.getString(R.string.select_account), true, obj1 -> {
                Bundle asset2s = (Bundle) obj1;
                String asset1 = billInfo2.getAccountName();//原始资产
                Assets.isInAsset(asset1, obj -> {
                    Boolean bool = (Boolean) obj;
                    //如果原始资产不再已有范围之内
                    if (!bool) {
                        TaskThread.onThread(() -> {
                            Db.db.AssetMapDao().add(asset1, asset2s.getString("name"));
                        });
                    }
                });
                billInfo2.setAccountName(asset2s.getString("name"));
                mMainHandler.sendEmptyMessage(0);
            });

        });
        ll_account2.setOnClickListener(v -> {
            Log.i("账户2选择");
            Assets.showAssetSelect(getContext(), context.getString(R.string.select_account), true, obj1 -> {
                Bundle asset2s = (Bundle) obj1;
                String asset1 = billInfo2.getAccountName2();//原始资产
                Assets.isInAsset(asset1, obj -> {
                    Boolean bool = (Boolean) obj;
                    if (!bool) {
                        TaskThread.onThread(() -> {
                            Db.db.AssetMapDao().add(asset1, asset2s.getString("name"));
                        });
                    }
                });
                billInfo2.setAccountName2(asset2s.getString("name"));
                mMainHandler.sendEmptyMessage(0);
            });

        });
        layout_money.setOnClickListener(v -> {
            input(getContext().getString(R.string.input_money), billInfo2.getMoney(), new InputData() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOK(String data) {
                    billInfo2.setMoney(BillTools.getMoney(data));
                }
            });
        });
        ll_fee.setOnClickListener(v -> {
            input(getContext().getString(R.string.input_fee), billInfo2.getFee(), new InputData() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOK(String data) {
                    billInfo2.setFee(BillTools.getMoney(data));
                }
            });
        });
        ll_remark.setOnClickListener(v -> {
            Log.i("请输入备注信息");

            input(getContext().getString(R.string.select_remark), billInfo2.getRemark(), new InputData() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOK(String data) {
                    billInfo2.setRemark(data);
                }
            });

        });
        ll_time.setOnClickListener(v -> {
            Log.i("请修改时间信息");
            BottomArea.selectTime(getContext(), false, true, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    billInfo2.setTime(DateUtils.getTime("yyyy-MM-dd HH:mm:ss", date.getTime()));
                    mMainHandler.sendEmptyMessage(0);
                }
            });

        });
        ll_type.setOnClickListener(v -> {
            Log.i("请选择收支类型");

            String[] strings = {getContext().getString(R.string.float_out), getContext().getString(R.string.float_in), getContext().getString(R.string.float_transfer), getContext().getString(R.string.float_pay_for)};

            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.title(null, getContext().getString(R.string.select_type));


            DialogListExtKt.listItems(dialog, null, Arrays.asList(strings), null, true, (materialDialog, index, text) -> {
                billInfo2.setType(BillInfo.getTypeId(text.toString()));
                if (!text.toString().equals(getContext().getString(R.string.float_transfer)) && !text.toString().equals(getContext().getString(R.string.float_pay_for)))
                    billInfo2.setAccountName2(null);//不等于就去掉第二个账户


                mMainHandler.sendEmptyMessage(0);
                return null;
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
            } else {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            }

            dialog.cornerRadius(15f, null);
            dialog.show();

        });
        button_next.setOnClickListener(v -> {
            SendDataToApp.goApp(getContext(), billInfo2);
            this.clear();
        });
        button_fail.setOnClickListener(v -> this.clear());
        chip_bx.setOnClickListener(v -> {
            billInfo2.setRrimbursement(chip_bx.getText().toString().equals(context.getString(R.string.n_bx)));
            RegularCenter.getInstance("category").run(billInfo2, null, new TaskThread.TaskResult() {
                @Override
                public void onEnd(Object obj) {
                    String cate = (String) obj;
                    if (cate.equals("NotFound")) {
                        billInfo2.setCateName("其它");//设置自动分类
                    } else {
                        billInfo2.setCateName(cate);//设置自动分类
                    }
                    mMainHandler.sendEmptyMessage(0);
                }
            });

        });


    }

    public void input(String title, String defData, InputData inputData) {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.include_list_input, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.title(null, title);

        TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);

        md_input_message.setText(defData);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);

        button_next.setOnClickListener(v -> {
            inputData.onOK(md_input_message.getText().toString());
            //billInfo2.setRemark();
            mMainHandler.sendEmptyMessage(0);
            dialog.dismiss();
        });

        button_last.setOnClickListener(v -> {
            inputData.onClose();
            //billInfo2.setRemark();
            mMainHandler.sendEmptyMessage(0);
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        } else {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        }

        dialog.cornerRadius(15f, null);
        dialog.show();
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
        String remark = billInfo.getRemark();
        tv_remark.setText(remark);


        tv_money.setText(BillTools.getCustomBill(billInfo));
        tv_money.setTextColor(BillTools.getColor(getContext(), billInfo));


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
            chip_bx.setVisibility(View.INVISIBLE);
        } else {
            type = "0";
            chip_bx.setVisibility(View.VISIBLE);
        }
        if (billInfo.getType().equals(BillInfo.TYPE_PAY) || billInfo.getType().equals(BillInfo.TYPE_INCOME)) {
            ll_fee.setVisibility(View.GONE);
        } else {
            ll_fee.setVisibility(View.VISIBLE);
        }

        setVisible();

        TaskThread.onThread(() -> {
            BookName[] bookNames = Db.db.BookNameDao().getAll();
            if (bookNames.length != 0) {
                BookName[] bookName = Db.db.BookNameDao().get(billInfo.getBookName());
                book_id = bookName[0].book_id;
            }
        });


        Handler mHandler1 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                GlideLoadUtils.getInstance().glideLoad(getContext(), (String) msg.obj, iv_category, R.drawable.bg);
            }
        };
        Categorys.getPic(billInfo.getCateName(), type, book_id, pic -> {
            //  myBitmapUtils.disPlay(iv_category, pic);
            Message message = new Message();
            message.obj = pic;
            mHandler1.sendMessage(message);

        });

        iv_category.setColorFilter(getContext().getColor(R.color.darkGrey));
        Handler mHandler2 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                GlideLoadUtils.getInstance().glideLoad(getContext(), (String) msg.obj, iv_account1, R.drawable.bg);
            }
        };
        Assets.getPic(billInfo.getAccountName(), asset2s -> {
            //  myBitmapUtils.disPlay(iv_account1, asset2s);
            Message message = new Message();
            message.obj = asset2s;
            mHandler2.sendMessage(message);
        });
        Handler mHandler3 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                GlideLoadUtils.getInstance().glideLoad(getContext(), (String) msg.obj, iv_account2, R.drawable.bg);
            }
        };
        Assets.getPic(billInfo.getAccountName2(), asset2s -> {
            //  myBitmapUtils.disPlay(iv_account2, asset2s);
            Message message = new Message();
            message.obj = asset2s;
            mHandler3.sendMessage(message);
        });

        setVisible();
    }


    public void clear() {
        //  super.clear();
        mMainHandler.removeCallbacksAndMessages(null);
        this.dismiss();
    }

    public void bindView() {
        layout_money = mView.findViewById(R.id.layout_money);
        tv_money = mView.findViewById(R.id.tv_money);
        ll_fee = mView.findViewById(R.id.ll_fee);
        tv_fee = mView.findViewById(R.id.tv_fee);
        ll_book = mView.findViewById(R.id.ll_book);
        tv_book = mView.findViewById(R.id.tv_book);
        ll_category = mView.findViewById(R.id.ll_category);
        iv_category = mView.findViewById(R.id.iv_category);
        tv_category = mView.findViewById(R.id.tv_category);
        ll_type = mView.findViewById(R.id.ll_type);
        tv_type = mView.findViewById(R.id.tv_type);
        chip_bx = mView.findViewById(R.id.chip_bx);
        ll_account1 = mView.findViewById(R.id.ll_account1);
        iv_account1 = mView.findViewById(R.id.iv_account1);
        tv_account1 = mView.findViewById(R.id.tv_account1);
        iv_account2 = mView.findViewById(R.id.iv_account2);
        ll_account2 = mView.findViewById(R.id.ll_account2);
        tv_account2 = mView.findViewById(R.id.tv_account2);
        ll_time = mView.findViewById(R.id.ll_time);
        tv_time = mView.findViewById(R.id.tv_time);
        ll_remark = mView.findViewById(R.id.ll_remark);
        tv_remark = mView.findViewById(R.id.tv_remark);
        button_fail = mView.findViewById(R.id.button_last);
        button_next = mView.findViewById(R.id.button_next);

    }


    public void dismiss() {
        dialog.dismiss();

    }

    public void show() {
        DialogCustomViewExtKt.customView(dialog, null, mView,
                false, true, false, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        } else {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        }
        dialog.cornerRadius(15f, null);
        dialog.show();

    }

    public void setVisible() {
        MMKV mmkv = MMKV.defaultMMKV();

        if (mmkv.getBoolean("lazy_mode", true)) {
            ll_fee.setVisibility(View.GONE);
            ll_book.setVisibility(View.GONE);
            ll_category.setVisibility(View.GONE);
            ll_account1.setVisibility(View.GONE);
            ll_account2.setVisibility(View.GONE);
            ll_time.setVisibility(View.GONE);
        } else {
            if (!mmkv.getBoolean("auto_style", true)) {
                ll_category.setVisibility(View.GONE);
            }

            if (mmkv.getBoolean("layout_money", false)) {
                layout_money.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_fee", false)) {
                ll_fee.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_book", false)) {
                ll_book.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_category", false)) {
                ll_category.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_type", false)) {
                ll_type.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_account1", false)) {
                ll_account1.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_account2", false)) {
                ll_account2.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_time", false)) {
                ll_time.setVisibility(View.GONE);
            } else if (mmkv.getBoolean("ll_remark", false)) {
                ll_remark.setVisibility(View.GONE);
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setCheckable(boolean check) {
        //TODO 4.0新增 报销,此处需修改
        if (check) {
            chip_bx.setText(context.getString(R.string.bx));
            chip_bx.setBackground(getContext().getDrawable(R.drawable.btn_normal_1));
            chip_bx.setTextColor(getContext().getColor(R.color.background_white));
        } else {
            chip_bx.setText(context.getString(R.string.n_bx));
            chip_bx.setBackground(getContext().getDrawable(R.drawable.btn_normal_2));
            chip_bx.setTextColor(getContext().getColor(R.color.button_go_setting_bg));
        }
    }

    public interface InputData {
        void onClose();
        void onOK(String data);
    }
}
