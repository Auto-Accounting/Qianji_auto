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

package cn.dreamn.qianji_auto.ui.fragment.category;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.base.Manager;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.core.db.Helper.Category;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.JsEngine;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/*
import com.eclipsesource.v8.V8;*/


@Page(name = "JS规则编辑")
public class JsFragment extends BaseFragment {


    @BindView(R.id.regular_name)
    MaterialEditText regular_name;
    @BindView(R.id.btn_test)
    ButtonView btn_test;
    @BindView(R.id.btn_save)
    ButtonView btn_save;
    @BindView(R.id.js_data)
    MultiLineEditText js_data;

    private int regularId = -1;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auto_catgory_edit_js;
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
        String data = arguments.getString("data");
        String name = arguments.getString("name");
        if (id != null && !id.equals("")) {
            Logs.d(data);
            regularId = Integer.parseInt(id);
            regular_name.setText(name);
            js_data.setContentText(data);
        }

    }

    private void initListen() {

        btn_test.setOnClickListener(v -> {
            LayoutInflater factory = LayoutInflater.from(getContext());
            @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.fragment_auto_catgory_edit_test, null);
            final MaterialEditText cate_time = textEntryView.findViewById(R.id.cate_time);
            final MaterialEditText cate_money = textEntryView.findViewById(R.id.cate_money);
            final MaterialEditText cate_shopName = textEntryView.findViewById(R.id.cate_shopName);
            final MaterialEditText cate_shopRemark = textEntryView.findViewById(R.id.cate_shopRemark);
            final RoundButton cate_type = textEntryView.findViewById(R.id.cate_type);
            final RoundButton cate_type2 = textEntryView.findViewById(R.id.cate_type2);
            cate_time.setText(Tools.getTime("HH"));
            cate_type.setText("支出");
            cate_type.setOnClickListener(v22 -> {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.tip_options)
                        .items(R.array.menu_values_regular_type)
                        .itemsCallback((dialog, itemView, position, text) -> {
                            cate_type.setText(text);
                        })
                        .show();
            });
            cate_type2.setText("任意类型");
            cate_type2.setOnClickListener(v33 -> {
                try {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.tip_options)
                            .items(Manager.getAll())
                            .itemsCallback((dialog, itemView, position, text) -> {
                                cate_type2.setText(text);
                            })
                            .show();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            cate_shopName.setText(Caches.getOneString("cate_shopName", ""));
            cate_shopRemark.setText(Caches.getOneString("cate_shopRemark", ""));
            new MaterialDialog.Builder(getContext())
                    .customView(textEntryView, true)
                    .positiveText("测试")
                    .onPositive((dialog, which) -> {
                        String js = js_data.getContentText();
                        Caches.AddOrUpdate("cate_shopName", cate_shopName.getEditValue());
                        Caches.AddOrUpdate("cate_shopRemark", cate_shopRemark.getEditValue());
                        try {
                            /*V8 runtime = V8.createV8Runtime();

                            String result = runtime.executeStringScript(Category.getOneRegularJs(js,cate_shopName.getEditValue(), cate_shopRemark.getEditValue(), cate_type.getEditValue(),cate_time.getEditValue()));
*/
                            String result = JsEngine.run(Category.getOneRegularJs(js, cate_shopName.getEditValue(), cate_shopRemark.getEditValue(), cate_type.getText().toString(), cate_time.getEditValue(), cate_type2.getText().toString(), cate_money.getEditValue()));
                            Logs.d("Qianji_Cate", "自动分类结果：" + result);
                            new MaterialDialog.Builder(getContext())
                                    .title("自动分类结果")
                                    .content(result)
                                    .positiveText(getString(R.string.input_ok))
                                    .show();
                        } catch (Exception e) {
                            Logs.d("自动分类执行出错！" + e.toString());
                            new MaterialDialog.Builder(getContext())
                                    .title("自动分类执行出错")
                                    .content(e.toString())
                                    .positiveText(getString(R.string.input_ok))
                                    .show();
                        }
                    })
                    .show();
        });

        btn_save.setOnClickListener(v -> {
            //获取名称
            String name = regular_name.getEditValue();
            if (name.equals("")) {
                SnackbarUtils.Long(getView(), "名称不能为空").danger().show();
                return;
            }


            String data = js_data.getContentText();

            int ret = data.lastIndexOf("return");
            if (ret == -1) {
                SnackbarUtils.Long(getView(), "没有返回任何分类").danger().show();
                return;
            }

            String sort = data.substring(ret).replace("return", "").replace("\"", "").replace("'", "").replace(";", "").replace(" ", "").trim();

            if (regularId != -1) {
                Category.changeCategory(regularId, data, name, sort, "");
            } else {
                Category.addCategory(data, name, sort, "");
            }
            popToBack("CategoryFragment", null);
        });

    }


}
