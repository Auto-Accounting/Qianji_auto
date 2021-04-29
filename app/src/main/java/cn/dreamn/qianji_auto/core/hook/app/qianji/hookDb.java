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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.dreamn.qianji_auto.core.hook.Task;
import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class hookDb {
    public static void init(Utils utils) {


        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) {
                Activity activity = (Activity) param.thisObject;
                final String activityClzName = activity.getClass().getName();
                utils.log(activityClzName);
                if (activityClzName.contains("com.mutangtech.qianji.ui.main.MainActivity")) {
                    doDbHook(activity, utils);
                }
            }
        });


    }

    private static void doDbHook(Activity activity, Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
        if (intent != null) {
            utils.log("钱迹收到数据:" + intent.getStringExtra("needAsync"));
            if (intent.getStringExtra("needAsync") != null && intent.getStringExtra("needAsync").equals("true")) {
                DBHelper dbHelper;
                try {
                    dbHelper = new DBHelper();
                    String res=dbHelper.getAllTables();
                    utils.log("钱迹表格数据："+res);
                    ArrayList<Data> asset = dbHelper.getAsset();
                    ArrayList<Data> category = dbHelper.getCategory();
                    ArrayList<Data> userBook = dbHelper.getUserBook();
                    dbHelper.finalize();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("asset", asset);
                    bundle.putParcelableArrayList("category", category);
                    bundle.putParcelableArrayList("userBook", userBook);
                    //  utils.log(bundle.toString());
                    utils.send2auto(bundle);


                    Toast.makeText(utils.getContext(), "钱迹数据信息获取完毕，现在请返回自动记账。", Toast.LENGTH_LONG).show();
                    XposedHelpers.callMethod(activity, "finish");
                } catch (Exception e) {
                    utils.log("钱迹数据获取失败" + e.toString(), false);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    utils.log("钱迹数据获取失败2" + throwable.toString(), false);
                }

            }

        } else {
            utils.log("intent获取失败");
        }
    }


}
