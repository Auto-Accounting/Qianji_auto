package cn.dreamn.qianji_auto.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseActivity;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.fragment.AboutFragment;
import cn.dreamn.qianji_auto.fragment.LearnFragment;
import cn.dreamn.qianji_auto.fragment.LogFragment;
import cn.dreamn.qianji_auto.fragment.MapFragment;
import cn.dreamn.qianji_auto.fragment.SettingsFragment;
import cn.dreamn.qianji_auto.fragment.StateFragment;

import cn.dreamn.qianji_auto.func.Fun;
import cn.dreamn.qianji_auto.func.Storage;
import cn.dreamn.qianji_auto.utils.MMKVUtils;
import cn.dreamn.qianji_auto.utils.Utils;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;

import cn.dreamn.qianji_auto.func.mobileInfoUtil;

import butterknife.BindView;

/**
 * 程序主页面
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    /**
     * 侧边栏
     */
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private String[] mTitles;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        initListeners();

        permission();
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(mTitles[0]);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);



        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new StateFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);
    }



    protected void initListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //侧边栏点击事件
        navView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.isCheckable()) {
                drawerLayout.closeDrawers();
                return handleNavigationItemSelected(menuItem);
            } else {
                switch (menuItem.getItemId()) {
                    case R.id.nav_set:
                        openNewPage(SettingsFragment.class);
                        break;
                    case R.id.nav_about:
                        openNewPage(AboutFragment.class);
                        break;
                    case R.id.nav_map:
                        openNewPage(MapFragment.class);
                        break;
                    case R.id.nav_help:
                        Fun.browser(this, getString(R.string.url_learn));
                        break;
                    case R.id.nav_log:
                        openNewPage(LogFragment.class);
                        break;
                    case R.id.nav_learn:
                        openNewPage(LearnFragment.class);
                        break;
                    default:
                        XToastUtils.success("该功能正在加紧制作中....");
                        break;
                }
            }
            return true;
        });

        //主页事件监听
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_privacy:
                Utils.showPrivacyDialog(this, null);
                break;
            default:
                break;
        }
        return false;
    }

    @SingleClick
    @Override
    public void onClick(View v) {

    }

    //=============ViewPager===================//

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //================Navigation================//

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);

            updateSideNavStatus(menuItem);
            return true;
        }
        return false;
    }

    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem
     */
    private void updateSideNavStatus(MenuItem menuItem) {
        MenuItem side = navView.getMenu().findItem(menuItem.getItemId());
        if (side != null) {
            side.setChecked(true);
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }



    private void permission(){

        boolean isAgree = MMKVUtils.getBoolean("key_agree_privacy", false);
        if(!isAgree){
            Utils.showPrivacyDialog(this,null);

            CookieBar.builder(this)
                    .setTitle("权限申请")
                    .setMessage("[读写存储空间权限]\n我们需要该权限存储日志以及您的配置。")
                    .setDuration(-1)
                    .setLayoutGravity(Gravity.BOTTOM)
                    .setAction("授予权限", view13 -> {
                        myRequetPermission();
                    })
                    .show();
            CookieBar.builder(this)
                    .setTitle("权限申请")
                    .setMessage("[自启动与关联启动]\n我们需要该权限与支付宝等APP共享配置文件。")
                    .setDuration(-1)
                    .setLayoutGravity(Gravity.BOTTOM)
                    .setAction("授予权限", view13 -> {
                        mobileInfoUtil.jumpStartInterface(this);
                    })
                    .show();

            MMKVUtils.put("key_agree_privacy", true);
        }


    }
    /**
     * 权限申请
     */
    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//选择了“始终允许”
                    CookieBar.builder(this)
                            .setTitle("权限提醒")
                            .setMessage("您拒绝了读写存储空间权限，可能导致设置无法生效，日志无法写入等问题。")
                            .setDuration(-1)
                            .setLayoutGravity(Gravity.BOTTOM)
                            .setAction("我已知晓", view13 -> XToastUtils.toast("点击消失！"))
                            .show();
                }
            }
        }
    }
}
