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

package cn.dreamn.qianji_auto.utils.tools;

import android.content.Context;
import android.util.DisplayMetrics;

public class DpUtil {

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    public static float dip2pxF(Context context, float dipValue) {
        return dipValue * getDensity(context);
    }

    private static float getDensity(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }
}