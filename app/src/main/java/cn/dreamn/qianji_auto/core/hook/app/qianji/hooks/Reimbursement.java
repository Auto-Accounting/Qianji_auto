package cn.dreamn.qianji_auto.core.hook.app.qianji.hooks;

import android.icu.math.BigDecimal;
import android.net.Uri;
import android.util.Base64;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Reimbursement {
    public static void init(Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> WebViewActivity = mAppClassLoader.loadClass("com.mutangtech.qianji.ui.webview.WebViewActivity");
        //增加报销校验
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.bill.auto.AddBillIntentAct", mAppClassLoader, "e", int.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) {
                int type = (int) param.args[0];
                param.setResult((boolean) param.getResult() || type == 998);
                // utils.log("type",String.valueOf(type),"hook方法1");
            }
        });
        Class<?> AutoTaskLog = mAppClassLoader.loadClass("com.mutangtech.qianji.data.model.AutoTaskLog");
        Class<?> BxManagePresenterImpl = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.BxManagePresenterImpl");
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.bill.auto.AddBillIntentAct", mAppClassLoader, "a", Uri.class, int.class, String.class, AutoTaskLog, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                Uri uri = (Uri) param.args[0];
                int type = (int) param.args[1];
                utils.log("uri", uri.toString(), "type", String.valueOf(type), "hook方法2");
                if (type != 998) return;
                //避免数据错误
                String data = new String(Base64.decode(uri.getQueryParameter("data"), Base64.URL_SAFE));
                String accountnameId = uri.getQueryParameter("accountnameId");
                long accountId = accountnameId != null ? Long.parseLong(accountnameId) : -1;
                double total = Double.parseDouble(uri.getQueryParameter("money"));

                HashMap<Long, Double> hashMap = new HashMap<>();
                long time = DateUtils.dateToStamp(uri.getQueryParameter("time"), "yyyy-MM-dd HH:mm:ss") / 1000;
                if (time <= 0) {
                    time = DateUtils.dateToStamp(DateUtils.getTime("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss") / 1000;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    int i = 0;
                    int size = jsonObject.size();
                    for (String str : jsonObject.keySet()) {
                        double money = Double.parseDouble(jsonObject.getString(str));
                        double input = 0.0d;
                        if (i < size - 1) {
                            double min = Math.min(total, money);
                            if (min >= 0.0d) {
                                input = min;
                            }
                            double[] dArr = new double[1];
                            dArr[0] = input;
                            total = subtract(total, dArr);
                        } else {
                            if (total >= 0.0d) {
                                input = total;
                            }

                        }
                        hashMap.put(Long.valueOf(str), input);
                        i++;
                    }

                    utils.log("数据", data, String.valueOf(accountId), String.valueOf(total), hashMap.toString(), String.valueOf(time));
                    //hashMap
                    //HashMap<Long, Double> hashMap, long j, double d2, long j2
                    Method doBaoXiao = BxManagePresenterImpl.getMethod("doBaoXiao", HashMap.class, long.class, double.class, long.class);
                    Class<?> s = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.s");
                    Object sObj = s.newInstance();
                    Class<?> d = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.r");
                    Constructor cs = BxManagePresenterImpl.getConstructor(d);
                    Object BxManagePresenterImplObj = cs.newInstance(sObj);
                    doBaoXiao.invoke(BxManagePresenterImplObj, hashMap, accountId, total, time);
                    // param.args[3]=null;

                    // XposedHelpers.callMethod(param.thisObject, "onSuccess");
                    //  Looper.prepare();
                    //   Toast.makeText(utils.getContext(),  "报销成功！", Toast.LENGTH_LONG).show();
                    //   Looper.loop();
                    //  XposedHelpers.callMethod(param.thisObject, "finishAndRemoveTask");
                    param.setThrowable(new Throwable("报销完成，后面不用管了~"));
                    //this.w0.doBaoXiao(hashMap, assetAccount != null ? assetAccount.getId().longValue() : -1, this.t0, j);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static double subtract(double d2, double... dArr) {
        if (dArr == null || dArr.length <= 0) {
            return d2;
        }
        BigDecimal valueOf = BigDecimal.valueOf(d2);
        for (double d3 : dArr) {
            valueOf = valueOf.subtract(BigDecimal.valueOf(d3));
        }
        return valueOf.doubleValue();
    }


}
