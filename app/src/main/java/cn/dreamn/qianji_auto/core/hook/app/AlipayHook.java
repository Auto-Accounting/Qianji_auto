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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.base.Receive;
import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.hook.HookBase;
import cn.dreamn.qianji_auto.core.hook.Task;
import cn.dreamn.qianji_auto.utils.tools.DpUtil;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.StyleUtil;
import cn.dreamn.qianji_auto.utils.tools.ViewUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


public class AlipayHook extends HookBase {
    private static AlipayHook alipayHook;

    public static synchronized AlipayHook getInstance() {
        if (alipayHook == null) {
            alipayHook = new AlipayHook();
        }
        return alipayHook;
    }

    @Override
    public void hookFirst() throws Error {

        hookSafe();
        hookRed();
        hookReceive();
        hookSetting();
        hookPayUi();

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
    public String[] getAppVer() {
        return new String[]{
                "10.2.13.9020",
        };
    }

    @Override
    public int getHookId() {
        return 1;
    }

    /**
     * hook掉安全校验
     */
    private void hookSafe() {
        try {

            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.alipay.apmobilesecuritysdk.scanattack.common.ScanAttack", mAppClassLoader), "getScanAttackInfo", Context.class, int.class, int.class, boolean.class, int.class, int.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.getResult() != null)
                        Logi("支付宝原本监测到的安全信息：" + param.getResult().toString(), true);
                    param.setResult(null);
                    Logi("hook 支付宝安全监测 succeed！", true);
                }

            });

        } catch (Error | Exception e) {
            Logi("hook 支付宝安全监测 error :" + e.toString(), false);
        }
    }

    private void hookPayUi() {
        Class<?> LogUtil = XposedHelpers.findClass("com.alipay.android.msp.utils.LogUtil", mAppClassLoader);
        XposedHelpers.findAndHookMethod(LogUtil, "record", int.class, String.class, String.class, String.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                String str = methodHookParam.args[2].toString();
                String data = methodHookParam.args[3].toString();
                try {
                    if (str.equals("MspUIClient:handleUiShow")) {
                        //找到所需
                        data = data.substring("data=".length());
                        JSONObject parseObject = JSON.parseObject(data);
                        if (parseObject.containsKey("data")) {
                            JSONObject parseObject2 = JSON.parseObject(parseObject.getString("data"));
                            if (parseObject2.containsKey("costTitle")) {
                                writeData("alipay_cache_shopremark", parseObject2.getString("product"));
                                writeData("alipay_cache_money", parseObject2.getString("cost"));
                                writeData("alipay_cache_payTool", parseObject2.getString("payTool"));
                            }
                        }
                    }
                } catch (Exception e) {
                    Logi("出现错误" + e.toString(), true);
                }
            }
        });

    }

    private void hookRed() {

        Class<?> proguard = XposedHelpers.findClass("com.alipay.mobile.redenvelope.proguard.c.b", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);


        XposedHelpers.findAndHookMethod(proguard, "onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = param.args[0].toString();
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length(), data.indexOf("], pushData=,"));

                JSONObject jsonObject = JSON.parseObject(data);
                if (!jsonObject.containsKey("pl")) return;
                String str = jsonObject.getString("pl");
                JSONObject jsonObject1 = JSON.parseObject(str);
                if (!jsonObject1.containsKey("templateJson")) return;
                String templateJson = jsonObject1.getString("templateJson");
                JSONObject jsonObject2 = JSON.parseObject(templateJson);
                if (!jsonObject2.containsKey("statusLine1Text")) return;
                if (!jsonObject2.containsKey("title")) return;
                if (!jsonObject2.containsKey("subtitle")) return;
                if (!jsonObject2.getString("subtitle").contains("来自")) return;
                Logi("hook 支付宝收到红包 succeed！", true);
                Logi("红包数据：" + data);

                Bundle bundle = new Bundle();
                bundle.putString("from", Alipay.RED_RECEIVED);
                bundle.putString("type", Receive.ALIPAY);
                bundle.putString("data", jsonObject2.toJSONString());
                bundle.putString("title", "红包");
                send(bundle);


            }
        });
    }

    private void hookReceive() {

        Class<?> MsgboxInfoServiceImpl = XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.sync.d", mAppClassLoader);
        Class<?> SyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", mAppClassLoader);

        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl, "onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data = param.args[0].toString();
                data = data.substring(data.indexOf("msgData=[") + "msgData=[".length(), data.indexOf("], pushData=,"));

                try {
                    data = "[" + data + "]";
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        analyze(jsonArray.get(i).toString());
                    }
                } catch (Exception e) {
                    analyze(data);
                }

            }
        });
    }


    private void analyze(String data) {
        Logi("收到消息：" + data, true);
        JSONObject jsonObject = JSON.parseObject(data);
        if (!jsonObject.containsKey("pl")) return;
        String str = jsonObject.getString("pl");
        JSONObject jsonObject1 = JSON.parseObject(str);

        if (!jsonObject1.containsKey("templateType")) return;
        if (!jsonObject1.containsKey("templateName")) return;
        if (!jsonObject1.containsKey("title")) return;
        if (!jsonObject1.containsKey("content")) return;

        Logi("-------消息开始解析-------");
        Bundle bundle = new Bundle();
        bundle.putString("type", Receive.ALIPAY);
        String title = jsonObject1.getString("title");
        String templateName = jsonObject1.getString("templateName");
        if (title.equals("其他")) title = templateName;

        if (jsonObject1.getString("templateType").equals("BN")) {
            JSONObject content = jsonObject1.getJSONObject("content");
            content.put("alipay_cache_shopremark", readData("alipay_cache_shopremark"));
            content.put("alipay_cache_money", readData("alipay_cache_money"));
            content.put("alipay_cache_payTool", readData("alipay_cache_payTool"));
            bundle.putString("data", content.toJSONString());
            // String title = jsonObject1.getString("title");
            Logi(title);
            bundle.putString("title", title);
            switch (title) {
                case "转账收款到余额宝":
                    Logi("-------转账收款到余额宝-------");
                    bundle.putString("from", Alipay.TRANSFER_YUEBAO);
                    break;
                case "转账到账成功":
                    Logi("-------转账到账成功-------");
                    bundle.putString("from", Alipay.TRANSFER_SUCCESS_ACCOUNT);
                    break;
                case "余额宝-笔笔攒-单笔攒入":
                case "笔笔攒自动存入消息":
                    Logi("-------余额宝-笔笔攒-单笔攒入-------");
                    bundle.putString("from", Alipay.BIBIZAN);
                    break;
                case "资金到账通知":
                    Logi("-------资金到账通知-------");
                    bundle.putString("from", Alipay.FUNDS_ARRIVAL);
                    break;
                case "充值-普通充值":
                    Logi("-------充值-普通充值-------");
                    bundle.putString("from", Alipay.PAYMENT_ORDING);
                    break;
                case "付款成功":
                    content.put("cache", true);
                    Logi("-------付款成功-------");
                    bundle.putString("from", Alipay.PAYMENT_SUCCESS);
                    break;
                case "余额宝-蚂蚁星愿自动攒入":
                    Logi("-------余额宝-蚂蚁星愿自动攒入-------");
                    bundle.putString("from", Alipay.MAYI);
                    break;
                case " 红包到账通知":
                    Logi("------- 红包到账通知-------");
                    bundle.putString("from", Alipay.CLIENT_CASH);
                    break;
                case "转账成功":
                    Logi("-------转账成功-------");
                    bundle.putString("from", Alipay.TRANSFER_SUCCESS);
                    break;
                case "退款通知":
                    Logi("-------退款通知-------");
                    bundle.putString("from", Alipay.REFUND);
                    break;
                case "收到一笔转账":
                    Logi("-------收到一笔转账-------");
                    bundle.putString("from", Alipay.TRANSFER_RECEIVED);
                    break;
                case "余额宝-自动转入":
                case "余额宝-单次转入":
                    Logi("-------余额宝自动、单次转入------");
                    bundle.putString("from", Alipay.TRANSFER_INTO_YUEBAO);
                    break;
                case "还款到账成功":
                    Logi("-------还款到账成功------");
                    bundle.putString("from", Alipay.CARD_REPAYMENT);
                    break;
                default:
                    Logi("-------未知数据结构-------", true);
                    bundle.putString("from", Alipay.CANT_UNDERSTAND);
                    break;
            }
            send(bundle);
        } else if (jsonObject1.getString("templateType").equals("S")) {
            JSONObject content = jsonObject1.getJSONObject("extraInfo");
            content.put("extra", jsonObject1.getString("content"));
            content.put("alipay_cache_shopremark", readData("alipay_cache_shopremark"));
            content.put("alipay_cache_money", readData("alipay_cache_money"));
            content.put("alipay_cache_payTool", readData("alipay_cache_payTool"));
            bundle.putString("data", content.toJSONString());
            //String title = jsonObject1.getString("title");
            Logi(title);
            bundle.putString("title", title);
            switch (title) {
                case "商家服务":
                    return;//没什么卵用
                case "商家服务·收款到账":
                    Logi("-------商家服务·收款到账-------");
                    bundle.putString("from", Alipay.QR_COLLECTION);
                    break;
                case "网商银行·余利宝":
                    Logi("-------网商银行·余利宝-------");
                    bundle.putString("from", Alipay.REC_YULIBAO);
                    break;
                case "网商银行":
                    Logi("-------网商银行-------");
                    bundle.putString("from", Alipay.REPAYMENT);
                    break;
                case "蚂蚁财富·我的余额宝":
                    Logi("-------蚂蚁财富·我的余额宝-------");
                    bundle.putString("from", Alipay.REC_YUEBAO);
                    break;
                default:
                    Logi("-------未知数据结构-------", true);
                    bundle.putString("from", Alipay.CANT_UNDERSTAND);
                    break;
            }
            send(bundle);
        }

    }

    private void hookSetting() {
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) {
                final Activity activity = (Activity) param.thisObject;
                final String activityClzName = activity.getClass().getName();
                if (activityClzName.contains(".MySettingActivity")) {
                    Task.onMain(100, () -> doSettingsMenuInject(activity));
                }
            }
        });
    }

    private void doSettingsMenuInject(final Activity activity) {
        Logi("hook支付宝设置");
        int listViewId = activity.getResources().getIdentifier("setting_list", "id", "com.alipay.android.phone.openplatform");

        ListView listView = activity.findViewById(listViewId);

        View lineTopView = new View(activity);
        lineTopView.setBackgroundColor(0xFFEEEEEE);

        LinearLayout itemHlinearLayout = new LinearLayout(activity);
        itemHlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemHlinearLayout.setWeightSum(1);
        itemHlinearLayout.setBackground(ViewUtil.genBackgroundDefaultDrawable(Color.WHITE, 0xFFD9D9D9));
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
        StyleUtil.apply(itemNameText);
        itemNameText.setText("自动记账");
        itemNameText.setGravity(Gravity.CENTER_VERTICAL);
        itemNameText.setPadding(defHPadding, 0, 0, 0);
        itemNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, StyleUtil.TEXT_SIZE_BIG);

        TextView itemSummerText = new TextView(activity);
        StyleUtil.apply(itemSummerText);
        itemSummerText.setText(BuildConfig.VERSION_NAME);
        itemSummerText.setGravity(Gravity.CENTER_VERTICAL);
        itemSummerText.setPadding(0, 0, defHPadding, 0);
        itemSummerText.setTextColor(0xFF999999);


        itemHlinearLayout.addView(itemNameText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        itemHlinearLayout.addView(itemSummerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View lineBottomView = new View(activity);
        lineBottomView.setBackgroundColor(0xFFEEEEEE);

        LinearLayout rootLinearLayout = new LinearLayout(activity);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
        rootLinearLayout.addView(lineTopView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        rootLinearLayout.addView(itemHlinearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dip2px(activity, 45)));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lineParams.bottomMargin = DpUtil.dip2px(activity, 20);
        rootLinearLayout.addView(lineBottomView, lineParams);

        listView.addHeaderView(rootLinearLayout);
    }
}
