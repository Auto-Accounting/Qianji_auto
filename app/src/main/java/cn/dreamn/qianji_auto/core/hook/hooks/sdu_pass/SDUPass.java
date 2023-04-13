package cn.dreamn.qianji_auto.core.hook.hooks.sdu_pass;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.sdu_pass.hooks.Pay;
import cn.dreamn.qianji_auto.core.hook.hooks.sdu_pass.hooks.Setting;

/**
 * @author JiunnTarn
 */
public class SDUPass extends hookBase {
    static final hookBase self = new SDUPass();

    public static hookBase getInstance() {
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            //山大v卡通设置
            Setting.init(utils);
        } catch (Throwable e) {
            utils.log("山大v卡通设置 hook 失败：" + e);
        }
        try {
            //山大v卡通支付
            Pay.init(utils);
            utils.log("山大v卡通支付 hook 成功");
        } catch (Throwable e) {
            utils.log("山大v卡通支付 hook 失败：" + e);
        }
    }


    @Override
    public String getPackPageName() {
        return "com.synjones.xuepay.sdu";
    }

    @Override
    public String getAppName() {
        return "山大v卡通";
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
