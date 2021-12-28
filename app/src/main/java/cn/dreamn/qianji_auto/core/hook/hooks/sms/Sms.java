package cn.dreamn.qianji_auto.core.hook.hooks.sms;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.SmsIntent;

public class Sms extends hookBase {


    @Override
    public void hookLoadPackage() {
        try {
            SmsIntent.init(utils);
        } catch (Throwable e) {
            e.printStackTrace();
            utils.log(e.toString());
        }
    }

    @Override
    public void hookInitZygoteMain() {

    }

    @Override
    public String getPackPageName() {
        return "com.android.phone";
    }

    @Override
    public String getAppName() {
        return "短信";
    }

    @Override
    public Integer getHookIndex() {
        return 1;
    }

    @Override
    public boolean needHelpFindApplication() {
        return true;
    }


}
