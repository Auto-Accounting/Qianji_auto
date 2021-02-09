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
    public static String TYPE_PAYMENT_REFUND = "5";//报销
    private String money;//大于0

    private String time;//yyyy-MM-dd HH:mm:ss


    private String billType;//账单类型
    //以上为必传参数

    private String remark;//备注信息

    private String catename;//分类

    private String catechoose = "0";//type=0或1有效

    private String bookname = "默认账本";//账本名称，不填写则使用默认账本

    private String accountname;//账单所属资产名称(或转账的转出账户）

    private String accountname2;//转账或者还款的转入账户


    private String shopAccount;//识别出来的收款账户

    private String shopRemark;//识别出来的备注


    private String source;//账单来源


    private String isSilent;//是否为静默模式

    public boolean getIsSilent() {
        return isSilent != null && isSilent.equals("true");
    }


    public void setSilent(boolean state) {
        isSilent = (state ? "true" : "false");
    }


    public String getBillType() {
        return billType;
    }


    public void setBillType(String type) {
        billType = type;
    }

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
        if (source != null) {
            url += "&source=" + source;
        }

        if (isSilent != null) {
            url += "&isSilent=" + isSilent;
        }
        if (billType != null) {
            url += "&billType=" + billType;
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
                    break;
                case "isSilent":
                    billInfo.setSilent(value.equals("true"));
                    break;
                case "billType":
                    billInfo.setBillType(value);
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

        if (this.accountname != null && this.accountname.equals(this.accountname2))
            return false;

        return true;
    }

    public static String getTypeId(String type) {
        switch (type) {
            case "支出":
                return BillInfo.TYPE_PAY;
            case "收入":
                return BillInfo.TYPE_INCOME;
            case "转账":
                return BillInfo.TYPE_TRANSFER_ACCOUNTS;
            case "信用还款":
                return BillInfo.TYPE_CREDIT_CARD_PAYMENT;
            case "报销":
                return BillInfo.TYPE_PAYMENT_REFUND;
            default:
                return BillInfo.TYPE_PAY;
        }
    }

    public static String getTypeName(String type) {
        if (type.equals(TYPE_PAY)) return "支出";
        if (type.equals(TYPE_CREDIT_CARD_PAYMENT)) return "信用还款";
        if (type.equals(TYPE_INCOME)) return "收入";
        if (type.equals(TYPE_TRANSFER_ACCOUNTS)) return "转账";
        if (type.equals(TYPE_PAYMENT_REFUND)) return "报销";
        return "支出";
    }

    public String dump() {
        String output = "";
        output += String.format("消费类型=%s\n", getTypeName(type));
        output += String.format("金额=%s\n", money);
        output += String.format("时间=%s\n", time);
        output += String.format("备注=%s\n", remark);
        output += String.format("分类=%s\n", catename);
        output += String.format("分类选择=%s\n", catechoose);
        output += String.format("账本名=%s\n", bookname);
        output += String.format("资产名1=%s\n", accountname);
        output += String.format("资产名2=%s\n", accountname2);
        output += String.format("商户名=%s\n", shopAccount);
        output += String.format("商户备注=%s\n", shopRemark);
        output += String.format("账单类型=%s\n", billType);
        output += String.format("数据来源=%s\n", source);
        output += String.format("是否静默=%s\n", getIsSilent());
        output += "是否有效？" + (isAvaiable() ? "有效" : "无效");
        return output;

    }


}
