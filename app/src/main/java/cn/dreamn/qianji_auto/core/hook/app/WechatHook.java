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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.core.hook.Task;
import cn.dreamn.qianji_auto.utils.tools.DpUtil;
import cn.dreamn.qianji_auto.utils.tools.StyleUtil;
import cn.dreamn.qianji_auto.utils.tools.ViewUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class WechatHook extends HookBase {
    private static WechatHook wechatHook;
    public static synchronized WechatHook getInstance() {
        if (wechatHook == null) {
            wechatHook = new WechatHook();
        }
        return wechatHook;
    }
    @Override
    public void hookFirst() throws Error {

        hookSetting();
        hookMsg();
        hookNickName();
        hookPayTools();
        hookRedpackage();


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
        return new String[]{
                "8.0.0", "8.0.1", "8.0.2"
        };
    }


    private void hookButton() {
        // 微信首页添加按钮
        String hookClass = "com.tencent.mm.ui.LauncherUI";
        String hookMethodName = "onCreateOptionsMenu";
        XposedHelpers.findAndHookMethod(hookClass, mAppClassLoader, hookMethodName, Menu.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Task.onMain(100, () -> {
                    Menu menu = (Menu) param.args[0];
                    menu.add(0, 230, 0, "自动记账");
                    for (int i = 0; i < menu.size(); i++) {
                        if (menu.getItem(i).getItemId() != 230) continue;
                        menu.getItem(i).setOnMenuItemClickListener(item -> {
                            Logi("listen");
                            //启动自动记账

                            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("cn.dreamn.qianji_auto");
                            if (intent != null) {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                            return false;
                        });
                    }
                });

            }
        });
    }

    private void hookNickName() {
        //获取昵称
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.d.ad", mAppClassLoader, "setMMTitle", CharSequence.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String UserName = param.args[0].toString();
                writeData("userName", UserName);
                Logi("微信名：" + UserName);
            }
        });
    }



    private void hookMsg() {
        Logi("微信hook启动", true);
        Class<?> SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", mAppClassLoader);
        XposedHelpers.findAndHookMethod(SQLiteDatabase, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Task.onMain(() -> {
                    try {
                        ContentValues contentValues = (ContentValues) param.args[2];
                        String tableName = (String) param.args[0];
                        String arg = (String) param.args[1];

                        Logi("----------------\n " +
                                "arg1" + tableName + "\n" +
                                "arg2：" + arg + "\n" + "" +
                                "arg3：" + contentValues.toString()
                        );

                        if (!tableName.equals("message") || TextUtils.isEmpty(tableName)) {
                            return;
                        }
                        Integer type = contentValues.getAsInteger("type");
                        if (null == type) {
                            return;
                        }
                        String contentStr = contentValues.getAsString("content");

                        //转账消息
                        if (type == 419430449) {
                            Integer isSend = contentValues.getAsInteger("isSend");
                            if (isSend == 1) {
                                //我发出去的转账
                                String talker = contentValues.getAsString("talker");
                                XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                                String xml = xmlToJson.toString();
                                JSONObject msg = JSONObject.parseObject(xml);

                                Logi("-------转账信息-------", true);
                                Logi("微信JSON消息：" + xml, true);

                                JSONObject wcpayinfo = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

                                wcpayinfo.put("talker", talker);
                                wcpayinfo.put("nickName", readData("userName"));
                                wcpayinfo.put("payTools", readData("cache_wechat_paytool"));//缓存的支付方式
                                Bundle bundle = new Bundle();
                                bundle.putString("type", Receive.WECHAT);
                                bundle.putString("data", wcpayinfo.toJSONString());
                                bundle.putString("title", "微信转账");
                                bundle.putString("from", Wechat.PAYMENT_TRANSFER);


                                send(bundle);
                            }
                        } else if (type == 436207665) {
                            Integer isSend = contentValues.getAsInteger("isSend");
                            if (isSend == 1) {
                                //我发出去的转账
                                String talker = contentValues.getAsString("talker");
                                XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                                String xml = xmlToJson.toString();
                                JSONObject msg = JSONObject.parseObject(xml);

                                Logi("-------红包信息-------", true);
                                Logi("微信JSON消息：" + xml, true);

                                JSONObject wcpayinfo = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("wcpayinfo");

                                wcpayinfo.put("talker", talker);
                                wcpayinfo.put("nickName", readData("userName"));
                                wcpayinfo.put("payTools", readData("cache_wechat_paytool"));//缓存的支付方式

                                wcpayinfo.put("money", readData("cache_wechat_payMoney"));//缓存的金额

                                Bundle bundle = new Bundle();
                                bundle.putString("type", Receive.WECHAT);
                                bundle.putString("data", wcpayinfo.toJSONString());
                                bundle.putString("title", "微信红包");
                                bundle.putString("from", Wechat.RED_PACKAGE);


                                send(bundle);
                            }

                        } else if (type == 318767153) {
                            Logi("微信XML消息：" + contentStr, true);

                            try {


                                XmlToJson xmlToJson = new XmlToJson.Builder(contentStr).build();
                                String xml = xmlToJson.toString();
                                JSONObject msg = JSONObject.parseObject(xml);
                                JSONObject mmreader = msg.getJSONObject("msg").getJSONObject("appmsg").getJSONObject("mmreader");

                                String title = mmreader.getJSONObject("template_header").getString("title");
                                String display_name = mmreader.getJSONObject("template_header").getString("display_name");
                                JSONObject jsonObject = mmreader.getJSONObject("line_content");
                                if (jsonObject == null) {
                                    try {
                                        jsonObject = mmreader.getJSONObject("template_detail").getJSONObject("line_content");
                                    } catch (Exception e) {
                                        //没有获取到
                                    }
                                }
                                if (jsonObject == null) return;
                                jsonObject.put("display_name", display_name);


                                Logi("收到消息：" + xml, true);
                                Logi("-------消息开始解析-------");
                                Bundle bundle = new Bundle();
                                bundle.putString("type", Receive.WECHAT);
                                bundle.putString("data", jsonObject.toJSONString());
                                bundle.putString("title", title);
                                Logi(title);
                                switch (title) {
                                    case "微信支付凭证":
                                        Logi("-------微信支付凭证-------");
                                        bundle.putString("from", Wechat.PAYMENT);
                                        break;
                                    case "收款到账通知":
                                        Logi("-------收款到账通知-------");
                                        bundle.putString("from", Wechat.RECEIVED_QR);
                                        break;
                                    case "转账收款汇总通知":
                                        Logi("-------转账收款汇总通知-------");
                                        bundle.putString("from", Wechat.PAYMENT_TRANSFER_RECEIVED);
                                        break;
                                    case "转账退款到账通知":
                                        Logi("-------转账退款到账通知-------");
                                        bundle.putString("from", Wechat.PAYMENT_TRANSFER_REFUND);
                                        break;
                                    case "红包退款到账通知":
                                        Logi("-------红包退款到账通知-------");
                                        bundle.putString("from", Wechat.RED_REFUND);
                                    case "退款到账通知":
                                        Logi("-------退款到账通知-------");
                                        bundle.putString("from", Wechat.PAYMENT_REFUND);
                                        break;
                                    default:
                                        Logi("-------未知数据结构-------", true);
                                        bundle.putString("from", Wechat.CANT_UNDERSTAND);
                                        break;

                                }
                                send(bundle);
                            }catch (Exception e){
                                Logi("JSON错误" + e.toString(), false);
                            }

                        }

                    } catch (Exception e) {
                        Logi("获取账单信息出错：" + e.getMessage(), true);
                    }
                });

            }
        });
    }


    private void hookPayTools() {
        Class<?> MMKRichLabelView = XposedHelpers.findClass("com.tencent.kinda.framework.widget.base.MMKRichLabelView", mAppClassLoader);
        Class<?> KTex = XposedHelpers.findClass("com.tencent.kinda.gen.KText", mAppClassLoader);
        //com.tencent.kinda.framework.widget.base.MMKRichText
        Class<?> MMKRichText = XposedHelpers.findClass("com.tencent.kinda.framework.widget.base.MMKRichText", mAppClassLoader);
        XposedHelpers.findAndHookMethod(MMKRichLabelView, "setRichText", KTex, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Method get = MMKRichText.getDeclaredMethod("get");
                if (param.args[0] == null) {
                    Logi("arg1 is null");
                    return;
                }
                String data = get.invoke(param.args[0]).toString();
                Logi("设置数据：" + data);
                if (data.contains("零钱") || (data.contains("卡(") && data.endsWith(")"))) {
                    writeData("cache_wechat_paytool", data);
                    Logi("识别的账户名：" + data);
                } else if (data.startsWith("￥")) {
                    //金额
                    writeData("cache_wechat_payMoney", data);
                } else if (data.contains("向") && data.endsWith(")转账")) {
                    //转账人
                    writeData("cache_wechat_payUser", data);
                }
            }

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });

    }

    private void hookRedpackage() {

        Class<?> LuckyMoneyDetailUI = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI", mAppClassLoader);
        Class<?> qVar = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.q", mAppClassLoader);

        XposedHelpers.findAndHookMethod(LuckyMoneyDetailUI, "a", qVar, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Logi("hooked qvar");
                if (param.args[0] == null) {
                    Logi("qVar = null");
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
                    Logi("属性名:" + f.getName() + " 属性值:" + str);
                }*/
                Field money = qVar.getDeclaredField("eht");
                Field remark = qVar.getDeclaredField("yPK");
                Field shopAccount = qVar.getDeclaredField("yVd");

                JSONObject jsonObject = new JSONObject();

                double m = Integer.parseInt(money.get(object).toString()) / 100.0d;

                jsonObject.put("money", String.valueOf(m));


                jsonObject.put("remark", remark.get(object).toString());
                jsonObject.put("shopAccount", shopAccount.get(object).toString());

                Logi("-------红包信息-------", true);
                Logi("微信JSON消息：" + jsonObject.toJSONString(), true);


                Bundle bundle = new Bundle();
                bundle.putString("type", Receive.WECHAT);
                bundle.putString("data", jsonObject.toJSONString());
                bundle.putString("title", "微信收红包");
                bundle.putString("from", Wechat.RED_PACKAGE_RECEIVED);


                send(bundle);

            }
        });


    }

    private void hookSetting() {
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                Activity activity = (Activity) param.thisObject;
                final String activityClzName = activity.getClass().getName();
                if (activityClzName.contains("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")) {
                    Task.onMain(100, () -> doSettingsMenuInject(activity));
                }
            }
        });

    }

    private void doSettingsMenuInject(final Activity activity) {
        ListView itemView = (ListView) ViewUtil.findViewByName(activity, "android", "list");
        if (ViewUtil.findViewByText(itemView, "自动记账") != null
                || isHeaderViewExistsFallback(itemView)) {
            return;
        }

        boolean isDarkMode = StyleUtil.isDarkMode(activity);

        LinearLayout settingsItemRootLLayout = new LinearLayout(activity);
        settingsItemRootLLayout.setOrientation(LinearLayout.VERTICAL);
        settingsItemRootLLayout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        settingsItemRootLLayout.setPadding(0, DpUtil.dip2px(activity, 20), 0, 0);

        LinearLayout settingsItemLinearLayout = new LinearLayout(activity);
        settingsItemLinearLayout.setOrientation(LinearLayout.VERTICAL);

        settingsItemLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout itemHlinearLayout = new LinearLayout(activity);
        itemHlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemHlinearLayout.setWeightSum(1);
        itemHlinearLayout.setBackground(ViewUtil.genBackgroundDefaultDrawable(isDarkMode ? 0xFF191919 : Color.WHITE, isDarkMode ? 0xFF1D1D1D : 0xFFE5E5E5));
        itemHlinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemHlinearLayout.setClickable(true);
        itemHlinearLayout.setOnClickListener(view -> {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("cn.dreamn.qianji_auto");
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        int defHPadding = DpUtil.dip2px(activity, 15);

        TextView itemNameText = new TextView(activity);
        itemNameText.setTextColor(isDarkMode ? 0xFFD3D3D3 : 0xFF353535);
        itemNameText.setText("自动记账");
        itemNameText.setGravity(Gravity.CENTER_VERTICAL);
        itemNameText.setPadding(DpUtil.dip2px(activity, 16), 0, 0, 0);
        itemNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, StyleUtil.TEXT_SIZE_BIG);

        TextView itemSummerText = new TextView(activity);
        StyleUtil.apply(itemSummerText);
        itemSummerText.setText(BuildConfig.VERSION_NAME);
        itemSummerText.setGravity(Gravity.CENTER_VERTICAL);
        itemSummerText.setPadding(0, 0, defHPadding, 0);
        itemSummerText.setTextColor(isDarkMode ? 0xFF656565 : 0xFF999999);


        itemHlinearLayout.addView(itemNameText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        itemHlinearLayout.addView(itemSummerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View lineView = new View(activity);
        lineView.setBackgroundColor(isDarkMode ? 0xFF2E2E2E : 0xFFD5D5D5);
        settingsItemLinearLayout.addView(lineView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        settingsItemLinearLayout.addView(itemHlinearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dip2px(activity, 55)));

        settingsItemRootLLayout.addView(settingsItemLinearLayout);
        settingsItemRootLLayout.setTag(BuildConfig.APPLICATION_ID);

        itemView.addHeaderView(settingsItemRootLLayout);

    }

    private boolean isHeaderViewExistsFallback(ListView listView) {
        if (listView == null) {
            return false;
        }
        if (listView.getHeaderViewsCount() <= 0) {
            return false;
        }
        try {
            Field mHeaderViewInfosField = ListView.class.getDeclaredField("mHeaderViewInfos");
            mHeaderViewInfosField.setAccessible(true);
            ArrayList<ListView.FixedViewInfo> mHeaderViewInfos = (ArrayList<ListView.FixedViewInfo>) mHeaderViewInfosField.get(listView);
            if (mHeaderViewInfos != null) {
                for (ListView.FixedViewInfo viewInfo : mHeaderViewInfos) {
                    if (viewInfo.view == null) {
                        continue;
                    }
                    // Object tag = viewInfo.view.getTag();

                }
            }
        } catch (Exception e) {
            Logi(e.toString(),true);
        }
        return false;
    }
}
