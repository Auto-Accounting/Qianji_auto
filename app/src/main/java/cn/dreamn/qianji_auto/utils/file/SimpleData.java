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

package cn.dreamn.qianji_auto.utils.file;


public class SimpleData {
    public static void init(){
        initAliMap();
        initLearn();
        initMap();
        initWeMap();
        initSet();
    }
    private static void initSet(){
        if(Storage.isExist(Storage.Set)){
            Storage.type(Storage.Set).set("mode","xposed");//默认模式
            Storage.type(Storage.Set).set("autoPay",true);
            Storage.type(Storage.Set).set("autoIncome",true);
            Storage.type(Storage.Set).set("bookname","默认账本");
            Storage.type(Storage.Set).set("remark","[交易对象]-[说明]");
        }
    }
    //资产映射
    private static void initMap(){
        if(Storage.isExist(Storage.Map)){
            Storage.type(Storage.Map).set("余额宝","支付宝");
            Storage.type(Storage.Map).set("支付宝余额","支付宝");
            Storage.type(Storage.Map).set("花呗支付","花呗");
            Storage.type(Storage.Map).set("京东白条","白条");
            Storage.type(Storage.Map).set("零钱","微信");
            Storage.type(Storage.Map).set("零钱通","微信");
        }
    }

    //支付宝分类映射
    private static void initAliMap(){
        if(Storage.isExist(Storage.AliMap)){
            Storage.type(Storage.AliMap).set("退款","外快");
            Storage.type(Storage.AliMap).set("收红包","外快");
            Storage.type(Storage.AliMap).set("二维码收款","外快");
            Storage.type(Storage.AliMap).set("转账","外快");
            Storage.type(Storage.AliMap).set("余额宝","理财收益");
            Storage.type(Storage.AliMap).set("余利宝","理财收益");
            Storage.type(Storage.AliMap).set("基金","理财收益");
        }
    }
    //微信分类映射
    private static void initWeMap(){
        if(Storage.isExist(Storage.WeMap)){
            Storage.type(Storage.WeMap).set("退款","外快");
            Storage.type(Storage.WeMap).set("收红包","外快");
            Storage.type(Storage.WeMap).set("二维码收款","外快");
            Storage.type(Storage.WeMap).set("转账","外快");
        }
    }
    //语义识别
    private static void initLearn(){
        if(Storage.isExist(Storage.Learn)){
            Storage.type(Storage.Learn).set("余利宝","理财收益");
            Storage.type(Storage.Learn).set("基金","理财收益");
            Storage.type(Storage.Learn).set("花呗","信用借还");
            Storage.type(Storage.Learn).set("超市","生活日用");
            Storage.type(Storage.Learn).set("购物","生活日用");
            Storage.type(Storage.Learn).set("肯德基","餐饮美食");
            Storage.type(Storage.Learn).set("麦当劳","餐饮美食");
        }
    }
}
