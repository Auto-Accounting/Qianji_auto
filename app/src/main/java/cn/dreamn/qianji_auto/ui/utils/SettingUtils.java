package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.Remark;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.views.SuperText;

public class SettingUtils {
    Context mContext;
    MMKV mmkv;


    SuperText pay_all;
    SuperText pay_half;

    SuperText income_all;
    SuperText income_half;

    SuperText default_bookName;

    SuperText remark;

    SuperText auto_sort_open;
    SuperText auto_sort_close;

    SuperText count_down;
    SeekBar count_down_seekbar;

    SuperText click_window_record;
    SuperText click_window_edit;
    SuperText click_window_close;

    SuperText long_click_window_record;
    SuperText long_click_window_edit;
    SuperText long_click_window_close;

    SuperText end_window_record;
    SuperText end_window_edit;
    SuperText end_window_close;

    SuperText notice_click_window_record;
    SuperText notice_click_window_edit;
    SuperText notice_click_window_close;

    SuperText qianji_auto;
    SuperText qianji_ui;

    SuperText lazy_mode_open;
    SuperText lazy_mode_close;

    public SettingUtils(Context context) {
        mContext = context;
        mmkv = MMKV.defaultMMKV();
    }

    public void init(
            SuperText pay_all,
            SuperText pay_half,

            SuperText income_all,
            SuperText income_half,

            SuperText default_bookName,

            SuperText remark,

            SuperText auto_sort_open,
            SuperText auto_sort_close,

            SuperText count_down,
            SeekBar count_down_seekbar,

            SuperText click_window_record,
            SuperText click_window_edit,
            SuperText click_window_close,

            SuperText long_click_window_record,
            SuperText long_click_window_edit,
            SuperText long_click_window_close,

            SuperText end_window_record,
            SuperText end_window_edit,
            SuperText end_window_close,

            SuperText notice_click_window_record,
            SuperText notice_click_window_edit,
            SuperText notice_click_window_close,

            SuperText qianji_auto,
            SuperText qianji_ui,
            SuperText lazy_mode_open,
            SuperText lazy_mode_close
    ){
        this.pay_all=pay_all;
        this.pay_half=pay_half;

        this.income_all=income_all;
        this.income_half=income_half;

        this.default_bookName=default_bookName;

        this.remark=remark;

        this.auto_sort_open=auto_sort_open;
        this.auto_sort_close=auto_sort_close;

        this.count_down=count_down;
        this.count_down_seekbar=count_down_seekbar;

        this.click_window_record=click_window_record;
        this.click_window_edit=click_window_edit;
        this.click_window_close=click_window_close;

        this.long_click_window_record=long_click_window_record;
        this.long_click_window_edit=long_click_window_edit;
        this.long_click_window_close=long_click_window_close;

        this.end_window_record=end_window_record;
        this.end_window_edit = end_window_edit;
        this.end_window_close = end_window_close;

        this.notice_click_window_record = notice_click_window_record;
        this.notice_click_window_edit = notice_click_window_edit;
        this.notice_click_window_close = notice_click_window_close;

        this.qianji_auto = qianji_auto;
        this.qianji_ui = qianji_ui;

        this.lazy_mode_close = lazy_mode_close;
        this.lazy_mode_open = lazy_mode_open;
        initUi();
        initListen();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }

