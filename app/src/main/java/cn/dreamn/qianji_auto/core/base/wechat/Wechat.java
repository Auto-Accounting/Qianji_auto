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

package cn.dreamn.qianji_auto.core.base.wechat;

public class Wechat {
    public final static String PAYMENT = "微信付款成功";//微信付款成功
    public final static String RECEIVED_QR = "微信二维码收款";//微信收到二维码支付信息
    public final static String PAYMENT_TRANSFER = "微信转账给别人";//微信转账给别人
    public final static String PAYMENT_TRANSFER_REFUND = "微信转账退款到账";//微信转账退款到账
    public final static String PAYMENT_TRANSFER_RECEIVED = "微信接收他人转账";//微信转账已接收

    public final static String RED_REFUND = "微信红包退款到账";//红包退款到账
    public final static String RED_PACKAGE = "微信发红包给别人";//微信发红包给别人
    public final static String RED_PACKAGE_RECEIVED = "微信接收他人红包";//微信收到红包
    public static final String PAYMENT_REFUND = "微信支付退款";
    public static final String CANT_UNDERSTAND = "微信无法解析的消息类型";

}
