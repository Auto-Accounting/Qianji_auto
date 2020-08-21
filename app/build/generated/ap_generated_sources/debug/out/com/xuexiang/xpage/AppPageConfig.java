package com.xuexiang.xpage;

import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.model.PageInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>这是PageConfigProcessor自动生成的类，用以自动进行页面的注册。</p>
 * <p><a href="mailto:xuexiangjys@163.com">Contact me.</a></p>
 *
 * @author xuexiang 
 * @date 2020-08-21 11:36:14
 */
public class AppPageConfig {
  private static AppPageConfig sInstance;

  private List<PageInfo> mPages;

  private AppPageConfig() {
    mPages = new ArrayList<>();
    mPages.add(new PageInfo("日志", "cn.dreamn.qianji_auto.fragment.LogFragment", "{\"\":\"\"}", CoreAnim.slide, -1));
    mPages.add(new PageInfo("插件设置", "cn.dreamn.qianji_auto.fragment.SettingsFragment", "{\"\":\"\"}", CoreAnim.slide, -1));
    mPages.add(new PageInfo("资产映射", "cn.dreamn.qianji_auto.fragment.MapFragment", "{\"\":\"\"}", CoreAnim.slide, -1));
    mPages.add(new PageInfo("语义学习", "cn.dreamn.qianji_auto.fragment.LearnFragment", "{\"\":\"\"}", CoreAnim.slide, -1));
    mPages.add(new PageInfo("StateFragment", "cn.dreamn.qianji_auto.fragment.StateFragment", "{\"\":\"\"}", CoreAnim.none, -1));
    mPages.add(new PageInfo("关于", "cn.dreamn.qianji_auto.fragment.AboutFragment", "{\"\":\"\"}", CoreAnim.slide, -1));
  }

  public static AppPageConfig getInstance() {
    if (sInstance == null) {
        synchronized (AppPageConfig.class) {
            if (sInstance == null) {
                sInstance = new AppPageConfig();
            }
        }
    }
    return sInstance;
  }

  public List<PageInfo> getPages() {
    return mPages;
  }
}