    private void initListen(){

        pay_all.setOnClickListener(v->{
            mmkv.encode("autoPay", true);
            initUi();
        });
        pay_half.setOnClickListener(v->{
            mmkv.encode("autoPay", false);
            initUi();
        });
        income_all.setOnClickListener(v->{
            mmkv.encode("autoIncome", true);
            initUi();
        });
        income_half.setOnClickListener(v->{
            mmkv.encode("autoIncome", false);
            initUi();
        });
        default_bookName.setOnClickListener(v-> {
            BookNames.showBookSelect(mContext, "请选择账本", false, new BookNames.BookSelect() {
                @Override
                public void onSelect(Bundle bundle) {
                    BookNames.change(bundle.getString("name"));
                    initUi();
                }
            });
        });
        remark.setOnClickListener(v-> {
            LayoutInflater factory = LayoutInflater.from(mContext);
            final View textEntryView = factory.inflate(R.layout.list_input, null);
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(mContext, bottomSheet);
            dialog.title(null, "请输入备注格式");

            TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);

            md_input_message.setText(Remark.getRemarkTpl());

            Button button_next = textEntryView.findViewById(R.id.button_next);
            Button button_last = textEntryView.findViewById(R.id.button_last);

            button_next.setOnClickListener(v2 -> {
                Remark.setTpl(md_input_message.getText().toString());
                initUi();
                dialog.dismiss();

            });

            button_last.setOnClickListener(v2 -> {
                dialog.dismiss();
            });

            DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                    false, true, false, false);

            dialog.cornerRadius(15f, null);
            dialog.show();
        });
        auto_sort_open.setOnClickListener(v->{
            mmkv.encode("auto_sort", true);
            initUi();
        });
        auto_sort_close.setOnClickListener(v->{
            mmkv.encode("auto_sort", false);
            initUi();
        });

        count_down.setOnClickListener(v->{
            //好像没什么用....
        });

        count_down_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               //count_down.setTitle("倒计时时间："+progress+"秒");
                mmkv.encode("auto_timeout", progress);
                initUi();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        click_window_record.setOnClickListener(v->{
            mmkv.encode("click_window","record");
            initUi();
        });
        click_window_edit.setOnClickListener(v->{
            mmkv.encode("click_window","edit");
            initUi();
        });
        click_window_close.setOnClickListener(v->{
            mmkv.encode("click_window","close");
            initUi();
        });

        long_click_window_record.setOnClickListener(v->{
            mmkv.encode("long_click_window","record");
            initUi();
        });
        long_click_window_edit.setOnClickListener(v->{
            mmkv.encode("long_click_window","edit");
            initUi();
        });
        long_click_window_close.setOnClickListener(v->{
            mmkv.encode("long_click_window","close");
            initUi();
        });

        end_window_record.setOnClickListener(v->{
            mmkv.encode("end_window","record");
            initUi();
        });
        end_window_edit.setOnClickListener(v->{
            mmkv.encode("end_window","edit");
            initUi();
        });
        end_window_close.setOnClickListener(v->{
            mmkv.encode("end_window","close");
            initUi();
        });

        notice_click_window_record.setOnClickListener(v->{
            mmkv.encode("notice_click_window","record");
            initUi();
        });
        notice_click_window_edit.setOnClickListener(v->{
            mmkv.encode("notice_click_window","edit");
            initUi();
        });
        notice_click_window_close.setOnClickListener(v->{
            mmkv.encode("notice_click_window","close");
            initUi();
        });

        qianji_auto.setOnClickListener(v -> {
            mmkv.encode("auto_cate_table", true);
            initUi();
        });
        qianji_ui.setOnClickListener(v -> {
            mmkv.encode("auto_cate_table", false);
            initUi();
        });


        lazy_mode_open.setOnClickListener(v -> {
            mmkv.encode("lazy_mode", true);
            initUi();
        });

        lazy_mode_close.setOnClickListener(v -> {
            mmkv.encode("lazy_mode", false);
            initUi();
        });


    }


    private void initUi() {

        if (mmkv.getBoolean("lazy_mode", true)) {
            lazy_mode_open.setSelect(true);
            lazy_mode_close.setSelect(false);
        } else {
            lazy_mode_open.setSelect(false);
            lazy_mode_close.setSelect(true);
        }

        if (mmkv.getBoolean("autoPay", false)) {
            pay_all.setSelect(true);
            pay_half.setSelect(false);
        } else {
            pay_all.setSelect(false);
            pay_half.setSelect(true);
        }

        if (mmkv.getBoolean("autoIncome", false)) {
            income_all.setSelect(true);
            income_half.setSelect(false);
        } else {
            income_all.setSelect(false);
            income_half.setSelect(true);
        }

        default_bookName.setTitle(BookNames.getDefault());
        remark.setTitle(Remark.getRemarkTpl());

        if (mmkv.getBoolean("auto_sort", true)) {
            auto_sort_open.setSelect(true);
            auto_sort_close.setSelect(false);
        } else {
            auto_sort_open.setSelect(false);
            auto_sort_close.setSelect(true);
        }
        int time = mmkv.getInt("auto_timeout", 10);
        count_down.setTitle("倒计时时间：" + time + "秒");
        count_down_seekbar.setProgress(time);

        switch (mmkv.getString("click_window","edit")){
            case "edit":
                click_window_record.setSelect(false);
                click_window_close.setSelect(false);
                click_window_edit.setSelect(true);
                break;
            case "record":
                click_window_record.setSelect(true);
                click_window_close.setSelect(false);
                click_window_edit.setSelect(false);
                break;
            case "close":
                click_window_record.setSelect(false);
                click_window_close.setSelect(true);
                click_window_edit.setSelect(false);
                break;
        }

        switch (mmkv.getString("long_click_window","edit")){
            case "edit":
                long_click_window_record.setSelect(false);
                long_click_window_close.setSelect(false);
                long_click_window_edit.setSelect(true);
                break;
            case "record":
                long_click_window_record.setSelect(true);
                long_click_window_close.setSelect(false);
                long_click_window_edit.setSelect(false);
                break;
            case "close":
                long_click_window_record.setSelect(false);
                long_click_window_close.setSelect(true);
                long_click_window_edit.setSelect(false);
                break;
        }

        switch (mmkv.getString("end_window","edit")){
            case "edit":
                end_window_record.setSelect(false);
                end_window_close.setSelect(false);
                end_window_edit.setSelect(true);
                break;
            case "record":
                end_window_record.setSelect(true);
                end_window_close.setSelect(false);
                end_window_edit.setSelect(false);
                break;
            case "close":
                end_window_record.setSelect(false);
                end_window_close.setSelect(true);
                end_window_edit.setSelect(false);
                break;
        }

        switch (mmkv.getString("notice_click_window","edit")){
            case "edit":
                notice_click_window_record.setSelect(false);
                notice_click_window_close.setSelect(false);
                notice_click_window_edit.setSelect(true);
                break;
            case "record":
                notice_click_window_record.setSelect(true);
                notice_click_window_close.setSelect(false);
                notice_click_window_edit.setSelect(false);
                break;
            case "close":
                notice_click_window_record.setSelect(false);
                notice_click_window_close.setSelect(true);
                notice_click_window_edit.setSelect(false);
                break;
        }

        if(mmkv.getBoolean("auto_cate_table",true)){
            qianji_auto.setSelect(true);
            qianji_ui.setSelect(false);
        }else{
            qianji_auto.setSelect(false);
            qianji_ui.setSelect(true);
        }

    }
}
