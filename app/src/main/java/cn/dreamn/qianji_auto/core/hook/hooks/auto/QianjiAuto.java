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

package cn.dreamn.qianji_auto.core.hook.hooks.auto;

import android.content.Context;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class QianjiAuto extends hookBase {

    static final hookBase self = new QianjiAuto();

    public static hookBase getInstance() {
        return self;
    }

    @Override
    public void hookLoadPackage() {
        XposedHelpers.findAndHookMethod("cn.dreamn.qianji_auto.setting.AppStatus", mAppClassLoader, "xposedActive", Context.class, XC_MethodReplacement.returnConstant(true));
    }


    @Override
    public String getPackPageName() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    public String getAppName() {
        return "自动记账";
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
