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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.app.AppManager;
import cn.dreamn.qianji_auto.core.broadcast.AppBroadcast;
import cn.dreamn.qianji_auto.data.data.Data;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.data.local.FileUtils;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.setting.AppStatus;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.floats.AutoBills;
import cn.dreamn.qianji_auto.ui.fragment.about.AboutFragment;
import cn.dreamn.qianji_auto.ui.fragment.about.BackUpFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainMapFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainModeFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.MainSetFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.cards.MainCardFragment;
import cn.dreamn.qianji_auto.ui.fragment.base.sorts.MainSortFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.AppFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.LogFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.MoneyFragment;
import cn.dreamn.qianji_auto.ui.fragment.data.regulars.MainAutoLearnFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.files.BackupManager;
import cn.dreamn.qianji_auto.utils.files.RegularManager;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.GlideLoadUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.UpdateUtils;


@Page(name = "自动记账", anim = CoreAnim.fade)
public class MainFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    RelativeLayout title_bar;

    @BindView(R.id.linelayout)
    RelativeLayout linearLayout;
    @BindView(R.id.title_count)
    RelativeLayout title_count;



    @BindView(R.id.rl_set)
    RelativeLayout rl_set;

    @BindView(R.id.rl_asset)
    RelativeLayout rl_asset;

    @BindView(R.id.rl_map)
    RelativeLayout rl_map;

    @BindView(R.id.rl_sort)
    RelativeLayout rl_sort;

    @BindView(R.id.rl_async)
    RelativeLayout rl_async;

    @BindView(R.id.rl_bill)
    RelativeLayout rl_bill;

    @BindView(R.id.rl_bill_check)
    RelativeLayout rl_bill_check;

    @BindView(R.id.rl_year)
    RelativeLayout rl_year;

    @BindView(R.id.rl_app_log)
    RelativeLayout rl_app_log;

    @BindView(R.id.rl_sms_log)
    RelativeLayout rl_sms_log;

    @BindView(R.id.rl_notice_log)
    RelativeLayout rl_notice_log;

    @BindView(R.id.rl_log)
    RelativeLayout rl_log;

    @BindView(R.id.rl_auto_sort)
    RelativeLayout rl_auto_sort;
    @BindView(R.id.rl_app)
    RelativeLayout rl_app;
    @BindView(R.id.rl_sms)
    RelativeLayout rl_sms;
    @BindView(R.id.rl_notice)
    RelativeLayout rl_notice;

    @BindView(R.id.rl_skin)
    RelativeLayout rl_skin;

    @BindView(R.id.rl_backup)
    RelativeLayout rl_backup;
    @BindView(R.id.rl_text_teach)
    RelativeLayout rl_text_teach;
    @BindView(R.id.rl_video_teach)
    RelativeLayout rl_video_teach;
    @BindView(R.id.rl_github)
    RelativeLayout rl_github;
    @BindView(R.id.rl_about)
    RelativeLayout rl_about;

    @BindView(R.id.mode_select1)
    IconView mode_select1;
    @BindView(R.id.mode_select2)
    RelativeLayout mode_select2;
    @BindView(R.id.active_status)
    TextView active_status;
    @BindView(R.id.app_log)
    TextView app_log;

    @BindView(R.id.book_img)
    ImageView book_img;
    @BindView(R.id.default_book)
    TextView default_book;

    @BindView(R.id.debug_flag)
    TextView debug_flag;

    @BindView(R.id.view_headImg)
    View view_headImg;

    LoadingDialog loadingDialog;
    BroadcastReceiver broadcastReceiver;
    private void restore() {
        Bundle bundle = getArguments();
        setArguments(null);
        if (bundle == null) return;
        String str = bundle.getString("url");
        if (str != null) {
            PermissionUtils permissionUtils = new PermissionUtils(getContext());
            permissionUtils.grant(PermissionUtils.Storage);
            if (permissionUtils.isGrant(PermissionUtils.Storage).equals("0")) {
                ToastUtils.show(R.string.restore_permission);
                return;
            }
            Log.i("恢复路径：" + str);
            str = str.replace("/external_files", "");
            if (str.endsWith("auto.backup")) {
                loadingDialog = new LoadingDialog(getContext(), getString(R.string.restore_loading));
                loadingDialog.show();
                BackupManager.init(getContext());
                BackupManager.restoreFromLocal(str, getContext(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        loadingDialog.close();
                        if (msg.what == 0) {
                            ToastUtils.show(R.string.restore_success);
                        } else {
                            ToastUtils.show(R.string.restore_error);
                        }
                    }
                });
            } else if (str.endsWith(".backup_category_ankio")) {
                RegularManager.restoreFromData(getContext(), "", "category", FileUtils.get(str), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {

                    }
                });
            } else if (str.endsWith(".backup_app_ankio")) {
                Log.i("file" + str);
                RegularManager.restoreFromData(getContext(), "", "app", FileUtils.get(str), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {

                    }
                });
            } else if (str.endsWith(".backup_helper_ankio")) {
                Log.i("file" + str);
                RegularManager.restoreFromData(getContext(), "", "helper", FileUtils.get(str), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {

                    }
                });
            } else if (str.endsWith(".backup_sms_ankio")) {
                RegularManager.restoreFromData(getContext(), "", "sms", FileUtils.get(str), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {

                    }
                });
            } else if (str.endsWith(".backup_notice_ankio")) {
                RegularManager.restoreFromData(getContext(), "", "notice", FileUtils.get(str), new RegularManager.End() {
                    @Override
                    public void onFinish(int code) {

                    }
                });
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected View getBarView() {
        return title_count;
    }

    @Override
    protected void initViews() {
        app_log.setText(BuildConfig.VERSION_NAME);
        UpdateUtils.checkUpdate(getContext(), () -> Log.i("无更新"));
        Data.init(getContext());
        Log.init("自动记账:MainFragment");
    }


    @Override
    public void onResume() {
        super.onResume();
        setActive();
        try {
            restore();
        } catch (Throwable e) {
            Log.i("数据恢复过程中发生错误！" + e.toString());
            ToastUtils.show(R.string.restore_error_2);
        }
    }

    @Override
    protected void initListeners() {
        book_img.setOnClickListener(v -> {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    setHeadImg();
                }
            };
            BookNames.showBookSelect(getContext(), getString(R.string.set_choose_book), false, obj -> {
                Bundle bundle = (Bundle) obj;
                BookNames.change(bundle.getString("name"));
                HandlerUtil.send(mHandler, HandlerUtil.HANDLE_OK);
            });
        });
        mode_select1.setOnClickListener(v -> openNewPage(MonitorFragment.class));
        mode_select2.setOnClickListener(v -> openNewPage(MainModeFragment.class));
        initGridLayout();
    }


    private void setAction() {
        if (!AppStatus.isXposed()) return;
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getBoolean("yearBill", false))
            return;
        if (DateUtils.afterDay("yyyy-MM-dd", "2021-12-31")) {
            mmkv.encode("yearBill", true);
            BottomArea.msg(getContext(), "新年好", "您有一份来自2021年的年度账单，需要查看吗？", "需要", "关闭", new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    rl_year.performClick();
                }
            });
        }
    }

    private void setActive() {

        if (AppStatus.isActive(getContext())) {
            mode_select2.setBackgroundColor(ThemeManager.getColor(getActivity(), R.color.button_go_setting_bg));
            mode_select2.setBackgroundTintList(ColorStateList.valueOf(ThemeManager.getColor(getActivity(), R.color.button_go_setting_bg)));
            active_status.setText(String.format(getString(R.string.active_true), AppStatus.getFrameWork(getContext())));

        } else {
            mode_select2.setBackgroundColor(ThemeManager.getColor(getActivity(), R.color.disable_bg));
            mode_select2.setBackgroundTintList(ColorStateList.valueOf(ThemeManager.getColor(getActivity(), R.color.disable_bg)));
            active_status.setText(String.format(getString(R.string.active_false), AppStatus.getFrameWork(getContext())));

        }
        active_status.setTextColor(ThemeManager.getColor(getActivity(), R.color.background_white));
        setHeadImg();

        if (AppStatus.isDebug(getContext())) {
            debug_flag.setVisibility(View.VISIBLE);
        } else {
            debug_flag.setVisibility(View.GONE);
        }
    }

    public void setHeadImg() {
        String def_book = BookNames.getDefault();
        default_book.setText(def_book);
        Handler mHandler3 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {

                GlideLoadUtils.getInstance().glideLoad(getContext(), (String) msg.obj, book_img, R.drawable.bg_timepicker);
            }
        };
        BookNames.getIcon(def_book, icon -> {
            HandlerUtil.send(mHandler3, icon, HandlerUtil.HANDLE_OK);
        });
    }

    public void initGridLayout() {
        rl_set.setOnClickListener(v -> {
            openNewPage(MainSetFragment.class);
        });
        rl_map.setOnClickListener(v -> {
            openNewPage(MainMapFragment.class);
        });
        rl_asset.setOnClickListener(v -> {
            openNewPage(MainCardFragment.class);
        });
        rl_sort.setOnClickListener(v -> {
            openNewPage(MainSortFragment.class);
        });

        rl_async.setOnClickListener(v -> {
            AppManager.Async(getContext(), AppBroadcast.BROADCAST_ASYNC);
        });
        rl_bill.setOnClickListener(v -> {
            openNewPage(MoneyFragment.class);
        });

        rl_bill.setOnLongClickListener(v -> {
            AutoBills autoBills = new AutoBills(getContext());
            autoBills.show();
            return false;
        });


        rl_bill_check.setOnClickListener(v -> {
            ToastUtils.show(R.string.wait);
            //TODO 4.0新增功能，从支付宝微信等位置导出账单，再从钱迹导出账单，最后比对缺少的账单信息，进行高亮展示，由用户选择合并更新。
        });
        rl_year.setOnClickListener(v -> {
            if (!DateUtils.afterDay("yyyy-MM-dd", "2021-12-31")) {
                ToastUtils.show(R.string.wait);
                return;
            }
            if (!AppStatus.isXposed()) {
                ToastUtils.show(R.string.helper_not_support);
                return;
            }
            loadingDialog = new LoadingDialog(getContext(), getString(R.string.wait_year));
            BaseFragment fragment = this;
            if (broadcastReceiver != null)
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    if (bundle == null) return;
                    String json = bundle.getString("data");
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    String url = "https://auto.dreamn.cn/Year/2021/index.html?data=" + Uri.encode(json);
                    Log.i("url", url);
                    WebViewFragment.openUrl(fragment, url);
                }
            };
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("net.ankio.auto.QIANJI_Year"));
            BottomArea.msg(getContext(), "隐私提醒", "年度账单功能将从【钱迹】本地数据库读取数据，并在自动记账的前端JS中进行分析，数据不会上传至服务器，请放心使用。\n另外：由于是读取本地数据库，请把钱迹【每个月】的账单列表都打开（包括借入借出）以获得【更准确】的分析。", "开启年度账单", "关闭", new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    loadingDialog.show();
                    ToastUtils.setGravity(Gravity.TOP);
                    ToastUtils.show("前往同步账单数据");
                    AppManager.Async(getContext(), AppBroadcast.BROADCAST_GET_YEAR);
                }
            });
        });
        rl_app_log.setOnClickListener(v -> {
            if (AppStatus.isXposed()) {
                AppFragment.openWithType(this, "app");
            } else {
                AppFragment.openWithType(this, "helper");
            }
        });
        rl_sms_log.setOnClickListener(v -> {
            AppFragment.openWithType(this, "sms");
        });
        rl_notice_log.setOnClickListener(v -> {
            AppFragment.openWithType(this, "notice");
        });
        rl_log.setOnClickListener(v->{
            openNewPage(LogFragment.class);
        });
        rl_auto_sort.setOnClickListener(v->{
            MainAutoLearnFragment.openWithType(this, "category");
        });
        rl_app.setOnClickListener(v -> {
            if (AppStatus.isXposed()) {
                MainAutoLearnFragment.openWithType(this, "app");
            } else {
                MainAutoLearnFragment.openWithType(this, "helper");
            }

        });
        rl_sms.setOnClickListener(v -> {
            MainAutoLearnFragment.openWithType(this, "sms");
        });
        rl_notice.setOnClickListener(v -> {
            MainAutoLearnFragment.openWithType(this, "notice");
        });

        rl_skin.setOnClickListener(v -> {
            ToastUtils.show(R.string.wait);
            //TODO 4.0新增功能：更换自动记账皮肤。
        });
        rl_backup.setOnClickListener(v -> {
            openNewPage(BackUpFragment.class);
        });
        rl_text_teach.setOnClickListener(v -> {
            WebViewFragment.openUrl(this, getString(R.string.learnUrl));
        });
        rl_video_teach.setOnClickListener(v -> {
            WebViewFragment.openUrl(this, getString(R.string.biliUrl));
        });
        rl_github.setOnClickListener(v -> {
            WebViewFragment.openUrl(this, getString(R.string.githubUrl));
        });
        rl_about.setOnClickListener(v -> {
            openNewPage(AboutFragment.class);
        });
        debug_flag.setOnClickListener(v -> {
            AppStatus.setDebug(getContext(), false);
            debug_flag.setVisibility(View.GONE);
            ToastUtils.show(R.string.debug_closed);
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                ToastUtils.show(getString(R.string.exit));
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        }
        return true;
    }


}
