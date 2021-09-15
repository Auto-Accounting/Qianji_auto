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

package cn.dreamn.qianji_auto.core.hook.app.qianji;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.core.hook.app.qianji.hooks.AutoError;
import cn.dreamn.qianji_auto.core.hook.app.qianji.hooks.DataBase;
import cn.dreamn.qianji_auto.core.hook.app.qianji.hooks.LeftSide;
import cn.dreamn.qianji_auto.core.hook.app.qianji.hooks.Timeout;

public class Qianji extends HookBase {


    @Override
    public void hookBefore() {

    }

    @Override
    public void hookFirst() throws Error {

        try {
            DataBase.init(utils);
        } catch (Throwable e) {
            utils.log("钱迹 DataBase HookError " + e.toString());
        }
        try {
            Timeout.init(utils);
        } catch (Throwable e) {
            utils.log("钱迹 Timeout HookError " + e.toString());
        }
        try {
            AutoError.init(utils);
        } catch (Throwable e) {
            utils.log("钱迹 AutoError HookError " + e.toString());
        }
        try {
            LeftSide.init(utils);
        } catch (Throwable e) {
            utils.log("钱迹 LeftSide HookError " + e.toString());
        }

    }

    @Override
    public String getPackPageName() {
        return "com.mutangtech.qianji";
    }

    @Override
    public String getAppName() {
        return "钱迹";
    }

    @Override
    public String[] getAppVer() {
        return null;
    }

    @Override
    public Integer getHookIndex() {
        // 钱迹hook要hook第一个  获取 SQLiteDatabase
        return 1;
    }
}
