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

package cn.dreamn.qianji_auto.core.hook.hooks.qianji;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks.AutoError;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks.DataBase;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks.LeftSide;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks.Reimbursement;
import cn.dreamn.qianji_auto.core.hook.hooks.qianji.hooks.Timeout;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Qianji extends hookBase {
    static final hookBase self = new Qianji();
    public static hookBase getInstance() {
        return self;
    }





    private Handler mHandler;
    private Thread mUiThread;

    final void attach(){
        mHandler = new Handler();
        mUiThread = Thread.currentThread();
    }

    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }


    interface RunBody {
         void run(Class<?> clz) throws IllegalAccessException, InstantiationException, InvocationTargetException;
    }
    private void scan(Context ctx,ClassLoader loader,RunBody runnable) {
        try {
          //  utils.log("Class-dump-dex:"+ctx.getPackageResourcePath());
            DexFile dex = new DexFile(ctx.getPackageResourcePath());
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String strClazz = entries.nextElement();
                if (!strClazz.startsWith("android.")
                        && !strClazz.startsWith("androidx.")
                        && !strClazz.startsWith("com.android.")
                        && !strClazz.startsWith(".system")
                        && !strClazz.contains(".system")
                        && !strClazz.startsWith("java.")
                        && !strClazz.startsWith("com.alipay.sdk")
                        && !strClazz.contains("com.google")
                        && !strClazz.contains("xposed")){
                   // utils.log("Class-dump:"+strClazz);
                    try{
                        Class<?> entryClass =loader.loadClass(strClazz);
                        runnable.run(entryClass);
                    }catch (Exception e){
                       // e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //查找需要hook的class
    private JSONObject findAllHookClass() throws ClassNotFoundException {
        String key  = "version_"+(utils.getVerName()+utils.getVerCode()).hashCode();
        String data = utils.readData(key);
        if(!Objects.equals(data, "")){
            try{
               return JSONObject.parseObject(data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Toast.makeText(utils.getContext(),"钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")未适配，尝试适配！",Toast.LENGTH_LONG).show();
        utils.log("钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")未适配，尝试适配！");
        attach();
        new Thread(){
            @Override
            public void run() {
                //新的线程适配
                JSONObject jsonObject = new JSONObject();
                // 查找timeout
                scan(utils.getContext(),utils.getClassLoader(), clz -> {
                    //遍历每一个class
                    if(clz.getName().equals("com.mutangtech.qianji.bill.auto.AddBillIntentAct")) {
                        Method[] m = clz.getDeclaredMethods();
                        // 打印获取到的所有的类方法的信息
                        Log.e("XPosed", Arrays.toString(m));
                        for (Method method : m) {

                                //私有
                                Class<?>[] parameterTypes = method.getParameterTypes();

                                Log.e("Xposed---method", method.toString());

                                if (parameterTypes.length == 2 && parameterTypes[1].getName().equals("com.mutangtech.qianji.data.model.AutoTaskLog") && parameterTypes[0].getName().equals("java.lang.String")) {
                                    //找到AutoError
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray.add("com.mutangtech.qianji.bill.auto.AddBillIntentAct");
                                    jsonArray.add(method.getName());
                                    jsonObject.put("autoError", jsonArray);
                                   // Log.e("Xposed-Find1",jsonObject.toString());

                                } else if (parameterTypes.length == 1 && parameterTypes[0].getName().equals("int") && method.getReturnType().getName().equals("boolean")) {
                                    Log.e("Xposed-find","查找到对应的方法");
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                         // Looper.prepare();
                                          Object obj = null;
                                          try {
                                              obj = clz.newInstance();
                                              method.setAccessible(true);
                                              Boolean result = (Boolean) method.invoke(obj, 2);
                                              if (Boolean.TRUE.equals(result)) {
                                                  JSONArray jsonArray = new JSONArray();
                                                  jsonArray.add("com.mutangtech.qianji.bill.auto.AddBillIntentAct");
                                                  jsonArray.add(method.getName());
                                                  jsonObject.put("baoxiaoTest", jsonArray);
                                           //       Log.e("Xposed-Find2", jsonObject.toString());
                                              }
                                          } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                                              e.printStackTrace();
                                          }


                                      }
                                  });


                            }
                        }
                    }else{
                        Method[] methods = clz.getMethods();
                        int flagIsVip = 0;
                        String[] vipMethods = new String[]{"getInstance","isVip","isVipExpired","checkVIP"};
                        for (Method m:methods) {
                            String mName = m.getName();
                            Class<?>[] parameterTypes = m.getParameterTypes();
                            if("timeoutApp".equals(m.getName())&&parameterTypes.length==2&&parameterTypes[0].getName().equals("java.lang.String")&&parameterTypes[1].getSimpleName().equals("long")){
                                //找到timeout类
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.add(clz.getName());
                                jsonArray.add(m.getName());
                                jsonObject.put("timeout", jsonArray);
                               // Log.e("Xposed-Find3", jsonObject.toString());

                            }else{
                                if(Arrays.asList(vipMethods).contains(mName)){
                                    flagIsVip++;
                                }
                            }
                        }

                        if(flagIsVip==vipMethods.length){
                            //找到User
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.add(clz.getName());
                            jsonArray.add("isVip");
                            jsonObject.put("user", jsonArray);
                      //      Log.e("Xposed-Find4",jsonObject.toString());
                        }
                    }

                });

                runOnUiThread(() -> {
                    if(jsonObject.containsKey("user")&&jsonObject.containsKey("timeout")&&jsonObject.containsKey("baoxiaoTest")&&jsonObject.containsKey("autoError")){
                        Toast.makeText(utils.getContext(),"钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")适配成功！",Toast.LENGTH_LONG).show();
                        utils.log("钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")适配成功！");
                        utils.writeData(key,jsonObject.toString());
                        try {
                            Thread.sleep(2000);
                            utils.restart();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else{
                        Toast.makeText(utils.getContext(),"钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")适配失败！",Toast.LENGTH_LONG).show();
                        utils.log("钱迹版本："+utils.getVerName()+"("+utils.getVerCode()+")适配失败！");
                        Log.e("Xposed",jsonObject.toJSONString());
                    }
                });
            }
        }.start();

return null;

    }
    @Override
    public void hookLoadPackage() throws ClassNotFoundException {

        try {
            LeftSide.init(utils);
        } catch (Throwable e) {
            utils.log("钱迹 LeftSide HookError " + e.toString());
        }

        JSONObject jsonObject = findAllHookClass();
        if(jsonObject==null)return;
        try {
            DataBase.init(utils,jsonObject.getJSONArray("user"));
        } catch (Throwable e) {
            utils.log("钱迹 DataBase HookError " + e.toString());
        }
        try {
            Timeout.init(utils,jsonObject.getJSONArray("timeout"));
        } catch (Throwable e) {
            utils.log("钱迹 Timeout HookError " + e.toString());
        }
        try {
            AutoError.init(utils,jsonObject.getJSONArray("autoError"));
        } catch (Throwable e) {
            utils.log("钱迹 AutoError HookError " + e.toString());
        }


        try {
            Reimbursement.init(utils,jsonObject.getJSONArray("baoxiaoTest"));
        } catch (Throwable e) {
            utils.log("钱迹 Reimbursement HookError " + e.toString());
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
    public boolean needHelpFindApplication() {
        return true;
    }

    @Override
    public int hookIndex() {
        return 1;
    }
}
