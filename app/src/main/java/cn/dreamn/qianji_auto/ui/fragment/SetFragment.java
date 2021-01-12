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
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.BookNames;
import cn.dreamn.qianji_auto.core.utils.Remark;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;

/**
 * 这个只是一个空壳Fragment，只是用于演示而已
 *
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
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
    @BindView(R.id.set_float)
    SuperTextView set_float;
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


            set_bookname.setLeftTopString(BookNames.getDefault());

            set_remark.setLeftTopString(Remark.getRemarkTpl());

            set_timeout.setLeftBottomString(mmkv.getString("auto_timeout", "10") + "s");
            set_float.setLeftBottomString(mmkv.getBoolean("auto_check", true) ? "已开启" : "已关闭");
        }

    }

    private void initListen(){
        MMKV mmkv = MMKV.defaultMMKV();
        set_pay_mode_half.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoPay",false);
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_pay_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoPay",true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_income_mode_half.setOnSuperTextViewClickListener(superTextView -> {

            mmkv.encode("autoIncome",false);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_income_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            mmkv.encode("autoIncome",true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_bookname.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_bookname),getString(R.string.set_data_booktip),BookNames.getDefault(),(str)->{
                BookNames.change(str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });

        set_remark.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_remark),getString(R.string.set_data_remarktip),Remark.getRemarkTpl(),(str)->{
                Remark.setTpl(str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });
        set_timeout.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_time),getString(R.string.set_data_timetip),mmkv.getString("auto_timeout", "0"),(str)->{
                mmkv.encode("auto_timeout",str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });
        set_float.setOnSuperTextViewClickListener(superTextView -> {
            if(mmkv.getBoolean("auto_check", true)){
                mmkv.encode("auto_check",false);
                SnackbarUtils.Long(getView(), getString(R.string.set_check_close)).info().show();
            }else{
                mmkv.encode("auto_check",true);
                SnackbarUtils.Long(getView(), getString(R.string.set_check_success)).info().show();
            }
            initSet();
        });
    }

}
