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

package cn.dreamn.qianji_auto.core.hook.hooks.wechat;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.LoginInfo;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.Msg;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.NickName;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.PayTools;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.RedPackage;
import cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks.Setting;

public class Wechat extends hookBase {
    static final hookBase self = new Wechat();
    public static hookBase getInstance() {
        return self;
    }


    @Override
    public void hookLoadPackage() {
/*
        if (utils.isDebug()) {
            try {
                OpenLog.init(utils);
            } catch (Throwable e) {
                utils.log("微信 Log HookError " + e.toString());
            }
        }*/
       /** try {
            LoginInfo.init(utils);
        } catch (Throwable e) {
            utils.log("微信 LoginInfo HookError " + e.toString());
        }**/
        try {
           Setting.init(utils);
        } catch (Throwable e) {
            utils.log("微信 Settings HookError " + e.toString());
        }
        try {
            RedPackage.init(utils);
        } catch (Throwable e) {
            utils.log("微信 RedPackage HookError " + e.toString());
        }
        try {
            NickName.init(utils);
        } catch (Throwable e) {
            utils.log("微信 NickName HookError " + e.toString());
        }
        try {
            PayTools.init(utils);
        } catch (Throwable e) {
            utils.log("微信 PayTools HookError " + e.toString());
        }
        try {
            Msg.init(utils);
        } catch (Throwable e) {
            utils.log("微信 Msg HookError " + e.toString());
        }

    }



    @Override
    public String getPackPageName() {
        return "com.tencent.mm";
    }

    @Override
    public String getAppName() {
        return "微信";
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
