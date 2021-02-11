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
import android.view.KeyEvent;

import com.tencent.mmkv.MMKV;
import com.thl.filechooser.FileChooser;
import com.thl.filechooser.FileInfo;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.App;
import cn.dreamn.qianji_auto.core.utils.Status;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.category.CategoryFragment;
import cn.dreamn.qianji_auto.ui.fragment.sms.SmsFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.tools.FileUtils;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.Permission;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "自动记账", anim = CoreAnim.none)
public class MainFragment extends BaseFragment implements ClickUtils.OnClick2ExitListener {
    @BindView(R.id.status)
    SuperTextView menu_status;
    @BindView(R.id.set)
    SuperTextView menu_set;
    @BindView(R.id.Asset)
    SuperTextView menu_asset;
    @BindView(R.id.sortMap)
    SuperTextView menu_sortMap;

    @BindView(R.id.Sms)
    SuperTextView menu_Sms;
    @BindView(R.id.Bill)
    SuperTextView menu_Bill;
    @BindView(R.id.Log)
    SuperTextView menu_Log;
    @BindView(R.id.Backup)
    SuperTextView menu_Backup;
    @BindView(R.id.About)
    SuperTextView menu_About;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        showUpdateLog();
        setActive();
        initListen();

    }
/*

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setActive();
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        setActive();
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.disableLeftView();
        return titleBar;
    }

    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    private void setActive() {
        if (Status.isActive(getContext())) {
            Logs.d("已激活");
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_success));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_succ));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_true));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_active), Status.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s(%d)", App.getAppVerName(), App.getAppVerCode()));

        } else {
            Logs.d("未激活");
            menu_status.setBackgroundColor(getResources().getColor(R.color.list_bg_err));
            menu_status.setLeftTopTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftBottomTextColor(getResources().getColor(R.color.list_text_color_err));
            menu_status.setLeftIcon(getResources().getDrawable(R.drawable.ic_false));
            menu_status.setLeftTopString(String.format(getString(R.string.menu_noactive), Status.getFrameWork(getContext())));
            menu_status.setLeftBottomString(String.format("v%s (%d)", App.getAppVerName(), App.getAppVerCode()));
        }


    }


    @SuppressLint("SdCardPath")
    private void initListen() {
        menu_status.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(ModeFragment.class);
        });
        menu_set.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(SetFragment.class);
        });
        menu_asset.setOnSuperTextViewClickListener(superTextView -> {
            //资产管理
            openNewPage(cn.dreamn.qianji_auto.ui.fragment.asset.MainFragment.class);
        });
        menu_sortMap.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(CategoryFragment.class);
        });
        menu_Sms.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(SmsFragment.class);
        });
        menu_Bill.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(BillFragment.class);
        });
        menu_Backup.setOnSuperTextViewClickListener(superTextView -> {

            Permission.getInstance().grant(getActivity(), Permission.Storage);
            new MaterialDialog.Builder(requireContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_backup)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        if (position == 0) {
                            try {
                                SnackbarUtils.Long(getView(), "备份中...").info().show();
                                FileUtils.backUp(getContext());
                                SnackbarUtils.Long(getView(), String.format(getString(R.string.bak_success), "/sdcard/Download/QianJiAuto")).info().show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            FileChooser fileChooser = new FileChooser(getActivity(), filePath -> {
                                String ret = FileUtils.restore(filePath.get(0).getFilePath(), getContext());
                                if (ret.equals("ok")) {
                                    SnackbarUtils.Long(getView(), getString(R.string.bak_success_2)).info().show();
                                    XUtil.rebootApp();
                                } else SnackbarUtils.Long(getView(), ret).info().show();
                            });

                            fileChooser.setTitle("请选择备份文件");
                            fileChooser.setDoneText("确定");


                            fileChooser.setChooseType(FileInfo.FILE_TYPE_BACKUP);
                            fileChooser.open();
                        }


                    })
                    .show();
            //  openNewPage(BackupFragment.class);

        });

        menu_Log.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(LogFragment.class);
        });
        menu_About.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(AboutFragment.class);
        });
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }


    private void showUpdateLog() {
        MMKV mmkv = MMKV.defaultMMKV();
        int version = mmkv.getInt("version", 0);
        int nowVersion = App.getAppVerCode();
        if (nowVersion > version) {
            String data = FileUtils.getAssetsData(getContext(), "update.txt");
            if (data == null) return;
            mmkv.encode("version", nowVersion);
            new MaterialDialog.Builder(requireContext())
                    .title("更新日志")
                    .content(data)
                    .negativeText("我知道了")
                    .show();
        }

    }
}
