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

package cn.dreamn.qianji_auto.core.hook.app.wechat;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.core.hook.app.wechat.hooks.CheckHook;
import cn.dreamn.qianji_auto.core.hook.app.wechat.hooks.LoginInfo;
import cn.dreamn.qianji_auto.core.hook.app.wechat.hooks.RedPackage;
import cn.dreamn.qianji_auto.core.hook.app.wechat.hooks.Setting;

public class Wechat extends HookBase {


    @Override
    public void hookFirst() throws Error {
        try {
            CheckHook.init(utils);
        } catch (Throwable e) {
            utils.log("微信 CheckHook HookError " + e.toString());
        }
        utils.log("hook check 成功");
       /* try {
            OpenLog.init(utils);
        } catch (Throwable e) {
            utils.log("微信 Log HookError " + e.toString());
        }*/
        try {
            LoginInfo.init(utils);
        } catch (Throwable e) {
            utils.log("微信 LoginInfo HookError " + e.toString());
        }
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
    public String[] getAppVer() {
        return null;
    }

    @Override
    public Integer getHookIndex() {
        return 2;
    }
}
