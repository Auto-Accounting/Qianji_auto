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
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.fragment.sort.AlipayFragment;
import cn.dreamn.qianji_auto.fragment.sort.WechatFragment;
import cn.dreamn.qianji_auto.utils.file.Storage;

import static com.xuexiang.xui.utils.ResUtils.getColor;

/**
 * 这个只是一个空壳Fragment，只是用于演示而已
 *
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
@Page(name = "分类映射")
public class SortFragment extends BaseFragment {
    @BindView(R.id.sort_alipay)
    SuperTextView sort_alipay;
    @BindView(R.id.sort_wechat)
    SuperTextView sort_wechat;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sort;
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
        sort_alipay.setBackgroundColor(getColor(R.color.list_bg_success2));
        sort_alipay.setLeftTopTextColor(getColor(R.color.list_bg_noraml));
        sort_alipay.setLeftBottomTextColor(getColor(R.color.list_bg_noraml));

        sort_wechat.setBackgroundColor(getColor(R.color.list_bg_success));
        sort_wechat.setLeftTopTextColor(getColor(R.color.list_bg_noraml));
        sort_wechat.setLeftBottomTextColor(getColor(R.color.list_bg_noraml));
    }

    private void initListen(){
        sort_wechat.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(WechatFragment.class);
        });
        sort_alipay.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(AlipayFragment.class);
        });

    }

}
