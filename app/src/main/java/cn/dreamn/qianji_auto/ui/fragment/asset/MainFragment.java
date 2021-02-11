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

package cn.dreamn.qianji_auto.ui.fragment.asset;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;


@Page(name = "资产管理")
public class MainFragment extends BaseFragment {
    @BindView(R.id.asset_manger)
    SuperTextView asset_manger;
    @BindView(R.id.asset_map)
    SuperTextView asset_map;
    @BindView(R.id.asset_book)
    SuperTextView asset_book;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_asset;
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

    }

    private void initListen() {
        asset_manger.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(MangerFragment.class);
        });
        asset_map.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(MapFragment.class);
        });
        asset_book.setOnSuperTextViewClickListener(superTextView -> {
            openNewPage(BookFragment.class);
        });

    }

}
