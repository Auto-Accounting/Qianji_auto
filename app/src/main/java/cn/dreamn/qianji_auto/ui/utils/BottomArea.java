package cn.dreamn.qianji_auto.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONArray;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import net.ankio.timepicker.builder.TimePickerBuilder;
import net.ankio.timepicker.listener.OnTimeSelectListener;
import net.ankio.timepicker.view.TimePickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.ListArrayAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class BottomArea {
    //显示底部信息
    static public void msg(Context context, String title, String msg) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        dialog.message(null, msg, null);
        dialog.show();
    }

    //底部信息，带有开关
    static public void msg(Context context, String title, String msg, String rightName, String leftName, MsgCallback callback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextView textView_body = textEntryView.findViewById(R.id.textView_body);
        textView_body.setText(msg);


        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_next.setOnClickListener(v -> {
            callback.sure();
            dialog.dismiss();
        });
        button_last.setText(leftName);
        button_last.setOnClickListener(v -> {
            callback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);

        dialog.cornerRadius(15f, null);

        dialog.show();
    }

    //底部信息，带有开关
    static public void msg(Context context, String title, String msg, String rightName, String leftName, boolean isFloat, MsgCallback callback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextView textView_body = textEntryView.findViewById(R.id.textView_body);
        textView_body.setText(msg);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_next.setOnClickListener(v -> {
            callback.sure();
            dialog.dismiss();
        });
        button_last.setText(leftName);
        button_last.setOnClickListener(v -> {
            callback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);

        dialog.cornerRadius(15f, null);

        if (isFloat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
            } else {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            }
        }


        dialog.show();
    }

    static public void msgAuto(Context context, String title, String msg, String rightName, String leftName, MsgCallback callback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextView textView_body = textEntryView.findViewById(R.id.textView_body);
        textView_body.setText(msg);
        textView_body.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_next.setOnClickListener(v -> {
            callback.sure();
            dialog.dismiss();
        });
        button_last.setText(leftName);
        button_last.setOnClickListener(v -> {
            callback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);

        dialog.cornerRadius(15f, null);
        dialog.show();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ViewGroup.LayoutParams lp = textView_body.getLayoutParams();
                int height1 = textView_body.getHeight();
                int height = ScreenUtils.getScreenHeight(context);
                Log.d("屏幕高度：" + height);
                Log.d("recyclerView高度：" + height1);
                int height2 = height - ScreenUtils.dip2px(context, 200);//减去底部和顶部
                // Log.d("55dip："+ScreenUtils.dip2px(mContext,55));
                Log.d("计算最大限制高度：" + height2);
                if (height1 > height2) {
                    Log.d("超出高度：recyclerView高度" + height1 + "_计算:" + height2);
                    lp.height = height2;
                    textView_body.setLayoutParams(lp);
                }
            }
        });
    }

    static public void input(Context context, String title, String msg, String rightName, String leftName, InputCallback inputCallback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_input, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);
        md_input_message.setText(msg);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_last.setText(leftName);
        button_next.setOnClickListener(v2 -> {
            inputCallback.input(md_input_message.getText().toString());
            dialog.dismiss();
        });

        button_last.setOnClickListener(v2 -> {
            inputCallback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    static public void inputWithCheckBox(Context context, String title, String msg, String rightName, String leftName, String checkBoxText, boolean checked, InputWithCheckBoxCallback inputCallback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_input, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);
        md_input_message.setText(msg);

        MaterialCheckBox checkbox = textEntryView.findViewById(R.id.md_checkbox_prompt);
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setText(checkBoxText);
        checkbox.setChecked(checked);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_last.setText(leftName);
        button_next.setOnClickListener(v2 -> {
            inputCallback.inputWithCheck(md_input_message.getText().toString(), checkbox.isChecked());
            dialog.dismiss();
        });

        button_last.setOnClickListener(v2 -> {
            inputCallback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void list(Context context, String title, List<String> list, ListCallback listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index);
            return null;
        });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void list(Context context, String title, List<String> list, ListCallback2 listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index, text.toString());
            return null;
        });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void listLong(Context context, String title, List<String> list, ListCallback listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        bottomSheet.setRatio(1f);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index);
            return null;
        });
        dialog.show();
    }

    static public void list(Context context, String title, JSONArray list, ListCallback listCallback) {


        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_data, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);
        dialog.cornerRadius(15f, null);
        ListView listView = textEntryView.findViewById(R.id.list_view);
        ListArrayAdapter listAdapter = new ListArrayAdapter(context, R.layout.adapter_list_items2, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            listCallback.onSelect(position);
            dialog.dismiss();
        });


        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    static public void selectTime(Context context, boolean onlyTime, boolean isFloat, OnTimeSelectListener onTimeSelectListener) {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2010, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Integer.parseInt(DateUtils.getTime("yyyy")) + 2, 0, 1);
        boolean[] show = new boolean[]{true, true, true, true, true, false};
        if (onlyTime) {
            show = new boolean[]{false, false, false, true, true, false};
        }

        TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                onTimeSelectListener.onTimeSelect(date, v);
            }
        }).setType(show)// 默认全部显示
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(18)//标题文字大小
                .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色

                .setSubmitColor(context.getColor(R.color.blue))//确定按钮文字颜色
                .setCancelColor(context.getColor(R.color.blue))//取消按钮文字颜色
                //    .setTitleBgColor(context.getColor(R.color.background_gray))//标题背景颜色 Night mode
                .setBgColor(context.getColor(R.color.background_deep_gray))//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //  .isDialog(true)//是否显示为对话框样式
                .build(isFloat);
        pvTime.show();
    }

    public interface MsgCallback {
        void cancel();

        void sure();
    }

    public interface InputCallback {
        void input(String data);

        void cancel();
    }

    public interface InputWithCheckBoxCallback {
        void inputWithCheck(String data, boolean checked);

        void cancel();
    }

    public interface ListCallback {
        void onSelect(int position);
    }

    public interface ListCallback2 {
        void onSelect(int position, String text);
    }
    //
}
