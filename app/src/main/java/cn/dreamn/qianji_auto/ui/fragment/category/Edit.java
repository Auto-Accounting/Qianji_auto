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

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.utils.Assets;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.core.utils.DataUtils;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.asset.MangerFragment;
import cn.dreamn.qianji_auto.ui.fragment.asset.MapFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;


@Page(name = "规则编辑")
public class Edit extends BaseFragment {


    @BindView(R.id.regular_name)
    EditText regular_name;
    @BindView(R.id.regular_time1_link)
    TextView regular_time1_link;
    @BindView(R.id.regular_time1)
    EditText regular_time1;
    @BindView(R.id.regular_time2_link)
    TextView regular_time2_link;
    @BindView(R.id.regular_time2)
    EditText regular_time2;

    @BindView(R.id.regular_shopName_link)
    TextView regular_shopName_link;
    @BindView(R.id.regular_shopName)
    EditText regular_shopName;

    @BindView(R.id.regular_shopRemark_link)
    TextView regular_shopRemark_link;
    @BindView(R.id.regular_shopRemark)
    EditText regular_shopRemark;

    @BindView(R.id.regular_type)
    TextView regular_type;
    @BindView(R.id.regular_sort)
    EditText regular_sort;


    @BindView(R.id.btn_cancel)
    ButtonView btn_cancel;
    @BindView(R.id.btn_save)
    ButtonView btn_save;

    private  int regularId=-1;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auto_catgory_edit;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        try {
            initSet();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        initListen();
    }

    private void initSet() throws UnsupportedEncodingException {
        regular_time1_link.setText(R.string.choose_time_equal);
        regular_time2_link.setText(R.string.choose_time_equal);
        Bundle arguments = getArguments();
        String id = arguments.getString("id");
        String data = arguments.getString("data");
        if(id!=null&& !id.equals("")){
            Logs.d(data);
            regularId=Integer.parseInt(id);
            DataUtils dataUtils=new DataUtils();
            dataUtils.parse(data);
            regular_name.setText(dataUtils.get("regular_name"));
            regular_time1_link.setText(dataUtils.get("regular_time1_link"));
            regular_time1.setText(dataUtils.get("regular_time1"));
            regular_time2_link.setText(dataUtils.get("regular_time2_link"));
            regular_time2.setText(dataUtils.get("regular_time2"));
            regular_shopName_link.setText(dataUtils.get("regular_shopName_link"));
            regular_shopName.setText(dataUtils.get("regular_shopName"));
            regular_shopRemark_link.setText(dataUtils.get("regular_shopRemark_link"));
            regular_shopRemark.setText(dataUtils.get("regular_shopRemark"));
            regular_type.setText(dataUtils.get("regular_type"));
            regular_sort.setText(dataUtils.get("regular_sort"));
        }

    }

