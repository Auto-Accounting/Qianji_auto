/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.ui.adapter.AssetAdapter;
import cn.dreamn.qianji_auto.ui.adapter.MapAdapter;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;


@Page(name = "插件设置")
public class SetFragment extends BaseFragment {
    @BindView(R.id.set_pay_mode_full)
    SuperTextView set_pay_mode_full;
    @BindView(R.id.set_pay_mode_half)
    SuperTextView set_pay_mode_half;
    @BindView(R.id.set_income_mode_full)
    SuperTextView set_income_mode_full;
    @BindView(R.id.set_income_mode_half)
    SuperTextView set_income_mode_half;
    @BindView(R.id.set_bookname)
    SuperTextView set_bookname;
    @BindView(R.id.set_remark)
    SuperTextView set_remark;
    @BindView(R.id.set_timeout)
    SuperTextView set_timeout;
    @BindView(R.id.set_float_click)
    SuperTextView set_float_click;
    @BindView(R.id.set_float_end)
    SuperTextView set_float_end;
    @BindView(R.id.set_float_style)
    SuperTextView set_float_style;
    @BindView(R.id.set_sort)
    SuperTextView set_sort;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        initSet();
        initListen();
    }

    private void initSet() {
        MMKV mmkv = MMKV.defaultMMKV();

        //二次确认
        //时间 auto_timeout

        if (mmkv.getBoolean("autoPay", false)) {
            setSelectedModel(set_pay_mode_half, false);
            setSelectedModel(set_pay_mode_full, true);
        } else {
            setSelectedModel(set_pay_mode_full, false);
            setSelectedModel(set_pay_mode_half, true);
        }

        if (mmkv.getBoolean("autoIncome", false)) {
            setSelectedModel(set_income_mode_half, false);
            setSelectedModel(set_income_mode_full, true);
        } else {
            setSelectedModel(set_income_mode_full, false);
            setSelectedModel(set_income_mode_half, true);

        }
        set_bookname.setLeftTopString(BookNames.getDefault());

        set_remark.setLeftTopString(Remark.getRemarkTpl());

        set_timeout.setLeftBottomString(mmkv.getString("auto_timeout", "10") + "s");

        //set_float.setLeftBottomString(mmkv.getBoolean("auto_check", true) ? "已开启" : "已关闭");
        set_float_click.setLeftBottomString(mmkv.getBoolean("auto_float_click_double", true) ? "二次确认面板弹窗" : "直接记账");
        set_float_end.setLeftBottomString(mmkv.getBoolean("auto_float_end_double", true) ? "二次确认面板弹窗" : "直接记账");

        set_float_style.setLeftTopString(mmkv.getBoolean("auto_style", true) ? "自动记账自带账单悬浮窗" : "钱迹分类选择窗口");
        set_sort.setLeftBottomString(mmkv.getBoolean("auto_sort", false) ? "已开启" : "已关闭");
    }

    private void initListen() {
        MMKV mmkv = MMKV.defaultMMKV();
        set_pay_mode_half.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoPay", false);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_pay_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoPay", true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_income_mode_half.setOnSuperTextViewClickListener(superTextView -> {

            mmkv.encode("autoIncome", false);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_income_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoIncome", true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_float_style.setOnSuperTextViewClickListener(superTextView -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(new String[]{"自动记账自带账单悬浮窗", "钱迹分类选择窗口"})
                    .itemsCallback((dialog, itemView, position, text) -> {
                        mmkv.encode("auto_style", text == "自动记账自带账单悬浮窗");
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        initSet();

                    })
                    .show();

        });

        set_bookname.setOnSuperTextViewClickListener(superTextView -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(BookNames.getAll())
                    .itemsCallback((dialog, itemView, position, text) -> {
                        BookNames.change(text.toString());
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        initSet();

                    })
                    .show();

        });

        set_remark.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_remark), getString(R.string.set_data_remarktip), Remark.getRemarkTpl(), (str) -> {
                Remark.setTpl(str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });
        set_timeout.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_time), getString(R.string.set_data_timetip), mmkv.getString("auto_timeout", "10"), (str) -> {

                try {
                    Integer.parseInt(str);
                    mmkv.encode("auto_timeout", str);
                    SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                    initSet();
                } catch (Exception e) {
                    new MaterialDialog.Builder(getContext())
                            .title("类型错误")
                            .content("只允许输入整数！")
                            .positiveText(getString(R.string.input_ok))
                            .show();
                }


            });

        });
        set_float_click.setOnSuperTextViewClickListener(superTextView -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(new String[]{"二次确认面板弹窗", "直接记账"})
                    .itemsCallback((dialog, itemView, position, text) -> {
                        mmkv.encode("auto_float_click_double", text == "二次确认面板弹窗");
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        initSet();

                    })
                    .show();

        });
        set_float_end.setOnSuperTextViewClickListener(superTextView -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(new String[]{"二次确认面板弹窗", "直接记账"})
                    .itemsCallback((dialog, itemView, position, text) -> {
                        mmkv.encode("auto_float_end_double", text == "二次确认面板弹窗");
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        initSet();

                    })
                    .show();

        });
        set_sort.setOnSuperTextViewClickListener(superTextView -> {
            if (mmkv.getBoolean("auto_sort", false)) {
                mmkv.encode("auto_sort", false);
                SnackbarUtils.Long(getView(), getString(R.string.set_sort_close)).info().show();
            } else {
                mmkv.encode("auto_sort", true);
                SnackbarUtils.Long(getView(), getString(R.string.set_sort_success)).info().show();
            }
            initSet();
        });
    }

}
