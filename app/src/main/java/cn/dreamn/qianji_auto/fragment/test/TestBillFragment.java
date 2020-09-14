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

package cn.dreamn.qianji_auto.fragment.test;



import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;


import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.active.Fun;

/**
 *
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
@Page(name = "钱迹Url测试")
public class TestBillFragment extends BaseFragment {

    @BindView(R.id.url_schema)
    MultiLineEditText url_schema;
    @BindView(R.id.btn_remark)
    ButtonView btn_remark;
    @BindView(R.id.btn_default)
    ButtonView btn_default;
    @BindView(R.id.btn_doc)
    ButtonView btn_doc;
    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_testbill;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        btn_remark.setOnClickListener(view -> {
            Fun.browser(getContext(),url_schema.getContentText());
        });
        btn_default.setOnClickListener(view -> {
            url_schema.setContentText("qianji://publicapi/addbill?&type=0&money=26.5&catename=咖啡");
        });
        btn_doc.setOnClickListener(view -> {
            Fun.browser(getContext(),"http://docs.qianjiapp.com/plugin/auto_tasker.html");
        });

    }
}
