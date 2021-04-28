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

import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;

import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookRedPackage {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
       try{
           hook801_802(mAppClassLoader,utils);
       }catch (Error|Exception e){
           try{
               hookmin800(mAppClassLoader,utils);
           }catch (Error|Exception e2){
              utils.log("hook红包失败！"+e2.toString());
           }
       }
    }

    public static void hook801_802( ClassLoader mAppClassLoader,Utils utils){
        Class<?> LuckyMoneyDetailUI = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI", mAppClassLoader);
        Class<?> qVar = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.q", mAppClassLoader);

        XposedHelpers.findAndHookMethod(LuckyMoneyDetailUI, "a", qVar, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                utils.log("hooked qvar");
                if (param.args[0] == null) {
                    utils.log("qVar = null");
                    return;
                }

                Object object = param.args[0];




               /* Field[] fields = qVar.getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object obj= f.get(object);
                    String str;
                    if(obj==null)str="null";
                    else str=obj.toString();
                     utils.log("属性名:" + f.getName() + " 属性值:" + str);
                }*/

                Field money = qVar.getDeclaredField("eht");
                Field remark = qVar.getDeclaredField("yPK");
                Field shopAccount = qVar.getDeclaredField("yVd");
                Field status = qVar.getDeclaredField("AcB");
                JSONObject jsonObject = new JSONObject();

                double m = Integer.parseInt(money.get(object).toString()) / 100.0d;

                jsonObject.put("money", String.valueOf(m));
                jsonObject.put("remark", remark.get(object).toString());
                jsonObject.put("shopAccount", shopAccount.get(object).toString());

                utils.log("-------红包信息-------", true);
                utils.log("微信JSON消息：" + jsonObject.toJSONString(), true);

                if(status.get(object).toString().equals("成功领到")){
                    utils.log("红包已被领取");
                    return;
                }


              Bundle bundle = new Bundle();
                bundle.putString("type", Receive.WECHAT);
                bundle.putString("data", jsonObject.toJSONString());
                bundle.putString("title", "微信收红包");
                bundle.putString("from", Wechat.RED_PACKAGE_RECEIVED);


                utils.send(bundle);

            }
        });
    }

    public static void hookmin800(ClassLoader mAppClassLoader,Utils utils){
        Class<?> LuckyMoneyDetailUI = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI", mAppClassLoader);
        Class<?> qVar = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.l", mAppClassLoader);

        XposedHelpers.findAndHookMethod(LuckyMoneyDetailUI, "a", qVar, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                utils.log("hooked qvar");
                if (param.args[0] == null) {
                    utils.log("qVar = null");
                    return;
                }

                Object object = param.args[0];

            /*    Field[] fields = qVar.getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object obj= f.get(object);
                    String str;
                    if(obj==null)str="null";
                    else str=obj.toString();
                    utils.log("属性名:" + f.getName() + " 属性值:" + str);
                }*/

                Field money = qVar.getDeclaredField("dEs");
                Field remark = qVar.getDeclaredField("rSK");
                Field shopAccount = qVar.getDeclaredField("rXz");
                Field status = qVar.getDeclaredField("rXx");
                JSONObject jsonObject = new JSONObject();

                double m = Integer.parseInt(money.get(object).toString()) / 100.0d;

                jsonObject.put("money", String.valueOf(m));
                jsonObject.put("remark", remark.get(object).toString());
                jsonObject.put("shopAccount", shopAccount.get(object).toString());

                utils.log("-------红包信息-------", true);
                utils.log("微信JSON消息：" + jsonObject.toJSONString(), true);


                if(status.get(object).toString().equals("成功领到")){
                    utils.log("红包已被领取");
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("type", Receive.WECHAT);
                bundle.putString("data", jsonObject.toJSONString());
                bundle.putString("title", "微信收红包");
                bundle.putString("from", Wechat.RED_PACKAGE_RECEIVED);


                utils.send(bundle);

            }
        });
    }

}
