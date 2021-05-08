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

import android.content.Intent;
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import cn.dreamn.qianji_auto.utils.supportUtils.DonateUtil;
import es.dmoral.toasty.Toasty;


@Page(name = "关于", anim = CoreAnim.slide)
public class AboutFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;
    @BindView(R.id.app_log)
    TextView app_log;

    @BindView(R.id.app_version)
    TextView app_version;
    @BindView(R.id.item_update)
    LinearLayout item_update;

    @BindView(R.id.item_github)
    LinearLayout item_github;

    @BindView(R.id.item_develop)
    LinearLayout item_develop;

    @BindView(R.id.item_license)
    LinearLayout item_license;

    @BindView(R.id.item_libs)
    LinearLayout item_libs;

    @BindView(R.id.item_tg)
    LinearLayout item_tg;

    @BindView(R.id.item_group)
    LinearLayout item_group;

    @BindView(R.id.item_star)
    LinearLayout item_star;

    @BindView(R.id.item_support)
    LinearLayout item_support;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }


    @Override
    protected void initViews() {
        app_version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void initListeners() {
        app_log.setOnClickListener(v-> WebViewFragment.openUrl(this,"https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/ChangeLog"));
     item_license.setOnClickListener(v->WebViewFragment.openUrl(this,"https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/LICENSE"));
        item_github.setOnClickListener(v->WebViewFragment.openUrl(this,"https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/Contribution"));
        item_develop.setOnClickListener(v->{
            openNewPage(DevsFragment.class);
        });
        item_libs.setOnClickListener(v->{
            openNewPage(LibsFragment.class);
        });
        item_update.setOnClickListener(v->{
            //TODO 检查更新
        });
        item_support.setOnClickListener(v-> DonateUtil.openAlipayPayPage(getContext()));
        item_group.setOnClickListener(v->{
            String key = "ifoJ5lHBaEqX-dloMkG4d3Ra89zXCLti";
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);

            } catch (Exception e) {
                // 未安装手Q或安装的版本不支持
                Toasty.error(getContext(), "未安装手机QQ或者当前QQ不支持加群。", Toast.LENGTH_LONG).show();
            }
        });
        item_tg.setOnClickListener(v->Tool.goUrl(requireContext(),"https://t.me/qianji_auto"));
        item_star.setOnClickListener(v-> Tool.goToMarket(BuildConfig.APPLICATION_ID));
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
