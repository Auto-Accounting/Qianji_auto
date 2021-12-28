package cn.dreamn.qianji_auto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;

import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.config.AppPageConfig;

import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.ui.activity.LockActivity;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.AppFrontBackHelper;
import cn.dreamn.qianji_auto.utils.runUtils.CrashUtils;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.SecurityAccess;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


public class App extends Application {


    public static boolean isLock = false;
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
        addListen();
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


    private void addListen() {

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront(Activity activity) {
                //应用切到前台处理
                Intent intent = activity.getIntent();
                MMKV mmkv = MMKV.defaultMMKV();

                Bundle bundle = null;
                if (intent != null) {
                    bundle = intent.getExtras();
                }
                String app = null;
                SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(getApplicationContext(), "apps", Context.MODE_PRIVATE);
                if (sharedPreferences.getString("lock_qianji_lock", "false").equals("true")) {

                    app = sharedPreferences.getString("lock_qianji_app", null);
                    sharedPreferences.edit().putString("lock_qianji_lock", "false").apply();
                    sharedPreferences.edit().putString("lock_qianji_app", null).apply();
                }

                if (isLock || intent == null || bundle == null || bundle.size() == 0) {
                    if (app != null || mmkv.getInt("lock_style", SecurityAccess.LOCK_NONE) != SecurityAccess.LOCK_NONE) {
                        Intent intent1 = new Intent(activity, LockActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("app", app);
                        intent1.putExtras(bundle1);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }
                }

            }

            @Override
            public void onBack(Activity activity) {
                //应用切到后台处理
                isLock = true;
            }
        });

    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        CrashUtils crashUtils = CrashUtils.getInstance();
        crashUtils.init(this);
        initMMKV();
        initFragment();
        initTheme();
        initToast();
        initDatabase();
        initTasker();
        XXPermissions.setScopedStorage(true);
        MultiprocessSharedPreferences.setAuthority("cn.dreamn.qianji_auto.provider");
    }


    private void initDatabase() {
        Db.init(this);
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

    private void initTasker() {
        TaskThread.initThread(20);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        Configuration configuration = res.getConfiguration();
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
        return res;
    }
}
