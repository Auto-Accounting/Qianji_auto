package cn.dreamn.qianji_auto.core.hook.hooks.qq;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks.Msg;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks.QLog;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks.Setting;

public class QQ extends hookBase {
    final static hookBase self = new QQ();
    public static hookBase getInstance() {
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            Msg.init(utils);
        } catch (Throwable e) {
            utils.log("hook Msg 出错！" + e.toString());
        }
        try {
            if (utils.isDebug()) {
                QLog.init(utils);
            }
        } catch (Throwable e) {
            utils.log("hook QLog 出错！" + e.toString());
        }
        try {
            Setting.init(utils);
        } catch (Throwable e) {
            utils.log("hook Setting 出错！" + e.toString());
        }
    }

    @Override
    public String getPackPageName() {
        return "com.tencent.mobileqq";
    }

    @Override
    public String getAppName() {
        return "QQ";
    }


    @Override
    public boolean needHelpFindApplication() {
        return true;
    }
    @Override
    public int hookIndex() {
        return 1;
    }
}
