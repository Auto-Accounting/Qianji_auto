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

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.CardData;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;


@Page(name = "开源库列表", anim = CoreAnim.slide)
public class LibsFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;

    @BindView(R.id.AndroidAutoSize)
    CardData AndroidAutoSize;
    @BindView(R.id.XPage)
    CardData XPage;
    @BindView(R.id.butterknife)
    CardData butterknife;
    @BindView(R.id.MMKV)
    CardData MMKV;
    @BindView(R.id.ZSkinPlugin)
    CardData ZSkinPlugin;
    @BindView(R.id.SmartRefreshLayout)
    CardData SmartRefreshLayout;
    @BindView(R.id.CookieBar)
    CardData CookieBar;
    @BindView(R.id.XXPermissions)
    CardData XXPermissions;
    @BindView(R.id.ToastUtils)
    CardData ToastUtils;
    @BindView(R.id.materialripple)
    CardData materialripple;
    @BindView(R.id.NiceImageView)
    CardData NiceImageView;
    @BindView(R.id.Room)
    CardData Room;
    @BindView(R.id.Rhino)
    CardData Rhino;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_libs;
    }


    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        AndroidAutoSize.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/JessYanCoding/AndroidAutoSize"));
        XPage.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/xuexiangjys/XPage"));
        butterknife.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/JakeWharton/butterknife"));
        MMKV.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/tencent/mmkv"));
        ZSkinPlugin.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/LillteZheng/ZSkinPlugin"));
        SmartRefreshLayout.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/xuexiangjys/SmartRefreshLayout"));
        CookieBar.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/liuguangqiang/CookieBar"));
        XXPermissions.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/getActivity/XXPermissions"));
        ToastUtils.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/getActivity/ToastUtils"));
        materialripple.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/balysv/material-ripple"));
        NiceImageView.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://github.com/SheHuan/NiceImageView"));
        Room.setOnClickListener(v->WebViewFragment.openUrl(this,"https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh_cn#java"));
        Rhino.setOnClickListener(v->WebViewFragment.openUrl(this,"https://github.com/mozilla/rhino"));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        //  return null;
    }





}