    private void initListen(){
        regular_time1_link.setOnClickListener(v->{
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_time)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        regular_time1_link.setText(text);
                    })
                    .show();
        });
        regular_time2_link.setOnClickListener(v->{
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_time)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        regular_time2_link.setText(text);
                    })
                    .show();
        });
        regular_shopName_link.setOnClickListener(v->{
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_regular)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        regular_shopName_link.setText(text);
                    })
                    .show();
        });
        regular_shopRemark_link.setOnClickListener(v->{
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_regular)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        regular_shopRemark_link.setText(text);
                    })
                    .show();
        });
        regular_type.setOnClickListener(v->{
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_values_regular_type)
                    .itemsCallback((dialog, itemView, position, text) ->{
                        regular_type.setText(text);
                    })
                    .show();
        });
        btn_cancel.setOnClickListener(v->{
            popToBack("CategoryFragment",null);
        });

        btn_save.setOnClickListener(v->{
            //获取名称
            String name=regular_name.getText().toString();
            if(name.equals("")){
                SnackbarUtils.Long(getView(), "名称不能为空").danger().show();
                return;
            }
            //获取分类
            String sort=regular_sort.getText().toString();
            if(sort.equals("")){
                SnackbarUtils.Long(getView(), "分类不能为空").danger().show();
                return;
            }


            String regularList="";
            //时间获取
            if(!regular_time1.getText().toString().equals(""))
                regularList+=String.format("time %s %s & ",regular_time1_link.getText().toString(),regular_time1.getText().toString());
            if(!regular_time2.getText().toString().equals(""))
                regularList+=String.format("time %s %s & ",regular_time2_link.getText().toString(),regular_time2.getText().toString());
            String shopName=regular_shopName.getText().toString();
            String shopNameLink=regular_shopName_link.getText().toString();
            if(!shopName.equals("")){
                if(shopNameLink.equals(getString(R.string.choose_regular_contains))){
                    regularList+=String.format("shopName.indexOf('%s')!=-1 & ",shopName);
                }else if(shopNameLink.equals(getString(R.string.choose_regular_equal))){
                    regularList+=String.format("shopName == '%s' &",shopName);
                }else if(shopNameLink.equals(getString(R.string.choose_regular_startOf))){
                    regularList+=String.format("shopName.startsWith('%s') & ",shopName);
                }else if(shopNameLink.equals(getString(R.string.choose_regular_endOf))){
                    regularList+=String.format("shopName.endsWith('%s') & ",shopName);
                }else if(shopNameLink.equals(getString(R.string.choose_regular_regex))){
                    regularList+=String.format("(/%s/g).test(shopName) & ",shopName);
                }
            }
            String shopRemark=regular_shopRemark.getText().toString();
            String shopRemarkLink=regular_shopRemark_link.getText().toString();
            if(!shopRemark.equals("")){
                if(shopRemarkLink.equals(getString(R.string.choose_regular_contains))){
                    regularList+=String.format("shopRemark.indexOf('%s')!=-1 & ",shopRemark);
                }else if(shopRemarkLink.equals(getString(R.string.choose_regular_equal))){
                    regularList+=String.format("shopRemark == '%s' &",shopRemark);
                }else if(shopRemarkLink.equals(getString(R.string.choose_regular_startOf))){
                    regularList+=String.format("shopRemark.startsWith('%s') & ",shopRemark);
                }else if(shopRemarkLink.equals(getString(R.string.choose_regular_endOf))){
                    regularList+=String.format("shopRemark.endsWith('%s') & ",shopRemark);
                }else if(shopRemarkLink.equals(getString(R.string.choose_regular_regex))){
                    regularList+=String.format("(/%s/g).test(shopRemark) & ",shopRemark);
                }
            }
            //获取类型
            String type=regular_type.getText().toString();
            if(!type.equals(getString(R.string.choose_type_null))){
                regularList+=String.format("type = '%s' & ",type);
            }

            regularList = regularList.substring(0, regularList.lastIndexOf('&'));
            //获取规则
            String regular = String.format("if(%s)return '%s';",regularList,sort);
            Logs.d(regular);

            DataUtils dataUtils=new DataUtils();
            dataUtils.put("regular_name",regular_name.getText());
            dataUtils.put("regular_time1_link",regular_time1_link.getText());
            dataUtils.put("regular_time1",regular_time1.getText());
            dataUtils.put("regular_time2_link",regular_time2_link.getText());
            dataUtils.put("regular_time2",regular_time2.getText());

            dataUtils.put("regular_shopName_link",regular_shopName_link.getText());
            dataUtils.put("regular_shopName",regular_shopName.getText());
            dataUtils.put("regular_shopRemark_link",regular_shopRemark_link.getText());
            dataUtils.put("regular_shopRemark",regular_shopRemark.getText());

            dataUtils.put("regular_type",regular_type.getText());
            dataUtils.put("regular_sort",regular_sort.getText());

            if(regularId!=-1){
                Category.changeCategory(regularId,regular,name,sort,dataUtils.toString());
            }else{
                Category.addCategory(regular,name,sort,dataUtils.toString());
            }

            popToBack("CategoryFragment",null);
        });

    }

}
