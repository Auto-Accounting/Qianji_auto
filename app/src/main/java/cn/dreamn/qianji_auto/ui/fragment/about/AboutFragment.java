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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.fragment.helper.HelperFragment;
import cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import cn.dreamn.qianji_auto.utils.supportUtils.DonateUtil;


@Page(name = "关于", anim = CoreAnim.slide)
public class AboutFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.app_log)
    TextView app_log;

    @BindView(R.id.app_version)
    TextView app_version;
    @BindView(R.id.mode_name)
    TextView app_new_version;
    @BindView(R.id.item_update)
    LinearLayout item_update;

    @BindView(R.id.item_github)
    LinearLayout item_github;

    @BindView(R.id.item_develop)
    LinearLayout item_develop;

    @BindView(R.id.item_license)
    LinearLayout item_license;

    @BindView(R.id.item_source)
    LinearLayout item_source;
    @BindView(R.id.sourceIcon_name)
    TextView sourceIcon_name;

    @BindView(R.id.item_group)
    LinearLayout item_group;

    @BindView(R.id.item_star)
    LinearLayout item_star;

    @BindView(R.id.item_support)
    LinearLayout item_support;
    @BindView(R.id.item_home)
    LinearLayout item_home;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }


    @Override
    protected void initViews() {
        app_version.setText(BuildConfig.VERSION_NAME);
        app_new_version.setText(BuildConfig.VERSION_NAME);
        MMKV mmkv = MMKV.defaultMMKV();
        sourceIcon_name.setText(mmkv.getString("baseUrlName", "ghProxy"));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        app_log.setOnClickListener(v -> WebViewFragment.openUrl(this, "https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/ChangeLog"));
        item_license.setOnClickListener(v -> WebViewFragment.openUrl(this, "https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/LICENSE"));
        item_github.setOnClickListener(v -> WebViewFragment.openUrl(this, "https://doc.ankio.net/doc/%E8%87%AA%E5%8A%A8%E8%AE%B0%E8%B4%A6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3/#/Contribution"));
        item_develop.setOnClickListener(v -> {
            openNewPage(DevsFragment.class);
        });
        item_update.setOnClickListener(v -> {
            //"正在检测更新中,请稍候"
            ToastUtils.show(R.string.about_check_update);
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    ToastUtils.show(R.string.about_check_no_update);
                }
            };
            AutoBillWeb.update(getContext(), new AutoBillWeb.CallbackWith() {
                @Override
                public void onUpdateEnd() {
                    mHandler.sendEmptyMessage(0);
                }
            });

        });
        item_update.setOnLongClickListener(v -> {

            MMKV mmkv = MMKV.defaultMMKV();
            String channel = mmkv.getString("version_channel", "stable");
            String channelName = getString(R.string.version_stable);
            switch (channel) {
                case "stable":
                    channelName = getString(R.string.version_stable);
                    break;
                case "alpha":
                    channelName = getString(R.string.version_alpha);
                    break;
                case "beta":
                    channelName = getString(R.string.version_beta);
                    break;
            }

            BottomArea.list(getContext(), String.format(getString(R.string.check_version), channelName), Arrays.asList(getString(R.string.version_stable), getString(R.string.version_alpha), getString(R.string.version_beta)), new BottomArea.ListCallback() {
                @Override
                public void onSelect(int position) {
                    String[] s = new String[]{"stable", "alpha", "beta"};
                    mmkv.encode("version_channel", s[position]);
                    ToastUtils.show(R.string.log_set_success);
                }
            });

            return false;
        });
        item_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(AppStatus.xposedActive(getContext())){
                    BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
                    MaterialDialog dialog2 = new MaterialDialog(getContext(), bottomSheet2);
                    dialog2.title(null, "请选择捐赠渠道");
                    dialog2.cornerRadius(15f,null);
                    DialogListExtKt.listItems(dialog2, null, Arrays.asList("支付宝", "微信", "QQ"), null, true, (materialDialog, index, text) -> {
                        String[] s = new String[]{"stable", "alpha", "beta"};
                        mmkv.encode("version_channel", s[index]);
                        ToastUtils.show( "设置成功！");
                        return null;
                    });
                    dialog2.show();
                }*/
                DonateUtil.openAlipayPayPage(getContext());
            }
        });
        item_group.setOnClickListener(v -> {
            String key = "ifoJ5lHBaEqX-dloMkG4d3Ra89zXCLti";
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            } catch (Exception e) {
                // 未安装手Q或安装的版本不支持
                ToastUtils.show(R.string.qq_error);
            }
        });
        item_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MMKV mmkv = MMKV.defaultMMKV();
                List<String> list = Arrays.asList("jsDelivr", "Raw", "FastGit", "ghProxy", getString(R.string.source_custom));
                BottomArea.list(getContext(), String.format(getString(R.string.about_source), mmkv.getString("baseUrlName", "ghProxy")), list, new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {
                        mmkv.encode("baseUrlName", list.get(position));
                        if (position == 4) {
                            BottomArea.input(getContext(), getString(R.string.source_input), mmkv.getString("baseUrl", ""), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                                @Override
                                public void input(String data) {
                                    mmkv.encode("baseUrl", data);
                                    initViews();
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                        } else {
                            initViews();
                        }
                    }
                });
            }
        });
        item_star.setOnClickListener(v -> Tool.goUrl(requireContext(), "https://www.coolapk.com/apk/cn.dreamn.qianji_auto"));
        item_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MMKV mmkv = MMKV.defaultMMKV();
                mmkv.encode("version_3_0", true);
                mmkv.encode("helper_page", 0);
                openPage(HelperFragment.class);
            }
        });
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
    }





}
