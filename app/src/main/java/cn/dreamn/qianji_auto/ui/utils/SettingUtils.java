package cn.dreamn.qianji_auto.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.bills.Remark;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.activity.LockActivity;
import cn.dreamn.qianji_auto.ui.components.LineLay;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.SecurityAccess;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class SettingUtils {
    Context mContext;
    MMKV mmkv;
    LineLay set_app;
    LineLay set_need_cate;

    LineLay set_lazy_mode;
    LineLay set_show_qianji_result;
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
    LineLay set_lock_style;
    LineLay set_lock_qianji_style;

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
            LineLay set_app,
            LineLay set_need_cate,
            LineLay set_lazy_mode,
            LineLay set_show_qianji_result,
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
            TextView tv3,
            LineLay set_lock_style,
            LineLay set_lock_qianji_style
    ) {
        this.set_lock_style = set_lock_style;
        this.set_lock_qianji_style = set_lock_qianji_style;
        this.set_app = set_app;
        this.set_need_cate = set_need_cate;
        this.set_lazy_mode = set_lazy_mode;
        this.set_show_qianji_result = set_show_qianji_result;
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

        if (!AppStatus.isXposed()) {//无障碍不支持
            set_lock_qianji_style.setVisibility(View.GONE);
        }

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
        set_show_qianji_result.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_show_qianji_result), jsonArray.getJSONArray(9), position -> {
                mmkv.encode("show_qianji_result", position == 0);
                initUi();
            });
        });
        set_need_cate.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_need_cate), jsonArray.getJSONArray(7), position -> {
                mmkv.encode("need_cate", position == 0);
                initUi();
            });
        });
        set_app.setOnClickListener(view -> {
            BottomArea.list(mContext, mContext.getString(R.string.set_app), jsonArray.getJSONArray(6), position -> {
                AppManager.setApp(jsonArray.getJSONArray(6).getJSONObject(position).getString("value"));
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
            BookNames.showBookSelect(mContext, mContext.getString(R.string.set_choose_book), false, obj -> {
                Bundle bundle = (Bundle) obj;
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


        set_lock_style.setOnClickListener(view -> {
            if (mmkv.getInt("lock_style", SecurityAccess.LOCK_NONE) != SecurityAccess.LOCK_NONE) {
                App.isLock = true;
                Intent intent1 = new Intent(mContext, LockActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
            }
            BottomArea.list(mContext, "", jsonArray.getJSONArray(8), new BottomArea.ListCallback() {
                @Override
                public void onSelect(int position) {
                    mmkv.encode("lock_style", position);
                    initUi();
                }
            });
        });
        set_lock_qianji_style.setOnClickListener(view -> {
            if (mmkv.getInt("lock_qianji_style", SecurityAccess.LOCK_NONE) != SecurityAccess.LOCK_NONE) {
                App.isLock = true;
                Intent intent1 = new Intent(mContext, LockActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
            }
            BottomArea.list(mContext, "", jsonArray.getJSONArray(8), new BottomArea.ListCallback() {
                @Override
                public void onSelect(int position) {
                    mmkv.encode("lock_qianji_style", position);

                    SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(mContext, "apps", Context.MODE_PRIVATE);

                    sharedPreferences.edit().putString("lock_qianji_style", String.valueOf(position)).apply();

                    initUi();
                }
            });
        });
    }


    @SuppressLint("StringFormatMatches")
    private void initUi() {
        set_app.setVisibility(View.VISIBLE);
        set_need_cate.setVisibility(View.VISIBLE);
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
        set_app.setValue(AppManager.getAppInfo(mContext).getString("appName"));

        if (mmkv.getBoolean("show_qianji_result", true)) {
            set_show_qianji_result.setValue(R.string.set_open);
        } else {
            set_show_qianji_result.setValue(R.string.set_close);
        }

        if (mmkv.getBoolean("need_cate", true)) {
            set_need_cate.setValue(R.string.set_need_cate_y);
        } else {
            set_need_cate.setValue(R.string.set_need_cate_n);
        }

        if (mmkv.getInt("lock_style", SecurityAccess.LOCK_NONE) == SecurityAccess.LOCK_NONE) {
            set_lock_style.setValue(R.string.set_lock_no);
        } else {
            set_lock_style.setValue(R.string.set_lock);
        }

        if (mmkv.getInt("lock_qianji_style", SecurityAccess.LOCK_NONE) == SecurityAccess.LOCK_NONE) {
            set_lock_qianji_style.setValue(R.string.set_lock_no);
        } else {
            set_lock_qianji_style.setValue(R.string.set_lock);
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
