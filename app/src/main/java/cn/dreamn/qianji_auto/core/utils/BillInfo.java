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

package cn.dreamn.qianji_auto.core.utils;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.xuexiang.xutil.data.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Set;

import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * 钱迹数据字段
 */
public class BillInfo  {

    private String type;//账单类型

    public static String TYPE_PAY = "0";//支出
    public static String TYPE_INCOME = "1";//收入
    public static String TYPE_TRANSFER_ACCOUNTS = "2";//转账
    public static String TYPE_CREDIT_CARD_PAYMENT = "3";//信用卡还款

    private String money;//大于0

    private String time;//yyyy-MM-dd HH:mm:ss

    //以上为必传参数

    private String remark;//备注信息

    private String catename;//分类

    private String catechoose="0";//type=0或1有效

    private String bookname = "默认账本";//账本名称，不填写则使用默认账本

    private String accountname;//账单所属资产名称(或转账的转出账户）

    private String accountname2;//转账或者还款的转入账户


    private String shopAccount;//识别出来的收款账户

    private String shopRemark;//识别出来的备注


    private String source;//账单来源


    public String getType() {
        return type;
    }

    public String getMoney() {
        return this.money;
    }

    public String getTime() {
        return time;
    }

    public String getRemark() {
        return remark;
    }

    public String getCateName() {
        return catename;
    }

    public Boolean getCateChoose() {
        return catechoose.equals("1");
    }

    public String getBookName() {
        return bookname;
    }

    public String getAccountName() {
        return accountname;
    }

    public String getAccountName2() {
        return accountname2;
    }


    public String getShopAccount() {
        return this.shopAccount;
    }

    public String getShopRemark() {
        return this.shopRemark;
    }

    public String getSource(){ return this.source;}

    public void setSource(String source){
        this.source=source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @SuppressLint("SimpleDateFormat")
    public void setTime() {
        this.time = DateUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCateName(String name) {
        this.catename = name;
    }

    public void setCateChoose(Boolean Open) {
        if (Open) {
            this.catechoose = "1";
        } else {
            this.catechoose = "0";
        }

    }

    public void setBookName(String name) {
        this.bookname = name;
    }

    public void setAccountName(String name) {
        this.accountname = name;
    }

    public void setAccountName2(String name) {
        this.accountname2 = name;
    }

    public void setShopAccount(String name) {
        this.shopAccount = name;
    }

    public void setShopRemark(String name) {
        this.shopRemark = name;
    }

    public String getQianJi() {

        String url = "qianji://publicapi/addbill?&type=" + type + "&money=" + money;

        if (time != null) {
            url += "&time=" + time;
        }
        if (remark != null) {
            url += "&remark=" + remark;
        }
        if (catename != null) {
            url += "&catename=" + catename;
        }
        url += "&catechoose=" + catechoose;

        url += "&catetheme=auto";

        if (this.bookname != null && !this.bookname.equals("默认账本")) {
            url += "&bookname=" + bookname;
        }

        if (accountname != null) {
            url += "&accountname=" + accountname;
        }
        if (accountname2 != null) {
            url += "&accountname2=" + accountname2;
        }
        Logs.d("钱迹URL:" + url);
        return url;
    }
    @NotNull
    public String toString(){
        String url=this.getQianJi();
        if (shopAccount != null) {
            url += "&shopAccount=" + shopAccount;
        }
        if (shopRemark != null) {
            url += "&shopRemark=" + shopRemark;
        }
        if(source!=null){
            url +="&source="+source;
        }
        return url;
    }

    public static BillInfo parse(String url) {
        Uri mUri = Uri.parse(url);
        BillInfo billInfo = new BillInfo();
        // 获得所有参数 key
        Set<String> params = mUri.getQueryParameterNames();
        for (String param : params) {
            String value = mUri.getQueryParameter(param);
            switch (param) {
                case "type":
                    billInfo.setType(value);
                    break;
                case "money":
                    billInfo.setMoney(value);
                    break;
                case "time":
                    billInfo.setTime(value);
                    break;
                case "remark":
                    billInfo.setRemark(value);
                    break;
                case "catename":
                    billInfo.setCateName(value);
                    break;
                case "bookname":
                    billInfo.setBookName(value);
                    break;
                case "accountname":
                    billInfo.setAccountName(value);
                    break;
                case "accountname2":
                    billInfo.setAccountName2(value);
                    break;
                case "shopAccount":
                    billInfo.setShopAccount(value);
                    break;
                case "shopRemark":
                    billInfo.setShopRemark(value);
                    break;
                case "source":
                    billInfo.setSource(value);
                default:
                    break;
            }

        }
        return billInfo;


    }

    public boolean isAvaiable() {
        //检查时间
        if (this.time == null || this.time.equals("")) {
            Logs.d("Qianji_Analyze", "时间错误" + this.time);
            return false;
        }
        //检查金额
        if (this.money == null || BillTools.getMoney(this.money).equals("0")|| BillTools.getMoney(this.money).equals("0.0")|| BillTools.getMoney(this.money).equals("0.00")) {
            Logs.d("Qianji_Analyze", "金额 :" + this.money);
            return false;
        }
        //检查分类
        if (this.type == null) {
            Logs.d("Qianji_Analyze", "分类错误 -> null");
            return false;
        }

        return true;
    }

    public static String getTypeName(String type) {
        if (type.equals(TYPE_PAY)) return "支出";
        if (type.equals(TYPE_CREDIT_CARD_PAYMENT)) return "信用还款";
        if (type.equals(TYPE_INCOME)) return "收入";
        if (type.equals(TYPE_TRANSFER_ACCOUNTS)) return "转账";
        return "支出";
    }

    public void dump() {
        Logs.d("Qianji_Analyze", "type=" + type);
        Logs.d("Qianji_Analyze", "money=" + money);
        Logs.d("Qianji_Analyze", "time=" + time);
        Logs.d("Qianji_Analyze", "remark=" + remark);
        Logs.d("Qianji_Analyze", "catename=" + catename);
        Logs.d("Qianji_Analyze", "catechoose=" + catechoose);
        Logs.d("Qianji_Analyze", "bookname=" + bookname);
        Logs.d("Qianji_Analyze", "accountname=" + accountname);
        Logs.d("Qianji_Analyze", "accountname2=" + accountname2);
        Logs.d("Qianji_Analyze", "shopAccount=" + shopAccount);
        Logs.d("Qianji_Analyze", "shopRemark=" + shopRemark);
        Logs.d("Qianji_Analyze", "是否有效？" + (isAvaiable() ? "有效" : "无效"));

    }



}
