package cn.dreamn.qianji_auto.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.Remark;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.components.LineLay;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class SettingUtils {
    Context mContext;
    MMKV mmkv;

    LineLay set_lazy_mode;
    LineLay set_front;
    LineLay set_back;
    LineLay set_default;
    LineLay set_remark;
    LineLay set_sort;
    LineLay set_float_time;
    LineLay set_float_click;
    LineLay set_float_long_click;
    LineLay set_float_time_end;
    LineLay set_notice_click;
    LineLay set_float_style;

    TextView tv1;
    TextView tv2;
    TextView tv3;

    JSONArray jsonArray;

    public SettingUtils(Context context) {
        mContext = context;
        mmkv = MMKV.defaultMMKV();
        jsonArray = JSONArray.parseArray(Tool.getJson(mContext, "Settings"));
    }

    public void init(
            LineLay set_lazy_mode,
            LineLay set_front,
            LineLay set_back,
            LineLay set_default,
            LineLay set_remark,
            LineLay set_sort,
            LineLay set_float_time,
            LineLay set_float_click,
            LineLay set_float_long_click,
            LineLay set_float_time_end,
            LineLay set_notice_click,
            LineLay set_float_style,
            TextView tv1,
            TextView tv2,
            TextView tv3
    ) {
        this.set_lazy_mode = set_lazy_mode;
        this.set_front = set_front;
        this.set_back = set_back;
        this.set_default = set_default;
        this.set_remark = set_remark;
        this.set_sort = set_sort;
        this.set_float_time = set_float_time;
        this.set_float_click = set_float_click;
        this.set_float_long_click = set_float_long_click;
        this.set_float_time_end = set_float_time_end;
        this.set_notice_click = set_notice_click;
        this.set_float_style = set_float_style;
        this.tv1 = tv1;
        this.tv2 = tv2;
        this.tv3 = tv3;
        initUi();
        initListen();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }

    private void initListen() {
        set_lazy_mode.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_lazy_mode), jsonArray.getJSONArray(0), position -> {
                mmkv.encode("lazy_mode", position == 0);
                initUi();
            });
        });
        set_front.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_front), jsonArray.getJSONArray(1), position -> {
                mmkv.encode("autoPay", position == 0);
                initUi();
            });
        });
        set_back.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_back), jsonArray.getJSONArray(2), position -> {
                mmkv.encode("autoIncome", position == 0);
                initUi();
            });
        });

        set_default.setOnClickListener(v -> {
            BookNames.showBookSelect(mContext, mContext.getString(R.string.set_choose_book), false, bundle -> {
                BookNames.change(bundle.getString("name"));
                initUi();
            });
        });
        set_remark.setOnClickListener(v -> {
            BottomArea.input(mContext, mContext.getString(R.string.set_remark_tip), Remark.getRemarkTpl(), mContext.getString(R.string.set_sure), mContext.getString(R.string.set_cancle), new BottomArea.InputCallback() {
                @Override
                public void input(String data) {
                    Remark.setTpl(data);
                    initUi();
                }

                @Override
                public void cancel() {

                }
            });
        });

        set_sort.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_sort), jsonArray.getJSONArray(3), position -> {
                mmkv.encode("auto_sort", position == 0);
                initUi();
            });
        });

        set_float_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.input(
                        mContext,
                        mContext.getString(R.string.set_float_time),
                        String.valueOf(mmkv.getInt("auto_timeout", 10)),
                        mContext.getString(R.string.set_sure), mContext.getString(R.string.set_cancle), new BottomArea.InputCallback() {
                            @Override
                            public void input(String data) {
                                try {
                                    int time = Integer.parseInt(data);
                                    if (time < 0)
                                        throw new Exception("must >= 0");
                                    mmkv.encode("auto_timeout", time);
                                    initUi();

                                } catch (Throwable e) {
                                    ToastUtils.show(R.string.time_error);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        });
            }
        });


        set_float_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(mContext, mContext.getString(R.string.set_float_click), jsonArray.getJSONArray(4), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {
                        mmkv.encode("click_window", jsonArray.getJSONArray(4).getJSONObject(position).getString("value"));

                        initUi();
                    }
                });
            }
        });

        set_float_long_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(mContext, mContext.getString(R.string.set_float_long_click), jsonArray.getJSONArray(4), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {

                        mmkv.encode("long_click_window", jsonArray.getJSONArray(4).getJSONObject(position).getString("value"));

                        initUi();
                    }
                });
            }
        });


        set_float_time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(mContext, mContext.getString(R.string.set_float_time_end), jsonArray.getJSONArray(4), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {

                        mmkv.encode("end_window", jsonArray.getJSONArray(4).getJSONObject(position).getString("value"));

                        initUi();
                    }
                });
            }
        });

        set_notice_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(mContext, mContext.getString(R.string.set_notice_click), jsonArray.getJSONArray(4), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {
                        mmkv.encode("notice_click_window", jsonArray.getJSONArray(4).getJSONObject(position).getString("value"));

                        initUi();
                    }
                });
            }
        });

        set_float_style.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_float_style), jsonArray.getJSONArray(5), new BottomArea.ListCallback() {
                @Override
                public void onSelect(int position) {
                    mmkv.encode("auto_style", position == 0);
                    initUi();
                }
            });
        });


    }


    @SuppressLint("StringFormatMatches")
    private void initUi() {

        if (mmkv.getBoolean("lazy_mode", true)) {
            set_lazy_mode.setValue(R.string.set_open);

            set_front.setVisibility(View.GONE);
            set_back.setVisibility(View.GONE);
            set_default.setVisibility(View.GONE);
            set_remark.setVisibility(View.GONE);
            set_sort.setVisibility(View.GONE);
            set_float_time.setVisibility(View.GONE);
            set_float_click.setVisibility(View.GONE);
            set_float_long_click.setVisibility(View.GONE);
            set_float_time_end.setVisibility(View.GONE);
            set_notice_click.setVisibility(View.GONE);
            set_float_style.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);

        } else {
            set_lazy_mode.setValue(R.string.set_close);
            set_front.setVisibility(View.VISIBLE);
            set_back.setVisibility(View.VISIBLE);
            set_default.setVisibility(View.VISIBLE);
            set_remark.setVisibility(View.VISIBLE);
            set_sort.setVisibility(View.VISIBLE);
            set_float_time.setVisibility(View.VISIBLE);
            set_float_click.setVisibility(View.VISIBLE);
            set_float_long_click.setVisibility(View.VISIBLE);
            set_float_time_end.setVisibility(View.VISIBLE);
            set_notice_click.setVisibility(View.VISIBLE);
            set_float_style.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
        }

        if (mmkv.getBoolean("autoPay", false)) {
            set_front.setValue(R.string.set_all);
        } else {
            set_front.setValue(R.string.set_half);
        }

        if (mmkv.getBoolean("autoIncome", false)) {
            set_back.setValue(R.string.set_all);
        } else {
            set_back.setValue(R.string.set_half);
        }

        set_default.setValue(BookNames.getDefault());
        set_remark.setValue(Remark.getRemarkTpl());

        if (mmkv.getBoolean("auto_sort", false)) {
            set_sort.setValue(R.string.set_open);
        } else {
            set_sort.setValue(R.string.set_close);
        }
        int time = mmkv.getInt("auto_timeout", 10);
        set_float_time.setValue(String.format(mContext.getString(R.string.set_time), time));

        switch (mmkv.getString("click_window", "edit")) {
            case "edit":
                set_float_click.setValue(R.string.set_click_double);
                break;
            case "record":
                set_float_click.setValue(R.string.set_click_book);
                break;
            case "close":
                set_float_click.setValue(R.string.set_click_close);
                break;
        }

        switch (mmkv.getString("long_click_window","edit")){
            case "edit":
                set_float_long_click.setValue(R.string.set_click_double);
                break;
            case "record":
                set_float_long_click.setValue(R.string.set_click_book);
                break;
            case "close":
                set_float_long_click.setValue(R.string.set_click_close);
                break;
        }

        switch (mmkv.getString("end_window","edit")){
            case "edit":
                set_float_time_end.setValue(R.string.set_click_double);
                break;
            case "record":
                set_float_time_end.setValue(R.string.set_click_book);
                break;
            case "close":
                set_float_time_end.setValue(R.string.set_click_close);
                break;
        }

        switch (mmkv.getString("notice_click_window","edit")){
            case "edit":
                set_notice_click.setValue(R.string.set_click_double);
                break;
            case "record":
                set_notice_click.setValue(R.string.set_click_book);
                break;
            case "close":
                set_notice_click.setValue(R.string.set_click_close);
                break;
        }

        if (mmkv.getBoolean("auto_style", true)) {
            set_float_style.setValue(R.string.set_auto);
        } else {
            set_float_style.setValue(R.string.set_qianji);
        }

    }
}
