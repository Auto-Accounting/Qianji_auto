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

package cn.dreamn.qianji_auto.core.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cn.dreamn.qianji_auto.core.base.alipay.Alipay;
import cn.dreamn.qianji_auto.core.base.wechat.Wechat;

public class Manager {
    public static String[] getAll() throws IllegalAccessException {
        Field[] fields = Wechat.class.getDeclaredFields();

        Array<String> array = new Array<>();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().toString().endsWith("java.lang.String") && Modifier.isStatic(field.getModifiers()))
                array.addLast((String) field.get(Wechat.class));
        }

        fields = Alipay.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().toString().endsWith("java.lang.String") && Modifier.isStatic(field.getModifiers()))
                array.addLast((String) field.get(Alipay.class));
        }

        return array.toArray();
    }
}
