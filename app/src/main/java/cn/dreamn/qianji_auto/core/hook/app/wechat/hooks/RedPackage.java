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

package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import java.lang.reflect.Field;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.app.wechat.Info;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class RedPackage {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        try {
            hook801_802(mAppClassLoader, utils);
        } catch (Error | Exception e) {
            try {
                hookmin800(mAppClassLoader, utils);
            } catch (Error | Exception e2) {
                utils.log("hook红包失败！" + e2.toString());
            }
        }
    }

    public static void hook801_802(ClassLoader mAppClassLoader, Utils utils) {
        Class<?> LuckyMoneyDetailUI = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI", mAppClassLoader);
        Class<?> qVar = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.q", mAppClassLoader);

        XposedHelpers.findAndHookMethod(LuckyMoneyDetailUI, "a", qVar, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                setParam(param, utils, qVar);
            }
        });
    }

    public static void hookmin800(ClassLoader mAppClassLoader, Utils utils) {
        Class<?> LuckyMoneyDetailUI = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI", mAppClassLoader);
        Class<?> qVar = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.l", mAppClassLoader);

        XposedHelpers.findAndHookMethod(LuckyMoneyDetailUI, "a", qVar, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                setParam(param, utils, qVar);
            }
        });
    }

    private static void setParam(XC_MethodHook.MethodHookParam param, Utils utils, Class<?> qVar) throws IllegalAccessException, NoSuchFieldException {
        utils.log("hooked qvar");
        if (param.args[0] == null) {
            utils.log("qVar = null");
            return;
        }

        Object object = param.args[0];
        Field[] fields = qVar.getDeclaredFields();
        StringBuilder log = new StringBuilder("微信的红包相关数据\n");
        for (Field f : fields) {
            f.setAccessible(true);
            Object obj = f.get(object);
            String str;
            if (obj == null) str = "null";
            else str = obj.toString();
            log.append("属性名:").append(f.getName()).append(" 属性值:").append(str).append("\n");
        }

        utils.log(log.toString());

        Field money = qVar.getDeclaredField(Info.redPackage.money(utils));
        Field remark = qVar.getDeclaredField(Info.redPackage.remark(utils));
        Field shopAccount = qVar.getDeclaredField(Info.redPackage.shop(utils));
        Field status = qVar.getDeclaredField(Info.redPackage.status(utils));
        Field groups = qVar.getDeclaredField(Info.redPackage.groups(utils));
        double m = Integer.parseInt(money.get(object).toString()) / 100.0d;
        if (m == 0)//金额为0直接忽略
            return;
        String remarkStr = remark.get(object).toString();
        if (remarkStr == null || remarkStr.equals("")) {
            remarkStr = "大吉大利，恭喜发财";
        }
        //增加 isGroup
        String data = "money=%s,remark=%s,status=%s,shop=%s,totals=%s,title=微信收到红包";

        data = String.format(data, m, remarkStr, status.get(object).toString(), shopAccount.get(object).toString(), groups.get(object).toString());

        utils.log("红包数据：" + data);

        utils.sendString(data);
    }
}
