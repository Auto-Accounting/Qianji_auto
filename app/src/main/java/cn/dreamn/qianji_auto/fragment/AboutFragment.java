package cn.dreamn.qianji_auto.fragment;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xutil.app.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import cn.dreamn.qianji_auto.func.Fun;
import cn.dreamn.qianji_auto.utils.XToastUtils;

@Page(name = "关于")
public class AboutFragment extends BaseFragment {

    @BindView(R.id.version)
    TextView mVersionTextView;
    @BindView(R.id.desc)
    TextView descTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright)
    TextView mCopyrightTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initViews() {
        mVersionTextView.setText(String.format("%s(%s)", AppUtils.getAppVersionName(),AppUtils.getAppVersionCode()));
        descTextView.setText(getResources().getString(R.string.about_desc));
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_homepage)), v -> Fun.browser(getContext(), getString(R.string.url_author_blog)))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_learn)), v -> Fun.browser(getContext(), getString(R.string.url_learn)))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_donation_link)), v -> Fun.browser(getContext(), getString(R.string.url_donation_link)))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_add_qq_group)), v -> {
                    String key="ifoJ5lHBaEqX-dloMkG4d3Ra89zXCLti";
                    Intent intent = new Intent();
                    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
                    // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);

                    } catch (Exception e) {
                        // 未安装手Q或安装的版本不支持
                        XToastUtils.error("未安装手机QQ或者当前QQ不支持加群。");
                    }
                })
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }
}
