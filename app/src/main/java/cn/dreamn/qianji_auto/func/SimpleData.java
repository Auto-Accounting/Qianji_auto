/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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
 * 补充一些基础的
 */

package cn.dreamn.qianji_auto.func;

import com.alibaba.fastjson.JSONObject;

public class SimpleData {
    public static void initMap(){
        JSONObject data = Storage.type(Storage.Map).getAll();
        if(data==null){
            Storage.type(Storage.Map).set("余额宝","支付宝");
            Storage.type(Storage.Map).set("支付宝余额","支付宝");
            Storage.type(Storage.Map).set("花呗支付","花呗");
            Storage.type(Storage.Map).set("京东白条","白条");
            Storage.type(Storage.Map).set("零钱","微信");
            Storage.type(Storage.Map).set("零钱通","微信");
        }
    }

    public static void initLearn(){
        JSONObject data = Storage.type(Storage.Learn).getAll();
        if(data==null){
            Storage.type(Storage.Learn).set("花呗","信用借还");
            Storage.type(Storage.Learn).set("超市","生活日用");
            Storage.type(Storage.Learn).set("购物","生活日用");
            Storage.type(Storage.Learn).set("肯德基","餐饮美食");
            Storage.type(Storage.Learn).set("麦当劳","餐饮美食");
        }
    }
}
