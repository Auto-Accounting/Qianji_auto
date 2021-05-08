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

package cn.dreamn.qianji_auto.ui.fragment.about;

import android.widget.RelativeLayout;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;


@Page(name = "开发人员", anim = CoreAnim.slide)
public class DevsFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;

    @BindView(R.id.include_set)
    RelativeLayout ankio_head;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_devs;
    }


    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        ankio_head.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://www.ankio.net"));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected TitleBar initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        return null;
    }






}
