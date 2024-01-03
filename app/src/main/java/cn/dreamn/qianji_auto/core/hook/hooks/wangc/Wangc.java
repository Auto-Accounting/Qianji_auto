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

package cn.dreamn.qianji_auto.core.hook.hooks.wangc;

import android.os.Handler;

import cn.dreamn.qianji_auto.core.hook.core.hookBase;
import cn.dreamn.qianji_auto.core.hook.hooks.wangc.hooks.DataBase;
import cn.dreamn.qianji_auto.core.hook.hooks.wangc.hooks.Setting;

public class Wangc extends hookBase {
    static final hookBase self = new Wangc();
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

    @Override
    public void hookLoadPackage() throws ClassNotFoundException {

        try {
            DataBase.init(utils);
        } catch (Throwable e) {
            utils.log("一木记账 DataBase HookError " + e.toString());
        }

        try {
            Setting.init(utils);
        } catch (Throwable e) {
            utils.log("一木记账 Setting HookError " + e.toString());
        }

    }



    @Override
    public String getPackPageName() {
        return "com.wangc.bill";
    }

    @Override
    public String getAppName() {
        return "一木记账";
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
