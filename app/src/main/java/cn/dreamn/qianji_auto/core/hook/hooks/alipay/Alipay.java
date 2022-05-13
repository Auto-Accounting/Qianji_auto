/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.hook.hooks.alipay;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.hooks.hookPayUI;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.hooks.hookReceive;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.hooks.hookRed;
import cn.dreamn.qianji_auto.core.hook.hooks.alipay.hooks.hookSetting;


public class Alipay extends hookBase {
    static final hookBase self = new Alipay();

    public static hookBase getInstance() {
        return self;
    }

    @Override
    public void hookLoadPackage() {
        try {
            //支付宝设置
            hookSetting.init(utils);
        } catch (Throwable e) {
            utils.log("支付宝设置hook失败：" + e.toString());
        }

        //取消UI的Hook
        if (utils.isDebug()) {
            try {
                //支付宝支付UI
                hookPayUI.init(utils);
            } catch (Throwable e) {
                utils.log("支付宝支付UI hook失败：" + e.toString());
            }
        }
        try {
            //支付宝消息通知
            hookReceive.init(utils);
        } catch (Throwable e) {
            utils.log("支付宝消息通知hook失败：" + e.toString());
        }
        try {

            //支付宝红包
            hookRed.init(utils);
        } catch (Throwable e) {
            utils.log("支付宝红包hook失败：" + e.toString());
        }


    }




    @Override
    public String getPackPageName() {
        return "com.eg.android.AlipayGphone";
    }

    @Override
    public String getAppName() {
        return "支付宝";
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
