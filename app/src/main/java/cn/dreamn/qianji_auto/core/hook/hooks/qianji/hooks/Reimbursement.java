package cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks;

import android.app.Activity;
import android.content.Intent;
import android.icu.math.BigDecimal;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Reimbursement {
    public static void init(Utils utils, JSONArray jsonArray) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        //增加报销校验
        XposedHelpers.findAndHookMethod("com.mutangtech.qianji.bill.auto.AddBillIntentAct", mAppClassLoader, jsonArray.getString(1), int.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) {
                int type = (int) param.args[0];
                if (type == 998) {
                    Activity activity = (Activity) param.thisObject;
                    Intent intent = activity.getIntent();
                    if (intent != null) {
                        Uri uri = activity.getIntent().getData();
                        if (uri != null) {
                            String data = new String(Base64.decode(uri.getQueryParameter("data"), Base64.URL_SAFE));
                            utils.log("url数据", uri.toString());

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
                                Class<?> BxManagePresenterImpl = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.BxManagePresenterImpl");
                                //hashMap
                                //HashMap<Long, Double> hashMap, long j, double d2, long j2
                                Method doBaoXiao = BxManagePresenterImpl.getMethod("doBaoXiao", HashMap.class, long.class, double.class, long.class);
                                Constructor<?>[] constructors = BxManagePresenterImpl.getConstructors();
                                for (Constructor<?> c : constructors) {
                                    //第一个，获取参数
                                    Class<?>[] parameterTypes =  c.getParameterTypes();
                                    if(parameterTypes.length==1){
                                        Class<?> p = parameterTypes[0];

                                        class MyInvocationHandler implements InvocationHandler{
                                            public Object invoke(Object proxy, Method method, Object[] args)
                                            {
                                                return  null;
                                            }
                                        }

                                        InvocationHandler handler = new MyInvocationHandler();
                                        Object proxy =  Proxy.newProxyInstance(
                                                p.getClassLoader(),
                                                new Class[] {p},
                                                handler);

                                        //  Class<?> s = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.s");
                                        //   Object sObj = s.newInstance();
                                        //  Class<?> d = mAppClassLoader.loadClass("com.mutangtech.qianji.bill.baoxiao.r");
                                       // Constructor<?> cs = BxManagePresenterImpl.getConstructor(d);
                                        Object BxManagePresenterImplObj = c.newInstance(proxy);
                                        //Object BxManagePresenterImplObj = BxManagePresenterImpl.newInstance();
                                        doBaoXiao.invoke(BxManagePresenterImplObj, hashMap, accountId, total, time);
                                        // param.args[3]=null;

                                        // XposedHelpers.callMethod(param.thisObject, "onSuccess");
                                        Toast.makeText(utils.getContext(), "报销成功！", Toast.LENGTH_LONG).show();
                                        param.setResult(false);
                                        break;
                                    }


                                 }

                                //  XposedHelpers.callMethod(activity, "finishAndRemoveTask");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

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
