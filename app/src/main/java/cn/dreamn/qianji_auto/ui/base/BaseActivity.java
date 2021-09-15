
package cn.dreamn.qianji_auto.ui.base;

import android.content.Context;
import android.os.Bundle;

import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.CoreSwitchBean;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;

/**
 * 基础容器Activity
 *
 * @author XUE
 * @since 2019/3/22 11:21
 */
public class BaseActivity extends XPageActivity {

    Unbinder mUnbinder;


    @Override
    protected void attachBaseContext(Context newBase) {
      super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme(this);
        super.onCreate(savedInstanceState);
        mUnbinder = ButterKnife.bind(this);


    }

    private void initTheme(Context context) {
        ThemeManager manager=new ThemeManager(context);
        manager.setTheme();
    }

    /**
     * 打开fragment
     *
     * @param clazz          页面类
     * @param addToBackStack 是否添加到栈中
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> T openPage(Class<T> clazz, boolean addToBackStack) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
                .setAddToBackStack(addToBackStack);
        return (T) openPage(page);
    }


    /**
     * 打开fragment
     *
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> T openNewPage(Class<T> clazz) {
        CoreSwitchBean page = new CoreSwitchBean(clazz)
                .setNewActivity(true);
        return (T) openPage(page);
    }

    public <T extends XPageFragment> T openNewPage(Class<T> clazz, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("str", str);
        CoreSwitchBean page = new CoreSwitchBean(clazz)
                .setNewActivity(true).setBundle(bundle);
        return (T) openPage(page);
    }

    /**
     * 切换fragment
     *
     * @param clazz 页面类
     * @return 打开的fragment对象
     */
    public <T extends XPageFragment> T switchPage(Class<T> clazz) {
        return openPage(clazz, false);
    }



    @Override
    protected void onRelease() {
        mUnbinder.unbind();
        super.onRelease();
    }

    @Override
    protected void onResume() {

        super.onResume();

    }
}
