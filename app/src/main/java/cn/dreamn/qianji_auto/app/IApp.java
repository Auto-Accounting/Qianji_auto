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

package cn.dreamn.qianji_auto.app;

import android.content.Context;
import android.os.Bundle;

import cn.dreamn.qianji_auto.bills.BillInfo;

public interface IApp {

    /**
     * 返回包名
     *
     * @return
     */
    String getPackPageName();

    /**
     * 返回APP名，可以自己乱定义
     *
     * @return
     */
    String getAppName();


    /**
     * 获取app图标
     * @return
     */
    int getAppIcon();

    /**
     * 发送数据给app
     *
     * @param str
     */
    void sendToApp(Context context, BillInfo str);

    /**
     * 同步数据(发出请求)
     *
     * @return
     */
    void asyncDataBefore(Context context, int type);

    /**
     * 同步数据(通过广播获取)
     *
     * @return
     */
    void asyncDataAfter(Context context, Bundle data, int type);

    /**
     * 同步数据的说明性文字
     */
    String getAsyncDesc(Context context);


}
