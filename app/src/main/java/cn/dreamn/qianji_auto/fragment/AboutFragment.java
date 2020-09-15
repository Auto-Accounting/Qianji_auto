package cn.dreamn.qianji_auto.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.didikee.donate.AlipayDonate;
import android.didikee.donate.WeiXinDonate;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.text.InputType;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xutil.app.AppUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.fragment.test.TestBillFragment;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import cn.dreamn.qianji_auto.utils.active.Fun;
import cn.dreamn.qianji_auto.utils.file.Storage;

import static cn.dreamn.qianji_auto.core.LogListAdapter.KEY_TITLE;

@Page(name = "关于")
public class AboutFragment extends BaseFragment {
    @BindView(R.id.logo)
    AppCompatImageView logo;
    @BindView(R.id.version)
    TextView mVersionTextView;
    @BindView(R.id.desc)
    TextView descTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright)
    TextView mCopyrightTextView;
    //彩蛋
    private int COUNTS = 10;// 点击次数
    private long[] mHits = new long[COUNTS];//记录点击次数
    private long DURATION = 2000;//有效时间

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    private void egg(){
        new MaterialDialog.Builder(getContext())
                .title("死亡问答")
                .content("魔镜啊魔镜,告诉我,谁是世界上最优秀的开发者?")
                .positiveText("Dreamn")
                .negativeText("其他")
                .onPositive((dialog, which) -> {
                    new MaterialDialog.Builder(getContext())
                            .title("感谢你诚实的回答！")
                            .content("为了表扬你的诚实，我决定奖励你一个小姐姐！")
                            .positiveText("确定")
                            .negativeText("不需要")
                            .onPositive((dialog1, which1) -> {
                                Fun.browser(getContext(),"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2632901607,3908648202&fm=26&gp=0.jpg");
                            })
                            .cancelable(false)
                            .show();
                })
                .onNegative((dialog, which) -> {
                    new MaterialDialog.Builder(getContext())
                            .title("死亡问答")
                            .content("嗯?我再给你一次机会！")
                            .positiveText("好的")
                            .onPositive((dialog2, which2) -> {
                                egg();
                            })
                            .cancelable(false)
                            .show();
                })
                .cancelable(false)
                .show();
    }
    @Override
    protected void initViews() {
        logo.setOnClickListener(view -> {
//将mHints数组内的所有元素左移一个位置
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//获得当前系统已经启动的时间
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)){
                egg();
                mHits = new long[COUNTS];
            }
        });
        mVersionTextView.setText(String.format("%s(%s)", AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
        descTextView.setText(getResources().getString(R.string.about_desc));
        XUIGroupListView.newSection(getContext())
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_homepage)), v -> Fun.browser(getContext(), getString(R.string.url_author_blog)))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_learn)), v -> Fun.browser(getContext(), getString(R.string.url_learn)))
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_donation_link)), v -> {
                    new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                            .title(R.string.tip_options)
                            .items(R.array.menu_values_donate)
                            .itemsCallback((dialog, itemView, position, text) ->{
                                switch (position){
                                    case 0:
                                        boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(getContext());
                                        if (hasInstalledAlipayClient) {
                                            AlipayDonate.startAlipayClient(getActivity(), "fkx13398cgtyl92srtra836");
                                        }

                                        break;
                                    case 1:
                                        CookieBar.builder(getActivity())
                                                .setMessage("请在微信扫码页面选择「第一张」二维码捐赠")
                                                .setDuration(3000)
                                                .setBackgroundColor(R.color.toast_normal_tint_color)
                                                .setActionColor(android.R.color.white)
                                                .setTitleColor(android.R.color.white)
                                                .show();
                                        InputStream weixinQrIs = getResources().openRawResource(R.raw.wechat);
                                        @SuppressLint("SdCardPath") String qrPath = "/sdcard/Download/wechat.png";
                                        WeiXinDonate.saveDonateQrImage2SDCard(qrPath, BitmapFactory.decodeStream(weixinQrIs));
                                        WeiXinDonate.donateViaWeiXin(getActivity(), qrPath);
                                        break;
                                }


                            })
                            .show();
                })
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
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_open_url)), v -> Fun.browser(getContext(), getString(R.string.url_open_url)))
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));
    }
}
