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

package cn.dreamn.qianji_auto.fragment;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.file.Storage;

import static com.xuexiang.xui.utils.ResUtils.getColor;

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

    private void initSet(){
        if(Storage.type(Storage.Set).getBoolean("pay",false)){
            setSelectedModel(set_pay_mode_half,false);
            setSelectedModel(set_pay_mode_full,true);
        }else{
            setSelectedModel(set_pay_mode_full,false);
            setSelectedModel(set_pay_mode_half,true);
        }
        if(Storage.type(Storage.Set).getBoolean("income",false)){
            setSelectedModel(set_income_mode_half,false);
            setSelectedModel(set_income_mode_full,true);
        }else{
            setSelectedModel(set_income_mode_full,false);
            setSelectedModel(set_income_mode_half,true);
        }
        set_bookname.setLeftTopString(Storage.type(Storage.Set).get("bookname","默认账本"));
        set_remark.setLeftTopString(Storage.type(Storage.Set).get("remark","[交易对象] - [说明]"));
    }

    private void initListen(){
        set_pay_mode_half.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("pay",false);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_pay_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("pay",true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_income_mode_half.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("income",false);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_half)).info().show();
        });
        set_income_mode_full.setOnSuperTextViewClickListener(superTextView -> {
            Storage.type(Storage.Set).set("income",true);
            initSet();
            SnackbarUtils.Long(getView(), getString(R.string.set_msg_mode_full)).info().show();
        });
        set_bookname.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_bookname),getString(R.string.set_data_booktip),Storage.type(Storage.Set).get("bookname","默认账本"),(str)->{
                Storage.type(Storage.Set).set("bookname",str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });

        set_remark.setOnSuperTextViewClickListener(superTextView -> {
            showInputDialog(getString(R.string.set_data_remark),getString(R.string.set_data_remarktip),Storage.type(Storage.Set).get("remark","[交易对象] - [说明] "),(str)->{
                Storage.type(Storage.Set).set("remark",str);
                SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                initSet();
            });

        });
    }

}
