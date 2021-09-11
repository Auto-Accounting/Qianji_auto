package cn.dreamn.qianji_auto;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;

import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.config.AppPageConfig;

import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.utils.runUtils.CrashHandler;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;


public class App extends Application {
    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
    }

    public static String getAppVerName() {
        return BuildConfig.VERSION_NAME;
    }

    public static int getAppVerCode() {
        return BuildConfig.VERSION_CODE;
    }

    public static String getAppPackage() {
        return BuildConfig.APPLICATION_ID;
    }


    /**
     * 初始化基础库
     */
    private void initLibs() {
        initFragment();
        initMMKV();
        initTheme();
        initToast();
        initDatabase();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        XXPermissions.setScopedStorage(true);

        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");

    }


    private void initDatabase() {
        DbManger.init(this);
    }

    private void initToast() {  // 初始化 Toast 框架

        ToastUtils.init(this);
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 20);
    }

    /**
     * 初始化主题框架
     */
    private void initTheme() {
        ThemeManager.init();
        //添加自定义组件换肤
    }

    private void initMMKV() {
        MMKV.initialize(this);
    }

    private void initFragment() {
        PageConfig.getInstance()
                //页面注册
                .setPageConfiguration(context -> {
                    //自动注册页面,是编译时自动生成的，build一MyApp
                    //BaseActivity
                    //BaseFragment下就出来了
                    return AppPageConfig.getInstance().getPages();
                })
                .debug(isDebug() ? "PageLog" : null)
                .setContainActivityClazz(BaseActivity.class)
                .init(this);
    }
}
