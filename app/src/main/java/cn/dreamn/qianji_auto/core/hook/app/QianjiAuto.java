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

package cn.dreamn.qianji_auto.core.hook.app;

import android.content.Context;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class QianjiAuto extends HookBase {
    private static QianjiAuto qianjiAuto;

    public static synchronized QianjiAuto getInstance() {
        if (qianjiAuto == null) {
            qianjiAuto = new QianjiAuto();
        }
        return qianjiAuto;
    }


    @Override
    public void hookFirst() throws Error {

        XposedHelpers.findAndHookMethod("cn.dreamn.qianji_auto.core.utils.Status", mAppClassLoader, "xposedActive", Context.class, XC_MethodReplacement.returnConstant(true));


    }

    @Override
    public String getPackPageName() {
        return "cn.dreamn.qianji_auto";
    }

    @Override
    public String getAppName() {
        return "自动记账";
    }

    @Override
    public String[] getAppVer() {
        return null;
    }

    @Override
    public int getHookId() {
        return 1;
    }
}
