package cn.dreamn.qianji_auto;

import android.app.Application;
import android.content.Context;


import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.config.AppPageConfig;

import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;


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

    /**
     * 初始化基础库
     */
    private void initLibs() {
        initFragment();
        initMMKV();
        initTheme();

        initDatabase();
    }

    private void initDatabase() {
        DbManger.init(this);
    }


    /**
     * 初始化主题框架
     */
    private void initTheme() {
        ThemeManager.init();
        //添加自定义组件换肤
    }

    private void initMMKV(){
        MMKV.initialize(this);
    }
    private void initFragment(){
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
