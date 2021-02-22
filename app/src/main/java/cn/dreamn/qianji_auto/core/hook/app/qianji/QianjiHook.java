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

import android.content.Intent;
import android.os.Bundle;

import cn.dreamn.qianji_auto.core.hook.HookBase;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class QianjiHook extends HookBase {


    @Override
    public void hookFirst() throws Error {
        /*Class<?> AddBillIntentAct = XposedHelpers.findClass("com.mutangtech.qianji.bill.auto.AddBillIntentAct", mAppClassLoader);
        XposedHelpers.findAndHookMethod(AddBillIntentAct, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                utils.log("钱迹自动记账窗口创建。", false);
            }
        });*/
        hookDb.init(utils);



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

}
