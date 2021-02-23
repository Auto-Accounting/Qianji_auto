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

package cn.dreamn.qianji_auto.ui.fragment.sms;

import android.os.Bundle;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import java.util.Arrays;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.core.db.Helper.Smses;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.JsEngine;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/*
import com.eclipsesource.v8.V8;*/


@Page(name = "短信识别规则编辑")
public class EditFragment extends BaseFragment {


    @BindView(R.id.sms_name)
    MaterialEditText sms_name;
    @BindView(R.id.sms_account)
    MaterialEditText sms_account;
    @BindView(R.id.sms_money)
    MaterialEditText sms_money;

    @BindView(R.id.sms_regex)
    MultiLineEditText sms_regex;

    @BindView(R.id.sms_num)
    MaterialEditText sms_num;
    @BindView(R.id.sms_remark)
    MaterialEditText sms_remark;
    @BindView(R.id.sms_type)
    RoundButton sms_type;


    @BindView(R.id.sms_account2)
    MaterialEditText sms_account2;
    @BindView(R.id.sms_shop)
    MaterialEditText sms_shop;
    @BindView(R.id.sms_client)
    RoundButton sms_client;

    @BindView(R.id.btn_test)
    ButtonView btn_test;
    @BindView(R.id.btn_save)
    ButtonView btn_save;

    private int regularId = -1;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sms_edit;
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

        Bundle arguments = getArguments();
        if (arguments == null) return;
        String id = arguments.getString("id");
        String num = arguments.getString("num");
        String regex = arguments.getString("regex");
        String title = arguments.getString("title");

        if (id != null && !id.equals("")) {
            regularId = Integer.parseInt(id);

            String[] s = Smses.getSmsNum(num);
            Logs.d(Arrays.toString(s));

            sms_name.setText(title);
            sms_regex.setContentText(regex);

            sms_account.setText(s[1]);
            sms_money.setText(s[3]);
            sms_num.setText(s[4]);
            sms_remark.setText(s[0]);
            sms_type.setText(s[2]);
            sms_account2.setText(s[6]);
            sms_shop.setText(s[5]);
            sms_client.setText(s[7].equals("") ? "否" : s[7]);

        }

    }

    private void initListen() {
        sms_type.setOnClickListener(v -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_regular_type)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        sms_type.setText(text);
                    })
                    .show();
        });
        sms_client.setOnClickListener(v -> {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_client)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        sms_client.setText(text);
                    })
                    .show();
        });
        btn_test.setOnClickListener(v -> {
            // popToBack("SmsFragment",null);

            showInputDialog("请输入测试短信", "测试短信", Caches.getOneString("test_sms", ""), data -> {
                Caches.AddOrUpdate("test_sms", data);
                String func = Smses.getFunction(
                        sms_regex.getContentText(),
                        data,
                        sms_remark.getEditValue(),
                        sms_account.getEditValue(),
                        sms_type.getText().toString(),
                        sms_money.getEditValue(),
                        sms_num.getEditValue(),
                        sms_shop.getEditValue(),
                        sms_account2.getEditValue(),
                        sms_client.getText().toString()
                );
                Logs.d(func);


                try {
                  /*  V8 runtime = V8.createV8Runtime();


                    String result=runtime.executeStringScript(func);*/
                    String result = JsEngine.run(func);
                    Logs.d("Qianji_Sms", "短信分析结果：" + result);
                    String[] strings = Smses.getSmsNum(result);
                    String datas = "";
                    datas += "尾号：" + strings[4] + "\n";
                    datas += "账户名称1：" + strings[1] + "\n";
                    datas += "账户名称2：" + strings[6] + "\n";
                    datas += "收支类型：" + strings[2] + "\n";
                    datas += "后台交易：" + strings[7] + "\n";
                    datas += "金额：" + strings[3] + "\n";
                    datas += "商户名称：" + strings[5] + "\n";
                    datas += "商户备注：" + strings[0] + "\n";


                    new MaterialDialog.Builder(getContext())
                            .title("识别结果")
                            .content(datas)
                            .positiveText(getString(R.string.input_ok))
                            .show();
                } catch (Exception e) {
                    new MaterialDialog.Builder(getContext())
                            .title("运行错误")
                            .content(e.toString())
                            .positiveText(getString(R.string.input_ok))
                            .show();
                }


            });
        });

        btn_save.setOnClickListener(v -> {
            //获取名称
            String name = sms_name.getEditValue();
            if (name.equals("")) {
                SnackbarUtils.Long(getView(), "名称不能为空").danger().show();
                return;
            }
            //获取分类
            String regex = sms_regex.getContentText();
            if (regex.equals("")) {
                SnackbarUtils.Long(getView(), "正则不能为空").danger().show();
                return;
            }


            String num = sms_remark.getEditValue() + "|" + sms_account.getEditValue() + "|" + sms_type.getText().toString() + "|" + sms_money.getEditValue() + "|" + sms_num.getEditValue() + "|" + sms_shop.getEditValue() + "|" + sms_account2.getEditValue() + "|" + sms_client.getText();

            if (regularId != -1) {
                Smses.change(regularId, regex, name, num);
            } else {
                // Logs.d("add 1");
                Smses.add(regex, name, num);
                // Logs.d("add 2");
            }

            popToBack("SmsFragment", null);
        });

    }

    public void showInputDialog(String title, String tip, String def, CallBack callBack) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(tip)
                .input(
                        getString(R.string.input_tip),
                        def,
                        false,
                        ((dialog, input) -> {
                        })
                )
                .positiveText(getString(R.string.input_ok))
                .negativeText(getString(R.string.set_cancel))
                .onPositive((dialog, which) -> callBack.onResponse(dialog.getInputEditText().getText().toString()))
                .show();
    }

    // 回调接口
    public interface CallBack {
        void onResponse(String data);
    }

}
