package cn.dreamn.qianji_auto.core.hook.hooks.qq;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks.Msg;
import cn.dreamn.qianji_auto.core.hook.hooks.qq.hooks.QLog;

public class QQ extends hookBase {
    static hookBase self = null;

    public static hookBase getInstance() {
        if (self == null)
            self = new QQ();
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            Msg.init(utils);
        } catch (Throwable e) {
            utils.log("hook出错！" + e.toString());
        }
        try {
            QLog.init(utils);
        } catch (Throwable e) {
            utils.log("hook出错！" + e.toString());
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
}
